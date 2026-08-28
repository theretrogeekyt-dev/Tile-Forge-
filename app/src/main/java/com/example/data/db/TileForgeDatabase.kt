package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [GameStatsEntity::class, LevelProgressEntity::class, AchievementEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TileForgeDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var INSTANCE: TileForgeDatabase? = null

        fun getDatabase(context: Context): TileForgeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TileForgeDatabase::class.java,
                    "tile_forge_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
