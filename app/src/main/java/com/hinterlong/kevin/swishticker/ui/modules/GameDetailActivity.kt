package com.hinterlong.kevin.swishticker.ui.modules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.data.*
import com.hinterlong.kevin.swishticker.service.getTotalStats
import com.hinterlong.kevin.swishticker.service.playerStats
import com.hinterlong.kevin.swishticker.service.winLossFromGame
import com.hinterlong.kevin.swishticker.ui.adapters.ActionItem
import com.hinterlong.kevin.swishticker.ui.adapters.DTF
import com.hinterlong.kevin.swishticker.ui.adapters.GamePlayerStatsItem
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.android.synthetic.main.activity_game_detail.*
import kotlinx.android.synthetic.main.toolbar.*
import timber.log.Timber

class GameDetailActivity : AppCompatActivity() {
    private val adapter = FlexibleAdapter<ActionItem>(null)
    private val homePlayerStatsAdapter = FlexibleAdapter<GamePlayerStatsItem>(null)
    private val awayPlayerStatsAdapter = FlexibleAdapter<GamePlayerStatsItem>(null)
    private var gameId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detail)

        val intent = intent
        if (!intent.hasExtra(GAME_ID)) {
            Timber.e("Can't display game with no id")
            finish()
            return
        }
        gameId = intent.getLongExtra(GAME_ID, 0)
        val db = AppDatabase.getInstance(this)


        db.gameDao().getGame(gameId).observe(this, Observer { game ->
            if (game == null) {
                return@Observer
            }

            datePlayed.text = DTF.format(game.dateCreated)

            if (game.active) {
                activeGameCard.visibility = View.VISIBLE
                activeGameIcon.visibility = View.VISIBLE
            } else {
                activeGameCard.visibility = View.GONE
                activeGameIcon.visibility = View.GONE
            }

            endGame.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.end_game))
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        db.gameDao().updateGame(game.copy(active = false).also { it.id = game.id })
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
            resumeGame.setOnClickListener {
                InGameTrackerActivity.resume(this, game.id)
            }


            db.teamDao().getTeamAndPlayers(game.team1).observe(this, Observer { home ->
                homeTeamName.setOnClickListener {
                    TeamDetailActivity.withTeam(this, home.team.id)
                }
                homeTeamName.text = home.team.name
                homePlayerStatsTitle.text = getString(R.string.team_stats, home.team.name)

                var homeGames: Long = 0
                Transformations.map(db.gameDao().getGamesAndActions(home.team.id)) {
                    homeGames = it.size.toLong()
                    winLossFromGame(it, home.team.id)
                }.observe(this, Observer { it ->
                    homeTeamWinLoss.text = "(${it.wins} - ${it.losses})"
                })

                Transformations.map(db.actionDao().getGameActions(gameId)) {
                    playerStats(it, home.team.id)
                }.observe(this, Observer { stats ->
                    listOf(GamePlayerStatsItem(home.team.name, getTotalStats(stats.values, homeGames)))
                        .plus(home.players.map { GamePlayerStatsItem(it.name, stats[it.id]) })
                        .let {
                            homePlayerStatsAdapter.updateDataSet(it)
                            homePlayerStatsAdapter.notifyDataSetChanged()
                        }
                })
            })

            db.teamDao().getTeamAndPlayers(game.team2).observe(this, Observer { away ->
                awayTeamName.setOnClickListener {
                    TeamDetailActivity.withTeam(this, away.team.id)
                }
                awayTeamName.text = away.team.name
                awayPlayerStatsTitle.text = getString(R.string.team_stats, away.team.name)

                var awayGames: Long = 0
                Transformations.map(db.gameDao().getGamesAndActions(away.team.id)) {
                    awayGames = it.size.toLong()
                    winLossFromGame(it, away.team.id)
                }.observe(this, Observer {
                    awayTeamWinLoss.text = "(${it.wins} - ${it.losses})"
                })

                Transformations.map(db.actionDao().getGameActions(gameId)) {
                    playerStats(it, away.team.id)
                }.observe(this, Observer { stats ->
                    listOf(GamePlayerStatsItem(away.team.name, getTotalStats(stats.values, awayGames)))
                        .plus(away.players.map { GamePlayerStatsItem(it.name, stats[it.id]) })
                        .let {
                            awayPlayerStatsAdapter.updateDataSet(it)
                            awayPlayerStatsAdapter.notifyDataSetChanged()
                        }
                })
            })


            db.teamDao().getTeamAndPlayers(game.team2).observe(this, Observer { away ->
                db.teamDao().getTeamAndPlayers(game.team1).observe(this, Observer { home ->
                    title = "${home.team.name} vs ${away.team.name}"


                    db.actionDao().getGameActions(gameId).observe(this, Observer {
                        val period = it.map { it.interval }.max() ?: 0
                        currentPeriod.text = toQuarterName(period)

                        adapter.updateDataSet(it.map { action ->
                            val team = when (action.team) {
                                home.team.id -> home
                                else -> away
                            }
                            val player = team.players.firstOrNull { it.id == action.player }
                            ActionItem(action, team.team, action.team == home.team.id, player)
                        })
                        updateStats(it, home.team, away.team)
                    })
                })
            })
        })

        homePlayerStats.adapter = homePlayerStatsAdapter
        awayPlayerStats.adapter = awayPlayerStatsAdapter
        gameActions.adapter = adapter

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun updateStats(actions: List<Action>, homeTeam: Team, awayTeam: Team) {
        val lastInterval = Math.max(actions.map { it.interval }.max() ?: 0, 3)
        val periods = actions.groupBy { it.interval }.toSortedMap()
        var homeTotal = 0
        var awayTotal = 0
        // only keep the first view (title) for each of these)
        resetQuarterStatsView(homeTeam, awayTeam)

        (0..lastInterval).forEach { period ->
            val periodActions = periods[period] ?: listOf()

            val quarterTitle = AppCompatTextView(ContextThemeWrapper(this, R.style.QuarterTitle), null, R.style.QuarterTitle)
            quarterTitle.text = toPeriodName(period)
            quarterTitle.layoutParams = LinearLayoutCompat.LayoutParams(0, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1f)
            quarterTitleContainer.addView(quarterTitle)

            val homePoints = periodActions.filter { it.team == homeTeam.id }.sumBy(::toPoints)
            homeTotal += homePoints
            val homeQuarterValue = AppCompatTextView(ContextThemeWrapper(this, R.style.QuarterValue), null, R.style.QuarterValue)
            homeQuarterValue.text = homePoints.toString()
            homeQuarterValue.layoutParams = LinearLayoutCompat.LayoutParams(0, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1f)
            homeQuarterValues.addView(homeQuarterValue)

            val awayPoints = periodActions.filter { it.team == awayTeam.id }.sumBy(::toPoints)
            awayTotal += awayPoints
            val awayQuarterValue = AppCompatTextView(ContextThemeWrapper(this, R.style.QuarterValue), null, R.style.QuarterValue)
            awayQuarterValue.text = awayPoints.toString()
            awayQuarterValue.layoutParams = LinearLayoutCompat.LayoutParams(0, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1f)
            awayQuarterValues.addView(awayQuarterValue)
        }

        homeTeamScore.text = homeTotal.toString()
        awayTeamScore.text = awayTotal.toString()
    }

    private fun resetQuarterStatsView(homeTeam: Team, awayTeam: Team) {
        val quarterTitle = AppCompatTextView(ContextThemeWrapper(this, R.style.TeamNameSummary), null, R.style.TeamNameSummary)
        quarterTitle.setTextColor(ContextCompat.getColor(this, R.color.demphasized_text_color))
        quarterTitle.layoutParams = LinearLayoutCompat.LayoutParams(0, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 3f)
        quarterTitle.text = getString(R.string.team)
        quarterTitleContainer.removeAllViews()
        quarterTitleContainer.addView(quarterTitle)

        val homeQuarterValue = AppCompatTextView(ContextThemeWrapper(this, R.style.TeamNameSummary), null, R.style.TeamNameSummary)
        homeQuarterValue.setTextColor(ContextCompat.getColor(this, R.color.black))
        homeQuarterValue.layoutParams = LinearLayoutCompat.LayoutParams(0, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 3f)
        homeQuarterValue.text = homeTeam.name
        homeQuarterValues.removeAllViews()
        homeQuarterValues.addView(homeQuarterValue)

        val awayQuarterValue = AppCompatTextView(ContextThemeWrapper(this, R.style.TeamNameSummary), null, R.style.TeamNameSummary)
        awayQuarterValue.setTextColor(ContextCompat.getColor(this, R.color.black))
        awayQuarterValue.layoutParams = LinearLayoutCompat.LayoutParams(0, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 3f)
        awayQuarterValue.text = awayTeam.name
        awayQuarterValues.removeAllViews()
        awayQuarterValues.addView(awayQuarterValue)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.game_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.deleteGame -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.delete_game_question)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        AppDatabase.getInstance(this).gameDao().deleteGame(gameId)
                        dialog.dismiss()
                        finish()
                    }
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
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
