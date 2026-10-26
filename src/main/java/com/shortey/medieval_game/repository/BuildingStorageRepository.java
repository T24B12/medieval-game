package com.shortey.medieval_game.repository;

import com.shortey.medieval_game.entity.Building;
import com.shortey.medieval_game.entity.BuildingStorage;
import com.shortey.medieval_game.entity.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuildingStorageRepository
        extends JpaRepository<BuildingStorage, Long> {

    List<BuildingStorage> findByBuilding(Building building);

    Optional<BuildingStorage> findByBuildingAndResourceType(
            Building building,
            ResourceType resourceType
    );
}