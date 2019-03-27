package com.hinterlong.kevin.swishticker.ui;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.hinterlong.kevin.swishticker.QueryEngine;
import com.hinterlong.kevin.swishticker.R;
import com.hinterlong.kevin.swishticker.data.Game;
import com.hinterlong.kevin.swishticker.data.Team;
import com.hinterlong.kevin.swishticker.ui.adapters.HomePageAdapter;
import com.hinterlong.kevin.swishticker.ui.modules.HistoryFragment;
import com.hinterlong.kevin.swishticker.ui.modules.NewGameActivity;
import com.hinterlong.kevin.swishticker.ui.modules.NewTeamActivity;
import com.hinterlong.kevin.swishticker.ui.modules.TeamsFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.addNew) FloatingActionButton fab;
    @BindView(R.id.homeTabs) TabLayout tabs;
    @BindView(R.id.homeViewPager) ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        tabs.addTab(tabs.newTab().setText(R.string.title_history), true);
        tabs.addTab(tabs.newTab().setText(R.string.title_teams));
        tabs.setupWithViewPager(viewPager);
        viewPager.setAdapter(new HomePageAdapter(getSupportFragmentManager(), this));

        fab.setOnClickListener(view -> {
            switch (tabs.getSelectedTabPosition()) {
                case 0:
                    startActivity(new Intent(this, NewGameActivity.class));
                    break;
                case 1:
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
