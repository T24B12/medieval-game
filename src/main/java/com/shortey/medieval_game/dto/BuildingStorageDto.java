
package com.shortey.medieval_game.dto;

import com.shortey.medieval_game.entity.ResourceType;

public record BuildingStorageDto(
        Long id,
        Long buildingId,
        ResourceType resourceType,
        int amount,
        int capacity) {
}