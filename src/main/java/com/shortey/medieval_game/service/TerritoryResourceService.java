
package com.shortey.medieval_game.service;

import com.shortey.medieval_game.entity.Game;
import com.shortey.medieval_game.entity.ResourceType;
import com.shortey.medieval_game.entity.Territory;
import com.shortey.medieval_game.entity.TerritoryResource;
import com.shortey.medieval_game.repository.TerritoryRepository;
import com.shortey.medieval_game.repository.TerritoryResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.shortey.medieval_game.dto.TerritoryResourceDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

import java.util.EnumMap;
import java.util.Map;

@Service
public class TerritoryResourceService {

    private final TerritoryRepository territoryRepository;
    private final TerritoryResourceRepository territoryResourceRepository;

    private static final ResourceType[] NATURAL_RESOURCES = {
            ResourceType.WOOD,
            ResourceType.STONE,
            ResourceType.IRON,
            ResourceType.WATER,
            ResourceType.FOOD,
            ResourceType.FISH,
            ResourceType.MEAT
    };

    public TerritoryResourceService(
            TerritoryRepository territoryRepository,
            TerritoryResourceRepository territoryResourceRepository) {

        this.territoryRepository = territoryRepository;
        this.territoryResourceRepository = territoryResourceRepository;
    }

    @Transactional
    public void initializeGameResources(Game game) {

        var territories = territoryRepository.findAllByOrderByIdAsc();

       if (territories.size() != 6) {
    throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            "The game requires exactly six territories.");

        }

        for (Territory territory : territories) {

            for (ResourceType resourceType : NATURAL_RESOURCES) {

                if (territoryResourceRepository
                        .findByGameAndTerritoryAndResourceType(
                                game, territory, resourceType
                        ).isPresent()) {
                    continue;
                }

                double[] values = getResourceValues(
                        territory.getName(), resourceType
                );

                TerritoryResource resource = new TerritoryResource();

                resource.setGame(game);
                resource.setTerritory(territory);
                resource.setResourceType(resourceType);
                resource.setAvailableAmount(values[0]);
                resource.setMaximumAmount(values[1]);
                resource.setRegenerationRate(values[2]);

                territoryResourceRepository.save(resource);
            }
        }
    }

    private double[] getResourceValues(
            String territoryName,
            ResourceType resourceType) {

        Map<ResourceType, double[]> profile =
                getTerritoryProfile(territoryName);

        double[] values = profile.get(resourceType);

        if (values == null) {
            return new double[]{0, 0, 0};
        }

        return values;
    }

    private Map<ResourceType, double[]> getTerritoryProfile(
            String territoryName) {

        Map<ResourceType, double[]> profile =
                new EnumMap<>(ResourceType.class);

        switch (territoryName) {

            case "Tigray Plateau" -> {
                add(profile, ResourceType.WOOD, 30, 30, 0.5);
                add(profile, ResourceType.STONE, 90, 90, 0.08);
                add(profile, ResourceType.IRON, 100, 100, 0.05);
                add(profile, ResourceType.WATER, 20, 20, 0.35);
                add(profile, ResourceType.FOOD, 25, 25, 0.3);
                add(profile, ResourceType.FISH, 5, 5, 0.1);
                add(profile, ResourceType.MEAT, 20, 20, 0.25);
            }

            case "Gojam Plateau" -> {
                add(profile, ResourceType.WOOD, 75, 75, 1.2);
                add(profile, ResourceType.STONE, 55, 55, 0.08);
                add(profile, ResourceType.IRON, 35, 35, 0.05);
                add(profile, ResourceType.WATER, 60, 60, 1.0);
                add(profile, ResourceType.FOOD, 55, 55, 0.7);
                add(profile, ResourceType.FISH, 20, 20, 0.3);
                add(profile, ResourceType.MEAT, 35, 35, 0.5);
            }

            case "Shewa Plateau" -> {
                add(profile, ResourceType.WOOD, 45, 45, 1.0);
                add(profile, ResourceType.STONE, 25, 25, 0.04);
                add(profile, ResourceType.IRON, 15, 15, 0.03);
                add(profile, ResourceType.WATER, 100, 100, 2.0);
                add(profile, ResourceType.FOOD, 75, 75, 1.0);
                add(profile, ResourceType.FISH, 90, 90, 1.5);
                add(profile, ResourceType.MEAT, 45, 45, 0.6);
            }

            case "Gondar Plateau" -> {
                add(profile, ResourceType.WOOD, 100, 100, 2.0);
                add(profile, ResourceType.STONE, 45, 45, 0.06);
                add(profile, ResourceType.IRON, 25, 25, 0.04);
                add(profile, ResourceType.WATER, 65, 65, 1.2);
                add(profile, ResourceType.FOOD, 45, 45, 0.6);
                add(profile, ResourceType.FISH, 20, 20, 0.3);
                add(profile, ResourceType.MEAT, 55, 55, 1.2);
            }

            case "Begemder" -> {
                add(profile, ResourceType.WOOD, 30, 30, 0.4);
                add(profile, ResourceType.STONE, 40, 40, 0.08);
                add(profile, ResourceType.IRON, 35, 35, 0.05);
                add(profile, ResourceType.WATER, 25, 25, 0.25);
                add(profile, ResourceType.FOOD, 55, 55, 0.6);
                add(profile, ResourceType.FISH, 5, 5, 0.1);
                add(profile, ResourceType.MEAT, 90, 90, 0.8);
            }

            case "Southern Territories" -> {
                add(profile, ResourceType.WOOD, 45, 45, 1.0);
                add(profile, ResourceType.STONE, 25, 25, 0.04);
                add(profile, ResourceType.IRON, 15, 15, 0.03);
                add(profile, ResourceType.WATER, 80, 80, 1.5);
                add(profile, ResourceType.FOOD, 100, 100, 1.2);
                add(profile, ResourceType.FISH, 50, 50, 0.8);
                add(profile, ResourceType.MEAT, 55, 55, 0.8);
            }

            default -> throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Unknown territory: " + territoryName);
            
        }

        return profile;
    }

    private void add(
            Map<ResourceType, double[]> profile,
            ResourceType resourceType,
            double amount,
            double maximum,
            double regenerationRate) {

        profile.put(
                resourceType,
                new double[]{amount, maximum, regenerationRate}
        );
    }
    public List<TerritoryResourceDto> getGameResources(Long gameId) {

    return territoryResourceRepository.findAll().stream()
            .filter(resource ->
                    resource.getGame().getId().equals(gameId))
            .map(resource -> new TerritoryResourceDto(
                    resource.getId(),
                    resource.getGame().getId(),
                    resource.getTerritory().getId(),
                    resource.getTerritory().getName(),
                    resource.getResourceType().name(),
                    resource.getAvailableAmount(),
                    resource.getMaximumAmount(),
                    resource.getRegenerationRate()
            ))
            .toList();
}
}
