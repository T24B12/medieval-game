
package com.shortey.medieval_game.dto;

public class PlayerSummaryDto {

    private Long id;
    private String name;
    private String territoryName;

    public PlayerSummaryDto() {
    }

    public PlayerSummaryDto(Long id, String name, String territoryName) {
        this.id = id;
        this.name = name;
        this.territoryName = territoryName;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTerritoryName() {
        return territoryName;
    }
}
