package com.hinterlong.kevin.swishticker.ui.modules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import timber.log.Timber


class InGameTrackerActivity : AppCompatActivity() {
    private var gameId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_game_tracker)

        if (intent.hasExtra(GAME_ID)) {
            gameId = intent.getLongExtra(GAME_ID, 0)
        } else {
            Timber.e("Must set home and away team ids or game id to resume")
            finish()
        }

        AppDatabase.getInstance(this).gameDao().getGame(gameId).observe(this, Observer {
            Timber.d("Updated game as $it")
        })
    }


    companion object {
        const val GAME_ID = "GAME_ID"

        fun resume(context: Context, gameId: Long) {
            val intent = Intent(context, InGameTrackerActivity::class.java)
            intent.putExtra(GAME_ID, gameId)
            context.startActivity(intent)
        }
    }
}
