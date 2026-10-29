package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.FantasyQuote
import com.example.game.FantasyQuotes
import com.example.game.GameModeType
import com.example.ui.components.AchievementsDialog
import com.example.ui.components.CreditsDialog
import com.example.ui.components.LevelSelectDialog
import com.example.ui.components.LocalBackupDialog
import com.example.ui.components.ThemeSelectorDialog
import com.example.ui.viewmodel.GameViewModel

/**
 * Main Menu / Home Screen for Tile Forge.
 * Features glowing emblem, high score, authentic inspirational quotes,
 * bold game mode cards, and bottom utility toolbar.
 */
@Composable
fun MainMenuScreen(
    viewModel: GameViewModel,
    onStartGame: () -> Unit,
    onStartTutorial: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gameStats by viewModel.gameStats.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val levelProgressList by viewModel.levelProgressList.collectAsState()
    val achievements by viewModel.achievements.collectAsState()

    val highScore = gameStats?.highScore ?: 0
    val unlockedLevelsCount = levelProgressList.count { it.starsEarned > 0 }

    var currentQuote by remember { mutableStateOf(FantasyQuotes.getRandomQuote()) }

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

    val hasAnyDialogOpen = uiState.showCreditsDialog || uiState.showAchievementsDialog ||
            uiState.showThemeDialog || uiState.showLevelSelectDialog || uiState.showBackupDialog

    BackHandler(enabled = hasAnyDialogOpen) {
        viewModel.dismissAllDialogs()
    }

    // Subtle pulsing animation for hero glow
    val infiniteTransition = rememberInfiniteTransition(label = "menuGlow")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF140D1F),
                        Color(0xFF0D0914),
                        Color(0xFF08060D)
                    )
                )
            )
            .windowInsetsPadding(if (uiState.isImmersiveModeEnabled) WindowInsets.displayCutout else WindowInsets.safeDrawing)
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 440.dp)
                .align(Alignment.Center)
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Controls Bar (Clean Sound, Music, Immersive Toggles)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1B1428),
                    modifier = Modifier.border(
                        1.dp,
                        Color.White.copy(alpha = 0.12f),
                        RoundedCornerShape(12.dp)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.toggleImmersiveMode() },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("main_immersive_toggle_btn")
                        ) {
                            Icon(
                                imageVector = if (uiState.isImmersiveModeEnabled) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                                contentDescription = if (uiState.isImmersiveModeEnabled) "Exit Fullscreen" else "Immersive Fullscreen",
                                tint = if (uiState.isImmersiveModeEnabled) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleSoundFx() },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("main_sound_toggle_btn")
                        ) {
                            Icon(
                                imageVector = if (uiState.soundFxEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                                contentDescription = "Toggle Sound",
                                tint = if (uiState.soundFxEnabled) Color(0xFF80D8FF) else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleMusic() },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("main_music_toggle_btn")
                        ) {
                            Icon(
                                imageVector = if (uiState.musicEnabled) Icons.Filled.MusicNote else Icons.Filled.MusicOff,
                                contentDescription = "Toggle Music",
                                tint = if (uiState.musicEnabled) Color(0xFFFF80AB) else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Center Branding & Title Area
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Classic Glowing Forge Emblem
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(68.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .scale(pulseGlow)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFF6D00).copy(alpha = 0.35f),
                                        Color(0xFF7C4DFF).copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF221733),
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .size(50.dp)
                            .border(
                                width = 1.5.dp,
                                brush = Brush.linearGradient(
                                    listOf(
                                        Color(0xFFFF9100),
                                        Color(0xFFFF3D00),
                                        Color(0xFF7C4DFF)
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0xFFFF6D00).copy(alpha = 0.25f),
                                            Color(0xFF1E142B)
                                        )
                                    )
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = "Tile Crucible",
                                tint = Color(0xFFFF9100),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "TILE CRUCIBLE",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Slide • Merge • Forge Relics",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // High score badge
                val animatedHighScore by animateIntAsState(
                    targetValue = highScore,
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                    label = "mainMenuHighScore"
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1B1426),
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = if (highScore > 0) Color(0xFFFFD700).copy(alpha = 0.40f) else Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (highScore > 0) Color(0xFFFFD700) else Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (highScore > 0) "BEST SCORE: $animatedHighScore" else "BEST SCORE: 0",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (highScore > 0) Color(0xFFFFE082) else Color.White.copy(alpha = 0.7f),
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Quotes Card (Words of the Forge / Fantasy & Anime Quotes)
            FantasyQuoteCard(
                quote = currentQuote,
                onRefreshQuote = { currentQuote = FantasyQuotes.getRandomQuote() }
            )

            // Primary Game Action Buttons (Play, Challenges, Daily)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Main "PLAY" Button (Endless Mode)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF261938),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            width = 1.5.dp,
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFFFF6D00), Color(0xFFFFAB00))
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            viewModel.startNewGame(GameModeType.ENDLESS)
                            onStartGame()
                        }
                        .testTag("start_endless_btn")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFFF6D00).copy(alpha = 0.35f),
                                        Color(0xFF261938)
                                    )
                                )
                            )
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFF6D00),
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.PlayArrow,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "PLAY FORGE",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Endless sliding puzzle",
                                        fontSize = 10.5.sp,
                                        color = Color.White.copy(alpha = 0.70f)
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFFFFAB00),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Challenge Stages Button
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF1E152E),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            width = 1.2.dp,
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFF7C4DFF), Color(0xFFB388FF))
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { viewModel.showLevelSelector(true) }
                        .testTag("open_levels_btn")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF7C4DFF).copy(alpha = 0.25f),
                                        Color(0xFF1E152E)
                                    )
                                )
                            )
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF7C4DFF),
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "CHALLENGE STAGES",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "10 stages • $unlockedLevelsCount cleared",
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.65f)
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFFB388FF),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Daily Quest Button
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF132238),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            width = 1.2.dp,
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFF00E5FF), Color(0xFFFFD700))
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            viewModel.startDailyQuest()
                            onStartGame()
                        }
                        .testTag("start_daily_quest_btn")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF00E5FF).copy(alpha = 0.22f),
                                        Color(0xFF132238)
                                    )
                                )
                            )
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF00B0FF),
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.CalendarMonth,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "DAILY QUEST",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Worldwide daily trial",
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.65f)
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Bottom Classic Toolbar (Trophies, Themes, Tutorial, Backup, Credits)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ClassicIconAction(
                    icon = Icons.Filled.EmojiEvents,
                    label = "Trophies",
                    tint = Color(0xFFFFD700),
                    testTag = "main_achievements_btn",
                    onClick = { viewModel.showAchievements(true) }
                )

                ClassicIconAction(
                    icon = Icons.Filled.Palette,
                    label = "Themes",
                    tint = Color(0xFF00E5FF),
                    testTag = "main_themes_btn",
                    onClick = { viewModel.showThemeSelector(true) }
                )

                ClassicIconAction(
                    icon = Icons.Filled.School,
                    label = "Tutorial",
                    tint = Color(0xFFFFB74D),
                    testTag = "start_tutorial_btn",
                    onClick = onStartTutorial
                )

                ClassicIconAction(
                    icon = Icons.Filled.FolderZip,
                    label = "Backup",
                    tint = Color(0xFFB388FF),
                    testTag = "main_local_backup_btn",
                    onClick = { viewModel.showLocalBackup(true) }
                )

                ClassicIconAction(
                    icon = Icons.Filled.Info,
                    label = "Credits",
                    tint = Color(0xFF81C784),
                    testTag = "main_credits_btn",
                    onClick = { viewModel.showCredits(true) }
                )
            }
        }

        // Modals & Dialogs
        if (uiState.showCreditsDialog) {
            CreditsDialog(
                onDismiss = { viewModel.showCredits(false) },
                onOpenTutorial = onStartTutorial
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
                    onStartGame()
                },
                onSelectLevel = { levelId ->
                    viewModel.startNewGame(GameModeType.CHALLENGE, levelId)
                    onStartGame()
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
                onExportZip = { exportZipLauncher.launch("TileCrucible_SaveBackup.zip") },
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
                            putExtra(Intent.EXTRA_SUBJECT, "Tile Crucible Save Backup (.ZIP)")
                            putExtra(Intent.EXTRA_TEXT, "Here is my Tile Crucible game save backup (.zip).")
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
    }
}

