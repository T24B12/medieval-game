
package com.shortey.medieval_game.repository;

import com.shortey.medieval_game.entity.Worker;
import com.shortey.medieval_game.entity.WorkerJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

    Optional<Worker> findByIdAndPlayer_IdAndGame_Id(
            Long workerId,
            Long playerId,
            Long gameId
    );

    List<Worker> findByPlayer_IdAndGame_IdOrderByIdAsc(
            Long playerId,
            Long gameId
    );

    List<Worker> findByGame_IdAndJobInOrderByIdAsc(
            Long gameId,
            List<WorkerJob> jobs
    );
}
