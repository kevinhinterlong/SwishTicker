package com.hinterlong.kevin.swishticker.ui.modules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase

class GameDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detail)

        val intent = intent
        val gameId = intent.getLongExtra(GAME_ID, 0)
        AppDatabase.getInstance(this).gameDao().getGame(gameId).observe(this, Observer {

            val home = AppDatabase.getInstance(this).teamDao().getTeamAndPlayers(it.team1)
            val away = AppDatabase.getInstance(this).teamDao().getTeamAndPlayers(it.team2)
            title = "${home.team.name} vs ${away.team.name}"
        })

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
