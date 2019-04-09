package com.hinterlong.kevin.swishticker.ui.adapters

import android.view.View
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.PlayerStats
import com.hinterlong.kevin.swishticker.service.data.Player
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.game_player_stats_item.view.*

data class GamePlayerStatsItem(val player: Player, val playerStats: PlayerStats?) : AbstractFlexibleItem<GamePlayerStatsItem.GamePlayerStatsViewHolder>() {

    override fun getLayoutRes() = R.layout.game_player_stats_item

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) = GamePlayerStatsViewHolder(view, adapter)

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: GamePlayerStatsItem.GamePlayerStatsViewHolder, position: Int, payloads: List<Any>) {
        val statsItem = adapter.getItem(position) as GamePlayerStatsItem

        holder.itemView.playerName.text = player.name
        holder.itemView.playerOnePointShots.text = (statsItem.playerStats?.shot1 ?: "n/a").toString()
        holder.itemView.playerTwoPointShots.text = (statsItem.playerStats?.shot2 ?: "n/a").toString()
        holder.itemView.playerThreePointShots.text = (statsItem.playerStats?.shot3 ?: "n/a").toString()
        holder.itemView.playerFouls.text = (statsItem.playerStats?.fouls ?: 0).toString()
    }

    class GamePlayerStatsViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter)

}
