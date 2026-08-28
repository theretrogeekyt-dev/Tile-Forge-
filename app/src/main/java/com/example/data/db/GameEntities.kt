package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_stats")
data class GameStatsEntity(
    @PrimaryKey val id: Int = 1,
    val highScore: Int = 0,
    val dailyQuestHighScore: Int = 0,
    val lastDailyQuestDate: String = "",
    val highestTile: Int = 2,
    val totalEnergyCollected: Int = 0,
    val totalArtifactsForged: Int = 0,
    val totalGamesPlayed: Int = 0,
    val maxComboChain: Int = 0,
    val activeThemeId: String = "classic_obsidian"
)

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey val levelId: Int,
    val isUnlocked: Boolean = false,
    val starsEarned: Int = 0,
    val highScore: Int = 0
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean = false,
    val progress: Int = 0,
    val maxProgress: Int = 1
)

