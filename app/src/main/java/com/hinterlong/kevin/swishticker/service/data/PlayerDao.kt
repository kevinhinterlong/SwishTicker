package com.hinterlong.kevin.swishticker.service.data

import androidx.room.*

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players")
    fun getPlayers(): List<Player>

    @Query("SELECT * FROM players WHERE team_id = :teamId")
    fun getPlayers(teamId: Long): List<Player>

    @Query("SELECT * FROM players WHERE id = :playerId")
    fun getPlayer(playerId: Long): Player

    @Insert
    fun insertPlayer(player: Player): Long

    @Update
    fun updatePlayer(player: Player)

    @Delete
    fun deletePlayer(player: Player)
}