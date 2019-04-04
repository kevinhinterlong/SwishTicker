package com.hinterlong.kevin.swishticker.service.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY date_created")
    fun getGames(): LiveData<List<Game>>

    @Query("SELECT * FROM games WHERE team1_id = :teamId OR team2_id = :teamId")
    fun getGames(teamId: Long): List<Game>

    @Query("SELECT * FROM games WHERE id = :gameId")
    fun getGame(gameId: Long): Game

    @Insert
    fun insertGame(game: Game): Long

    @Update
    fun updateGame(game: Game)

    @Delete
    fun deleteGame(game: Game)

}