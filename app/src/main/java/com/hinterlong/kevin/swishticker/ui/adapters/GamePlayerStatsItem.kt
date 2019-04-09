package com.hinterlong.kevin.swishticker.ui.adapters

import android.content.Context
import android.view.View
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.PlayerStats
import com.hinterlong.kevin.swishticker.service.ShotCount
import com.hinterlong.kevin.swishticker.service.data.Player
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.game_player_stats_item.view.*

data class GamePlayerStatsItem(
    val name: String,
    val playerStats: PlayerStats?,
    val usePercent: Boolean = false,
    val extraField: (PlayerStats?) -> Any? = { (it?.fouls ?: 0).toString() }
) : AbstractFlexibleItem<GamePlayerStatsItem.GamePlayerStatsViewHolder>() {

    override fun getLayoutRes() = R.layout.game_player_stats_item

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) = GamePlayerStatsViewHolder(view, adapter)

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: GamePlayerStatsItem.GamePlayerStatsViewHolder, position: Int, payloads: List<Any>) {
        val statsItem = adapter.getItem(position) as GamePlayerStatsItem

        holder.itemView.playerName.text = name
        holder.itemView.playerOnePointShots.text = toString(holder.itemView.context, statsItem.playerStats?.shot1)
        holder.itemView.playerTwoPointShots.text = toString(holder.itemView.context, statsItem.playerStats?.shot2)
        holder.itemView.playerThreePointShots.text = toString(holder.itemView.context, statsItem.playerStats?.shot3)
        val extra = extraField(statsItem.playerStats)
        holder.itemView.playerExtra.text = if (extra != null) {
            extra.toString()
        } else {
            holder.itemView.context.getString(R.string.not_applicable)
        }
    }

    private fun toString(context: Context, shotCount: ShotCount?): String {
        return if (shotCount == null || shotCount.made + shotCount.missed == 0L) {
            context.getString(R.string.not_applicable)
        } else if (!usePercent) {
            context.getString(R.string.stats_fraction, shotCount.made, shotCount.made + shotCount.missed)
        } else {
            context.getString(R.string.stats_percent, 100 * shotCount.made.toDouble() / (shotCount.made + shotCount.missed))
        }
    }

    class GamePlayerStatsViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter)

}
