package com.hinterlong.kevin.swishticker.ui.modules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hinterlong.kevin.swishticker.AppDatabase
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.data.Game

class GameDetailActivity : AppCompatActivity() {
    private lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detail)

        val intent = intent
        val gameId = intent.getLongExtra(GAME_ID, 0)
        game = AppDatabase.getInstance(this).gameDao().getGame(gameId)
        val home = AppDatabase.getInstance(this).teamDao().getTeam(game.team1)
        val away = AppDatabase.getInstance(this).teamDao().getTeam(game.team2)
        title = "${home.name} vs ${away.name}"

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
