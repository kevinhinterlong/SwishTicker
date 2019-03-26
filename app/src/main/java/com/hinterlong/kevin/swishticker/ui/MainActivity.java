package com.hinterlong.kevin.swishticker.ui;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hinterlong.kevin.swishticker.QueryEngine;
import com.hinterlong.kevin.swishticker.R;
import com.hinterlong.kevin.swishticker.data.Game;
import com.hinterlong.kevin.swishticker.data.Team;
import com.hinterlong.kevin.swishticker.ui.modules.HistoryFragment;
import com.hinterlong.kevin.swishticker.ui.modules.NewGameActivity;
import com.hinterlong.kevin.swishticker.ui.modules.NewTeamActivity;
import com.hinterlong.kevin.swishticker.ui.modules.TeamsFragment;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private HistoryFragment historyFragment = new HistoryFragment();
    private TeamsFragment teamsFragment = new TeamsFragment();
    @BindView(R.id.addNew)
    FloatingActionButton fab;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_history:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, historyFragment).commit();
                return true;
            case R.id.navigation_teams:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, teamsFragment).commit();
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, historyFragment).commit();
        }

        fab.setOnClickListener(view -> {
            switch (navigation.getSelectedItemId()) {
                case R.id.navigation_history:
                    startActivity(new Intent(this, NewGameActivity.class));
                    break;
                case R.id.navigation_teams:
                    startActivity(new Intent(this, NewTeamActivity.class));
                    break;
            }
        });

        addTestGame();
    }

    /**
     * Adds a single game to test the view
     */
    private void addTestGame() {
        if (QueryEngine.getInstance().getGames().size() == 0) {
            Team home = new Team("Winners");
            Team away = new Team("Losers");
            int homeId = QueryEngine.getInstance().addTeam(home);
            int awayId = QueryEngine.getInstance().addTeam(away);
            Game test = new Game(homeId, awayId);
            QueryEngine.getInstance().addGame(test);
        }
    }
}
