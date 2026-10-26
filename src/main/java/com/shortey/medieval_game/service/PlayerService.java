package com.shortey.medieval_game.service;

import com.shortey.medieval_game.entity.Player;
import com.shortey.medieval_game.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import com.shortey.medieval_game.dto.PlayerDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player createPlayer(PlayerDto playerDto) {
    if (playerDto == null
            || playerDto.getName() == null
            || playerDto.getName().isBlank()) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Player name is required");
    }

    Player player = new Player();
    player.setName(playerDto.getName().trim());

    return playerRepository.save(player);
}
public List<Player> getPlayers() {
    return playerRepository.findAll();
}
}