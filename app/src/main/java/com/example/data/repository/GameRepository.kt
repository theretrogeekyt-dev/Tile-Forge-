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

        // Only insert default levels if not yet populated
        val existingLevels = gameDao.getAllLevelProgressDirect()
        if (existingLevels.isEmpty()) {
            val defaultLevels = (1..15).map { levelId ->
                LevelProgressEntity(
                    levelId = levelId,
                    isUnlocked = levelId == 1,
                    starsEarned = 0,
                    highScore = 0
                )
            }
            gameDao.insertLevelProgress(defaultLevels)
        }

        // Only insert default achievements if not yet populated
        val existingAchievements = gameDao.getAllAchievementsDirect()
        if (existingAchievements.isEmpty()) {
            val defaultAchievements = listOf(
                AchievementEntity("ach_first_forge", "Apprentice Smith", "Forge your first numbered tile combination", "anvil", false, 0, 1),
                AchievementEntity("ach_tile_256", "Silver Artisan", "Forge a 256 tile", "shield", false, 0, 1),
                AchievementEntity("ach_tile_2048", "Master Forger", "Forge a 2048 tile or higher", "crown", false, 0, 1),
                AchievementEntity("ach_energy_500", "Power Collector", "Gather 500 Energy Crystals total", "crystal", false, 0, 500),
                AchievementEntity("ach_combo_3", "Chain Reaction", "Trigger a 3x combo in a single slide", "fire", false, 0, 3),
                AchievementEntity("ach_artifacts_10", "Relic Hunter", "Forge 10 Artifact tiles", "artifact", false, 0, 10),
                AchievementEntity("ach_levels_5", "Challenge Conqueror", "Complete 5 Challenge levels", "star", false, 0, 5),
                AchievementEntity("ach_plus_ultra", "Go Beyond! PLUS ULTRA!", "Unleash a 4x combo or higher like All Might", "crown", false, 0, 4),
                AchievementEntity("ach_one_for_all", "One For All: Full Cowling", "Gather 250 Energy Crystals to power your hero meter", "crystal", false, 0, 250)
            )
            gameDao.insertAchievements(defaultAchievements)
        }
    }

    suspend fun saveGameStats(stats: GameStatsEntity) {
        gameDao.insertOrUpdateGameStats(stats)
        checkAndUnlockAchievements(stats)
    }

    suspend fun updateLevelProgress(levelId: Int, stars: Int, score: Int) {
        val currentLevels = gameDao.getAllLevelProgressDirect()
        val existingLevel = currentLevels.find { it.levelId == levelId }
        val updatedLevel = LevelProgressEntity(
            levelId = levelId,
            isUnlocked = true,
            starsEarned = maxOf(existingLevel?.starsEarned ?: 0, stars),
            highScore = maxOf(existingLevel?.highScore ?: 0, score)
        )
        gameDao.insertLevelProgress(listOf(updatedLevel))

        // Unlock next level without resetting any prior progress
        val nextLevelId = levelId + 1
        if (nextLevelId <= 15) {
            val existingNext = currentLevels.find { it.levelId == nextLevelId }
            val nextLevel = existingNext?.copy(isUnlocked = true) ?: LevelProgressEntity(
                levelId = nextLevelId,
                isUnlocked = true,
                starsEarned = 0,
                highScore = 0
            )
            gameDao.insertLevelProgress(listOf(nextLevel))
        }

        val updatedStats = gameDao.getGameStatsDirect() ?: GameStatsEntity()
        checkAndUnlockAchievements(updatedStats)
    }

    suspend fun checkAndUnlockAchievements(stats: GameStatsEntity) {
        val achievements = gameDao.getAllAchievementsDirect()
        if (achievements.isEmpty()) return

        val levels = gameDao.getAllLevelProgressDirect()
        val completedLevelsCount = levels.count { it.starsEarned > 0 }

        var changed = false
        val updatedAchievements = achievements.map { ach ->
            var newProgress = ach.progress
            var isUnlocked = ach.isUnlocked

            when (ach.id) {
                "ach_first_forge" -> {
                    if (stats.highScore > 0 || stats.highestTile > 2 || stats.totalEnergyCollected > 0) {
                        newProgress = 1
                        isUnlocked = true
                    }
                }
                "ach_tile_256" -> {
                    if (stats.highestTile >= 256) {
                        newProgress = 1
                        isUnlocked = true
                    }
                }
                "ach_tile_2048" -> {
                    if (stats.highestTile >= 2048) {
                        newProgress = 1
                        isUnlocked = true
                    }
                }
                "ach_energy_500" -> {
                    newProgress = minOf(stats.totalEnergyCollected, 500)
                    if (newProgress >= 500) isUnlocked = true
                }
                "ach_combo_3" -> {
                    newProgress = minOf(stats.maxComboChain, 3)
                    if (newProgress >= 3) isUnlocked = true
                }
                "ach_artifacts_10" -> {
                    newProgress = minOf(stats.totalArtifactsForged, 10)
                    if (newProgress >= 10) isUnlocked = true
                }
                "ach_levels_5" -> {
                    newProgress = minOf(completedLevelsCount, 5)
                    if (newProgress >= 5) isUnlocked = true
                }
                "ach_plus_ultra" -> {
                    newProgress = minOf(stats.maxComboChain, 4)
                    if (newProgress >= 4) isUnlocked = true
                }
                "ach_one_for_all" -> {
                    newProgress = minOf(stats.totalEnergyCollected, 250)
                    if (newProgress >= 250) isUnlocked = true
                }
            }

            if (newProgress != ach.progress || isUnlocked != ach.isUnlocked) {
                changed = true
                ach.copy(progress = newProgress, isUnlocked = isUnlocked)
            } else {
                ach
            }
        }

        if (changed) {
            gameDao.insertAchievements(updatedAchievements)
        }
    }

    suspend fun updateAchievementProgress(id: String, addProgress: Int) {
        val achievements = gameDao.getAllAchievementsDirect()
        val target = achievements.find { it.id == id } ?: return
        val newProgress = minOf(target.progress + addProgress, target.maxProgress)
        val isUnlocked = newProgress >= target.maxProgress
        gameDao.updateAchievement(target.copy(progress = newProgress, isUnlocked = isUnlocked))
    }

    suspend fun getGameStatsDirect(): GameStatsEntity? = gameDao.getGameStatsDirect()
    suspend fun getAllLevelProgressDirect(): List<LevelProgressEntity> = gameDao.getAllLevelProgressDirect()
    suspend fun getAllAchievementsDirect(): List<AchievementEntity> = gameDao.getAllAchievementsDirect()

    suspend fun addExperienceAndCoins(xpToAdd: Int, coinsToAdd: Int): Pair<Int, Boolean> {
        val currentStats = gameDao.getGameStatsDirect() ?: GameStatsEntity()
        var currentLevel = currentStats.playerLevel
        var currentXp = currentStats.playerXp + xpToAdd
        var leveledUp = false

        while (true) {
            val xpNeeded = currentLevel * 150
            if (currentXp >= xpNeeded && currentLevel < 50) {
                currentXp -= xpNeeded
                currentLevel += 1
                leveledUp = true
            } else {
                break
            }
        }

        val bonusCoins = if (leveledUp) 100 else 0
        val updated = currentStats.copy(
            playerLevel = currentLevel,
            playerXp = currentXp,
            goldCoins = currentStats.goldCoins + coinsToAdd + bonusCoins
        )
        gameDao.insertOrUpdateGameStats(updated)
        return Pair(currentLevel, leveledUp)
    }

    suspend fun claimDailyStreak(day: Int, coinReward: Int, energyReward: Int): Boolean {
        val currentStats = gameDao.getGameStatsDirect() ?: GameStatsEntity()
        if (currentStats.claimedDailyStreakDay >= day) return false
        val updated = currentStats.copy(
            claimedDailyStreakDay = day,
            goldCoins = currentStats.goldCoins + coinReward,
            totalEnergyCollected = currentStats.totalEnergyCollected + energyReward
        )
        gameDao.insertOrUpdateGameStats(updated)
        return true
    }

    suspend fun claimDailyQuest(questBit: Int, coinReward: Int, xpReward: Int): Boolean {
        val currentStats = gameDao.getGameStatsDirect() ?: GameStatsEntity()
        if ((currentStats.claimedQuestsMask and questBit) != 0) return false
        val newMask = currentStats.claimedQuestsMask or questBit
        val updated = currentStats.copy(
            claimedQuestsMask = newMask,
            goldCoins = currentStats.goldCoins + coinReward
        )
        gameDao.insertOrUpdateGameStats(updated)
        addExperienceAndCoins(xpReward, 0)
        return true
    }

    suspend fun purchasePerk(perkBit: Int, costGold: Int): Boolean {
        val currentStats = gameDao.getGameStatsDirect() ?: GameStatsEntity()
        if ((currentStats.unlockedPerksMask and perkBit) != 0) return false
        if (currentStats.goldCoins < costGold) return false

        val updated = currentStats.copy(
            goldCoins = currentStats.goldCoins - costGold,
            unlockedPerksMask = currentStats.unlockedPerksMask or perkBit
        )
        gameDao.insertOrUpdateGameStats(updated)
        return true
    }

    suspend fun equipTitle(title: String) {
        val currentStats = gameDao.getGameStatsDirect() ?: GameStatsEntity()
        val updated = currentStats.copy(equippedTitle = title)
        gameDao.insertOrUpdateGameStats(updated)
    }

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
