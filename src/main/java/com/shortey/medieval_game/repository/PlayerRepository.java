
package com.shortey.medieval_game.repository;

import com.shortey.medieval_game.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByGames_Id(Long gameId);
}
