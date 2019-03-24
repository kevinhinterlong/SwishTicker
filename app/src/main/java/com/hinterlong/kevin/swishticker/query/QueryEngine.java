package com.hinterlong.kevin.swishticker.query;

// QueryEngine connects to SharedPreferences
// Allows for queries and modification of stored data

// list of teams ids
// list of game ids
// list of player ids
// (team id -> team)
// (game id -> game)
// (player id -> player)

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QueryEngine {

    private static String FILE_KEY = "query_file";      // all keys stored in one file atm

    private static String COUNTER_KEY = "counter";
    private static String TEAMS_KEY = "team_list";
    private static String GAMES_KEY = "game_list";
    private static String PLAYERS_KEY = "player_list";

    private static Type intListType = new TypeToken<ArrayList<Integer>>(){}.getType();

    private static Gson gson = new Gson();

    private Context context;
    private SharedPreferences sp;

    public QueryEngine(Context context){
        this.context = context;
        this.sp = context.getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
    }

    // race condition?
    private int getCounter(){
        int counter = sp.getInt(COUNTER_KEY, 0) + 1; // increment counter

        SharedPreferences.Editor se = sp.edit();
        se.putInt(COUNTER_KEY, counter);
        se.apply();

        return counter;
    }

    // addId to particular id list
    private void addId(String key, int id){
        List<Integer> list = getIds(key);
        list.add(id);
        setIds(key, list);
    }

    // removeId from particular id list
    private void removeId(String key, int id){
        List<Integer> list = getIds(key);
        list.remove(new Integer(id));
        setIds(key, list);
    }

    // removeId from Shared Preferences
    private void removeId(int id){
        SharedPreferences.Editor se = sp.edit();
        se.remove(String.valueOf(id));
        se.apply();
    }

    // getIds from particular id list
    private List<Integer> getIds(String key){
        String json = sp.getString(key, "[]");

        return gson.fromJson(json, intListType);
    }

    // setIds to particular id list
    private void setIds(String key, List<Integer> list){
        String json = gson.toJson(list);

        SharedPreferences.Editor se = sp.edit();
        se.putString(key, json);
        se.apply();
    }

    // Teams

    public List<Integer> getTeams(){
        return getIds(TEAMS_KEY);
    }

    public int addTeam(Team team){
        int counter = getCounter();

        addId(TEAMS_KEY, counter);

        setTeam(counter, team);

        return counter;
    }

    public Team getTeam(int teamId){
        String json = sp.getString(String.valueOf(teamId), null);

        return gson.fromJson(json, Team.class);
    }

    public void setTeam(int teamId, Team team){
        String json = gson.toJson(team);

        SharedPreferences.Editor se = sp.edit();
        se.putString(String.valueOf(teamId), json);
        se.apply();
    }

    public void removeTeam(int teamId){

        removeId(TEAMS_KEY, teamId);

        removeId(teamId);
    }

    // Games

    public List<Integer> getGames(){
        return getIds(GAMES_KEY);
    }

    private List<Integer> getActiveGames(){
        List<Integer> gameIds = getGames();

        List<Integer> activeIds = new ArrayList<>();

        for(Integer i: gameIds){
            Game game = getGame(i);
            if(game.isActive()){
                activeIds.add(i);
            }
        }

        return activeIds;
    }

    public int addGame(Game game){
        int counter = getCounter();

        addId(GAMES_KEY, counter);

        setGame(counter, game);

        return counter;
    }

    public Game getGame(int gameId){
        String json = sp.getString(String.valueOf(gameId), null);

        return gson.fromJson(json, Game.class);
    }

    public void setGame(int gameId, Game game){
        String json = gson.toJson(game);

        SharedPreferences.Editor se = sp.edit();
        se.putString(String.valueOf(gameId), json);
        se.apply();
    }

    public void removeGame(int gameId){

        removeId(GAMES_KEY, gameId);

        removeId(gameId);
    }

    public void addHomeAction(int gameId, Action action){
        Game game = getGame(gameId);

        game.addHomeAction(action);

        setGame(gameId, game);
    }

    public void addAwayAction(int gameId, Action action){
        Game game = getGame(gameId);

        game.addAwayAction(action);

        setGame(gameId, game);
    }


    // Players

    public List<Integer> getPlayers(){
        return getIds(PLAYERS_KEY);
    }

    public List<Integer> getPlayers(int teamId){
        return getTeam(teamId).getPlayers();
    }

    public int addPlayer(int teamId, Player player){
        int counter = getCounter();

        addId(PLAYERS_KEY, counter);

        player.setTeamId(teamId);

        Team team = getTeam(teamId);
        team.addPlayer(counter);
        setTeam(teamId, team);

        setPlayer(counter, player);

        return counter;
    }

    public Player getPlayer(int playerId){
        String json = sp.getString(String.valueOf(playerId), null);

        return gson.fromJson(json, Player.class);
    }

    public void setPlayer(int playerId, Player player){
        String json = gson.toJson(player);

        SharedPreferences.Editor se = sp.edit();
        se.putString(String.valueOf(playerId), json);
        se.apply();
    }

    public void removePlayer(int gameId){

        removeId(PLAYERS_KEY, gameId);

        removeId(gameId);
    }

}
