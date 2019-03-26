package com.hinterlong.kevin.swishticker.data;

public class Action {

    private ActionType actionType;
    private int playerId;  // -1 if no player set

    public Action(ActionType actionType) {
        this.actionType = actionType;
        this.playerId = -1;
    }

    public Action(ActionType actionType, int playerId) {
        this.actionType = actionType;
        this.playerId = playerId;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public boolean isPlayerSet() {
        return (playerId != -1);
    }

    public int getPlayerId() {
        return playerId;
    }
}
