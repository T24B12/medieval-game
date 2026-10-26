package com.shortey.medieval_game.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import com.shortey.medieval_game.entity.Game;
import jakarta.persistence.JoinColumn;

@Entity
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Gender gender;

    private WorkerJob job;
    @ManyToOne
@JoinColumn(name = "game_id")
private Game game;

    @ManyToOne
    private Player player;

    @ManyToOne
    private Territory territory;

    @ManyToOne
    private Building home;

    @ManyToOne
    private Building workplace;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public WorkerJob getJob() {
        return job;
    }

    public void setJob(WorkerJob job) {
        this.job = job;
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

    public Building getHome() {
        return home;
    }

    public void setHome(Building home) {
        this.home = home;
    }

    public Building getWorkplace() {
        return workplace;
    }

    public void setWorkplace(Building workplace) {
        this.workplace = workplace;
    }
    public Game getGame() {
    return game;
}

public void setGame(Game game) {
    this.game = game;
}
}