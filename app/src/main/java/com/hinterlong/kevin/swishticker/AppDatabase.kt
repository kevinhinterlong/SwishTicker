package com.hinterlong.kevin.swishticker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hinterlong.kevin.swishticker.converters.ActionTypeConverter
import com.hinterlong.kevin.swishticker.converters.TimeConverter
import com.hinterlong.kevin.swishticker.data.*
import com.hinterlong.kevin.swishticker.utilities.DATABASE_NAME

@Database(entities = arrayOf(Game::class, Team::class, Player::class, Action::class), version = 1)
@TypeConverters(value = arrayOf(TimeConverter::class, ActionTypeConverter::class ))
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun teamDao(): TeamDao
    abstract fun playerDao(): PlayerDao
    abstract fun actionDao(): ActionDao

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).allowMainThreadQueries().build()
        }
    }
}