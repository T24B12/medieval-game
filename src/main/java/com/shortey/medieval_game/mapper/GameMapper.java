
package com.shortey.medieval_game.mapper;

import com.shortey.medieval_game.dto.GameResponseDto;
import com.shortey.medieval_game.dto.GameClockDto;
import com.shortey.medieval_game.dto.PlayerSummaryDto;
import com.shortey.medieval_game.entity.Game;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameMapper {

    private static final int MAX_PLAYERS = 6;

    private final PlayerMapper playerMapper;

    public GameMapper(PlayerMapper playerMapper) {
        this.playerMapper = playerMapper;
    }

    public GameClockDto toClockDto(Game game) {
        return new GameClockDto(
                game.getId(),
                game.getStatus().name(),
                game.getCurrentDay(),
                game.getDayProgressMillis()
        );
    }

    public GameResponseDto toResponseDto(Game game) {
        List<PlayerSummaryDto> players = game.getPlayers().stream()
                .map(playerMapper::toSummaryDto)
                .toList();

        return new GameResponseDto(
                game.getId(),
                game.getStatus().name(),
                game.getCurrentDay(),
                game.getDayProgressMillis(),
                players.size(),
                MAX_PLAYERS,
                players
        );
    }
}
