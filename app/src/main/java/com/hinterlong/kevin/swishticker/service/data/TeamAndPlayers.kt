package com.hinterlong.kevin.swishticker.service.data

import androidx.room.Embedded
import androidx.room.Relation

class TeamAndPlayers {
    @Embedded
    lateinit var team: Team

    @Relation(parentColumn = "id", entityColumn = "team_id")
    var players: List<Player> = arrayListOf()
}