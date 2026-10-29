package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.LevelGoal
import com.example.ui.theme.ForgeThemeStyle
import kotlinx.coroutines.delay

@Composable
fun VictoryStageClearDialog(
    goal: LevelGoal,
    starsEarned: Int,
    finalScore: Int,
    movesTaken: Int,
    goldEarned: Int,
    xpEarned: Int,
    hasNextLevel: Boolean,
    theme: ForgeThemeStyle,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onReturnToMenu: () -> Unit
) {
    val star1Scale = remember { Animatable(0f) }
    val star2Scale = remember { Animatable(0f) }
    val star3Scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(200)
        star1Scale.animateTo(1.2f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        star1Scale.animateTo(1f, tween(100))

        if (starsEarned >= 2) {
            delay(150)
            star2Scale.animateTo(1.2f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            star2Scale.animateTo(1f, tween(100))
        }
        if (starsEarned >= 3) {
            delay(150)
            star3Scale.animateTo(1.2f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            star3Scale.animateTo(1f, tween(100))
        }
    }

    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("stage_clear_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF14131A)),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD700))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Ribbon
                Box(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFFFFB300), Color(0xFFFFD700), Color(0xFFFF8F00))),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "VICTORY! STAGE CLEARED",
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = goal.title,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3 Animated Stars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Star 1
                    Box(modifier = Modifier.scale(if (starsEarned >= 1) star1Scale.value else 1f)) {
                        Icon(
                            imageVector = if (starsEarned >= 1) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (starsEarned >= 1) Color(0xFFFFD700) else Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    // Star 2 (Middle, slightly elevated)
                    Box(
                        modifier = Modifier
                            .scale(if (starsEarned >= 2) star2Scale.value else 1f)
                            .padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (starsEarned >= 2) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (starsEarned >= 2) Color(0xFFFFD700) else Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    // Star 3
                    Box(modifier = Modifier.scale(if (starsEarned >= 3) star3Scale.value else 1f)) {
                        Icon(
                            imageVector = if (starsEarned >= 3) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (starsEarned >= 3) Color(0xFFFFD700) else Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats breakdown card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1E28))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Score Achieved", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                            Text("$finalScore pts", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Moves Used", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                            Text(
                                if (goal.maxMoves > 0) "$movesTaken / ${goal.maxMoves}" else "$movesTaken moves",
                                color = theme.accentColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.1f)))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Loot Claimed", color = Color(0xFFFFD700), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("+$goldEarned 🪙  +$xpEarned XP", color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                if (hasNextLevel) {
                    Button(
                        onClick = onNextLevel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("next_stage_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primaryColor)
                    ) {
                        Text("CONTINUE TO NEXT STAGE", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color.Black)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onReplay,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Replay", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onReturnToMenu,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Hub", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
