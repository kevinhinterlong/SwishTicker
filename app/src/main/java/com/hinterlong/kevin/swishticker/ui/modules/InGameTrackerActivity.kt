package com.hinterlong.kevin.swishticker.ui.modules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.data.Action
import com.hinterlong.kevin.swishticker.service.data.ActionResult
import com.hinterlong.kevin.swishticker.service.data.ActionType
import com.hinterlong.kevin.swishticker.service.data.toPoints
import com.hinterlong.kevin.swishticker.ui.adapters.ActionItem
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.android.synthetic.main.activity_in_game_tracker.*
import timber.log.Timber


class InGameTrackerActivity : AppCompatActivity() {
    private val adapter = FlexibleAdapter<ActionItem>(null)
    private var gameId: Long = 0
    private val homeActionMap by lazy {
        mapOf(
            homePt1 to Pair(ActionType.FREE_THROW, ActionResult.SHOT_HIT),
            homePt2 to Pair(ActionType.TWO_POINT, ActionResult.SHOT_HIT),
            homePt3 to Pair(ActionType.THREE_POINT, ActionResult.SHOT_HIT),
            homeFoul to Pair(ActionType.FOUL, ActionResult.NONE)
        )
    }
    private val awayActionMap by lazy {
        mapOf(
            awayPt1 to Pair(ActionType.FREE_THROW, ActionResult.SHOT_HIT),
            awayPt2 to Pair(ActionType.TWO_POINT, ActionResult.SHOT_HIT),
            awayPt3 to Pair(ActionType.THREE_POINT, ActionResult.SHOT_HIT),
            awayFoul to Pair(ActionType.FOUL, ActionResult.NONE)
        )
    }
    val SCROLLING_UP = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_game_tracker)

        if (intent.hasExtra(GAME_ID)) {
            gameId = intent.getLongExtra(GAME_ID, 0)
        } else {
            Timber.e("Must set home and away team ids or game id to resume")
            finish()
        }

        val db = AppDatabase.getInstance(this)
        db.gameDao().getGame(gameId).observe(this, Observer {
            val homeTeam = db.teamDao().getTeamAndPlayers(it.team1)
            val awayTeam = db.teamDao().getTeamAndPlayers(it.team2)
            setButtonListeners(homeActionMap, it.team1)
            setButtonListeners(awayActionMap, it.team2)

            homeTeamName.text = homeTeam.team.name
            awayTeamName.text = awayTeam.team.name
            Timber.d("Updated game as $it")

            db.actionDao().getGameActions(gameId).observe(this, Observer {
                val teamActions = it.groupingBy { it.team }.fold(0) { sum, action -> sum + toPoints(action.actionType) }
                homeTeamScore.text = (teamActions[homeTeam.team.id] ?: 0).toString()
                awayTeamScore.text = (teamActions[awayTeam.team.id] ?: 0).toString()

                val scrollToNewTop = !gameActions.canScrollVertically(SCROLLING_UP)

                adapter.updateDataSet(it.map { action ->
                    val team = when (action.team) {
                        homeTeam.team.id -> homeTeam
                        else -> awayTeam
                    }
                    val player = team.players.firstOrNull { it.id == action.player }
                    ActionItem(action, team.team, action.team == homeTeam.team.id, player)
                })
                adapter.notifyDataSetChanged()
                if (scrollToNewTop) {
                    gameActions.scrollToPosition(adapter.itemCount - 1)
                }
            })
        })

        gameActions.adapter = adapter
        val llm = LinearLayoutManager(this)
        llm.reverseLayout = true
        llm.stackFromEnd = true
        gameActions.layoutManager = llm

    }

    private fun setButtonListeners(actionMap: Map<AppCompatButton, Pair<ActionType, ActionResult>>, teamId: Long) {
        actionMap.keys.forEach { t ->
            t.setOnClickListener {
                val action = actionMap.getValue(t)
                AppDatabase.getInstance(this).actionDao().insertAction(Action(action.first, action.second, teamId, gameId, null))
                //popup bottom sheet
            }
        }
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
