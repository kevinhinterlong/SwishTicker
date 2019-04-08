package com.hinterlong.kevin.swishticker.ui.modules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.data.Action
import com.hinterlong.kevin.swishticker.service.data.TeamAndPlayers
import com.hinterlong.kevin.swishticker.service.data.toPeriodName
import com.hinterlong.kevin.swishticker.service.data.toPoints
import com.hinterlong.kevin.swishticker.ui.adapters.ActionItem
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.android.synthetic.main.activity_game_detail.*
import kotlinx.android.synthetic.main.toolbar.*

class GameDetailActivity : AppCompatActivity() {
    private val adapter = FlexibleAdapter<ActionItem>(null)
    private lateinit var home: TeamAndPlayers
    private lateinit var away: TeamAndPlayers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detail)

        val intent = intent
        val gameId = intent.getLongExtra(GAME_ID, 0)
        val db = AppDatabase.getInstance(this)
        db.gameDao().getGame(gameId).observe(this, Observer {
            home = db.teamDao().getTeamAndPlayers(it.team1)
            away = db.teamDao().getTeamAndPlayers(it.team2)
            title = "${home.team.name} vs ${away.team.name}"

            homeTeamName.text = home.team.name
            homeTeamNameQuarters.text = home.team.name
            awayTeamName.text = away.team.name
            awayTeamNameQuarters.text = away.team.name
            db.actionDao().getGameActions(gameId).observe(this, Observer {
                adapter.updateDataSet(it.map { action ->
                    val team = when (action.team) {
                        home.team.id -> home
                        else -> away
                    }
                    val player = team.players.firstOrNull { it.id == action.player }
                    ActionItem(action, team.team, action.team == home.team.id, player)
                })
                updateStats(it)
            })
        })

        // Todo: Team wins/losses
        // Todo: Player stats
        gameActions.adapter = adapter

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun updateStats(actions: List<Action>) {
        val periods = actions.groupBy { it.interval }.toMap()
        var homeTotal = 0
        var awayTotal = 0
        periods.forEach {
            val quarterTitle = AppCompatTextView(this, null, R.style.QuarterTitle)
            quarterTitle.text = toPeriodName(it.key)
            quarterTitleContainer.addView(quarterTitle)

            val homePoints = it.value.filter { it.team == home.team.id }.map { it.actionType }.sumBy(::toPoints)
            homeTotal += homePoints
            val homeQuarterValue = AppCompatTextView(this, null, R.style.QuarterValue)
            homeQuarterValue.text = homePoints.toString()
            homeQuarterValues.addView(homeQuarterValue)

            val awayPoints = it.value.filter { it.team == away.team.id }.map { it.actionType }.sumBy(::toPoints)
            awayTotal += awayPoints
            val awayQuarterValue = AppCompatTextView(this, null, R.style.QuarterValue)
            awayQuarterValue.text = awayPoints.toString()
            awayQuarterValues.addView(awayQuarterValue)
        }

        homeTeamScore.text = homeTotal.toString()
        awayTeamScore.text = awayTotal.toString()
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
