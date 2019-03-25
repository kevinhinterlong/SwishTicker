package com.hinterlong.kevin.swishticker.query;

public class Player {
    private String name;
    private int number;
    private int teamId;

    public Player(String name, int number) {
        this.name = name;
        this.number = number;
        this.teamId = -1;
    }

    public Player(String name, int number, int teamId) {
        this.name = name;
        this.number = number;
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }
}
