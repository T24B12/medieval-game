package com.shortey.medieval_game.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BuildingType type;

    private int level;

    @ManyToOne
    private Player player;
    @ManyToOne
@JoinColumn(name = "game_id")
private Game game;

    @ManyToOne
    private Territory territory;
    @OneToMany(mappedBy = "workplace")
private List<Worker> workers = new ArrayList<>();


public List<Worker> getWorkers() {
    return workers;
}

public void setWorkers(List<Worker> workers) {
    this.workers = workers;
}
@OneToMany(mappedBy = "home")
private List<Worker> residents = new ArrayList<>();
public List<Worker> getResidents() {
    return residents;
}

public void setResidents(List<Worker> residents) {
    this.residents = residents;
}

    public Long getId() {
        return id;
    }

    public BuildingType getType() {
        return type;
    }

    public void setType(BuildingType type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Territory getTerritory() {
        return territory;
    }

    public void setTerritory(Territory territory) {
        this.territory = territory;
    }
    public Game getGame() {
    return game;
}

public void setGame(Game game) {
    this.game = game;
}
}