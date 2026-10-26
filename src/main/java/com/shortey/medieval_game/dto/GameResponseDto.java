
package com.shortey.medieval_game.dto;

import java.util.List;

public class GameResponseDto {

    private Long id;
    private String status;
    private int currentDay;
    private long dayProgressMillis;
    private int playerCount;
    private int maxPlayers;
    private List<PlayerSummaryDto> players;

    public GameResponseDto() {
    }

    public GameResponseDto(
            Long id,
            String status,
            int currentDay,
            long dayProgressMillis,
            int playerCount,
            int maxPlayers,
            List<PlayerSummaryDto> players) {
        this.id = id;
        this.status = status;
        this.currentDay = currentDay;
        this.dayProgressMillis = dayProgressMillis;
        this.playerCount = playerCount;
        this.maxPlayers = maxPlayers;
        this.players = players;
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public long getDayProgressMillis() {
        return dayProgressMillis;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<PlayerSummaryDto> getPlayers() {
        return players;
    }
}
