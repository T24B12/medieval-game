
package com.shortey.medieval_game.repository;

import com.shortey.medieval_game.entity.Game;
import com.shortey.medieval_game.entity.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import com.shortey.medieval_game.entity.Player;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByStatus(GameStatus status);
    @Query("SELECT p FROM Game g JOIN g.players p WHERE g.id = :gameId")
List<Player> findPlayersByGameId(@Param("gameId") Long gameId);
}
