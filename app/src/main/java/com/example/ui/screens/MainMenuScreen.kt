package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Bolt
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
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

@Composable
fun MainMenuScreen(
    viewModel: GameViewModel,
    onStartGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gameStats by viewModel.gameStats.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val levelProgressList by viewModel.levelProgressList.collectAsState()
    val achievements by viewModel.achievements.collectAsState()

    val highScore = gameStats?.highScore ?: 0
    val unlockedLevelsCount = levelProgressList.count { it.starsEarned > 0 }
    val unlockedTrophiesCount = achievements.count { it.isUnlocked }

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
        initialValue = 0.85f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    var currentQuote by remember { mutableStateOf(FantasyQuotes.getRandomQuote()) }

    BoxWithConstraints(
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
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val isLandscape = screenWidth > screenHeight

        if (isLandscape) {
            // Adaptive Landscape Layout (Clean, balanced 2-column layout that fits 100% on screen)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Brand, Status, Quick Bar & Lore Highlight
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    MainMenuTopQuickBar(
                        isImmersiveMode = uiState.isImmersiveModeEnabled,
                        soundFxEnabled = uiState.soundFxEnabled,
                        musicEnabled = uiState.musicEnabled,
                        onToggleImmersive = { viewModel.toggleImmersiveMode() },
                        onToggleSound = { viewModel.toggleSoundFx() },
                        onToggleMusic = { viewModel.toggleMusic() }
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ForgeHeroEmblem(pulseGlow = pulseGlow, sizeDp = 48)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "TILE FORGE",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "Slide • Merge • Forge Relics",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            HighScorePill(highScore = highScore)
                        }
                    }

                    FantasyQuoteCard(
                        quote = currentQuote,
                        compact = true,
                        onRefreshQuote = { currentQuote = FantasyQuotes.getRandomQuote() }
                    )
                }

                // Right Column: Play Modes & Single-Row Utility Bar
                Column(
                    modifier = Modifier
                        .weight(1.35f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MainMenuPlayModes(
                        unlockedLevelsCount = unlockedLevelsCount,
                        dailyQuestHighScore = gameStats?.dailyQuestHighScore ?: 0,
                        compact = true,
                        onStartDailyQuest = {
                            viewModel.startDailyQuest()
                            onStartGame()
                        },
                        onStartEndless = {
                            viewModel.startNewGame(GameModeType.ENDLESS)
                            onStartGame()
                        },
                        onOpenLevels = { viewModel.showLevelSelector(true) }
                    )

                    MainMenuUtilityGrid(
                        unlockedTrophiesCount = unlockedTrophiesCount,
                        activeThemeName = uiState.activeTheme.name,
                        compact = true,
                        singleRow = true,
                        onOpenAchievements = { viewModel.showAchievements(true) },
                        onOpenThemes = { viewModel.showThemeSelector(true) },
                        onOpenBackup = { viewModel.showLocalBackup(true) },
                        onOpenCredits = { viewModel.showCredits(true) }
                    )
                }
            }
        } else {
            // Adaptive Portrait Layout - Intentionally scaled to fit 100% inside screen without scrolling
            val isCompactScreen = screenHeight < 720.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 440.dp)
                    .align(Alignment.Center)
                    .padding(
                        horizontal = 16.dp,
                        vertical = if (isCompactScreen) 6.dp else 10.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Action & Branding Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MainMenuTopQuickBar(
                        isImmersiveMode = uiState.isImmersiveModeEnabled,
                        soundFxEnabled = uiState.soundFxEnabled,
                        musicEnabled = uiState.musicEnabled,
                        onToggleImmersive = { viewModel.toggleImmersiveMode() },
                        onToggleSound = { viewModel.toggleSoundFx() },
                        onToggleMusic = { viewModel.toggleMusic() }
                    )

                    Spacer(modifier = Modifier.height(if (isCompactScreen) 0.dp else 2.dp))

                    ForgeHeroEmblem(
                        pulseGlow = pulseGlow,
                        sizeDp = if (isCompactScreen) 54 else 64
                    )

                    Spacer(modifier = Modifier.height(if (isCompactScreen) 4.dp else 6.dp))

                    Text(
                        text = "TILE FORGE",
                        fontSize = if (isCompactScreen) 22.sp else 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )

                    Text(
                        text = "Slide • Merge • Gather Energy • Forge Relics",
                        fontSize = if (isCompactScreen) 10.5.sp else 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.65f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(if (isCompactScreen) 4.dp else 6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HighScorePill(highScore = highScore)
                    }

                    Spacer(modifier = Modifier.height(if (isCompactScreen) 6.dp else 8.dp))

                    FantasyQuoteCard(
                        quote = currentQuote,
                        compact = isCompactScreen,
                        onRefreshQuote = { currentQuote = FantasyQuotes.getRandomQuote() }
                    )
                }

                // Middle & Bottom: Primary Play Modes + Quick Utilities
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(if (isCompactScreen) 6.dp else 8.dp)
                ) {
                    MainMenuPlayModes(
                        unlockedLevelsCount = unlockedLevelsCount,
                        dailyQuestHighScore = gameStats?.dailyQuestHighScore ?: 0,
                        compact = isCompactScreen,
                        onStartDailyQuest = {
                            viewModel.startDailyQuest()
                            onStartGame()
                        },
                        onStartEndless = {
                            viewModel.startNewGame(GameModeType.ENDLESS)
                            onStartGame()
                        },
                        onOpenLevels = { viewModel.showLevelSelector(true) }
                    )

                    // Secondary Tools & Customization Grid
                    MainMenuUtilityGrid(
                        unlockedTrophiesCount = unlockedTrophiesCount,
                        activeThemeName = uiState.activeTheme.name,
                        compact = isCompactScreen,
                        onOpenAchievements = { viewModel.showAchievements(true) },
                        onOpenThemes = { viewModel.showThemeSelector(true) },
                        onOpenBackup = { viewModel.showLocalBackup(true) },
                        onOpenCredits = { viewModel.showCredits(true) }
                    )
                }
            }
        }

        // Modals & Dialogs
        if (uiState.showCreditsDialog) {
            CreditsDialog(onDismiss = { viewModel.showCredits(false) })
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
    }
}

