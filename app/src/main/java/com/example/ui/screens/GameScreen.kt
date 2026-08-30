package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameModeType
import com.example.ui.components.AchievementsDialog
import com.example.ui.components.CreditsDialog
import com.example.ui.components.EnergyMeterView
import com.example.ui.components.GameBoardView
import com.example.ui.components.GameOverDialog
import com.example.ui.components.HeaderBar
import com.example.ui.components.LevelSelectDialog
import com.example.ui.components.LocalBackupDialog
import com.example.ui.components.ObjectiveCard
import com.example.ui.components.PowerUpActionBar
import com.example.ui.components.ThemeSelectorDialog
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.PowerAbilityType
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val gameStats by viewModel.gameStats.collectAsState()
    val levelProgressList by viewModel.levelProgressList.collectAsState()
    val achievements by viewModel.achievements.collectAsState()

    val highScore = maxOf(gameStats?.highScore ?: 0, uiState.score)

    val exportZipLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        if (uri != null) {
            viewModel.exportZipBackup(uri)
        }
    }

    val importZipLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.importZipBackup(uri)
        }
    }

    BackHandler {
        if (uiState.showCreditsDialog || uiState.showAchievementsDialog || uiState.showThemeDialog || uiState.showLevelSelectDialog || uiState.showBackupDialog) {
            viewModel.dismissAllDialogs()
        } else {
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState.notificationMessage) {
        if (uiState.notificationMessage != null) {
            delay(2000)
            viewModel.clearNotification()
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(uiState.activeTheme.backgroundColor)
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val isLandscapeTwoPane = screenWidth > screenHeight

        if (isLandscapeTwoPane) {
            // Adaptive Landscape Two-Pane Layout (Clean, streamlined, fits comfortably on screen)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(if (uiState.isImmersiveModeEnabled) WindowInsets.displayCutout else WindowInsets.safeDrawing)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Pane: Controls & Status Panel
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        HeaderBar(
                            score = uiState.score,
                            highScore = highScore,
                            gameMode = uiState.gameMode,
                            levelId = uiState.currentLevelId,
                            hearts = uiState.hearts,
                            maxHearts = uiState.maxHearts,
                            theme = uiState.activeTheme,
                            soundFxEnabled = uiState.soundFxEnabled,
                            musicEnabled = uiState.musicEnabled,
                            isImmersiveModeEnabled = uiState.isImmersiveModeEnabled,
                            compact = true,
                            onNavigateBack = onNavigateBack,
                            onToggleSound = { viewModel.toggleSoundFx() },
                            onToggleMusic = { viewModel.toggleMusic() },
                            onToggleImmersiveMode = { viewModel.toggleImmersiveMode() },
                            onOpenTheme = { viewModel.showThemeSelector(true) },
                            onOpenAchievements = { viewModel.showAchievements(true) },
                            onOpenLevels = { viewModel.showLevelSelector(true) },
                            onOpenBackup = { viewModel.showLocalBackup(true) },
                            onOpenCredits = { viewModel.showCredits(true) }
                        )

                        if (uiState.gameMode == GameModeType.CHALLENGE) {
                            ObjectiveCard(
                                goal = uiState.levelGoal,
                                currentMoves = uiState.moves,
                                currentHighestTile = uiState.highestTile,
                                theme = uiState.activeTheme
                            )
                        }

                        EnergyMeterView(
                            energy = uiState.energy,
                            hasAegisShield = uiState.hasAegisShield,
                            midasTurnsLeft = uiState.midasTurnsLeft,
                            theme = uiState.activeTheme,
                            onUndo = { viewModel.handleUndo() }
                        )
                    }

                    PowerUpActionBar(
                        energy = uiState.energy,
                        activeAbility = uiState.activePowerAbility,
                        theme = uiState.activeTheme,
                        onSelectAbility = { ability -> viewModel.selectPowerAbility(ability) },
                        onActivateIgnis = { viewModel.activateIgnisAbility() }
                    )
                }

                // Right Pane: Board
                Box(
                    modifier = Modifier
                        .weight(1.15f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    GameBoardView(
                        grid = viewModel.gameBoard.grid,
                        theme = uiState.activeTheme,
                        isTargeting = uiState.activePowerAbility != PowerAbilityType.NONE,
                        onSwipe = { direction -> viewModel.handleSwipe(direction) },
                        onTileClick = { r, c -> viewModel.handleTileClick(r, c) },
                        lastSlideDirection = uiState.lastSlideDirection,
                        lastSlideTimestamp = uiState.lastSlideTimestamp,
                        modifier = Modifier.fillMaxHeight(0.95f)
                    )
                }
            }
        } else {
            // Adaptive Portrait Mode (Phones, Foldables, Tablets)
            val isShortScreen = screenHeight < 680.dp
            val scrollModifier = if (isShortScreen) Modifier.verticalScroll(rememberScrollState()) else Modifier

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 520.dp)
                    .align(Alignment.Center)
                    .windowInsetsPadding(if (uiState.isImmersiveModeEnabled) WindowInsets.displayCutout else WindowInsets.safeDrawing)
                    .padding(horizontal = 6.dp, vertical = if (isShortScreen) 4.dp else 6.dp)
                    .then(scrollModifier),
                verticalArrangement = if (isShortScreen) Arrangement.Top else Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Header
                    HeaderBar(
                        score = uiState.score,
                        highScore = highScore,
                        gameMode = uiState.gameMode,
                        levelId = uiState.currentLevelId,
                        hearts = uiState.hearts,
                        maxHearts = uiState.maxHearts,
                        theme = uiState.activeTheme,
                        soundFxEnabled = uiState.soundFxEnabled,
                        musicEnabled = uiState.musicEnabled,
                        isImmersiveModeEnabled = uiState.isImmersiveModeEnabled,
                        onNavigateBack = onNavigateBack,
                        onToggleSound = { viewModel.toggleSoundFx() },
                        onToggleMusic = { viewModel.toggleMusic() },
                        onToggleImmersiveMode = { viewModel.toggleImmersiveMode() },
                        onOpenTheme = { viewModel.showThemeSelector(true) },
                        onOpenAchievements = { viewModel.showAchievements(true) },
                        onOpenLevels = { viewModel.showLevelSelector(true) },
                        onOpenBackup = { viewModel.showLocalBackup(true) },
                        onOpenCredits = { viewModel.showCredits(true) }
                    )

                    // Objective Card (if Challenge Mode)
                    if (uiState.gameMode == GameModeType.CHALLENGE) {
                        ObjectiveCard(
                            goal = uiState.levelGoal,
                            currentMoves = uiState.moves,
                            currentHighestTile = uiState.highestTile,
                            theme = uiState.activeTheme
                        )
                    }

                    // Energy Meter
                    EnergyMeterView(
                        energy = uiState.energy,
                        hasAegisShield = uiState.hasAegisShield,
                        midasTurnsLeft = uiState.midasTurnsLeft,
                        theme = uiState.activeTheme,
                        onUndo = { viewModel.handleUndo() }
                    )
                }

                // Game Board
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (isShortScreen) Modifier else Modifier.weight(1f, fill = false)),
                    contentAlignment = Alignment.Center
                ) {
                    GameBoardView(
                        grid = viewModel.gameBoard.grid,
                        theme = uiState.activeTheme,
                        isTargeting = uiState.activePowerAbility != PowerAbilityType.NONE,
                        onSwipe = { direction -> viewModel.handleSwipe(direction) },
                        onTileClick = { r, c -> viewModel.handleTileClick(r, c) },
                        lastSlideDirection = uiState.lastSlideDirection,
                        lastSlideTimestamp = uiState.lastSlideTimestamp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Power-up Action Bar
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PowerUpActionBar(
                        energy = uiState.energy,
                        activeAbility = uiState.activePowerAbility,
                        theme = uiState.activeTheme,
                        onSelectAbility = { ability -> viewModel.selectPowerAbility(ability) },
                        onActivateIgnis = { viewModel.activateIgnisAbility() }
                    )
                    Spacer(modifier = Modifier.height(if (isShortScreen) 12.dp else 6.dp))
                }
            }
        }

        // Notification Banner Toast
        AnimatedVisibility(
            visible = uiState.notificationMessage != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp)
        ) {
            uiState.notificationMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(uiState.activeTheme.primaryAccent)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = msg,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        // Modal Dialogs
        if (uiState.showCreditsDialog) {
            CreditsDialog(
                onDismiss = { viewModel.showCredits(false) }
            )
        }

        if (uiState.showAchievementsDialog) {
            AchievementsDialog(
                achievements = achievements,
                onDismiss = { viewModel.showAchievements(false) }
            )
        }

        if (uiState.showThemeDialog) {
            ThemeSelectorDialog(
                activeTheme = uiState.activeTheme,
                onSelectTheme = { theme -> viewModel.setTheme(theme) },
                onDismiss = { viewModel.showThemeSelector(false) }
            )
        }

        if (uiState.showLevelSelectDialog) {
            LevelSelectDialog(
                levelProgressList = levelProgressList,
                onSelectEndless = {
                    viewModel.startNewGame(GameModeType.ENDLESS)
                    viewModel.showLevelSelector(false)
                },
                onSelectLevel = { levelId ->
                    viewModel.startNewGame(GameModeType.CHALLENGE, levelId)
                    viewModel.showLevelSelector(false)
                },
                onDismiss = { viewModel.showLevelSelector(false) }
            )
        }

        if (uiState.showBackupDialog) {
            LocalBackupDialog(
                gameStats = gameStats,
                lastBackupTime = uiState.lastBackupTime,
                isProcessing = uiState.isBackupProcessing,
                statusMessage = uiState.backupMessage,
                onExportZip = { exportZipLauncher.launch("TileForge_SaveBackup.zip") },
                onImportZip = {
                    importZipLauncher.launch(
                        arrayOf(
                            "application/zip",
                            "application/x-zip-compressed",
                            "application/octet-stream",
                            "*/*"
                        )
                    )
                },
                onShareZip = {
                    viewModel.shareZipBackup { uri ->
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/zip"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            putExtra(Intent.EXTRA_SUBJECT, "TileForge Save Backup (.ZIP)")
                            putExtra(Intent.EXTRA_TEXT, "Here is my TileForge game save backup (.zip).")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Save File (.ZIP)"))
                    }
                },
                onQuickBackup = { viewModel.quickBackupInternal() },
                onQuickRestore = { viewModel.quickRestoreInternal() },
                onDismiss = { viewModel.showLocalBackup(false) }
            )
        }

        if (uiState.isGameOver || uiState.isLevelWon) {
            val isVictory = uiState.isLevelWon || (uiState.gameMode == GameModeType.CHALLENGE &&
                    uiState.levelGoal != null &&
                    (uiState.levelGoal!!.targetTileValue == 0 || uiState.highestTile >= uiState.levelGoal!!.targetTileValue) &&
                    uiState.score >= uiState.levelGoal!!.targetScore)

            GameOverDialog(
                isVictory = isVictory,
                score = uiState.score,
                highestTile = uiState.highestTile,
                energyCollected = uiState.energy,
                gameMode = uiState.gameMode,
                onRestart = { viewModel.restartCurrentGame() },
                onNextLevel = if (isVictory && uiState.currentLevelId < 10) {
                    { viewModel.startNewGame(GameModeType.CHALLENGE, uiState.currentLevelId + 1) }
                } else null,
                onBackToMenu = onNavigateBack,
                onDismiss = { viewModel.dismissGameOver() }
            )
        }
    }
}
