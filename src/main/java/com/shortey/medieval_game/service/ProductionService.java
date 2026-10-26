
package com.shortey.medieval_game.service;

import com.shortey.medieval_game.entity.Building;
import com.shortey.medieval_game.entity.BuildingStorage;
import com.shortey.medieval_game.entity.BuildingType;
import com.shortey.medieval_game.entity.ResourceType;
import com.shortey.medieval_game.entity.TerritoryResource;
import com.shortey.medieval_game.entity.Worker;
import com.shortey.medieval_game.entity.WorkerJob;
import com.shortey.medieval_game.repository.BuildingRepository;
import com.shortey.medieval_game.repository.BuildingStorageRepository;
import com.shortey.medieval_game.repository.TerritoryResourceRepository;
import com.shortey.medieval_game.repository.WorkerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductionService {

    private static final int LEVEL_ONE_LUMBERJACK_PRODUCTION = 2;

    private final WorkerRepository workerRepository;
    private final TerritoryResourceRepository territoryResourceRepository;
    private final BuildingStorageRepository buildingStorageRepository;
    private final BuildingRepository buildingRepository;

    public ProductionService(
            WorkerRepository workerRepository,
            TerritoryResourceRepository territoryResourceRepository,
            BuildingStorageRepository buildingStorageRepository,
            BuildingRepository buildingRepository) {

        this.workerRepository = workerRepository;
        this.territoryResourceRepository = territoryResourceRepository;
        this.buildingStorageRepository = buildingStorageRepository;
        this.buildingRepository = buildingRepository;
    }

    @Transactional
    public int produceWood(Long workerId, Long playerId, Long gameId) {

        Worker worker = workerRepository
                .findByIdAndPlayer_IdAndGame_Id(workerId, playerId, gameId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Worker not found for this player and game"));

        Building hut = worker.getWorkplace();

        if (worker.getJob() != WorkerJob.LUMBERJACK
                || hut == null
                || hut.getType() != BuildingType.LUMBER_HUT
                || hut.getLevel() < 1
                || !hut.getPlayer().getId().equals(playerId)
                || !hut.getGame().getId().equals(gameId)
                || worker.getTerritory() == null
                || hut.getTerritory() == null
                || !worker.getTerritory().getId()
                        .equals(hut.getTerritory().getId())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Worker must be a valid Lumberjack assigned to their Lumber Hut");
        }

        TerritoryResource naturalWood = territoryResourceRepository
                .findByGameAndTerritoryAndResourceType(
                        worker.getGame(),
                        worker.getTerritory(),
                        ResourceType.WOOD)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Natural wood resource not found for this territory"));

        int requestedAmount = LEVEL_ONE_LUMBERJACK_PRODUCTION
                * hut.getLevel();

        int availableWood = (int) Math.floor(
                naturalWood.getAvailableAmount());

        int remainingToProduce = Math.min(
                requestedAmount, availableWood);

        if (remainingToProduce <= 0) {
            return 0;
        }

        var buildings = buildingRepository
                .findByPlayer_IdAndGame_IdOrderByIdAsc(playerId, gameId);

        int totalProduced = 0;

        // Storage buildings first, then Castle.
        for (BuildingType destinationType : new BuildingType[]{
                BuildingType.STORAGE,
                BuildingType.CASTLE
        }) {

            for (Building destination : buildings) {

                if (remainingToProduce <= 0) {
                    break;
                }

                if (destination.getType() != destinationType
                        || destination.getTerritory() == null
                        || !destination.getTerritory().getId()
                                .equals(worker.getTerritory().getId())) {
                    continue;
                }

                var woodStorage = buildingStorageRepository
                        .findByBuildingAndResourceType(
                                destination, ResourceType.WOOD);

                if (woodStorage.isEmpty()) {
                    continue;
                }

                BuildingStorage storage = woodStorage.get();

                int freeCapacity = storage.getCapacity()
                        - storage.getAmount();

                if (freeCapacity <= 0) {
                    continue;
                }

                int amountToStore = Math.min(
                        remainingToProduce, freeCapacity);

                storage.setAmount(
                        storage.getAmount() + amountToStore);

                buildingStorageRepository.save(storage);

                totalProduced += amountToStore;
                remainingToProduce -= amountToStore;
            }
        }

        if (totalProduced > 0) {
            naturalWood.setAvailableAmount(
                    naturalWood.getAvailableAmount() - totalProduced);

            territoryResourceRepository.save(naturalWood);
        }

        return totalProduced;
    }

   
@Transactional
public void processDailyProduction(Long gameId) {

    List<Worker> workers = workerRepository
            .findByGame_IdAndJobInOrderByIdAsc(
                    gameId,
                    List.of(
                            WorkerJob.LUMBERJACK,
                            WorkerJob.MINER,
                            WorkerJob.FISHERMAN
                    ));

    for (Worker worker : workers) {

        if (worker.getJob() != WorkerJob.LUMBERJACK) {
            continue;
        }

        // Skip legacy workers whose game has no matching natural wood resource.
        if (worker.getGame() == null || worker.getTerritory() == null) {
            continue;
        }

        boolean hasNaturalWood = territoryResourceRepository
                .findByGameAndTerritoryAndResourceType(
                        worker.getGame(),
                        worker.getTerritory(),
                        ResourceType.WOOD)
                .isPresent();

        if (!hasNaturalWood) {
            System.out.println(
                    "AUTO PRODUCTION SKIPPED: gameId=" + gameId
                            + ", workerId=" + worker.getId()
                            + " — natural WOOD resource not found");
            continue;
        }

        produceWood(
                worker.getId(),
                worker.getPlayer().getId(),
                gameId);
    }
}

}
