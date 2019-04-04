package com.hinterlong.kevin.swishticker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "players",
    foreignKeys = arrayOf(
        ForeignKey(entity = Team::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("team_id"),
            onDelete = ForeignKey.NO_ACTION)
    )
)
data class Player(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "team_id") val team: Long
){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}