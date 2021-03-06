package com.hinterlong.kevin.swishticker.service.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY date_created")
    fun getGames(): LiveData<List<Game>>

    @Transaction
    @Query("SELECT * FROM games WHERE team1_id = :teamId OR team2_id = :teamId")
    fun getGamesAndActions(teamId: Long): LiveData<List<GameAndActions>>

    @Transaction
    @Query("SELECT * FROM games")
    fun getGamesAndActions(): LiveData<List<GameAndActions>>

    @Query("SELECT * FROM games WHERE id = :gameId")
    fun getGame(gameId: Long): LiveData<Game>

    @Insert
    fun insertGame(game: Game): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateGame(game: Game)

    @Delete
    fun deleteGame(game: Game)

    @Query("DELETE FROM games WHERE id = :id")
    fun deleteGame(id: Long)

}