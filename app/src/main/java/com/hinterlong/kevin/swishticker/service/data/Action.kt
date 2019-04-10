package com.hinterlong.kevin.swishticker.service.data

import androidx.room.*

@Entity(
    tableName = "actions",
    indices = arrayOf(
        Index("team_id"),
        Index("game_id"),
        Index("player_id")
    ),
    foreignKeys = arrayOf(
        ForeignKey(entity = Team::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("team_id"),
            onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Game::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("game_id"),
            onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Player::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("player_id"),
            onDelete = ForeignKey.SET_NULL)
    )
)
data class Action(
    @ColumnInfo(name = "action_type") val actionType: ActionType,
    @ColumnInfo(name = "action_result") val actionResult: ActionResult,
    @ColumnInfo(name = "team_id") val team: Long,
    @ColumnInfo(name = "game_id") val game: Long,
    @ColumnInfo(name = "player_id") val player: Long?,
    @ColumnInfo(name = "interval") val interval: Long = 0
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}