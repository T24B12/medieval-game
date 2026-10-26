package com.shortey.medieval_game.dto;

public record TerritoryResourceDto(
        Long id,
        Long gameId,
        Long territoryId,
        String territoryName,
        String resourceType,
        double availableAmount,
        double maximumAmount,
        double regenerationRate
) {
}