package com.hinterlong.kevin.swishticker.ui.adapters

import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.data.Team
import com.hinterlong.kevin.swishticker.ui.modules.TeamDetailActivity
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder

data class TeamItem(private val team: Team) : AbstractFlexibleItem<TeamItem.TeamViewHolder>() {

    override fun getLayoutRes() = R.layout.fragment_team

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) = TeamViewHolder(view, adapter)

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: TeamViewHolder, position: Int, payloads: List<Any>) {
        holder.teamName.text = team.name
        holder.teamSize.text = "0"
        val games = AppDatabase.getInstance(holder.itemView.context).gameDao().getGames()

        holder.numberWon.text = "0"
        holder.numberLost.text = "0"
    }

    class TeamViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        @BindView(R.id.team_name) lateinit var teamName: TextView
        @BindView(R.id.team_size) lateinit var teamSize: TextView
        @BindView(R.id.number_won) lateinit var numberWon: TextView
        @BindView(R.id.number_lost) lateinit var numberLost: TextView

        init {
            ButterKnife.bind(this, view)
            view.setOnClickListener {
                val teamItem = adapter.getItem(adapterPosition) as TeamItem
                TeamDetailActivity.withTeam(view.context, teamItem.team.id)
            }
        }
    }
}
