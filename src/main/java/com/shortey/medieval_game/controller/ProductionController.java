
package com.shortey.medieval_game.controller;

import com.shortey.medieval_game.service.ProductionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/production")
public class ProductionController {

    private final ProductionService productionService;

    public ProductionController(ProductionService productionService) {
        this.productionService = productionService;
    }

    @PostMapping("/lumberjack/{workerId}")
    public ProductionResponse produceWood(
            @PathVariable Long workerId,
            @RequestParam Long playerId,
            @RequestParam Long gameId) {

        int amountProduced = productionService.produceWood(
                workerId, playerId, gameId);

        return new ProductionResponse(
                workerId, amountProduced, "WOOD");
    }

    public record ProductionResponse(
            Long workerId,
            int amountProduced,
            String resourceType) {
    }
}
