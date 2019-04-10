package com.hinterlong.kevin.swishticker.service.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players WHERE team_id = :teamId")
    fun getPlayersSync(teamId: Long): List<Player>

    @Query("SELECT * FROM players WHERE team_id = :teamId")
    fun getPlayers(teamId: Long): LiveData<List<Player>>

    @Query("SELECT * FROM players WHERE id = :id")
    fun getPlayer(id: Long): LiveData<Player>

    @Insert
    fun insertPlayer(player: Player): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePlayer(player: Player)

    @Delete
    fun deletePlayer(player: Player)
}