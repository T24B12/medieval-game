
package com.shortey.medieval_game.dto;

import com.shortey.medieval_game.entity.BuildingType;

public record BuildingDto(
        Long id,
        BuildingType type,
        int level,
        Long playerId,
        Long territoryId) {
}