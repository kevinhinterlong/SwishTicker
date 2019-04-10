package com.hinterlong.kevin.swishticker.service.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "teams"
)
data class Team(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "color") val color: String? = null,
    @ColumnInfo(name = "generated") val generated: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}