/**
 * Modern, clean glowing emblem for Tile Forge
 */
@Composable
private fun ForgeHeroEmblem(pulseGlow: Float, sizeDp: Int = 64) {
    val totalSize = (sizeDp * 1.25f).dp
    val auraSize = (sizeDp * 1.125f).dp
    val emblemSize = sizeDp.dp
    val iconSize = (sizeDp * 0.56f).dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(totalSize)
    ) {
        // Glowing aura
        Box(
            modifier = Modifier
                .size(auraSize)
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

        // Center Tile Emblem
        Surface(
            shape = RoundedCornerShape((sizeDp * 0.3f).dp),
            color = Color(0xFF221733),
            shadowElevation = 6.dp,
            modifier = Modifier
                .size(emblemSize)
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0xFFFF9100),
                            Color(0xFFFF3D00),
                            Color(0xFF7C4DFF)
                        )
                    ),
                    shape = RoundedCornerShape((sizeDp * 0.3f).dp)
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
                    contentDescription = "Tile Forge Emblem",
                    tint = Color(0xFFFF9100),
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

/**
 * Sleek High Score Tag
 */
@Composable
private fun HighScorePill(highScore: Int) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1B1426),
        modifier = Modifier.border(
            width = 1.dp,
            color = if (highScore > 0) Color(0xFFFFD700).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.12f),
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
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = if (highScore > 0) "BEST SCORE: $highScore" else "BEST SCORE: 0",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (highScore > 0) Color(0xFFFFE082) else Color.White.copy(alpha = 0.7f),
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * Primary Game Mode Selection Cards
 */
