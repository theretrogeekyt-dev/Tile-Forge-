package com.example.ui.components

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.db.LevelProgressEntity
import com.example.game.LevelGoal
import com.example.ui.theme.ForgeThemeStyle

@Composable
fun CampaignMapComponent(
    theme: ForgeThemeStyle,
    levelsProgress: List<LevelProgressEntity>,
    onSelectLevel: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedChapter by remember { mutableIntStateOf(1) }
    var selectedStageGoal by remember { mutableStateOf<LevelGoal?>(null) }

    val chapterLevels = remember(selectedChapter) {
        val startLevel = (selectedChapter - 1) * 5 + 1
        val endLevel = selectedChapter * 5
        (startLevel..endLevel).map { LevelGoal.getLevel(it) }
    }

    val totalStarsInChapter = remember(levelsProgress, selectedChapter) {
        val startLevel = (selectedChapter - 1) * 5 + 1
        val endLevel = selectedChapter * 5
        levelsProgress.filter { it.levelId in startLevel..endLevel }.sumOf { it.starsEarned }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Chapter Switcher Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Triple(1, "Ch. I", "Apprentice"),
                Triple(2, "Ch. II", "Elemental"),
                Triple(3, "Ch. III", "Plus Ultra")
            ).forEach { (chId, label, sub) ->
                val isSelected = selectedChapter == chId
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedChapter = chId },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) theme.primaryColor.copy(alpha = 0.22f) else theme.surfaceColor.copy(alpha = 0.6f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 1.5.dp else 0.5.dp,
                        color = if (isSelected) theme.primaryColor else theme.borderColor.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) theme.primaryColor else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = sub,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Chapter Header Info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = theme.surfaceColor.copy(alpha = 0.85f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, theme.borderColor.copy(alpha = 0.35f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = LevelGoal.getChapterName(selectedChapter),
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Clear stages to forge higher relics and unlock hero rank!",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFF261C02), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stars",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$totalStarsInChapter/15",
                        color = Color(0xFFFFD700),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Stage Node List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chapterLevels.size) { index ->
                val goal = chapterLevels[index]
                val progress = levelsProgress.find { it.levelId == goal.levelId }
                val isUnlocked = progress?.isUnlocked ?: (goal.levelId == 1)
                val stars = progress?.starsEarned ?: 0
                val highScore = progress?.highScore ?: 0

                StageNodeCard(
                    goal = goal,
                    isUnlocked = isUnlocked,
                    stars = stars,
                    highScore = highScore,
                    theme = theme,
                    onClick = {
                        if (isUnlocked) {
                            selectedStageGoal = goal
                        }
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Stage Briefing Modal
    selectedStageGoal?.let { goal ->
        val progress = levelsProgress.find { it.levelId == goal.levelId }
        val stars = progress?.starsEarned ?: 0
        val highScore = progress?.highScore ?: 0

        StageBriefingDialog(
            goal = goal,
            stars = stars,
            highScore = highScore,
            theme = theme,
            onDismiss = { selectedStageGoal = null },
            onStartStage = {
                selectedStageGoal = null
                onSelectLevel(goal.levelId)
            }
        )
    }
}

@Composable
private fun StageNodeCard(
    goal: LevelGoal,
    isUnlocked: Boolean,
    stars: Int,
    highScore: Int,
    theme: ForgeThemeStyle,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isUnlocked && stars == 0) 1.03f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "stageScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(if (isUnlocked && stars == 0) pulseScale else 1f)
            .clickable(enabled = isUnlocked) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                !isUnlocked -> Color(0xFF141416).copy(alpha = 0.7f)
                stars > 0 -> theme.surfaceColor.copy(alpha = 0.95f)
                else -> theme.surfaceColor
            }
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isUnlocked && stars == 0) 1.5.dp else 1.dp,
            color = when {
                !isUnlocked -> Color.White.copy(alpha = 0.1f)
                stars == 3 -> Color(0xFFFFD700).copy(alpha = 0.7f)
                isUnlocked && stars == 0 -> theme.primaryColor
                else -> theme.borderColor.copy(alpha = 0.4f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Level Badge circle
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .then(
                        if (!isUnlocked) {
                            Modifier.background(Color(0xFF222226))
                        } else if (stars == 3) {
                            Modifier.background(Brush.radialGradient(listOf(Color(0xFFFFD700), Color(0xFFB8860B))))
                        } else {
                            Modifier.background(Brush.radialGradient(listOf(theme.primaryColor, theme.primaryColor.copy(alpha = 0.6f))))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "${goal.levelId}",
                        color = if (stars == 3) Color.Black else Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Stage Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = goal.title,
                        color = if (isUnlocked) Color.White else Color.White.copy(alpha = 0.4f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = goal.description,
                    color = if (isUnlocked) Color.White.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.3f),
                    fontSize = 11.sp,
                    maxLines = 1
                )

                if (isUnlocked && highScore > 0) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Best: $highScore pts",
                        color = theme.accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Star Rating or Lock
            if (isUnlocked) {
                Column(horizontalAlignment = Alignment.End) {
                    Row {
                        (1..3).forEach { starIndex ->
                            Icon(
                                imageVector = if (starIndex <= stars) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (starIndex <= stars) Color(0xFFFFD700) else Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "+${goal.goldReward}🪙",
                            color = Color(0xFFFFD700),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Text(
                    text = "LOCKED",
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StageBriefingDialog(
    goal: LevelGoal,
    stars: Int,
    highScore: Int,
    theme: ForgeThemeStyle,
    onDismiss: () -> Unit,
    onStartStage: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16151B)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.primaryColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STAGE ${goal.levelId}",
                        color = theme.primaryColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                Text(
                    text = goal.title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Star Rating
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    (1..3).forEach { starIndex ->
                        Icon(
                            imageVector = if (starIndex <= stars) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (starIndex <= stars) Color(0xFFFFD700) else Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mission Brief Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF201F26))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "MISSION OBJECTIVE",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = goal.description,
                            color = Color.White,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = "Target: ${goal.targetScore} pts",
                                color = theme.accentColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (goal.maxMoves > 0) {
                                Text(
                                    text = "Moves: ${goal.maxMoves}",
                                    color = Color(0xFFFFB74D),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Rewards Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF281E09), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "First-Clear Rewards",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "+${goal.goldReward}🪙  +${goal.xpReward} XP",
                            color = Color(0xFFFFD700),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onStartStage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primaryColor)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "START STAGE",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
