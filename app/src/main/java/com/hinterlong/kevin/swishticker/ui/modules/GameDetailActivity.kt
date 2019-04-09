package com.hinterlong.kevin.swishticker.ui.modules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.data.Action
import com.hinterlong.kevin.swishticker.service.data.TeamAndPlayers
import com.hinterlong.kevin.swishticker.service.data.toPeriodName
import com.hinterlong.kevin.swishticker.service.data.toPoints
import com.hinterlong.kevin.swishticker.service.getTotalStats
import com.hinterlong.kevin.swishticker.service.playerStats
import com.hinterlong.kevin.swishticker.service.winLossFromGame
import com.hinterlong.kevin.swishticker.ui.adapters.ActionItem
import com.hinterlong.kevin.swishticker.ui.adapters.DTF
import com.hinterlong.kevin.swishticker.ui.adapters.GamePlayerStatsItem
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.android.synthetic.main.activity_game_detail.*
import kotlinx.android.synthetic.main.toolbar.*

class GameDetailActivity : AppCompatActivity() {
    private val adapter = FlexibleAdapter<ActionItem>(null)
    private val homePlayerStatsAdapter = FlexibleAdapter<GamePlayerStatsItem>(null)
    private val awayPlayerStatsAdapter = FlexibleAdapter<GamePlayerStatsItem>(null)
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

            Transformations.map(db.gameDao().getGamesAndActions(home.team.id)) {
                winLossFromGame(it, home.team.id)
            }.observe(this, Observer {
                homeTeamWinLoss.text = "(${it.wins} - ${it.losses})"
            })

            Transformations.map(db.gameDao().getGamesAndActions(away.team.id)) {
                winLossFromGame(it, away.team.id)
            }.observe(this, Observer {
                awayTeamWinLoss.text = "(${it.wins} - ${it.losses})"
            })

            datePlayed.text = DTF.format(it.dateCreated)

            homeTeamName.text = home.team.name
            homeTeamNameQuarters.text = home.team.name
            if (home.players.isEmpty()) {
                homePlayerStatsContainer.visibility = View.GONE
            } else {
                homePlayerStatsTitle.text = getString(R.string.team_stats, home.team.name)
                Transformations.map(db.actionDao().getGameActions(gameId)) {
                    playerStats(it, home.team.id)
                }.observe(this, Observer { stats ->
                    listOf(GamePlayerStatsItem(home.team.name, getTotalStats(stats.values)))
                        .plus(home.players.map { GamePlayerStatsItem(it.name, stats[it.id]) })
                        .let {
                            homePlayerStatsAdapter.updateDataSet(it)
                            homePlayerStatsAdapter.notifyDataSetChanged()
                        }
                })
            }
            awayTeamName.text = away.team.name
            awayTeamNameQuarters.text = away.team.name
            if (away.players.isEmpty()) {
                awayPlayerStatsContainer.visibility = View.GONE
            } else {
                awayPlayerStatsTitle.text = getString(R.string.team_stats, away.team.name)
                Transformations.map(db.actionDao().getGameActions(gameId)) {
                    playerStats(it, away.team.id)
                }.observe(this, Observer { stats ->
                    listOf(GamePlayerStatsItem(away.team.name, getTotalStats(stats.values)))
                        .plus(away.players.map { GamePlayerStatsItem(it.name, stats[it.id]) })
                        .let {
                            awayPlayerStatsAdapter.updateDataSet(it)
                            awayPlayerStatsAdapter.notifyDataSetChanged()
                        }
                })
            }

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

        homePlayerStats.adapter = homePlayerStatsAdapter
        awayPlayerStats.adapter = awayPlayerStatsAdapter
        gameActions.adapter = adapter

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun updateStats(actions: List<Action>) {
        val lastInterval = Math.max(actions.map { it.interval }.max() ?: 0, 3)
        val periods = actions.groupBy { it.interval }.toSortedMap()
        var homeTotal = 0
        var awayTotal = 0
        (0..lastInterval).forEach { period ->
            val periodActions = periods[period] ?: listOf()

            val quarterTitle = AppCompatTextView(ContextThemeWrapper(this, R.style.QuarterTitle), null, R.style.QuarterTitle)
            quarterTitle.text = toPeriodName(period)
            quarterTitle.layoutParams = LinearLayoutCompat.LayoutParams(0, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1f)
            quarterTitleContainer.addView(quarterTitle)

            val homePoints = periodActions.filter { it.team == home.team.id }.sumBy(::toPoints)
            homeTotal += homePoints
            val homeQuarterValue = AppCompatTextView(ContextThemeWrapper(this, R.style.QuarterValue), null, R.style.QuarterValue)
            homeQuarterValue.text = homePoints.toString()
            homeQuarterValue.layoutParams = LinearLayoutCompat.LayoutParams(0, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1f)
            homeQuarterValues.addView(homeQuarterValue)

            val awayPoints = periodActions.filter { it.team == away.team.id }.sumBy(::toPoints)
            awayTotal += awayPoints
            val awayQuarterValue = AppCompatTextView(ContextThemeWrapper(this, R.style.QuarterValue), null, R.style.QuarterValue)
            awayQuarterValue.text = awayPoints.toString()
            awayQuarterValue.layoutParams = LinearLayoutCompat.LayoutParams(0, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1f)
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
