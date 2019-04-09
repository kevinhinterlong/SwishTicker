package com.hinterlong.kevin.swishticker.ui.adapters

import android.view.View
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.data.Team
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFilterable
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.utils.FlexibleUtils
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.fragment_team.view.*


data class TeamItem(val team: Team) : AbstractFlexibleItem<TeamItem.TeamViewHolder>(), IFilterable<String> {

    override fun getLayoutRes() = R.layout.fragment_team

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) = TeamViewHolder(view, adapter)

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: TeamViewHolder, position: Int, payloads: List<Any>) {
        if (adapter.hasFilter()) {
            val filter = adapter.getFilter(String::class.java)
            FlexibleUtils.highlightText(holder.itemView.teamName, team.name, filter)
        } else {
            holder.itemView.teamName.text = team.name
        }
        holder.itemView.teamSize.text = AppDatabase.getInstance(holder.itemView.context).playerDao().getPlayers(team.id).size.toString()
    }

    class TeamViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter)

    override fun filter(constraint: String?) = when (constraint) {
        null -> false
        else -> team.name.toLowerCase().startsWith(constraint.toLowerCase())
    }
}
