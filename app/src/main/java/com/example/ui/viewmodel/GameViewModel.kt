package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AchievementEntity
import com.example.data.db.GameStatsEntity
import com.example.data.db.LevelProgressEntity
import com.example.data.db.TileForgeDatabase
import com.example.data.repository.GameRepository
import com.example.game.ArtifactType
import com.example.game.AudioEngine
import com.example.game.GameBoard
import com.example.game.GameModeType
import com.example.game.LevelGoal
import com.example.game.SwipeDirection
import com.example.ui.theme.ForgeThemeStyle
import com.example.ui.theme.ForgeThemes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class PowerAbilityType {
    NONE,
    SHATTER,
    DUPLICATE,
    TRANSMUTE
}

data class GameUiState(
    val gameMode: GameModeType = GameModeType.ENDLESS,
    val currentLevelId: Int = 1,
    val levelGoal: LevelGoal? = null,
    val score: Int = 0,
    val energy: Int = 0,
    val moves: Int = 0,
    val highestTile: Int = 2,
    val hearts: Int = 3,
    val maxHearts: Int = 3,
    val heartDamageTrigger: Long = 0L,
    val isGameOver: Boolean = false,
    val isLevelWon: Boolean = false,
    val activePowerAbility: PowerAbilityType = PowerAbilityType.NONE,
    val activeTheme: ForgeThemeStyle = ForgeThemes.ClassicObsidian,
    val isImmersiveModeEnabled: Boolean = true,
    val soundFxEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val showCreditsDialog: Boolean = false,
    val showAchievementsDialog: Boolean = false,
    val showThemeDialog: Boolean = false,
    val showLevelSelectDialog: Boolean = false,
    val hasAegisShield: Boolean = false,
    val midasTurnsLeft: Int = 0,
    val notificationMessage: String? = null,
    val showBackupDialog: Boolean = false,
    val isBackupProcessing: Boolean = false,
    val backupMessage: String? = null,
    val lastBackupTime: String = "Never",
    val dailyQuestDateFormatted: String = "",
    val dailyQuestHighScore: Int = 0,
    val todayDateKey: String = "",
    val lastSlideDirection: SwipeDirection? = null,
    val lastSlideTimestamp: Long = 0L
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    val gameBoard = GameBoard()
    val audioEngine = AudioEngine()
    val zipBackupManager = com.example.data.backup.LocalZipBackupManager(application)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    val gameStats: StateFlow<GameStatsEntity?>
    val levelProgressList: StateFlow<List<LevelProgressEntity>>
    val achievements: StateFlow<List<AchievementEntity>>

    init {
        val db = TileForgeDatabase.getDatabase(application)
        repository = GameRepository(db.gameDao())

        gameStats = repository.gameStats.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        levelProgressList = repository.levelProgressList.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        achievements = repository.achievements.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.initializeDefaultDataIfNeeded()
        }

        // Initialize Endless Mode
        startNewGame(GameModeType.ENDLESS, levelId = 1)
    }

    private var lastSavedSessionEnergy = 0
    private var lastSavedSessionArtifacts = 0

    fun startDailyQuest() {
        startNewGame(GameModeType.DAILY_QUEST, levelId = 999)
    }

    fun startNewGame(mode: GameModeType, levelId: Int = 1) {
        val dateKey = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val readableDate = SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date())

        val goal = when (mode) {
            GameModeType.CHALLENGE -> LevelGoal.getLevel(levelId)
            GameModeType.DAILY_QUEST -> LevelGoal.getDailyQuestGoal(readableDate)
            GameModeType.ENDLESS -> null
        }

        if (mode == GameModeType.DAILY_QUEST) {
            val seed = dateKey.toLongOrNull() ?: 20260828L
            gameBoard.resetBoard(seed = seed, isDailyQuest = true)
        } else {
            gameBoard.resetBoard(initialObstacles = goal?.initialObstacles ?: 0)
        }

        lastSavedSessionEnergy = 0
        lastSavedSessionArtifacts = 0

        _uiState.value = _uiState.value.copy(
            gameMode = mode,
            currentLevelId = levelId,
            levelGoal = goal,
            score = 0,
            energy = 0,
            moves = 0,
            highestTile = gameBoard.highestTileValue,
            hearts = gameBoard.hearts,
            maxHearts = gameBoard.maxHearts,
            isGameOver = false,
            isLevelWon = false,
            activePowerAbility = PowerAbilityType.NONE,
            hasAegisShield = false,
            midasTurnsLeft = 0,
            showCreditsDialog = false,
            showAchievementsDialog = false,
            showThemeDialog = false,
            showLevelSelectDialog = false,
            showBackupDialog = false,
            lastSlideDirection = null,
            lastSlideTimestamp = 0L,
            dailyQuestDateFormatted = readableDate,
            todayDateKey = dateKey,
            dailyQuestHighScore = gameStats.value?.dailyQuestHighScore ?: 0,
            notificationMessage = when (mode) {
                GameModeType.CHALLENGE -> "Level $levelId: ${goal?.title}"
                GameModeType.DAILY_QUEST -> "⚡ Daily Quest ($readableDate) • Seeded Board Ready!"
                GameModeType.ENDLESS -> "Endless Forge Started!"
            }
        )
    }

    fun handleSwipe(direction: SwipeDirection) {
        if (_uiState.value.isGameOver || _uiState.value.isLevelWon) return

        val result = gameBoard.slide(direction)
        if (result.moved) {
            audioEngine.playSlideSound()

            if (result.mergesCount > 0) {
                audioEngine.playMergeSound(gameBoard.highestTileValue)
            }

            if (result.energyGained > 0) {
                audioEngine.playEnergySound()
            }

            if (result.artifactForged != null) {
                audioEngine.playForgeSound()
                showNotification("Forged Artifact: ${result.artifactForged.displayName}!")
            }

            if (result.heartLost) {
                if (result.wasRescued) {
                    audioEngine.playHeartLostSound()
                    audioEngine.playHeartRescueSound()
                    _uiState.value = _uiState.value.copy(heartDamageTrigger = System.currentTimeMillis())
                    showNotification("💔 Heart Lost! Forge Rescued (${result.heartsRemaining}/3 Hearts Left)")
                } else {
                    audioEngine.playGameOverSound()
                    _uiState.value = _uiState.value.copy(heartDamageTrigger = System.currentTimeMillis())
                    showNotification("💀 All Hearts Depleted! Game Over.")
                }
            }

            checkLevelProgressAndWinCondition()
            updateStateFromBoard()
            saveCurrentStats()
        }
    }

    private fun checkLevelProgressAndWinCondition() {
        val state = _uiState.value
        val goal = state.levelGoal ?: return

        if (state.gameMode != GameModeType.CHALLENGE) return

        val scoreWon = gameBoard.score >= goal.targetScore
        val tileWon = goal.targetTileValue == 0 || gameBoard.highestTileValue >= goal.targetTileValue
        val energyWon = goal.targetEnergyCollected == 0 || gameBoard.energyCollectedCount >= goal.targetEnergyCollected
        val artifactsWon = goal.targetArtifactsForged == 0 || gameBoard.artifactsForgedCount >= goal.targetArtifactsForged

        if (scoreWon && tileWon && energyWon && artifactsWon) {
            _uiState.value = _uiState.value.copy(isLevelWon = true, activePowerAbility = PowerAbilityType.NONE)
            audioEngine.playForgeSound()
            showNotification("Level ${state.currentLevelId} Completed!")

            viewModelScope.launch {
                val stars = when {
                    gameBoard.moves <= goal.maxMoves - 5 -> 3
                    gameBoard.moves <= goal.maxMoves -> 2
                    else -> 1
                }
                repository.updateLevelProgress(state.currentLevelId, stars, gameBoard.score)
            }
        } else if (goal.maxMoves > 0 && gameBoard.moves >= goal.maxMoves && !gameBoard.canUndo()) {
            _uiState.value = _uiState.value.copy(isGameOver = true)
            audioEngine.playGameOverSound()
        }
    }

    fun selectPowerAbility(ability: PowerAbilityType) {
        val current = _uiState.value.activePowerAbility
        val newAbility = if (current == ability) PowerAbilityType.NONE else ability
        _uiState.value = _uiState.value.copy(activePowerAbility = newAbility)
    }

    fun handleTileClick(row: Int, col: Int) {
        val ability = _uiState.value.activePowerAbility
        if (ability == PowerAbilityType.NONE) return

        var success = false
        when (ability) {
            PowerAbilityType.SHATTER -> {
                success = gameBoard.shatterTile(row, col)
                if (success) showNotification("Tile Shattered!")
            }
            PowerAbilityType.DUPLICATE -> {
                success = gameBoard.duplicateTile(row, col)
                if (success) showNotification("Tile Duplicated!")
            }
            PowerAbilityType.TRANSMUTE -> {
                success = gameBoard.transmuteTile(row, col)
                if (success) showNotification("Tile Transmuted!")
            }
            PowerAbilityType.NONE -> {}
        }

        if (success) {
            audioEngine.playPowerSound()
            _uiState.value = _uiState.value.copy(activePowerAbility = PowerAbilityType.NONE)
            updateStateFromBoard()
            saveCurrentStats()
        } else {
            showNotification("Not enough energy or invalid target!")
        }
    }

    fun handleUndo() {
        if (gameBoard.undo()) {
            audioEngine.playPowerSound()
            showNotification("Move Reverted (-15⚡)")
            updateStateFromBoard()
        } else {
            showNotification("Need 15⚡ Energy to Undo!")
        }
    }

    fun activateIgnisAbility() {
        if (gameBoard.activateIgnisEmberArtifact()) {
            audioEngine.playForgeSound()
            showNotification("Ignis Ember Destroyed Low Tiles!")
            updateStateFromBoard()
        } else {
            showNotification("No 2 or 4 tiles to burn!")
        }
    }

    private fun updateStateFromBoard() {
        val isGameOver = gameBoard.isGameOver
        if (isGameOver && !_uiState.value.isGameOver) {
            audioEngine.playGameOverSound()
        }

        _uiState.value = _uiState.value.copy(
            score = gameBoard.score,
            energy = gameBoard.energy,
            moves = gameBoard.moves,
            highestTile = gameBoard.highestTileValue,
            hearts = gameBoard.hearts,
            maxHearts = gameBoard.maxHearts,
            isGameOver = isGameOver,
            hasAegisShield = gameBoard.hasAegisShield,
            midasTurnsLeft = gameBoard.midasTurnsLeft,
            lastSlideDirection = gameBoard.lastSlideDirection,
            lastSlideTimestamp = gameBoard.lastSlideTimestamp
        )
    }

    private fun saveCurrentStats() {
        viewModelScope.launch {
            val currentStats = gameStats.value ?: GameStatsEntity()
            val newHighScore = maxOf(currentStats.highScore, gameBoard.score)
            val newHighestTile = maxOf(currentStats.highestTile, gameBoard.highestTileValue)

            val isDaily = _uiState.value.gameMode == GameModeType.DAILY_QUEST
            val dateKey = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
            val newDailyScore = if (isDaily) maxOf(currentStats.dailyQuestHighScore, gameBoard.score) else currentStats.dailyQuestHighScore
            val lastDate = if (isDaily) dateKey else currentStats.lastDailyQuestDate

            val energyDelta = maxOf(0, gameBoard.energyCollectedCount - lastSavedSessionEnergy)
            val artifactDelta = maxOf(0, gameBoard.artifactsForgedCount - lastSavedSessionArtifacts)
            lastSavedSessionEnergy = gameBoard.energyCollectedCount
            lastSavedSessionArtifacts = gameBoard.artifactsForgedCount

            val totalEnergy = currentStats.totalEnergyCollected + energyDelta
            val totalArtifacts = currentStats.totalArtifactsForged + artifactDelta
            val maxCombo = maxOf(currentStats.maxComboChain, gameBoard.comboCount)

            repository.saveGameStats(
                currentStats.copy(
                    highScore = newHighScore,
                    dailyQuestHighScore = newDailyScore,
                    lastDailyQuestDate = lastDate,
                    highestTile = newHighestTile,
                    totalEnergyCollected = totalEnergy,
                    totalArtifactsForged = totalArtifacts,
                    maxComboChain = maxCombo,
                    activeThemeId = _uiState.value.activeTheme.id
                )
            )
        }
    }

    fun restartCurrentGame() {
        startNewGame(_uiState.value.gameMode, _uiState.value.currentLevelId)
    }

    fun dismissGameOver() {
        _uiState.value = _uiState.value.copy(isGameOver = false, isLevelWon = false)
    }

    fun setTheme(theme: ForgeThemeStyle) {
        _uiState.value = _uiState.value.copy(activeTheme = theme)
        saveCurrentStats()
    }

    fun toggleSoundFx() {
        val newSetting = !_uiState.value.soundFxEnabled
        _uiState.value = _uiState.value.copy(soundFxEnabled = newSetting)
        audioEngine.soundFxEnabled = newSetting
    }

    fun toggleImmersiveMode() {
        val newSetting = !_uiState.value.isImmersiveModeEnabled
        _uiState.value = _uiState.value.copy(
            isImmersiveModeEnabled = newSetting,
            notificationMessage = if (newSetting) "✨ Immersive Fullscreen Mode Enabled" else "Immersive Mode Disabled"
        )
    }

    fun toggleMusic() {
        val newSetting = !_uiState.value.musicEnabled
        _uiState.value = _uiState.value.copy(musicEnabled = newSetting)
        audioEngine.musicEnabled = newSetting
    }

    fun dismissAllDialogs() {
        _uiState.value = _uiState.value.copy(
            showCreditsDialog = false,
            showAchievementsDialog = false,
            showThemeDialog = false,
            showLevelSelectDialog = false,
            showBackupDialog = false
        )
    }

    fun showCredits(show: Boolean) {
        if (show) dismissAllDialogs()
        _uiState.value = _uiState.value.copy(showCreditsDialog = show)
    }

    fun showAchievements(show: Boolean) {
        if (show) dismissAllDialogs()
        _uiState.value = _uiState.value.copy(showAchievementsDialog = show)
    }

    fun showThemeSelector(show: Boolean) {
        if (show) dismissAllDialogs()
        _uiState.value = _uiState.value.copy(showThemeDialog = show)
    }

    fun showLevelSelector(show: Boolean) {
        if (show) dismissAllDialogs()
        _uiState.value = _uiState.value.copy(showLevelSelectDialog = show)
    }

    fun showLocalBackup(show: Boolean) {
        if (show) dismissAllDialogs()
        val lastTime = zipBackupManager.getLastBackupTimeFormatted()
        _uiState.value = _uiState.value.copy(
            showBackupDialog = show,
            lastBackupTime = lastTime,
            backupMessage = null
        )
    }

    fun exportZipBackup(uri: android.net.Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isBackupProcessing = true,
                backupMessage = "Exporting save to ZIP file..."
            )
            val stats = repository.getGameStatsDirect() ?: GameStatsEntity()
            val levels = repository.getAllLevelProgressDirect()
            val achievements = repository.getAllAchievementsDirect()

            val result = zipBackupManager.exportToZipUri(uri, stats, levels, achievements)
            if (result.isSuccess) {
                val newTime = zipBackupManager.getLastBackupTimeFormatted()
                _uiState.value = _uiState.value.copy(
                    isBackupProcessing = false,
                    lastBackupTime = newTime,
                    backupMessage = "✅ Save data exported to ZIP successfully!"
                )
                showNotification("ZIP Save Backup Exported Successfully!")
            } else {
                _uiState.value = _uiState.value.copy(
                    isBackupProcessing = false,
                    backupMessage = "❌ Export failed: " + result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun shareZipBackup(onReady: (android.net.Uri) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isBackupProcessing = true,
                backupMessage = "Preparing ZIP archive for sharing..."
            )
            val stats = repository.getGameStatsDirect() ?: GameStatsEntity()
            val levels = repository.getAllLevelProgressDirect()
            val achievements = repository.getAllAchievementsDirect()

            val result = zipBackupManager.createShareableZip(stats, levels, achievements)
            if (result.isSuccess) {
                val uri = result.getOrNull()
                val newTime = zipBackupManager.getLastBackupTimeFormatted()
                _uiState.value = _uiState.value.copy(
                    isBackupProcessing = false,
                    lastBackupTime = newTime,
                    backupMessage = "✅ ZIP backup ready to share!"
                )
                if (uri != null) {
                    onReady(uri)
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isBackupProcessing = false,
                    backupMessage = "❌ Failed to prepare ZIP: " + result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun importZipBackup(uri: android.net.Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isBackupProcessing = true,
                backupMessage = "Importing save data from ZIP..."
            )
            val result = zipBackupManager.importFromZipUri(uri)
            if (result.isSuccess) {
                val payload = result.getOrNull()
                if (payload != null) {
                    repository.restoreBackup(payload)
                    _uiState.value = _uiState.value.copy(
                        isBackupProcessing = false,
                        backupMessage = "✅ Save data imported from ZIP successfully!"
                    )
                    showNotification("Save Data Restored From ZIP!")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isBackupProcessing = false,
                        backupMessage = "❌ Invalid backup data inside ZIP."
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isBackupProcessing = false,
                    backupMessage = "❌ " + (result.exceptionOrNull()?.message ?: "Failed to import ZIP file.")
                )
            }
        }
    }

    fun quickBackupInternal() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isBackupProcessing = true,
                backupMessage = "Saving quick local backup..."
            )
            val stats = repository.getGameStatsDirect() ?: GameStatsEntity()
            val levels = repository.getAllLevelProgressDirect()
            val achievements = repository.getAllAchievementsDirect()

            val result = zipBackupManager.saveQuickInternalBackup(stats, levels, achievements)
            if (result.isSuccess) {
                val newTime = zipBackupManager.getLastBackupTimeFormatted()
                _uiState.value = _uiState.value.copy(
                    isBackupProcessing = false,
                    lastBackupTime = newTime,
                    backupMessage = "✅ Quick local save backup created!"
                )
                showNotification("Quick Backup Saved!")
            } else {
                _uiState.value = _uiState.value.copy(
                    isBackupProcessing = false,
                    backupMessage = "❌ Quick backup failed: " + result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun quickRestoreInternal() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isBackupProcessing = true,
                backupMessage = "Restoring quick local backup..."
            )
            val result = zipBackupManager.restoreQuickInternalBackup()
            if (result.isSuccess) {
                val payload = result.getOrNull()
                if (payload != null) {
                    repository.restoreBackup(payload)
                    _uiState.value = _uiState.value.copy(
                        isBackupProcessing = false,
                        backupMessage = "✅ Quick local save backup restored!"
                    )
                    showNotification("Quick Backup Restored!")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isBackupProcessing = false,
                        backupMessage = "❌ Corrupt quick backup."
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isBackupProcessing = false,
                    backupMessage = "❌ " + (result.exceptionOrNull()?.message ?: "No quick backup found.")
                )
            }
        }
    }

    private fun showNotification(msg: String) {
        _uiState.value = _uiState.value.copy(notificationMessage = msg)
    }

    fun clearNotification() {
        _uiState.value = _uiState.value.copy(notificationMessage = null)
    }
}
