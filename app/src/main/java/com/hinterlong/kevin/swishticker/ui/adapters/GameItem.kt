package com.hinterlong.kevin.swishticker.ui.adapters

import android.graphics.Typeface
import android.view.View
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.Score
import com.hinterlong.kevin.swishticker.service.data.Game
import com.hinterlong.kevin.swishticker.service.data.toQuarterName
import com.hinterlong.kevin.swishticker.ui.modules.GameDetailActivity
import com.hinterlong.kevin.swishticker.ui.modules.TeamDetailActivity
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.fragment_game.view.*
import org.threeten.bp.format.DateTimeFormatter

val DTF: DateTimeFormatter = DateTimeFormatter.ofPattern("E, M/d")

data class GameItem(val game: Game, val score: Score) : AbstractFlexibleItem<GameItem.GameViewHolder>() {

    override fun getLayoutRes(): Int {
        return R.layout.fragment_game
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): GameViewHolder {
        return GameViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: GameViewHolder, position: Int, payloads: List<Any>) {
        val db = AppDatabase.getInstance(holder.itemView.context)
        val home = db.teamDao().getTeam(game.team1)
        val away = db.teamDao().getTeam(game.team2)
        holder.itemView.homeTeamName.text = home.name
        holder.itemView.awayTeamName.text = away.name

        if (game.active) {
            holder.itemView.activeGame.visibility = View.VISIBLE
            // TODO: Don't do it on main thread
            val period = db.actionDao().getGameActionsSync(game.id).map { it.interval }.max() ?: 0
            holder.itemView.currentPeriod.text = toQuarterName(period)
        } else {
            holder.itemView.activeGame.visibility = View.GONE
        }

        holder.itemView.datePlayed.text = DTF.format(game.dateCreated)

        holder.itemView.homeTeamScore.text = score.home.toString()
        holder.itemView.awayTeamScore.text = score.away.toString()
        if (score.home > score.away) {
            holder.itemView.homeTeamScore.setTypeface(null, Typeface.BOLD)
        } else {
            holder.itemView.awayTeamScore.setTypeface(null, Typeface.BOLD)
        }
    }

    class GameViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {

        init {

            view.homeTeamName.setOnClickListener {
                val gameItem = adapter.getItem(adapterPosition) as GameItem
                TeamDetailActivity.withTeam(view.context, gameItem.game.team1)
            }
            view.awayTeamName.setOnClickListener {
                val gameItem = adapter.getItem(adapterPosition) as GameItem
                TeamDetailActivity.withTeam(view.context, gameItem.game.team2)
            }

            view.setOnClickListener {
                val gameItem = adapter.getItem(adapterPosition) as GameItem
                GameDetailActivity.withGame(view.context, gameItem.game.id)
            }
        }
    }
}
