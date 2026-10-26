
package com.shortey.medieval_game.controller;

import com.shortey.medieval_game.entity.Worker;
import com.shortey.medieval_game.service.WorkerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workers")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @GetMapping
    public List<WorkerDto> getAllWorkers() {
        return workerService.getAllWorkers()
                .stream()
                .map(this::toDto)
                .toList();
    }

   
@PutMapping("/{workerId}/workplace/{buildingId}")
public WorkerDto assignWorkplace(
        @PathVariable Long workerId,
        @PathVariable Long buildingId,
        @RequestParam String buildingType,
        @RequestParam Long playerId,
        @RequestParam Long gameId) {

    Worker worker = workerService.assignWorkplace(
            workerId,
            buildingId,
            buildingType,
            playerId,
            gameId
    );

    return toDto(worker);
}


    public record WorkerDto(
            Long id,
            String name,
            String gender,
            String job,
            Long playerId,
            Long territoryId,
            Long homeId,
            Long workplaceId) {
    }

    private WorkerDto toDto(Worker worker) {
        return new WorkerDto(
                worker.getId(),
                worker.getName(),
                worker.getGender() == null
                        ? null : worker.getGender().name(),
                worker.getJob() == null
                        ? null : worker.getJob().name(),
                worker.getPlayer() == null
                        ? null : worker.getPlayer().getId(),
                worker.getTerritory() == null
                        ? null : worker.getTerritory().getId(),
                worker.getHome() == null
                        ? null : worker.getHome().getId(),
                worker.getWorkplace() == null
                        ? null : worker.getWorkplace().getId()
        );
    }
    @GetMapping("/player/{playerId}/game/{gameId}")
public List<WorkerDto> getWorkersByPlayerAndGame(
        @PathVariable Long playerId,
        @PathVariable Long gameId) {

    return workerService.getWorkersByPlayerAndGame(playerId, gameId)
            .stream()
            .map(this::toDto)
            .toList();
}
}
