
package com.shortey.medieval_game.mapper;

import com.shortey.medieval_game.dto.PlayerSummaryDto;
import com.shortey.medieval_game.entity.Player;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

    public PlayerSummaryDto toSummaryDto(Player player) {
        return new PlayerSummaryDto(
                player.getId(),
                player.getName(),
                player.getTerritory() != null
                        ? player.getTerritory().getName()
                        : null
        );
    }
}
