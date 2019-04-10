package com.hinterlong.kevin.swishticker.ui.adapters

import android.view.View
import androidx.core.content.ContextCompat
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.data.Player
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.player_item.view.*


data class PlayerItem(val player: Player) : AbstractFlexibleItem<PlayerItem.PlayerViewHolder>() {

    override fun getLayoutRes() = R.layout.player_item

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) = PlayerViewHolder(view, adapter)

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: PlayerViewHolder, position: Int, payloads: List<Any>) {
        if (adapter.isSelected(position)) {
            holder.itemView.playerCard.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.gray_transparent))
        } else {
            holder.itemView.playerCard.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        }
        holder.itemView.playerName.text = player.name
    }

    class PlayerViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter)
}
