package com.hinterlong.kevin.swishticker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "teams"
)
data class Team(
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "color") var color: String? = null
){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}