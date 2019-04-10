package com.hinterlong.kevin.swishticker.ui.modules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.data.*
import com.hinterlong.kevin.swishticker.ui.adapters.ActionItem
import com.hinterlong.kevin.swishticker.ui.adapters.PlayerItem
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.SelectableAdapter
import eu.davidea.flexibleadapter.helpers.ActionModeHelper
import kotlinx.android.synthetic.main.activity_in_game_tracker.*
import timber.log.Timber


class InGameTrackerActivity : AppCompatActivity() {
    private val adapterMap: MutableMap<Long, FlexibleAdapter<ActionItem>> = mutableMapOf(
        0L to FlexibleAdapter<ActionItem>(null),
        1L to FlexibleAdapter(null),
        2L to FlexibleAdapter(null),
        3L to FlexibleAdapter(null)
    )
    private var bottomSheetAction: LiveData<Action>? = null
    private var gameId: Long = 0
    private var firstLoad = true
    private var currentPeriod: Long = 0
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
    private val actionPlayersAdapter = FlexibleAdapter<PlayerItem>(null)
    private lateinit var actionModeHelper: ActionModeHelper
    private lateinit var bottomsheetBehavior: BottomSheetBehavior<NestedScrollView>
    private val SCROLLING_UP = -1

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
            val homeTeam = db.teamDao().getTeamAndPlayersSync(it.team1)
            val awayTeam = db.teamDao().getTeamAndPlayersSync(it.team2)

            setButtonListeners(homeActionMap, it.team1)
            setButtonListeners(awayActionMap, it.team2)

            homeTeamName.text = homeTeam.team.name
            awayTeamName.text = awayTeam.team.name
            Timber.d("Updated game as $it")

