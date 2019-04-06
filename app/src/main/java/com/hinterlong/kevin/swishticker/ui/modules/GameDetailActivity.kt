package com.hinterlong.kevin.swishticker.ui.modules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.data.Game

class GameDetailActivity : AppCompatActivity() {
    private lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detail)

        val intent = intent
        val gameId = intent.getLongExtra(GAME_ID, 0)
        game = AppDatabase.getInstance(this).gameDao().getGame(gameId)
        val home = AppDatabase.getInstance(this).teamDao().getTeamAndPlayers(game.team1)
        val away = AppDatabase.getInstance(this).teamDao().getTeamAndPlayers(game.team2)
        title = "${home.team.name} vs ${away.team.name}"

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
