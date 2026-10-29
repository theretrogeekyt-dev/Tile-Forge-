package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import com.example.game.GameModeType
import com.example.ui.theme.ForgeThemeStyle
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HeaderBar(
    score: Int,
    highScore: Int,
    gameMode: GameModeType,
    levelId: Int,
    hearts: Int = 3,
    maxHearts: Int = 3,
    theme: ForgeThemeStyle,
    soundFxEnabled: Boolean,
    musicEnabled: Boolean,
    isImmersiveModeEnabled: Boolean = true,
    compact: Boolean = false,
    lastScoreGained: Int = 0,
    lastScoreGainTimestamp: Long = 0L,
    comboStreak: Int = 1,
    onNavigateBack: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleMusic: () -> Unit,
    onToggleImmersiveMode: () -> Unit = {},
    onOpenScoreTracker: (() -> Unit)? = null,
    onOpenTheme: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenLevels: () -> Unit,
    onOpenBackup: () -> Unit,
    onOpenCredits: () -> Unit,
    onOpenTutorial: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val healthFraction = (hearts.toFloat() / maxHearts.toFloat()).coerceIn(0f, 1f)
    val animatedHealth by animateFloatAsState(targetValue = healthFraction, animationSpec = tween(400), label = "healthProgress")

    // Smooth rolling numerical counter for scores
    val animatedScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "scoreRoll"
    )
    val animatedHighScore by animateIntAsState(
        targetValue = highScore,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "highScoreRoll"
    )

    // Floating Score Delta Animation
    val floatYAnim = remember { Animatable(0f) }
    val floatAlphaAnim = remember { Animatable(0f) }
    var displayedDelta by remember { mutableStateOf(0) }
    var displayedCombo by remember { mutableStateOf(1) }

    LaunchedEffect(lastScoreGainTimestamp) {
        if (lastScoreGainTimestamp > 0L && lastScoreGained > 0) {
            displayedDelta = lastScoreGained
            displayedCombo = comboStreak
            floatYAnim.snapTo(0f)
            floatAlphaAnim.snapTo(1f)
            coroutineScope {
                launch {
                    floatYAnim.animateTo(
                        targetValue = -34f,
                        animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    delay(350)
                    floatAlphaAnim.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                    )
                }
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val recordGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "recordGlow"
    )

    val paddingV = if (compact) 3.dp else 6.dp
    val paddingH = if (compact) 10.dp else 14.dp
    val backBtnSize = if (compact) 34.dp else 42.dp
    val backIconSize = if (compact) 17.dp else 20.dp
    val actionBtnSize = if (compact) 28.dp else 34.dp
    val actionIconSize = if (compact) 14.dp else 16.dp
    val titleFontSize = if (compact) 15.sp else 18.sp
    val scoreCardPaddingV = if (compact) 4.dp else 8.dp
    val scoreFontSize = if (compact) 17.sp else 22.sp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = paddingH, vertical = paddingV)
    ) {
        // Top Navigation & Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(backBtnSize)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Home Screen",
                        tint = Color.White,
                        modifier = Modifier.size(backIconSize)
                    )
                }

                Spacer(modifier = Modifier.width(if (compact) 6.dp else 10.dp))

                Column {
                    Text(
                        text = "TILE FORGE",
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Black,
                        color = theme.primaryAccent,
                        letterSpacing = 1.2.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (gameMode) {
                                    GameModeType.DAILY_QUEST -> Color(0xFF00E5FF).copy(alpha = 0.25f)
                                    GameModeType.ENDLESS -> theme.secondaryAccent.copy(alpha = 0.2f)
                                    GameModeType.CHALLENGE -> Color(0xFFFF9100).copy(alpha = 0.22f)
                                }
                            )
                            .padding(horizontal = if (compact) 4.dp else 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = when (gameMode) {
                                GameModeType.DAILY_QUEST -> "⚡ DAILY QUEST"
                                GameModeType.ENDLESS -> "ENDLESS FORGE"
                                GameModeType.CHALLENGE -> "CHALLENGE L$levelId"
                            },
                            fontSize = if (compact) 8.5.sp else 10.sp,
                            fontWeight = FontWeight.Black,
                            color = when (gameMode) {
                                GameModeType.DAILY_QUEST -> Color(0xFF00E5FF)
                                GameModeType.ENDLESS -> theme.secondaryAccent
                                GameModeType.CHALLENGE -> Color(0xFFFFB74D)
                            }
                        )
                    }
                }
            }

            // Clean, beautifully styled Quick Action Bar
            Row(
                horizontalArrangement = Arrangement.spacedBy(if (compact) 2.dp else 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickActionButton(
                    icon = Icons.Filled.List,
                    description = "Levels",
                    tag = "levels_btn",
                    tint = Color.White,
                    btnSize = actionBtnSize,
                    iconSize = actionIconSize,
                    onClick = onOpenLevels
                )
                if (onOpenScoreTracker != null) {
                    QuickActionButton(
                        icon = Icons.Filled.Timeline,
                        description = "Score Tracker",
                        tag = "score_tracker_btn",
                        tint = Color(0xFF00E5FF),
                        btnSize = actionBtnSize,
                        iconSize = actionIconSize,
                        onClick = onOpenScoreTracker
                    )
                }
                QuickActionButton(
                    icon = Icons.Filled.Palette,
                    description = "Themes",
                    tag = "theme_btn",
                    tint = Color(0xFFFFB74D),
                    btnSize = actionBtnSize,
                    iconSize = actionIconSize,
                    onClick = onOpenTheme
                )
                QuickActionButton(
                    icon = Icons.Filled.EmojiEvents,
                    description = "Trophies",
                    tag = "achievements_btn",
                    tint = Color(0xFFFFD700),
                    btnSize = actionBtnSize,
                    iconSize = actionIconSize,
                    onClick = onOpenAchievements
                )
                QuickActionButton(
                    icon = Icons.Filled.FolderZip,
                    description = "ZIP Save Backup",
                    tag = "zip_backup_btn",
                    tint = Color(0xFFB388FF),
                    btnSize = actionBtnSize,
                    iconSize = actionIconSize,
                    onClick = onOpenBackup
                )
                QuickActionButton(
                    icon = if (isImmersiveModeEnabled) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                    description = if (isImmersiveModeEnabled) "Exit Fullscreen" else "Immersive Fullscreen",
                    tag = "immersive_btn",
                    tint = if (isImmersiveModeEnabled) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.7f),
                    btnSize = actionBtnSize,
                    iconSize = actionIconSize,
                    onClick = onToggleImmersiveMode
                )
                QuickActionButton(
                    icon = if (soundFxEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                    description = "Sound",
                    tag = "sound_btn",
                    tint = if (soundFxEnabled) Color(0xFF80D8FF) else Color.Gray,
                    btnSize = actionBtnSize,
                    iconSize = actionIconSize,
                    onClick = onToggleSound
                )
                QuickActionButton(
                    icon = Icons.Filled.Info,
                    description = "Credits",
                    tag = "credits_btn",
                    tint = Color.White.copy(alpha = 0.8f),
                    btnSize = actionBtnSize,
                    iconSize = actionIconSize,
                    onClick = onOpenCredits
                )
                if (onOpenTutorial != null) {
                    QuickActionButton(
                        icon = Icons.Filled.School,
                        description = "Tutorial",
                        tag = "header_tutorial_btn",
                        tint = Color(0xFFFFD700),
                        btnSize = actionBtnSize,
                        iconSize = actionIconSize,
                        onClick = onOpenTutorial
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(if (compact) 4.dp else 8.dp))

        // Health Bar & Hearts Row (Rescue Mechanic Display)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1C1324))
                .border(1.dp, Color(0xFFFF3366).copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = if (compact) 4.dp else 6.dp)
                .testTag("health_bar_container")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "HEALTH",
                        fontSize = if (compact) 9.sp else 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFF5277),
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.width(5.dp))

                    // 3 Interactive Hearts
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        for (i in 1..maxHearts) {
                            val isHeartActive = i <= hearts
                            Icon(
                                imageVector = if (isHeartActive) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Heart $i",
                                tint = if (isHeartActive) Color(0xFFFF2A6D) else Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(if (compact) 13.dp else 16.dp)
                            )
                        }
                    }
                }

                // Health Progress Gauge
                Box(
                    modifier = Modifier
                        .width(if (compact) 90.dp else 120.dp)
                        .height(if (compact) 6.dp else 8.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedHealth)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFFF0055),
                                        Color(0xFFFF5277),
                                        Color(0xFFFF85A1)
                                    )
                                )
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(if (compact) 4.dp else 8.dp))

        // Modern Score and High Score Dashboard Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 10.dp)
        ) {
            // Interactive Current Score Card (Tappable to launch Score Tracker)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                theme.gridBgColor,
                                theme.gridBgColor.copy(alpha = 0.85f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = if (onOpenScoreTracker != null) theme.primaryAccent.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable(enabled = onOpenScoreTracker != null) {
                        onOpenScoreTracker?.invoke()
                    }
                    .padding(vertical = scoreCardPaddingV, horizontal = 8.dp)
                    .testTag("score_tracker_card"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SCORE",
                            fontSize = if (compact) 8.5.sp else 10.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        if (onOpenScoreTracker != null) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(theme.primaryAccent.copy(alpha = 0.2f))
                                    .padding(horizontal = 4.dp, vertical = 0.5.dp)
                            ) {
                                Text(
                                    text = "TRACKER 📊",
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = theme.primaryAccent
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(1.dp))

                    Text(
                        text = animatedScore.toString(),
                        fontSize = scoreFontSize,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )

                    // Active combo badge
                    if (comboStreak > 1) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFF3D00).copy(alpha = 0.25f))
                                .border(0.5.dp, Color(0xFFFF9100), RoundedCornerShape(6.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "🔥 COMBO x$comboStreak (+${comboStreak * 25}%)",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFFAB40)
                            )
                        }
                    }
                }

                // Floating Score Gain Popup Badge
                if (floatAlphaAnim.value > 0.01f && displayedDelta > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = floatYAnim.value.dp)
                            .alpha(floatAlphaAnim.value)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFFFD700),
                                        Color(0xFFFF9100)
                                    )
                                )
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "+$displayedDelta" + if (displayedCombo > 1) " 🔥x$displayedCombo" else "",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }
            }

            // Best High Score Card
            val isRecordBeaten = score >= highScore && score > 0
            val scoreProgressFraction = if (highScore > 0) (score.toFloat() / highScore.toFloat()).coerceIn(0f, 1f) else 1f

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                theme.gridBgColor,
                                theme.gridBgColor.copy(alpha = 0.85f)
                            )
                        )
                    )
                    .border(
                        width = if (isRecordBeaten) 1.5.dp else 1.dp,
                        color = if (isRecordBeaten) Color(0xFFFFD700).copy(alpha = recordGlow) else theme.primaryAccent.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = scoreCardPaddingV, horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isRecordBeaten) "🏆 NEW RECORD!" else "BEST RECORD",
                        fontSize = if (compact) 8.5.sp else 10.sp,
                        color = if (isRecordBeaten) Color(0xFFFFD700) else theme.primaryAccent.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = animatedHighScore.toString(),
                        fontSize = scoreFontSize,
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Black
                    )

                    if (!isRecordBeaten && highScore > 0) {
                        Spacer(modifier = Modifier.height(3.dp))
                        // Mini record distance progress bar
                        Box(
                            modifier = Modifier
                                .width(if (compact) 55.dp else 75.dp)
                                .height(3.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(scoreProgressFraction)
                                    .height(3.dp)
                                    .clip(CircleShape)
                                    .background(theme.primaryAccent)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    description: String,
    tag: String,
    tint: Color,
    btnSize: androidx.compose.ui.unit.Dp = 34.dp,
    iconSize: androidx.compose.ui.unit.Dp = 16.dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(btnSize)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.07f))
            .clickable { onClick() }
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}