@Composable
private fun MainMenuPlayModes(
    unlockedLevelsCount: Int,
    dailyQuestHighScore: Int,
    compact: Boolean = false,
    onStartDailyQuest: () -> Unit,
    onStartEndless: () -> Unit,
    onOpenLevels: () -> Unit
) {
    val cardPaddingV = if (compact) 8.dp else 12.dp
    val cardPaddingH = if (compact) 12.dp else 16.dp
    val iconBoxSize = if (compact) 34.dp else 40.dp
    val iconSize = if (compact) 18.dp else 22.dp
    val titleSize = if (compact) 14.5.sp else 16.sp
    val subtitleSize = if (compact) 10.5.sp else 11.5.sp
    val spacing = if (compact) 6.dp else 8.dp

    Column(
        verticalArrangement = Arrangement.spacedBy(spacing),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Daily Quest Mode Card (Highlighted with Cyan & Gold)
        Card(
            onClick = onStartDailyQuest,
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF132238)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.4.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color(0xFF00E5FF),
                            Color(0xFFFFD700)
                        )
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
                .testTag("start_daily_quest_btn")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF00E5FF).copy(alpha = 0.22f),
                                Color(0xFF132238).copy(alpha = 0.90f)
                            )
                        )
                    )
                    .padding(horizontal = cardPaddingH, vertical = cardPaddingV)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF00B0FF),
                            modifier = Modifier.size(iconBoxSize)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.CalendarMonth,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(iconSize)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Daily Quest",
                                    fontSize = titleSize,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFFFD700).copy(alpha = 0.25f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "SEED",
                                        fontSize = 7.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFFFD700)
                                    )
                                }
                            }
                            Text(
                                text = if (dailyQuestHighScore > 0) "Daily trial • Best: $dailyQuestHighScore" else "Fixed daily board • High score trial",
                                fontSize = subtitleSize,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Play Daily Quest",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Endless Mode Hero Card
        Card(
            onClick = onStartEndless,
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF261938)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.4.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color(0xFFFF6D00),
                            Color(0xFFFFAB00)
                        )
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
                .testTag("start_endless_btn")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFFF6D00).copy(alpha = 0.22f),
                                Color(0xFF261938).copy(alpha = 0.85f)
                            )
                        )
                    )
                    .padding(horizontal = cardPaddingH, vertical = cardPaddingV)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFF6D00),
                            modifier = Modifier.size(iconBoxSize)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.AllInclusive,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(iconSize)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Endless Mode",
                                fontSize = titleSize,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Slide, merge & forge high scores",
                                fontSize = subtitleSize,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Play Endless",
                        tint = Color(0xFFFFAB00),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Challenge Levels Card
        Card(
            onClick = onOpenLevels,
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E152E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.2.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color(0xFF7C4DFF),
                            Color(0xFFB388FF)
                        )
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
                .testTag("open_levels_btn")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF7C4DFF).copy(alpha = 0.20f),
                                Color(0xFF1E152E).copy(alpha = 0.85f)
                            )
                        )
                    )
                    .padding(horizontal = cardPaddingH, vertical = cardPaddingV)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF7C4DFF),
                            modifier = Modifier.size(iconBoxSize)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(iconSize)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Challenge Levels",
                                fontSize = titleSize,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "10 stages • $unlockedLevelsCount cleared",
                                fontSize = subtitleSize,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Levels",
                        tint = Color(0xFFB388FF),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Clean Minimalist Utility Grid or Single-Row Bar for Trophies, Themes, Save Backup & Credits
 */
