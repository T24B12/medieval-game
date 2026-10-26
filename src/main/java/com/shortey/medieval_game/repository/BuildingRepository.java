
package com.shortey.medieval_game.repository;

import com.shortey.medieval_game.entity.Building;
import com.shortey.medieval_game.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuildingRepository extends JpaRepository<Building, Long> {

    List<Building> findByPlayerOrderByIdAsc(Player player);
    List<Building> findByPlayer_IdAndGame_IdOrderByIdAsc(
            Long playerId,
            Long gameId);

    Optional<Building> findByIdAndPlayer_IdAndGame_Id(
            Long buildingId,
            Long playerId,
            Long gameId
    );
    
    
}
