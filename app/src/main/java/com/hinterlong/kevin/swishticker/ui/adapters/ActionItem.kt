package com.hinterlong.kevin.swishticker.ui.adapters

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.data.*
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.action_item.view.*
import java.util.*

class ActionItem(
    val action: Action,
    val team: Team,
    val isHome: Boolean,
    val player: Player? = null,
    val background: Int? = null
) : AbstractFlexibleItem<ActionItem.ActionViewHolder>() {

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ActionViewHolder, position: Int, payloads: List<Any>) {
        val action = (adapter.getItem(position) as ActionItem).action

        val shotValue = shotValue(action.actionType)
        if (shotValue != 0) {
            holder.itemView.actionText.text = when (action.actionResult) {
                ActionResult.SHOT_MISS -> "${shotValue}pt miss"
                else -> "${shotValue}pt"
            }
        } else {
            holder.itemView.actionText.text = "foul"
        }

        if (isHome) {
            holder.itemView.actionText.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_blue))
        } else {
            holder.itemView.actionText.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_orange))
        }

        if (player != null) {
            holder.itemView.actionCause.text = "by ${player.name}"
        } else {
            holder.itemView.actionCause.text = "by ${team.name}"
        }
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): ActionViewHolder {
        return ActionViewHolder(view, adapter, background)
    }

    override fun getLayoutRes() = R.layout.action_item

    override fun hashCode() = Objects.hash(action, team, player)

    override fun equals(other: Any?) = other is ActionItem
        && other.action == action
        && other.team == team
        && other.player == player

    class ActionViewHolder(view: View, adapter: FlexibleAdapter<*>, background: Int?) : FlexibleViewHolder(view, adapter) {
        init {
            background?.let {
                view.setBackgroundColor(it)
            }
        }
    }
}