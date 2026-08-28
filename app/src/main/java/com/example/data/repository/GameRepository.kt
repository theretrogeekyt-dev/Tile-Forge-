package com.example.data.repository

import com.example.data.db.AchievementEntity
import com.example.data.db.GameDao
import com.example.data.db.GameStatsEntity
import com.example.data.db.LevelProgressEntity
import com.example.data.backup.TileForgeBackupPayload
import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {

    val gameStats: Flow<GameStatsEntity?> = gameDao.getGameStats()
    val levelProgressList: Flow<List<LevelProgressEntity>> = gameDao.getAllLevelProgress()
    val achievements: Flow<List<AchievementEntity>> = gameDao.getAllAchievements()

    suspend fun initializeDefaultDataIfNeeded() {
        val stats = gameDao.getGameStatsDirect()
        if (stats == null) {
            gameDao.insertOrUpdateGameStats(GameStatsEntity())
        }

        // Default 15 Levels
        val defaultLevels = (1..15).map { levelId ->
            LevelProgressEntity(
                levelId = levelId,
                isUnlocked = levelId == 1,
                starsEarned = 0,
                highScore = 0
            )
        }
        gameDao.insertLevelProgress(defaultLevels)

        // Default Achievements
        val defaultAchievements = listOf(
            AchievementEntity("ach_first_forge", "Apprentice Smith", "Forge your first numbered tile combination", "anvil", false, 0, 1),
            AchievementEntity("ach_tile_256", "Silver Artisan", "Forge a 256 tile", "shield", false, 0, 1),
            AchievementEntity("ach_tile_2048", "Master Forger", "Forge a 2048 tile or higher", "crown", false, 0, 1),
            AchievementEntity("ach_energy_500", "Power Collector", "Gather 500 Energy Crystals total", "crystal", false, 0, 500),
            AchievementEntity("ach_combo_3", "Chain Reaction", "Trigger a 3x combo in a single slide", "fire", false, 0, 3),
            AchievementEntity("ach_artifacts_10", "Relic Hunter", "Forge 10 Artifact tiles", "artifact", false, 0, 10),
            AchievementEntity("ach_levels_5", "Challenge Conqueror", "Complete 5 Challenge levels", "star", false, 0, 5)
        )
        gameDao.insertAchievements(defaultAchievements)
    }

    suspend fun saveGameStats(stats: GameStatsEntity) {
        gameDao.insertOrUpdateGameStats(stats)
    }

    suspend fun updateLevelProgress(levelId: Int, stars: Int, score: Int) {
        val currentLevels = gameDao.getAllLevelProgress()
        // Simple update
        val updatedLevel = LevelProgressEntity(
            levelId = levelId,
            isUnlocked = true,
            starsEarned = maxOf(stars, 0),
            highScore = maxOf(score, 0)
        )
        gameDao.updateLevelProgress(updatedLevel)

        // Unlock next level
        val nextLevelId = levelId + 1
        if (nextLevelId <= 15) {
            val nextLevel = LevelProgressEntity(
                levelId = nextLevelId,
                isUnlocked = true,
                starsEarned = 0,
                highScore = 0
            )
            gameDao.insertLevelProgress(listOf(nextLevel))
        }
    }

    suspend fun updateAchievementProgress(id: String, addProgress: Int) {
        // Simple helper
    }

    suspend fun getGameStatsDirect(): GameStatsEntity? = gameDao.getGameStatsDirect()
    suspend fun getAllLevelProgressDirect(): List<LevelProgressEntity> = gameDao.getAllLevelProgressDirect()
    suspend fun getAllAchievementsDirect(): List<AchievementEntity> = gameDao.getAllAchievementsDirect()

    suspend fun restoreBackup(payload: TileForgeBackupPayload) {
        gameDao.insertOrUpdateGameStats(payload.gameStats)
        if (payload.levelProgress.isNotEmpty()) {
            gameDao.insertLevelProgress(payload.levelProgress)
        }
        if (payload.achievements.isNotEmpty()) {
            gameDao.insertAchievements(payload.achievements)
        }
    }
}
