package com.hinterlong.kevin.swishticker.ui.modules

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.*
import com.hinterlong.kevin.swishticker.ui.adapters.GameItem
import com.hinterlong.kevin.swishticker.ui.adapters.GamePlayerStatsItem
import com.hinterlong.kevin.swishticker.utilities.Prefs
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.android.synthetic.main.fragment_team_detail.*

/**
 * A fragment representing a list of Items.
 */
class MyTeamFragment : Fragment() {
    private val gamesAdapter = FlexibleAdapter<GameItem>(null)
    private val statsAdapter = FlexibleAdapter<GamePlayerStatsItem>(null)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_team_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val teamId = Prefs.defaultTeamId
        if (teamId == null) {
            if (viewSwitcher.currentView.id == mainTeamDetails.id) {
                viewSwitcher.showNext()
                // setupNoTeamView(view.context)
            }
            return
        } else if (viewSwitcher.currentView.id == noTeamSetContainer.id) {
            viewSwitcher.showPrevious()
        }

        setupTeamView(view.context, teamId)

    }

//    private fun setupNoTeamView(context: Context) {
//
//    }

    private fun setupTeamView(context: Context, teamId: Long) {
        previousGames.adapter = gamesAdapter
        teamPlayerStats.adapter = statsAdapter

        val db = AppDatabase.getInstance(context)

        val teamAndPlayers = db.teamDao().getTeamAndPlayers(teamId)

        teamName.text = teamAndPlayers.team.name

        Transformations.map(db.gameDao().getGamesAndActions(teamId)) {
            playerStats(it.flatMap { it.actions }, teamId, it.size.toLong())
        }.observe(this, Observer { stats ->
            teamAndPlayers.players.map {
                GamePlayerStatsItem(it.name, stats[it.id], true) {
                    getString(R.string.stats_percent, it?.pointsPerGame)
                }
            }.let {
                listOf(GamePlayerStatsItem(teamAndPlayers.team.name, getTotalStats(it.mapNotNull { it.playerStats }), true) {
                    getString(R.string.stats_percent, it?.pointsPerGame)
                })
                    .plus(it)
                    .let {
                        statsAdapter.updateDataSet(it)
                        statsAdapter.notifyDataSetChanged()
                    }

            }
        })

        Transformations.map(db.gameDao().getGamesAndActions(teamId)) {
            winLossFromGame(it, teamId)
        }.observe(this, Observer {
            teamWins.text = it.wins.toString()
            teamLosses.text = it.losses.toString()
        })

        Transformations.map(AppDatabase.getInstance(context)
            .gameDao()
            .getGamesAndActions(teamId), ::gameScores)
            .observe(viewLifecycleOwner, Observer {
                gamesAdapter.updateDataSet(it.map { GameItem(it.game, it.score) }.toList())
                gamesAdapter.notifyDataSetChanged()
            })
    }
}
