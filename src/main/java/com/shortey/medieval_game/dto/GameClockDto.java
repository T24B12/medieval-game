
package com.shortey.medieval_game.dto;

public class GameClockDto {

    private Long id;
    private String status;
    private int currentDay;
    private long dayProgressMillis;

    public GameClockDto() {
    }

    public GameClockDto(
            Long id,
            String status,
            int currentDay,
            long dayProgressMillis) {
        this.id = id;
        this.status = status;
        this.currentDay = currentDay;
        this.dayProgressMillis = dayProgressMillis;
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
}
