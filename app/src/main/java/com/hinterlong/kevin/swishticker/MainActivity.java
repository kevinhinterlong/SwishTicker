package com.hinterlong.kevin.swishticker;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hinterlong.kevin.swishticker.query.Action;
import com.hinterlong.kevin.swishticker.query.ActionType;
import com.hinterlong.kevin.swishticker.query.Game;
import com.hinterlong.kevin.swishticker.query.QueryEngine;
import com.hinterlong.kevin.swishticker.query.Team;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements HistoryFragment.OnListFragmentInteractionListener {
    private HistoryFragment historyFragment = new HistoryFragment();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_history:
                return true;
            case R.id.navigation_teams:
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, historyFragment).commit();

        }
        if(QueryEngine.getInstance().getGames().size() == 0) {
            Team home = new Team("Winners");
            Team away = new Team("Losers");
            int homeId = QueryEngine.getInstance().addTeam(home);
            int awayId = QueryEngine.getInstance().addTeam(away);
            Game test = new Game(homeId, awayId);
            QueryEngine.getInstance().addGame(test);
        }
    }

    @Override
    public void onListFragmentInteraction(Game item) {
        Log.d("dummy", String.format("%d - %d clicked", item.getHomeScore(), item.getAwayScore()));
    }
}
