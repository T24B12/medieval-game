
package com.shortey.medieval_game.service;

import com.shortey.medieval_game.entity.Building;
import com.shortey.medieval_game.entity.BuildingType;
import com.shortey.medieval_game.entity.Game;
import com.shortey.medieval_game.entity.Player;
import com.shortey.medieval_game.entity.Territory;
import com.shortey.medieval_game.repository.BuildingRepository;
import com.shortey.medieval_game.repository.GameRepository;
import com.shortey.medieval_game.repository.PlayerRepository;
import com.shortey.medieval_game.repository.TerritoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.shortey.medieval_game.entity.Gender;
import com.shortey.medieval_game.entity.WorkerJob;

import java.util.List;

@Service
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final PlayerRepository playerRepository;
    private final TerritoryRepository territoryRepository;
    private final GameRepository gameRepository;
    private final WorkerService workerService;

    public BuildingService(
            BuildingRepository buildingRepository,
            PlayerRepository playerRepository,
            TerritoryRepository territoryRepository,
            GameRepository gameRepository,WorkerService workerService) {

        this.buildingRepository = buildingRepository;
        this.playerRepository = playerRepository;
        this.territoryRepository = territoryRepository;
        this.gameRepository = gameRepository;
        this.workerService = workerService;
    }

    public Building createBuilding(
            BuildingType type,
            Long playerId,
            Long territoryId,
            Long gameId) {

       if (type == null) {
    throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Building type is required");

        }

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Player not found"));

        Territory territory = territoryRepository.findById(territoryId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Territory not found"));

        if (player.getTerritory() == null
                || !player.getTerritory().getId().equals(territoryId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Territory does not belong to this player");
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Game not found"));

        if (game.getPlayers().stream()
                .noneMatch(gamePlayer ->
                        gamePlayer.getId().equals(playerId))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Player does not belong to this game");
        }

     Building building = new Building();
building.setType(type);
building.setLevel(1);
building.setPlayer(player);
building.setTerritory(territory);
building.setGame(game);

Building savedBuilding = buildingRepository.save(building);

if (type == BuildingType.VILLAGE_HOUSE) {
    workerService.createWorker(
            "Male Worker - House " + savedBuilding.getId(),
            Gender.MALE,
            WorkerJob.UNASSIGNED,
            player,
            territory,
            savedBuilding,
            game
    );

    workerService.createWorker(
            "Female Worker - House " + savedBuilding.getId(),
            Gender.FEMALE,
            WorkerJob.HOUSEHOLD,
            player,
            territory,
            savedBuilding,
            game
    );
}

return savedBuilding;
    }

    public List<Building> getPlayerBuildings(
            Long playerId,
            Long gameId) {

        playerRepository.findById(playerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Player not found"));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Game not found"));

        if (game.getPlayers().stream()
                .noneMatch(gamePlayer ->
                        gamePlayer.getId().equals(playerId))) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Player does not belong to this game");
        }

        return buildingRepository
                .findByPlayer_IdAndGame_IdOrderByIdAsc(
                        playerId,
                        gameId);
    }
}