/**
 * Words of the Forge / Fantasy Quote Card on the Main Menu
 */
@Composable
private fun FantasyQuoteCard(
    quote: FantasyQuote,
    onRefreshQuote: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1E1528),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFFFD700).copy(alpha = 0.35f),
                        Color(0xFF7C4DFF).copy(alpha = 0.35f)
                    )
                ),
                RoundedCornerShape(12.dp)
            )
            .clickable { onRefreshQuote() }
            .testTag("fantasy_quote_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF2A1B3B).copy(alpha = 0.65f),
                            Color(0xFF160F22).copy(alpha = 0.85f)
                        )
                    )
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "WORDS OF THE FORGE • ${quote.loreTag.uppercase()}",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD700),
                        letterSpacing = 0.6.sp
                    )
                }

                IconButton(
                    onClick = onRefreshQuote,
                    modifier = Modifier
                        .size(22.dp)
                        .testTag("refresh_quote_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "New Quote",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "“${quote.quote}”",
                fontSize = 11.5.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.95f),
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "— ${quote.speaker}, ${quote.source}",
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00E5FF).copy(alpha = 0.90f),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ClassicIconAction(
    icon: ImageVector,
    label: String,
    tint: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Surface(
            shape = CircleShape,
            color = tint.copy(alpha = 0.14f),
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.70f)
        )
    }
}
