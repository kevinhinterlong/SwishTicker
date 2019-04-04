package com.hinterlong.kevin.swishticker.ui.adapters

import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.hinterlong.kevin.swishticker.AppDatabase
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.data.Game
import com.hinterlong.kevin.swishticker.ui.modules.GameDetailActivity
import com.hinterlong.kevin.swishticker.ui.modules.TeamDetailActivity
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder

data class GameItem(val game: Game) : AbstractFlexibleItem<GameItem.GameViewHolder>() {

    override fun getLayoutRes(): Int {
        return R.layout.fragment_game
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): GameViewHolder {
        return GameViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: GameViewHolder, position: Int, payloads: List<Any>) {
        val home = AppDatabase.getInstance(holder.itemView.context).teamDao().getTeam(game.team1)
        val away = AppDatabase.getInstance(holder.itemView.context).teamDao().getTeam(game.team2)
        setUnderlineText(holder.homeTeamName, home.name)
        setUnderlineText(holder.awayTeamName, away.name)
        // TODO: Compute win/loss
        holder.homeTeamScore.text = "0"
        holder.awayTeamScore.text = "0"
    }

    private fun setUnderlineText(textView: TextView, text: String) {
        val content = SpannableString(text)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        textView.text = content
    }

    class GameViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        @BindView(R.id.home_team_name)
        lateinit var homeTeamName: TextView
        @BindView(R.id.away_team_name)
        lateinit var awayTeamName: TextView
        @BindView(R.id.home_team_score)
        lateinit var homeTeamScore: TextView
        @BindView(R.id.away_team_score)
        lateinit var awayTeamScore: TextView

        init {
            ButterKnife.bind(this, view)

            homeTeamName.setOnClickListener {
                val gameItem = adapter.getItem(adapterPosition) as GameItem
                TeamDetailActivity.withTeam(view.context, gameItem.game.team1)
            }
            awayTeamName.setOnClickListener {
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
