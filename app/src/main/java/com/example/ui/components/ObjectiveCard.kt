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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.LevelGoal
import com.example.ui.theme.ForgeThemeStyle

@Composable
fun ObjectiveCard(
    goal: LevelGoal?,
    currentMoves: Int,
    currentHighestTile: Int,
    theme: ForgeThemeStyle,
    modifier: Modifier = Modifier
) {
    if (goal == null) return

    val movesLeft = if (goal.maxMoves > 0) (goal.maxMoves - currentMoves).coerceAtLeast(0) else 999

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(theme.gridBgColor.copy(alpha = 0.9f))
            .border(1.dp, theme.secondaryAccent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag("objective_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Flag,
                    contentDescription = "Goal",
                    tint = theme.secondaryAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = goal.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = goal.description,
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }

            if (goal.maxMoves > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (movesLeft <= 5) Color(0xFFFF1744).copy(alpha = 0.25f) else Color(0xFFFFD54F).copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.HourglassTop,
                            contentDescription = "Moves Left",
                            tint = if (movesLeft <= 5) Color(0xFFFF5252) else Color(0xFFFFD54F),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$movesLeft MOVES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (movesLeft <= 5) Color(0xFFFF5252) else Color(0xFFFFD54F)
                        )
                    }
                }
            }
        }
    }
}
