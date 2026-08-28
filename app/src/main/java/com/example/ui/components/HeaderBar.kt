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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameModeType
import com.example.ui.theme.ForgeThemeStyle

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
    onNavigateBack: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleMusic: () -> Unit,
    onToggleImmersiveMode: () -> Unit = {},
    onOpenTheme: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenLevels: () -> Unit,
    onOpenBackup: () -> Unit,
    onOpenCredits: () -> Unit,
    modifier: Modifier = Modifier
) {
    val healthFraction = (hearts.toFloat() / maxHearts.toFloat()).coerceIn(0f, 1f)
    val animatedHealth by animateFloatAsState(targetValue = healthFraction, animationSpec = tween(400), label = "healthProgress")

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
                        contentDescription = "Back to Main Menu",
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
            // Current Score Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                theme.gridBgColor,
                                theme.gridBgColor.copy(alpha = 0.85f)
                            )
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                    .padding(vertical = scoreCardPaddingV, horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "CURRENT SCORE",
                        fontSize = if (compact) 8.5.sp else 10.sp,
                        color = Color.White.copy(alpha = 0.55f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = score.toString(),
                        fontSize = scoreFontSize,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Best High Score Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                theme.gridBgColor,
                                theme.gridBgColor.copy(alpha = 0.85f)
                            )
                        )
                    )
                    .border(1.dp, theme.primaryAccent.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .padding(vertical = scoreCardPaddingV, horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "BEST RECORD",
                        fontSize = if (compact) 8.5.sp else 10.sp,
                        color = theme.primaryAccent.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = highScore.toString(),
                        fontSize = scoreFontSize,
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Black
                    )
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
