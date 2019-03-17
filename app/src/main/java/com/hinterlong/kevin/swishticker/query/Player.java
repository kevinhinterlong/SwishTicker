package com.hinterlong.kevin.swishticker.query;

public class Player {
    private String name;
    private int number;
    private int teamId;

    public Player(String name, int number, int teamId) {
        this.name = name;
        this.number = number;
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }
}
