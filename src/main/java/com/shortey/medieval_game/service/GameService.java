package com.shortey.medieval_game.service;

import com.shortey.medieval_game.entity.Game;
import com.shortey.medieval_game.entity.Player;
import com.shortey.medieval_game.repository.GameRepository;
import com.shortey.medieval_game.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import com.shortey.medieval_game.entity.GameStatus;
import com.shortey.medieval_game.repository.TerritoryRepository;
import com.shortey.medieval_game.entity.BuildingType;
import com.shortey.medieval_game.service.WorkerService;
import com.shortey.medieval_game.repository.BuildingRepository;
import com.shortey.medieval_game.entity.Gender;
import com.shortey.medieval_game.entity.WorkerJob;
import com.shortey.medieval_game.entity.ResourceType;
import com.shortey.medieval_game.service.BuildingStorageService;
import com.shortey.medieval_game.service.TerritoryResourceService;
import com.shortey.medieval_game.dto.GameResponseDto;
import com.shortey.medieval_game.mapper.GameMapper;
import com.shortey.medieval_game.dto.GameClockDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private static final int MAX_PLAYERS = 6;
    private final TerritoryRepository territoryRepository;
    private final BuildingService buildingService;
    private final WorkerService workerService;
    private final BuildingRepository buildingRepository;
    private final BuildingStorageService buildingStorageService;
    private final TerritoryResourceService territoryResourceService;
    private final GameMapper gameMapper;
    

    public GameService(
            GameRepository gameRepository,
            PlayerRepository playerRepository,
            TerritoryRepository territoryRepository,
            BuildingService buildingService,
            WorkerService workerService,
            BuildingRepository buildingRepository,
            BuildingStorageService buildingStorageService,
            TerritoryResourceService territoryResourceService,
            GameMapper gameMapper) {

        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.territoryRepository = territoryRepository;
        this.buildingService = buildingService;
        this.workerService = workerService;
        this.buildingRepository = buildingRepository;
        this.buildingStorageService = buildingStorageService;
        this.territoryResourceService = territoryResourceService;
        this.gameMapper = gameMapper;
    }

    public GameResponseDto createGame() {
    Game game = new Game();
    game.setStatus(GameStatus.WAITING);

    Game savedGame = gameRepository.save(game);
   return gameMapper.toResponseDto(savedGame);
}

  public GameResponseDto addPlayerToGame(Long gameId, Long playerId){

    Game game = gameRepository.findById(gameId)
            .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Game not found"));

    Player player = playerRepository.findById(playerId)
            .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Player not found"));

    if (game.getStatus() != GameStatus.WAITING) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            "Game has already started"
        );
    }

    if (game.getPlayers().size() >= MAX_PLAYERS) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            "Game is full"
        );
    }

    if (game.getPlayers().contains(player)) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            "Player is already in this game"
        );
    }

    game.getPlayers().add(player);

 Game savedGame = gameRepository.save(game);
return gameMapper.toResponseDto(savedGame);
}
public GameResponseDto startGame(Long gameId){

    Game game = gameRepository.findById(gameId)
            .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Game not found"));

    if (game.getStatus() != GameStatus.WAITING) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            "Game has already started or finished"
        );
    }

    if (game.getPlayers().isEmpty()) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            "Game must have at least one player"
        );
    }
    territoryResourceService.initializeGameResources(game);

    var territories = territoryRepository.findAllByOrderByIdAsc();

    for (int i = 0; i < game.getPlayers().size(); i++) {

        Player player = game.getPlayers().get(i);

        player.setTerritory(territories.get(i));

        playerRepository.save(player);
        if (territories.size() < game.getPlayers().size()) {
    throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            "Not enough territories available for all players");
}

   var castle = buildingService.createBuilding(
        BuildingType.CASTLE,
        player.getId(),
        player.getTerritory().getId(),
        game.getId()
);

buildingStorageService.createStorage(castle, ResourceType.WOOD, 8);
buildingStorageService.createStorage(castle, ResourceType.STONE, 10);
buildingStorageService.createStorage(castle, ResourceType.IRON, 8);
buildingStorageService.createStorage(castle, ResourceType.STEEL, 4);
buildingStorageService.createStorage(castle, ResourceType.FISH, 10);
buildingStorageService.createStorage(castle, ResourceType.WATER, 0);
buildingStorageService.createStorage(castle, ResourceType.FOOD, 0);

var house1 = buildingService.createBuilding(
        BuildingType.VILLAGE_HOUSE,
        player.getId(),
        player.getTerritory().getId(),
        game.getId()
);

var house2 = buildingService.createBuilding(
        BuildingType.VILLAGE_HOUSE,
        player.getId(),
        player.getTerritory().getId(),
        game.getId()
);
     

     
workerService.createWorker(
        "Worker 1",
        Gender.MALE,
        WorkerJob.UNASSIGNED,
        player,
        player.getTerritory(),
        castle,
        game 
);

workerService.createWorker(
        "Worker 2",
        Gender.MALE,
        WorkerJob.UNASSIGNED,
        player,
        player.getTerritory(),
        castle,
        game
);

workerService.createWorker(
        "Castle Maid",
        Gender.FEMALE,
        WorkerJob.HOUSEHOLD,
        player,
        player.getTerritory(),
        castle,
        game
);

    }

    game.setStatus(GameStatus.STARTED);
   Game savedGame = gameRepository.save(game);
return gameMapper.toResponseDto(savedGame);
}


public GameClockDto pauseGame(Long gameId) {
    Game game = gameRepository.findById(gameId)
           .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Game not found"));

    if (game.getStatus() != GameStatus.STARTED) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            "Only a started game can be paused"
        );
    }

    long now = System.currentTimeMillis();
    long elapsed = Math.max(0L, now - game.getLastClockUpdateMillis());
    long progressMillis = game.getDayProgressMillis() + elapsed;

    long daysElapsed = progressMillis / 10_000L;
    long remainingMillis = progressMillis % 10_000L;

    if (daysElapsed > 0) {
        game.setCurrentDay(game.getCurrentDay() + (int) daysElapsed);
    }

    game.setDayProgressMillis(remainingMillis);
    game.setLastClockUpdateMillis(0L);
    game.setStatus(GameStatus.PAUSED);

    Game savedGame = gameRepository.save(game);
   return gameMapper.toClockDto(savedGame);
}

public GameClockDto resumeGame(Long gameId) {
    Game game = gameRepository.findById(gameId)
           .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Game not found"));

    if (game.getStatus() != GameStatus.PAUSED) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            "Only a paused game can be resumed"
        );
    }

    game.setLastClockUpdateMillis(System.currentTimeMillis());
    game.setStatus(GameStatus.STARTED);

    Game savedGame = gameRepository.save(game);
   return gameMapper.toClockDto(savedGame);
}




}