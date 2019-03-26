package com.hinterlong.kevin.swishticker.data;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private int homeTeamId;

    private int awayTeamId;

    // 0 = 1st quarter, etc.
    private List<Interval> intervals;

    private boolean active;
    private int currentInterval;

    public Game(int homeTeamId, int awayTeamId) {
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        active = true;
        currentInterval = 0;

        intervals = new ArrayList<>();
        intervals.add(new Interval());
    }

    // Score is updated after every action (Superfluous)

    public int getHomeTeamId() {
        return homeTeamId;
    }

    public int getAwayTeamId() {
        return awayTeamId;
    }

    public boolean isActive() {
        return active;
    }

    public void nextInterval() {
        currentInterval++;

        intervals.add(new Interval());
    }

    public List<Interval> getIntervals() {
        return intervals;
    }

    public void endGame() {
        active = false;
    }

    public int getHomeScore() {
        int score = 0;

        for (Interval i : intervals) {
            score += i.home.score;
        }

        return score;
    }

    public int getAwayScore() {
        int score = 0;

        for (Interval i : intervals) {
            score += i.away.score;
        }

        return score;
    }

    public void addHomeAction(Action action) {
        PlayingTeam home = intervals.get(currentInterval).home;

        home.actions.add(action);

        if (action.getActionType() == ActionType.TWO_POINT) {
            home.score += 2;
        } else if (action.getActionType() == ActionType.THREE_POINT) {
            home.score += 3;
        } else if (action.getActionType() == ActionType.FREE_THROW) {
            home.score += 1;
        }
    }

    public void addAwayAction(Action action) {
        PlayingTeam away = intervals.get(currentInterval).away;

        away.actions.add(action);

        if (action.getActionType() == ActionType.TWO_POINT) {
            away.score += 2;
        } else if (action.getActionType() == ActionType.THREE_POINT) {
            away.score += 3;
        } else if (action.getActionType() == ActionType.FREE_THROW) {
            away.score += 1;
        }
    }

    public List<Action> getHomeActions() {
        List<Action> actions = new ArrayList<>();

        for (Interval i : intervals) {
            actions.addAll(i.home.actions);
        }

        return actions;
    }

    public List<Action> getAwayActions() {
        List<Action> actions = new ArrayList<>();

        for (Interval i : intervals) {
            actions.addAll(i.away.actions);
        }

        return actions;
    }

    private class Interval {
        private PlayingTeam home;
        private PlayingTeam away;

        public Interval() {
            home = new PlayingTeam();
            away = new PlayingTeam();
        }
    }

    private class PlayingTeam {
        private int score;
        private List<Action> actions;

        public PlayingTeam() {
            score = 0;
            actions = new ArrayList<>();
        }
    }
}
