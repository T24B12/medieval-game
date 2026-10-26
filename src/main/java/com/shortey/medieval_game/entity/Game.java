package com.shortey.medieval_game.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


import java.util.ArrayList;
import java.util.List;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
@Enumerated(EnumType.STRING)
private GameStatus status;
private int currentDay = 1;
private long dayProgressMillis = 0;
private long lastClockUpdateMillis = 0;

    @ManyToMany
    @JoinTable(
        name = "game_players",
        joinColumns = @JoinColumn(name = "game_id"),
        inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<Player> players = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
    public int getCurrentDay() {
    return currentDay;
}

public void setCurrentDay(int currentDay) {
    this.currentDay = currentDay;
}
public long getDayProgressMillis() {
    return dayProgressMillis;
}

public void setDayProgressMillis(long dayProgressMillis) {
    this.dayProgressMillis = dayProgressMillis;
}
public long getLastClockUpdateMillis() {
    return lastClockUpdateMillis;
}

public void setLastClockUpdateMillis(long lastClockUpdateMillis) {
    this.lastClockUpdateMillis = lastClockUpdateMillis;
}
}