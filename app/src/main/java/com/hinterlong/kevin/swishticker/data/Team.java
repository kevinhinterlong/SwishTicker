package com.hinterlong.kevin.swishticker.data;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private String name;
    private List<Integer> playerIds;

    public Team(String name) {
        this.name = name;
        playerIds = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addPlayer(int playerId) {
        playerIds.add(playerId);
    }

    public void removePlayer(int playerId) {
        playerIds.remove(Integer.valueOf(playerId));
    }

    public List<Integer> getPlayers() {
        return playerIds;
    }
}
