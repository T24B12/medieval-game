package com.shortey.medieval_game.service;

import com.shortey.medieval_game.entity.Gender;
import com.shortey.medieval_game.entity.Player;
import com.shortey.medieval_game.entity.Territory;
import com.shortey.medieval_game.entity.Worker;
import com.shortey.medieval_game.entity.WorkerJob;
import com.shortey.medieval_game.repository.WorkerRepository;
import org.springframework.stereotype.Service;
import com.shortey.medieval_game.entity.Building;
import com.shortey.medieval_game.entity.Game;
import com.shortey.medieval_game.repository.BuildingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.shortey.medieval_game.repository.GameRepository;
import java.util.List;


@Service
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final BuildingRepository buildingRepository;
    private final GameRepository gameRepository;

    public WorkerService(WorkerRepository workerRepository, BuildingRepository buildingRepository, GameRepository gameRepository) {
        this.workerRepository = workerRepository;
        this.buildingRepository = buildingRepository;
        this.gameRepository = gameRepository;
    }

  public Worker createWorker(
        String name,
        Gender gender,
        WorkerJob job,
        Player player,
        Territory territory,
        Building home,
        Game game) {

    Worker worker = new Worker();

    worker.setName(name);
    worker.setGender(gender);
    worker.setJob(job);
    worker.setPlayer(player);
    worker.setTerritory(territory);
    worker.setHome(home);
    worker.setGame(game);

    return workerRepository.save(worker);
}


public Worker assignWorkplace(
        Long workerId,
        Long buildingId,
        String buildingType,
        Long playerId,
        Long gameId) {

    Worker worker = workerRepository
            .findByIdAndPlayer_IdAndGame_Id(workerId, playerId, gameId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Worker not found for this player and game"));

    Building building = buildingRepository
            .findByIdAndPlayer_IdAndGame_Id(buildingId, playerId, gameId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Building not found for this player and game"));

    if (worker.getTerritory() == null
            || building.getTerritory() == null
            || !worker.getTerritory().getId()
                    .equals(building.getTerritory().getId())) {

        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Worker and workplace must belong to the same territory");
    }

    com.shortey.medieval_game.entity.BuildingType requestedType;

    try {
        requestedType = com.shortey.medieval_game.entity.BuildingType.valueOf(
                buildingType.trim().toUpperCase());
    } catch (IllegalArgumentException | NullPointerException ex) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid building type. Use LUMBER_HUT, MINE, or FISHING_HUT");
    }

    if (building.getType() != requestedType) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "The specified building type does not match this building's type");
    }

    if (requestedType != com.shortey.medieval_game.entity.BuildingType.LUMBER_HUT
            && requestedType != com.shortey.medieval_game.entity.BuildingType.MINE
            && requestedType != com.shortey.medieval_game.entity.BuildingType.FISHING_HUT) {

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Workers can currently be assigned only to a Lumber Hut, Mine, or Fishing Hut");
    }

    if (worker.getJob() != WorkerJob.UNASSIGNED
            && worker.getJob() != WorkerJob.LUMBERJACK
            && worker.getJob() != WorkerJob.MINER
            && worker.getJob() != WorkerJob.FISHERMAN) {

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "This worker's current job is not eligible for workplace assignment");
    }

    if (requestedType == com.shortey.medieval_game.entity.BuildingType.LUMBER_HUT) {
        worker.setJob(WorkerJob.LUMBERJACK);
    } else if (requestedType == com.shortey.medieval_game.entity.BuildingType.MINE) {
        worker.setJob(WorkerJob.MINER);
    } else {
        worker.setJob(WorkerJob.FISHERMAN);
    }

    worker.setWorkplace(building);

    return workerRepository.save(worker);
}

public List<Worker> getAllWorkers() {
    return workerRepository.findAll();
}
public List<Worker> getWorkersByPlayerAndGame(
        Long playerId,
        Long gameId) {

    Game game = gameRepository.findById(gameId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Game not found"));

    boolean playerBelongsToGame = game.getPlayers()
            .stream()
            .anyMatch(player -> player.getId().equals(playerId));

    if (!playerBelongsToGame) {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Player does not belong to this game");
    }

    return workerRepository
            .findByPlayer_IdAndGame_IdOrderByIdAsc(playerId, gameId);
}
}
