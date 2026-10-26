package com.shortey.medieval_game.service;

import com.shortey.medieval_game.entity.Building;
import com.shortey.medieval_game.entity.BuildingStorage;
import com.shortey.medieval_game.entity.ResourceType;
import com.shortey.medieval_game.repository.BuildingStorageRepository;
import org.springframework.stereotype.Service;
import com.shortey.medieval_game.dto.BuildingStorageDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class BuildingStorageService {

    private static final int DEFAULT_CAPACITY = 15;

    private final BuildingStorageRepository storageRepository;

    public BuildingStorageService(
            BuildingStorageRepository storageRepository) {
        this.storageRepository = storageRepository;
    }

    public BuildingStorage createStorage(
            Building building,
            ResourceType resourceType,
            int startingAmount) {

        if (startingAmount < 0 || startingAmount > DEFAULT_CAPACITY) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Starting amount must be between 0 and 15");
        }

        if (storageRepository
                .findByBuildingAndResourceType(building, resourceType)
                .isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This resource already has storage in this building");
        }

        BuildingStorage storage = new BuildingStorage();
        storage.setBuilding(building);
        storage.setResourceType(resourceType);
        storage.setAmount(startingAmount);
        storage.setCapacity(DEFAULT_CAPACITY);

        return storageRepository.save(storage);
    }

    public BuildingStorage addResource(
            Building building,
            ResourceType resourceType,
            int amountToAdd) {

        if (amountToAdd <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Amount to add must be greater than zero");
        }

        BuildingStorage storage = storageRepository
                .findByBuildingAndResourceType(building, resourceType)
                .orElseThrow(() -> new ResponseStatusException(
                       HttpStatus.NOT_FOUND,
                        "Storage for this resource does not exist in this building"));

        if (storage.getAmount() + amountToAdd > storage.getCapacity()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Not enough storage capacity for " + resourceType);
        }

        storage.setAmount(storage.getAmount() + amountToAdd);

        return storageRepository.save(storage);
    }
    public List<BuildingStorage> getBuildingStorage(Building building) {
    return storageRepository.findByBuilding(building);
}
public List<BuildingStorageDto> getBuildingStorageDto(Building building) {
    return storageRepository.findByBuilding(building)
            .stream()
            .map(storage -> new BuildingStorageDto(
                    storage.getId(),
                    storage.getBuilding().getId(),
                    storage.getResourceType(),
                    storage.getAmount(),
                    storage.getCapacity()))
            .toList();
}

public BuildingStorageDto removeResource(
        Building building,
        ResourceType resourceType,
        int amountToRemove) {

    if (amountToRemove <= 0) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Amount to remove must be greater than zero");
    }

    BuildingStorage storage = storageRepository
            .findByBuildingAndResourceType(building, resourceType)
            .orElseThrow(() -> new ResponseStatusException(
                   HttpStatus.NOT_FOUND,
                    "Storage for this resource does not exist in this building"));

    if (amountToRemove > storage.getAmount()) {
        throw new ResponseStatusException(
               HttpStatus.CONFLICT,
                "Not enough " + resourceType + " available to remove");
    }

    storage.setAmount(storage.getAmount() - amountToRemove);

    BuildingStorage savedStorage = storageRepository.save(storage);

    return new BuildingStorageDto(
            savedStorage.getId(),
            savedStorage.getBuilding().getId(),
            savedStorage.getResourceType(),
            savedStorage.getAmount(),
            savedStorage.getCapacity());
}
}