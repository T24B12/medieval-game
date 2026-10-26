
package com.shortey.medieval_game.controller;

import com.shortey.medieval_game.dto.BuildingDto;
import com.shortey.medieval_game.entity.Building;
import com.shortey.medieval_game.entity.BuildingType;
import com.shortey.medieval_game.service.BuildingService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/buildings")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    public record CreateBuildingRequest(
            BuildingType type,
            Long playerId,
            Long territoryId,
            Long gameId) {
    }

    @PostMapping
    public BuildingDto createBuilding(
            @RequestBody CreateBuildingRequest request) {

        Building building = buildingService.createBuilding(
                request.type(),
                request.playerId(),
                request.territoryId(),
                request.gameId());

        return toDto(building);
    }

    @GetMapping("/player/{playerId}/game/{gameId}")
    public List<BuildingDto> getPlayerBuildings(
            @PathVariable Long playerId,
            @PathVariable Long gameId) {

        return buildingService.getPlayerBuildings(playerId, gameId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private BuildingDto toDto(Building building) {
        return new BuildingDto(
                building.getId(),
                building.getType(),
                building.getLevel(),
                building.getPlayer().getId(),
                building.getTerritory().getId());
    }
    
}
