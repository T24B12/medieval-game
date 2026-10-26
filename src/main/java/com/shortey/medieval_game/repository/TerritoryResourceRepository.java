
package com.shortey.medieval_game.repository;

import com.shortey.medieval_game.entity.Game;
import com.shortey.medieval_game.entity.Territory;
import com.shortey.medieval_game.entity.TerritoryResource;
import com.shortey.medieval_game.entity.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TerritoryResourceRepository
        extends JpaRepository<TerritoryResource, Long> {

    List<TerritoryResource> findByGameAndTerritory(Game game, Territory territory);

    Optional<TerritoryResource> findByGameAndTerritoryAndResourceType(
            Game game,
            Territory territory,
            ResourceType resourceType
    );
}

