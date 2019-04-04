package com.hinterlong.kevin.swishticker.ui.modules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hinterlong.kevin.swishticker.AppDatabase
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.data.Team

class TeamDetailActivity : AppCompatActivity() {
    private lateinit var team: Team

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_detail_view)

        val intent = intent
        val teamId = intent.getLongExtra(TEAM_ID, 0)
        team = AppDatabase.getInstance(this).teamDao().getTeam(teamId)
        title = team.name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val TEAM_ID = "TEAM_ID"

        fun withTeam(context: Context, teamId: Long) {
            val intent = Intent(context, TeamDetailActivity::class.java)
            intent.putExtra(TEAM_ID, teamId)
            context.startActivity(intent)
        }
    }
}
