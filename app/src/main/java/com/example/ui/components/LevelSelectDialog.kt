package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.LevelProgressEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelectDialog(
    levelProgressList: List<LevelProgressEntity>,
    onSelectEndless: () -> Unit,
    onSelectLevel: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF191322),
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .border(1.5.dp, Color(0xFFFF6D00).copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .testTag("level_select_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with title and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GAME MODES",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFF6D00),
                        letterSpacing = 1.sp
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Endless Mode Hero Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF3E2723),
                                    Color(0xFF241B30)
                                )
                            )
                        )
                        .border(1.5.dp, Color(0xFFFF6D00), RoundedCornerShape(14.dp))
                        .clickable {
                            onSelectEndless()
                            onDismiss()
                        }
                        .padding(14.dp)
                        .testTag("endless_mode_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF6D00).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AllInclusive,
                                    contentDescription = "Endless",
                                    tint = Color(0xFFFF6D00),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("ENDLESS FORGE", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                                Text("Infinite merges & high-score chasing", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                            }
                        }

                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFFFF6D00),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "CHALLENGE LEVELS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Challenge Level Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(levelProgressList) { level ->
                        LevelTile(
                            level = level,
                            onClick = {
                                if (level.isUnlocked) {
                                    onSelectLevel(level.levelId)
                                    onDismiss()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelTile(
    level: LevelProgressEntity,
    onClick: () -> Unit
) {
    val bgColor = if (level.isUnlocked) Color(0xFF2C1E3A) else Color(0xFF140F1C)
    val borderColor = if (level.isUnlocked) Color(0xFFFFAB00).copy(alpha = 0.7f) else Color.White.copy(alpha = 0.08f)

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(enabled = level.isUnlocked) { onClick() }
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (level.isUnlocked) {
                Text(
                    text = "L${level.levelId}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                    for (i in 1..3) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (i <= level.starsEarned) Color(0xFFFFD700) else Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            } else {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Locked",
                    tint = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "L${level.levelId}",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.3f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
