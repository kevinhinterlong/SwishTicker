package com.hinterlong.kevin.swishticker.service.data

import androidx.room.*

@Dao
interface TeamDao {
    @Query("SELECT * FROM teams")
    fun getTeams(): List<Team>

    @Query("SELECT * FROM teams WHERE id = :teamId")
    fun getTeam(teamId: Long): Team

    @Insert
    fun insertTeam(team: Team): Long

    @Update
    fun updateTeam(team: Team)

    @Delete
    fun deleteTeam(team: Team)
}