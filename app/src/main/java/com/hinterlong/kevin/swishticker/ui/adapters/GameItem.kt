package com.hinterlong.kevin.swishticker.ui.adapters

import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.Score
import com.hinterlong.kevin.swishticker.service.data.Game
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
        val home = AppDatabase.getInstance(holder.itemView.context).teamDao().getTeam(game.team1)
        val away = AppDatabase.getInstance(holder.itemView.context).teamDao().getTeam(game.team2)
        holder.itemView.homeTeamName.text = home.name
        holder.itemView.awayTeamName.text = away.name

        holder.itemView.datePlayed.text = DTF.format(game.dateCreated)

        holder.itemView.homeTeamScore.text = score.home.toString()
        holder.itemView.awayTeamScore.text = score.away.toString()
        if (score.home > score.away) {
            holder.itemView.homeTeamScore.setTypeface(null, Typeface.BOLD)
        } else {
            holder.itemView.awayTeamScore.setTypeface(null, Typeface.BOLD)
        }
    }

    private fun setUnderlineText(textView: TextView, text: String) {
        val content = SpannableString(text)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        textView.text = content
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
