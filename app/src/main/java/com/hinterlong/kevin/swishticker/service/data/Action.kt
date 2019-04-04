package com.hinterlong.kevin.swishticker.service.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "actions",
    foreignKeys = arrayOf(
        ForeignKey(entity = Team::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("team_id"),
            onDelete = ForeignKey.NO_ACTION),
        ForeignKey(entity = Game::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("game_id"),
            onDelete = ForeignKey.NO_ACTION),
        ForeignKey(entity = Player::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("player_id"),
            onDelete = ForeignKey.NO_ACTION)
    )
)
data class Action(
    @ColumnInfo(name = "action_type") val actionType: ActionType,
    @ColumnInfo(name = "team_id") val team: Long,
    @ColumnInfo(name = "game_id") val game: Long,
    @ColumnInfo(name = "player_id") val player: Long?,
    @ColumnInfo(name = "interval") var interval: Long = 0
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}