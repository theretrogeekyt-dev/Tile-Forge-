package com.example.data.backup

import com.example.data.db.AchievementEntity
import com.example.data.db.GameStatsEntity
import com.example.data.db.LevelProgressEntity
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TileForgeBackupPayload(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val gameStats: GameStatsEntity = GameStatsEntity(),
    val levelProgress: List<LevelProgressEntity> = emptyList(),
    val achievements: List<AchievementEntity> = emptyList()
)
