
package com.shortey.medieval_game.service;

import com.shortey.medieval_game.entity.Game;
import com.shortey.medieval_game.entity.GameStatus;
import com.shortey.medieval_game.repository.GameRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameDayService {

    private static final long DAY_DURATION_MILLIS = 10_000L;

    private final GameRepository gameRepository;
    private final ProductionService productionService;

    private volatile boolean clockInitialized = false;

    public GameDayService(
            GameRepository gameRepository,
            ProductionService productionService) {
        this.gameRepository = gameRepository;
        this.productionService = productionService;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeClockAfterStartup() {
        long now = System.currentTimeMillis();

        var games = gameRepository.findByStatus(GameStatus.STARTED);

        for (Game game : games) {
            game.setLastClockUpdateMillis(now);
        }

        gameRepository.saveAll(games);
        clockInitialized = true;
    }

    @Scheduled(fixedRate = 100)
    @Transactional
    public void advanceGameClocks() {
        if (!clockInitialized) {
            return;
        }

        long now = System.currentTimeMillis();
        var games = gameRepository.findByStatus(GameStatus.STARTED);

        for (Game game : games) {
            long lastUpdate = game.getLastClockUpdateMillis();

            if (lastUpdate <= 0) {
                game.setLastClockUpdateMillis(now);
                continue;
            }

            long elapsed = Math.max(0L, now - lastUpdate);
            long progressMillis = game.getDayProgressMillis() + elapsed;

            long daysElapsed = progressMillis / DAY_DURATION_MILLIS;
            long remainingMillis = progressMillis % DAY_DURATION_MILLIS;

           if (daysElapsed > 0) {
    System.out.println(
            "AUTO PRODUCTION: gameId=" + game.getId()
            + ", daysElapsed=" + daysElapsed
    );

    for (long day = 0; day < daysElapsed; day++) {
        productionService.processDailyProduction(game.getId());
    }

    game.setCurrentDay(
            game.getCurrentDay() + (int) daysElapsed);
}
            game.setDayProgressMillis(remainingMillis);
            game.setLastClockUpdateMillis(now);
        }

        gameRepository.saveAll(games);
    }
}
