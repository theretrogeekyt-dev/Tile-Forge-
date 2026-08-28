package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM game_stats WHERE id = 1")
    fun getGameStats(): Flow<GameStatsEntity?>

    @Query("SELECT * FROM game_stats WHERE id = 1")
    suspend fun getGameStatsDirect(): GameStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGameStats(stats: GameStatsEntity)

    @Query("SELECT * FROM level_progress ORDER BY levelId ASC")
    fun getAllLevelProgress(): Flow<List<LevelProgressEntity>>

    @Query("SELECT * FROM level_progress ORDER BY levelId ASC")
    suspend fun getAllLevelProgressDirect(): List<LevelProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevelProgress(levels: List<LevelProgressEntity>)

    @Update
    suspend fun updateLevelProgress(level: LevelProgressEntity)

    @Query("SELECT * FROM achievements ORDER BY id ASC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements ORDER BY id ASC")
    suspend fun getAllAchievementsDirect(): List<AchievementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)
}
