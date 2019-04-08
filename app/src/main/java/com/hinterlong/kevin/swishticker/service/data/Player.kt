package com.hinterlong.kevin.swishticker.service.data

import androidx.room.*

@Entity(
    tableName = "players",
    indices = arrayOf(
        Index("team_id")
    ),
    foreignKeys = arrayOf(
        ForeignKey(entity = Team::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("team_id"),
            onDelete = ForeignKey.CASCADE)
    )
)
data class Player(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "team_id") val team: Long
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}