            db.actionDao().getGameActions(gameId).observe(this, Observer {
                if (firstLoad) {
                    firstLoad = false
                    val lastInterval = it.map { it.interval }.max() ?: 0
                    setCurrentPeriod(lastInterval)
                    (0..lastInterval).forEach { period ->
                        putIfAbsent(adapterMap, period)
                        adapterMap[period]?.updateDataSet(it.filter { it.interval == period }.map(toActionItem(homeTeam, awayTeam)))
                    }
                }

                val teamActions = it.groupingBy { it.team }.fold(0) { sum, action -> sum + toPoints(action) }
                homeTeamScore.text = (teamActions[homeTeam.team.id] ?: 0).toString()
                awayTeamScore.text = (teamActions[awayTeam.team.id] ?: 0).toString()

                val scrollToNewTop = !gameActions.canScrollVertically(SCROLLING_UP)

                val actionItems = it.filter { it.interval == currentPeriod }.map(toActionItem(homeTeam, awayTeam))

                putIfAbsent(adapterMap, currentPeriod)
                val adapter = adapterMap[currentPeriod]!!
                adapter.updateDataSet(actionItems)
                adapter.notifyDataSetChanged()
                adapter.mItemClickListener = FlexibleAdapter.OnItemClickListener { _, position ->
                    val actionItem = adapter.getItem(position)
                    if (actionItem != null) {
                        setBottomSheetAction(actionItem.action.id)
                        bottomsheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    return@OnItemClickListener true
                }
                gameActions.adapter = adapter

                if (scrollToNewTop) {
                    gameActions.scrollToPosition(adapter.itemCount - 1)
                }
            })
        })

        gameMenu.setOnClickListener {
            bottomsheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            val constantItems = arrayOf(
                getString(R.string.finish_game),
                getString(R.string.next_period)
            )
            val conditionalItems = arrayOf(getString(R.string.previous_period))
            val items = if (currentPeriod == 0L) {
                constantItems
            } else {
                constantItems.plus(conditionalItems)
            }

            AlertDialog.Builder(this)
                .setItems(items) { dialog, which ->
                    when (which) {
                        0 -> {
                            AlertDialog.Builder(this)
                                .setTitle(R.string.finish_game_action)
                                .setMessage(getString(R.string.cannot_resume))
                                .setPositiveButton(getString(R.string.ok)) { finishGame, _ ->
                                    val game = db.gameDao().getGameSync(gameId)
                                        .copy(active = false)
                                        .also { it.id = gameId }
                                    db.gameDao().updateGame(game)
                                    finishGame.dismiss()
                                    finish()
                                }
                                .setNegativeButton(getString(R.string.cancel)) { finishGame, _ ->
                                    finishGame.dismiss()
                                }
                                .show()
                        }
                        1 -> {
                            setCurrentPeriod(currentPeriod + 1)
                            dialog.dismiss()
                        }
                        2 -> {
                            setCurrentPeriod(currentPeriod - 1)
                            dialog.dismiss()
                        }
                    }
                }
                .show()
        }

        val llm = LinearLayoutManager(this)
        llm.reverseLayout = true
        llm.stackFromEnd = true
        gameActions.layoutManager = llm

        actionPlayersAdapter.mode = SelectableAdapter.Mode.SINGLE
        actionPlayers.adapter = actionPlayersAdapter

        bottomsheetBehavior = BottomSheetBehavior.from(gameActionMenu)
        bottomsheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        dismissBottomSheet.setOnClickListener {
            bottomsheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun toActionItem(homeTeam: TeamAndPlayers, awayTeam: TeamAndPlayers): (Action) -> ActionItem {
        return { action ->
            val team = when (action.team) {
                homeTeam.team.id -> homeTeam
                else -> awayTeam
            }
            val player = team.players.firstOrNull { it.id == action.player }
            ActionItem(action, team.team, action.team == homeTeam.team.id, player)
        }
    }

    private fun putIfAbsent(map: MutableMap<Long, FlexibleAdapter<ActionItem>>, period: Long) {
        if (!map.containsKey(period)) {
            map[period] = FlexibleAdapter(null)
        }
    }

    private fun setCurrentPeriod(period: Long) {
        currentPeriod = period
        putIfAbsent(adapterMap, currentPeriod)
        gameActions.adapter = adapterMap[currentPeriod]
        quarterName.text = toQuarterName(period)
    }

    private fun setButtonListeners(actionMap: Map<AppCompatButton, Pair<ActionType, ActionResult>>, teamId: Long) {
        actionMap.keys.forEach { t ->
            t.setOnClickListener {
                val defaultAction = actionMap.getValue(t)
                val actionId = AppDatabase.getInstance(this).actionDao().insertAction(Action(defaultAction.first, defaultAction.second, teamId, gameId, null, currentPeriod))
                //popup bottom sheet
                bottomsheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                setBottomSheetAction(actionId)
            }
        }
    }

    private fun setBottomSheetAction(actionId: Long) {
        val db = AppDatabase.getInstance(this)
        bottomSheetAction?.removeObservers(this)
        bottomSheetAction = db.actionDao().getAction(actionId)
        bottomSheetAction?.observe(this, Observer { action ->
            when (action.actionResult) {
                ActionResult.NONE -> shotHitMissButtons.visibility = View.GONE
                ActionResult.SHOT_MISS -> {
                    hitShotButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

                    missShotButton.isChecked = true
                    missShotButton.setBackgroundColor(ContextCompat.getColor(this, R.color.red_transparent))
                    shotHitMissButtons.visibility = View.VISIBLE
                }
                ActionResult.SHOT_HIT -> {
                    missShotButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

                    hitShotButton.isChecked = true
                    hitShotButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green_transparent))
                    shotHitMissButtons.visibility = View.VISIBLE
                }
            }

            missShotButton.setOnClickListener {
                db.actionDao().updateAction(action.copy(actionResult = ActionResult.SHOT_MISS).also { it.id = actionId })
            }
            hitShotButton.setOnClickListener {
                db.actionDao().updateAction(action.copy(actionResult = ActionResult.SHOT_HIT).also { it.id = actionId })
            }

            actionPlayersAdapter.clearSelection()
            db.playerDao().getPlayers(action.team).observe(this, Observer {
                if(it.isEmpty()) {
                    playerListTitle.visibility = View.GONE
                } else {
                    playerListTitle.visibility = View.VISIBLE
                }

                actionPlayersAdapter.updateDataSet(it.map(::PlayerItem))
                actionPlayersAdapter.notifyDataSetChanged()

                val playerPosition = actionPlayersAdapter.currentItems.indexOfFirst { it.player.id == action.player }
                actionPlayersAdapter.addSelection(playerPosition)
            })


            actionPlayersAdapter.mItemClickListener = FlexibleAdapter.OnItemClickListener { _, position ->
                val player = if (actionPlayersAdapter.isSelected(position)) {
                    null
                } else {
                    val playerItem = actionPlayersAdapter.getItem(position) as PlayerItem
                    playerItem.player
                }
                db.actionDao().updateAction(action.copy(player = player?.id).also { it.id = actionId })

                return@OnItemClickListener true
            }

            deleteGameAction.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle(R.string.delete_action)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        bottomSheetAction?.removeObservers(this)
                        db.actionDao().deleteAction(action)
                        dialog.dismiss()
                        bottomsheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        bottomSheetAction?.removeObservers(this)
        bottomsheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
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
