package com.shortey.medieval_game.controller;
import com.shortey.medieval_game.dto.GameResponseDto;
import com.shortey.medieval_game.dto.GameClockDto;
import com.shortey.medieval_game.service.GameService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/games")
    public GameResponseDto createGame() {
        return gameService.createGame();
    }
    @PostMapping("/games/{gameId}/players/{playerId}")
public GameResponseDto addPlayerToGame(
        @PathVariable Long gameId,
        @PathVariable Long playerId) {

    return gameService.addPlayerToGame(gameId, playerId);
}
@PostMapping("/games/{gameId}/start")
public GameResponseDto startGame(@PathVariable Long gameId) {
    return gameService.startGame(gameId);
}


@PostMapping("/games/{gameId}/pause")
public GameClockDto pauseGame(@PathVariable Long gameId) {
    return gameService.pauseGame(gameId);
}

@PostMapping("/games/{gameId}/resume")
public GameClockDto resumeGame(@PathVariable Long gameId) {
    return gameService.resumeGame(gameId);
}

}

