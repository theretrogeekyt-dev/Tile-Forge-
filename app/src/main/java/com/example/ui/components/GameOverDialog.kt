package com.example.ui.components

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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.FantasyQuotes
import com.example.game.GameModeType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameOverDialog(
    isVictory: Boolean,
    score: Int,
    highestTile: Int,
    energyCollected: Int,
    gameMode: GameModeType = GameModeType.ENDLESS,
    onRestart: () -> Unit,
    onNextLevel: (() -> Unit)? = null,
    onBackToMenu: () -> Unit,
    onDismiss: () -> Unit
) {
    val quote = remember { FantasyQuotes.getRandomQuote() }

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF191322),
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    color = if (isVictory) Color(0xFFFFD700) else if (gameMode == GameModeType.DAILY_QUEST) Color(0xFF00E5FF) else Color(0xFFFF3D00).copy(alpha = 0.7f),
                    shape = RoundedCornerShape(24.dp)
                )
                .testTag("game_over_dialog")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header Icon & Title
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            if (isVictory) Color(0xFFFFD700).copy(alpha = 0.2f)
                            else if (gameMode == GameModeType.DAILY_QUEST) Color(0xFF00E5FF).copy(alpha = 0.2f)
                            else Color(0xFFFF3D00).copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isVictory || gameMode == GameModeType.DAILY_QUEST) Icons.Filled.EmojiEvents else Icons.Filled.SentimentDissatisfied,
                        contentDescription = null,
                        tint = if (isVictory) Color(0xFFFFD700) else if (gameMode == GameModeType.DAILY_QUEST) Color(0xFF00E5FF) else Color(0xFFFF5252),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = when {
                        isVictory -> "FORGE MASTERED! 🎉"
                        gameMode == GameModeType.DAILY_QUEST -> "DAILY QUEST RUN ENDED ⚡"
                        else -> "NO MORE MOVES ⚒️"
                    },
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    color = when {
                        isVictory -> Color(0xFFFFD700)
                        gameMode == GameModeType.DAILY_QUEST -> Color(0xFF00E5FF)
                        else -> Color(0xFFFF6E40)
                    },
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Dashboard Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF241B30))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatRow("Total Score", score.toString(), Color(0xFFFFD54F))
                        StatRow("Highest Tile", highestTile.toString(), Color(0xFFFF8A65))
                        StatRow("Energy Harvested", "$energyCollected⚡", Color(0xFF00E5FF))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fantasy Lore Quote Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF140D20))
                        .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.22f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "“${quote.quote}”",
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "— ${quote.speaker} (${quote.source})",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700).copy(alpha = 0.85f),
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onBackToMenu,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("menu_btn")
                    ) {
                        Icon(Icons.Filled.Home, contentDescription = "Menu", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Menu", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onRestart,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("play_again_btn")
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Retry", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Retry", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    if (isVictory && onNextLevel != null) {
                        Button(
                            onClick = onNextLevel,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("next_level_btn")
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Next", tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Next", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, valueColor: Color = Color.White) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.65f), fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Black, color = valueColor)
    }
}
