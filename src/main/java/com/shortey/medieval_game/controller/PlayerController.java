package com.shortey.medieval_game.controller;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.shortey.medieval_game.dto.PlayerDto;
import com.shortey.medieval_game.entity.Player;
import com.shortey.medieval_game.service.PlayerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/players")
    public Player createPlayer(@RequestBody PlayerDto playerDto) {
        return playerService.createPlayer(playerDto);
    }

    @GetMapping("/players")
    public List<Player> getPlayers() {
        return playerService.getPlayers();
    }
    
}