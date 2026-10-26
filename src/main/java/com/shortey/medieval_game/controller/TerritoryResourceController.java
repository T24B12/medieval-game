
package com.shortey.medieval_game.controller;

import com.shortey.medieval_game.dto.TerritoryResourceDto;
import com.shortey.medieval_game.service.TerritoryResourceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class TerritoryResourceController {

    private final TerritoryResourceService territoryResourceService;

    public TerritoryResourceController(
            TerritoryResourceService territoryResourceService) {
        this.territoryResourceService = territoryResourceService;
    }

    @GetMapping("/{gameId}/territory-resources")
    public List<TerritoryResourceDto> getGameResources(
            @PathVariable Long gameId) {
        return territoryResourceService.getGameResources(gameId);
    }
}