@Composable
private fun MainMenuUtilityGrid(
    unlockedTrophiesCount: Int,
    activeThemeName: String,
    compact: Boolean = false,
    singleRow: Boolean = false,
    onOpenAchievements: () -> Unit,
    onOpenThemes: () -> Unit,
    onOpenBackup: () -> Unit,
    onOpenCredits: () -> Unit
) {
    val spacing = if (compact) 6.dp else 8.dp

    if (singleRow) {
        // High-efficiency single horizontal toolbar for Landscape mode
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing),
            modifier = Modifier.fillMaxWidth()
        ) {
            UtilityGridItem(
                icon = Icons.Filled.EmojiEvents,
                title = "Trophies",
                subtitle = "$unlockedTrophiesCount Unlocked",
                accentColor = Color(0xFFFFD700),
                compact = true,
                singleRow = true,
                testTag = "main_achievements_btn",
                onClick = onOpenAchievements,
                modifier = Modifier.weight(1f)
            )

            UtilityGridItem(
                icon = Icons.Filled.Palette,
                title = "Themes",
                subtitle = activeThemeName,
                accentColor = Color(0xFF00E5FF),
                compact = true,
                singleRow = true,
                testTag = "main_themes_btn",
                onClick = onOpenThemes,
                modifier = Modifier.weight(1f)
            )

            UtilityGridItem(
                icon = Icons.Filled.FolderZip,
                title = "Backup",
                subtitle = ".ZIP Export",
                accentColor = Color(0xFFB388FF),
                compact = true,
                singleRow = true,
                testTag = "main_local_backup_btn",
                onClick = onOpenBackup,
                modifier = Modifier.weight(1f)
            )

            UtilityGridItem(
                icon = Icons.Filled.Info,
                title = "Credits",
                subtitle = "Rules & Info",
                accentColor = Color(0xFF81C784),
                compact = true,
                singleRow = true,
                testTag = "main_credits_btn",
                onClick = onOpenCredits,
                modifier = Modifier.weight(1f)
            )
        }
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                UtilityGridItem(
                    icon = Icons.Filled.EmojiEvents,
                    title = "Trophies",
                    subtitle = "$unlockedTrophiesCount Unlocked",
                    accentColor = Color(0xFFFFD700),
                    compact = compact,
                    testTag = "main_achievements_btn",
                    onClick = onOpenAchievements,
                    modifier = Modifier.weight(1f)
                )

                UtilityGridItem(
                    icon = Icons.Filled.Palette,
                    title = "Themes",
                    subtitle = activeThemeName,
                    accentColor = Color(0xFF00E5FF),
                    compact = compact,
                    testTag = "main_themes_btn",
                    onClick = onOpenThemes,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                UtilityGridItem(
                    icon = Icons.Filled.FolderZip,
                    title = "Save Backup",
                    subtitle = ".ZIP Export",
                    accentColor = Color(0xFFB388FF),
                    compact = compact,
                    testTag = "main_local_backup_btn",
                    onClick = onOpenBackup,
                    modifier = Modifier.weight(1f)
                )

                UtilityGridItem(
                    icon = Icons.Filled.Info,
                    title = "Credits",
                    subtitle = "Rules & Info",
                    accentColor = Color(0xFF81C784),
                    compact = compact,
                    testTag = "main_credits_btn",
                    onClick = onOpenCredits,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun UtilityGridItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    testTag: String,
    compact: Boolean = false,
    singleRow: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val padV = if (singleRow) 6.dp else if (compact) 7.dp else 9.dp
    val padH = if (singleRow) 6.dp else if (compact) 9.dp else 11.dp
    val iconSurfaceSize = if (singleRow) 24.dp else if (compact) 26.dp else 30.dp
    val iconSize = if (singleRow) 13.dp else if (compact) 14.dp else 16.dp
    val titleSize = if (singleRow) 11.sp else if (compact) 12.sp else 13.sp
    val subtitleSize = if (singleRow) 9.sp else if (compact) 9.5.sp else 10.5.sp

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1B1428),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = 0.28f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (singleRow) Arrangement.Center else Arrangement.Start,
            modifier = Modifier.padding(horizontal = padH, vertical = padV)
        ) {
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.15f),
                modifier = Modifier.size(iconSurfaceSize)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }

            Spacer(modifier = Modifier.width(if (singleRow) 6.dp else 8.dp))

            Column {
                Text(
                    text = title,
                    fontSize = titleSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    fontSize = subtitleSize,
                    color = Color.White.copy(alpha = 0.55f),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun MainMenuTopQuickBar(
    isImmersiveMode: Boolean,
    soundFxEnabled: Boolean,
    musicEnabled: Boolean,
    onToggleImmersive: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleMusic: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
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
                androidx.compose.material3.IconButton(
                    onClick = onToggleImmersive,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("main_immersive_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (isImmersiveMode) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                        contentDescription = if (isImmersiveMode) "Exit Fullscreen" else "Immersive Fullscreen",
                        tint = if (isImmersiveMode) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                androidx.compose.material3.IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("main_sound_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (soundFxEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                        contentDescription = "Toggle Sound",
                        tint = if (soundFxEnabled) Color(0xFF80D8FF) else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                androidx.compose.material3.IconButton(
                    onClick = onToggleMusic,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("main_music_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (musicEnabled) Icons.Filled.MusicNote else Icons.Filled.MusicOff,
                        contentDescription = "Toggle Music",
                        tint = if (musicEnabled) Color(0xFFFF80AB) else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FantasyQuoteCard(
    quote: FantasyQuote,
    compact: Boolean = false,
    onRefreshQuote: () -> Unit
) {
    val padH = if (compact) 10.dp else 12.dp
    val padV = if (compact) 6.dp else 8.dp
    val quoteFontSize = if (compact) 10.5.sp else 11.sp
    val quoteLineHeight = if (compact) 14.sp else 15.sp
    val authorFontSize = if (compact) 9.sp else 9.5.sp

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF1E1528),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFFFD700).copy(alpha = 0.35f),
                        Color(0xFF7C4DFF).copy(alpha = 0.35f)
                    )
                ),
                RoundedCornerShape(14.dp)
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
                .padding(horizontal = padH, vertical = padV)
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
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "WORDS OF THE FORGE • ${quote.loreTag.uppercase()}",
                        fontSize = if (compact) 8.sp else 8.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD700),
                        letterSpacing = 0.6.sp
                    )
                }

                androidx.compose.material3.IconButton(
                    onClick = onRefreshQuote,
                    modifier = Modifier
                        .size(20.dp)
                        .testTag("refresh_quote_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "New Quote",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "“${quote.quote}”",
                fontSize = quoteFontSize,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.95f),
                lineHeight = quoteLineHeight
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "— ${quote.speaker}, ${quote.source}",
                fontSize = authorFontSize,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00E5FF).copy(alpha = 0.9f),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
