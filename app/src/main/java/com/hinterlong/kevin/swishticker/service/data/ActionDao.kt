package com.hinterlong.kevin.swishticker.service.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ActionDao {
    @Query("SELECT * FROM actions WHERE game_id = :gameId")
    fun getGameActions(gameId: Long): LiveData<List<Action>>

    @Query("SELECT * FROM actions WHERE id = :actionId")
    fun getAction(actionId: Long): LiveData<Action>

    @Insert
    fun insertAction(action: Action): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAction(action: Action)

    @Delete
    fun deleteAction(action: Action)
}