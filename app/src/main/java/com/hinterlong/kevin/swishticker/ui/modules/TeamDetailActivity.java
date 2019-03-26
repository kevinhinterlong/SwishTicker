package com.hinterlong.kevin.swishticker.ui.modules;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hinterlong.kevin.swishticker.QueryEngine;
import com.hinterlong.kevin.swishticker.R;
import com.hinterlong.kevin.swishticker.data.Team;

import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;

public class TeamDetailActivity extends AppCompatActivity {
    public static final String TEAM_ID = "TEAM_ID";
    private Team team;

    public static void withTeam(Context context, int teamId) {
        Intent intent = new Intent(context, TeamDetailActivity.class);
        intent.putExtra(TEAM_ID, teamId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail_view);

        Intent intent = getIntent();
        int teamId = intent.getIntExtra(TEAM_ID, -1);
        if (teamId == -1) {
            Timber.e("No team id was provided");
        } else {
            team = QueryEngine.getInstance().getTeam(teamId);
        }
        setTitle(team.getName());
    }
}
