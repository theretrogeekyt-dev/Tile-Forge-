package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameModeType
import com.example.game.LevelGoal
import com.example.ui.theme.ForgeThemeStyle

data class ForgeMasteryRank(
    val title: String,
    val minScore: Int,
    val nextScore: Int,
    val icon: ImageVector,
    val color: Color
)

fun getMasteryRank(score: Int): ForgeMasteryRank {
    return when {
        score >= 25000 -> ForgeMasteryRank("Symbol of Peace • Plus Ultra", 25000, 50000, Icons.Filled.MilitaryTech, Color(0xFFFFD700))
        score >= 10000 -> ForgeMasteryRank("No. 1 Pro Hero • Arch-Forger", 10000, 25000, Icons.Filled.Stars, Color(0xFF00E676))
        score >= 4000 -> ForgeMasteryRank("Class 1-A Hero Elite", 4000, 10000, Icons.Filled.LocalFireDepartment, Color(0xFFFF9100))
        score >= 1500 -> ForgeMasteryRank("Full Cowling Artisan", 1500, 4000, Icons.Filled.Star, Color(0xFF00E5FF))
        score >= 500 -> ForgeMasteryRank("U.A. Hero Trainee", 500, 1500, Icons.Filled.Bolt, Color(0xFF64B5F6))
        else -> ForgeMasteryRank("Quirkless Apprentice Forger", 0, 500, Icons.Filled.AutoAwesome, Color(0xFFB0BEC5))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreTrackerDialog(
    score: Int,
    highScore: Int,
    gameMode: GameModeType,
    levelGoal: LevelGoal?,
    moves: Int,
    totalMerges: Int,
    comboStreak: Int,
    maxCombo: Int,
    highestSingleMoveScore: Int,
    totalScoreFromCombos: Int,
    highestTile: Int,
    theme: ForgeThemeStyle,
    onDismiss: () -> Unit
) {
    val animatedScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "dialogAnimatedScore"
    )

    val rank = getMasteryRank(score)
    val rankProgress = if (rank.nextScore > rank.minScore) {
        ((score - rank.minScore).toFloat() / (rank.nextScore - rank.minScore).toFloat()).coerceIn(0f, 1f)
    } else 1f

    val animatedRankProgress by animateFloatAsState(
        targetValue = rankProgress,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "rankProgressAnim"
    )

    val isNewRecord = score >= highScore && score > 0
    val pointsToNextRank = (rank.nextScore - score).coerceAtLeast(0)
    val avgScorePerMove = if (moves > 0) String.format("%.1f", score.toDouble() / moves) else "0.0"

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF181224),
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            theme.primaryAccent,
                            Color(0xFFFFD700).copy(alpha = 0.5f),
                            Color.White.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .testTag("score_tracker_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(theme.primaryAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Timeline,
                                contentDescription = "Score Tracker",
                                tint = theme.primaryAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SCORE TRACKER",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Live Run Telemetry & Mastery",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .testTag("score_tracker_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Current Score Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF261D36),
                                    Color(0xFF1E162B)
                                )
                            )
                        )
                        .border(1.dp, theme.primaryAccent.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isNewRecord) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFFFFD700), Color(0xFFFF9100))
                                        )
                                    )
                                    .padding(horizontal = 12.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = Color.Black, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "NEW PERSONAL RECORD!",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        Text(
                            text = "CURRENT RUN SCORE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = animatedScore.toString(),
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )

                        // High score comparison
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Personal Best: ",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "$highScore pts",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD700)
                            )
                            if (!isNewRecord && highScore > score) {
                                Text(
                                    text = " (${highScore - score} pts to beat)",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFF8A65)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mastery Tier Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF221930))
                        .border(1.dp, rank.color.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(rank.color.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = rank.icon,
                                        contentDescription = rank.title,
                                        tint = rank.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "MASTERY TIER",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.55f),
                                        letterSpacing = 0.8.sp
                                    )
                                    Text(
                                        text = rank.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = rank.color
                                    )
                                }
                            }

                            if (pointsToNextRank > 0) {
                                Text(
                                    text = "+$pointsToNextRank pts to next",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            } else {
                                Text(
                                    text = "MAX TIER REACHED!",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFFD700)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress Gauge to next rank
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedRankProgress)
                                    .height(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(rank.color, theme.primaryAccent)
                                        )
                                    )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Score Breakdown & Analytics Grid
                Text(
                    text = "RUN METRICS & EFFICIENCY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.6f),
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ScoreStatPill(
                            title = "Points / Move",
                            value = avgScorePerMove,
                            icon = Icons.Filled.Speed,
                            accentColor = Color(0xFF00E5FF),
                            modifier = Modifier.weight(1f)
                        )
                        ScoreStatPill(
                            title = "Best Single Turn",
                            value = highestSingleMoveScore.toString(),
                            icon = Icons.Filled.Star,
                            accentColor = Color(0xFFFFD700),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ScoreStatPill(
                            title = "Max Combo Streak",
                            value = "${maxCombo}x",
                            icon = Icons.Filled.LocalFireDepartment,
                            accentColor = Color(0xFFFF5722),
                            modifier = Modifier.weight(1f)
                        )
                        ScoreStatPill(
                            title = "Combo Bonus Pts",
                            value = "+$totalScoreFromCombos",
                            icon = Icons.Filled.Bolt,
                            accentColor = Color(0xFFE040FB),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ScoreStatPill(
                            title = "Total Moves",
                            value = moves.toString(),
                            icon = Icons.Filled.CheckCircle,
                            accentColor = Color(0xFF81C784),
                            modifier = Modifier.weight(1f)
                        )
                        ScoreStatPill(
                            title = "Total Merges",
                            value = totalMerges.toString(),
                            icon = Icons.Filled.AutoAwesome,
                            accentColor = Color(0xFFFFB74D),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // If in Challenge Mode: Show Goal Target Progress
                if (gameMode == GameModeType.CHALLENGE && levelGoal != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E1428))
                            .border(1.dp, Color(0xFFFF9100).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Flag, contentDescription = null, tint = Color(0xFFFF9100), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Level Objective Target",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "$score / ${levelGoal.targetScore} pts",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (score >= levelGoal.targetScore) Color(0xFF81C784) else Color(0xFFFF9100)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            val goalFraction = (score.toFloat() / levelGoal.targetScore.toFloat()).coerceIn(0f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(goalFraction)
                                        .height(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFF9100))
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("score_tracker_resume_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primaryAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "RESUME FORGING",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreStatPill(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF221A30))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(15.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.55f)
                )
                Text(
                    text = value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}
