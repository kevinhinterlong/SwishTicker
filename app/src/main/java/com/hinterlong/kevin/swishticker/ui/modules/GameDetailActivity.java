package com.hinterlong.kevin.swishticker.ui.modules;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hinterlong.kevin.swishticker.QueryEngine;
import com.hinterlong.kevin.swishticker.R;
import com.hinterlong.kevin.swishticker.data.Game;
import com.hinterlong.kevin.swishticker.data.Team;

import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;

public class GameDetailActivity extends AppCompatActivity {

    public static final String GAME_ID = "GAME_ID";
    private Game game;

    public static void withGame(Context context, int gameId) {
        Intent intent = new Intent(context, GameDetailActivity.class);
        intent.putExtra(GAME_ID, gameId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        Intent intent = getIntent();
        int gameId = intent.getIntExtra(GAME_ID, -1);
        if (gameId == -1) {
            Timber.e("No game id was provided");
        } else {
            game = QueryEngine.getInstance().getGame(gameId);
        }
        Team home = QueryEngine.getInstance().getTeam(game.getHomeTeamId());
        Team away = QueryEngine.getInstance().getTeam(game.getAwayTeamId());
        setTitle(String.format("%s vs %s", home.getName(), away.getName()));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
