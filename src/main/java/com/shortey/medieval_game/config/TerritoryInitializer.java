package com.shortey.medieval_game.config;

import com.shortey.medieval_game.entity.Territory;
import com.shortey.medieval_game.repository.TerritoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TerritoryInitializer implements CommandLineRunner {

    private final TerritoryRepository territoryRepository;

    public TerritoryInitializer(TerritoryRepository territoryRepository) {
        this.territoryRepository = territoryRepository;
    }

    @Override
    public void run(String... args) {
        System.out.println(">>> TerritoryInitializer is running...");

        if (territoryRepository.count() > 0) {
            return;
        }

        createTerritory("Tigray Plateau");
        createTerritory("Territory 2");
        createTerritory("Territory 3");
        createTerritory("Territory 4");
        createTerritory("Territory 5");
        createTerritory("Territory 6");
    }

    private void createTerritory(String name) {

        Territory territory = new Territory();
        territory.setName(name);

        territoryRepository.save(territory);
    }
}