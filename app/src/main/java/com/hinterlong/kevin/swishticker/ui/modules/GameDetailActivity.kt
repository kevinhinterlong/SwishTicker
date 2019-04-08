package com.hinterlong.kevin.swishticker.ui.modules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.data.Action
import com.hinterlong.kevin.swishticker.service.data.toPoints
import kotlinx.android.synthetic.main.toolbar.*
import timber.log.Timber

class GameDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detail)

        val intent = intent
        val gameId = intent.getLongExtra(GAME_ID, 0)
        val db = AppDatabase.getInstance(this)
        db.gameDao().getGame(gameId).observe(this, Observer {
            val home = db.teamDao().getTeamAndPlayers(it.team1)
            val away = db.teamDao().getTeamAndPlayers(it.team2)
            val actions = db.actionDao().getGameActions(it.id)
            updateStats(actions)
            title = "${home.team.name} vs ${away.team.name}"
        })

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun updateStats(actions: List<Action>) {
        val periods = actions.groupBy { it.interval }.toMap()
        periods.forEach {
            val points = it.value.map { it.actionType }.map(::toPoints)
            Timber.d("Quarter ${it.key}: $points poins")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val GAME_ID = "GAME_ID"

        fun withGame(context: Context, gameId: Long) {
            val intent = Intent(context, GameDetailActivity::class.java)
            intent.putExtra(GAME_ID, gameId)
            context.startActivity(intent)
        }
    }
}
