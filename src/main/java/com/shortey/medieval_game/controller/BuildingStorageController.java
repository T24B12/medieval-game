
package com.shortey.medieval_game.controller;

import com.shortey.medieval_game.entity.Building;
import com.shortey.medieval_game.entity.BuildingStorage;
import com.shortey.medieval_game.entity.ResourceType;
import com.shortey.medieval_game.repository.BuildingRepository;
import com.shortey.medieval_game.service.BuildingStorageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import com.shortey.medieval_game.dto.BuildingStorageDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

import java.util.Map;
@RestController
@RequestMapping("/building-storage")
public class BuildingStorageController {

    private final BuildingStorageService storageService;
    private final BuildingRepository buildingRepository;

    public BuildingStorageController(
            BuildingStorageService storageService,
            BuildingRepository buildingRepository) {
        this.storageService = storageService;
        this.buildingRepository = buildingRepository;
    }

    public record AddResourceRequest(
            ResourceType resourceType,
            int amount) {
    }
    public record RemoveResourceRequest(ResourceType resourceType, int amount) {}
@ResponseStatus(HttpStatus.OK)
    @PostMapping("/{buildingId}/add")
    public BuildingStorage addResource(
            @PathVariable Long buildingId,
            @RequestBody AddResourceRequest request) {

        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Building not found"));

      try {
    return storageService.addResource(
            building,
            request.resourceType(),
            request.amount());
} catch (IllegalArgumentException exception) {
    throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            exception.getMessage());
}
    }
    @ExceptionHandler(ResponseStatusException.class)
public ResponseEntity<Map<String, String>> handleResponseStatusException(
        ResponseStatusException exception) {

    Map<String, String> response = Map.of(
            "error", exception.getReason(),
            "status", String.valueOf(exception.getStatusCode().value())
    );

    return ResponseEntity
            .status(exception.getStatusCode())
            .body(response);
}

@GetMapping("/{buildingId}")
public List<BuildingStorageDto> getBuildingStorage(
        @PathVariable Long buildingId) {

    Building building = buildingRepository.findById(buildingId)
            .orElseThrow(() ->
                    new IllegalArgumentException("Building not found"));

    return storageService.getBuildingStorageDto(building);
}

@PostMapping("/{buildingId}/remove")
public BuildingStorageDto removeResource(
        @PathVariable Long buildingId,
        @RequestBody RemoveResourceRequest request) {

    Building building = buildingRepository.findById(buildingId)
            .orElseThrow(() ->
                    new IllegalArgumentException("Building not found"));

    try {
        return storageService.removeResource(
                building,
                request.resourceType(),
                request.amount());
    } catch (IllegalArgumentException exception) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                exception.getMessage());
    }
}
}