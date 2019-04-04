package com.hinterlong.kevin.swishticker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(
    tableName = "games",
    foreignKeys = arrayOf(
        ForeignKey(entity = Team::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("team1_id"),
            onDelete = ForeignKey.NO_ACTION),
        ForeignKey(entity = Team::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("team2_id"),
            onDelete = ForeignKey.NO_ACTION)
    )
)
data class Game(
    @ColumnInfo(name = "team1_id") val team1: Long,
    @ColumnInfo(name = "team2_id") val team2: Long,
    @ColumnInfo(name = "active") var active: Boolean = true,
    @ColumnInfo(name = "date_created") val dateCreated: OffsetDateTime = OffsetDateTime.now()
){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}