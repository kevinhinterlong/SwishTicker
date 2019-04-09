package com.hinterlong.kevin.swishticker.service.data

import androidx.room.Embedded
import androidx.room.Relation

class GameAndActions {
    @Embedded
    lateinit var game: Game

    @Relation(parentColumn = "id", entityColumn = "game_id")
    var actions: List<Action> = arrayListOf()

}
