package com.hinterlong.kevin.swishticker.service.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ActionDao {
    @Query("SELECT * FROM actions")
    fun getActions(): List<Action>

    @Query("SELECT * FROM actions WHERE game_id = :gameId")
    fun getGameActions(gameId: Long): LiveData<List<Action>>

    @Query("SELECT * FROM actions WHERE game_id = :gameId")
    fun getGameActionsSync(gameId: Long): List<Action>

    @Query("SELECT * FROM actions WHERE team_id = :teamId")
    fun getTeamActions(teamId: Long): List<Action>

    @Query("SELECT * FROM actions WHERE player_id = :playerId")
    fun getPlayerActions(playerId: Long): List<Action>

    @Query("SELECT * FROM actions WHERE id = :actionId")
    fun getAction(actionId: Long): Action

    @Insert
    fun insertAction(action: Action): Long

    @Update
    fun updateAction(action: Action)

    @Delete
    fun deleteAction(action: Action)
}