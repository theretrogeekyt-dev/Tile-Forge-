package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForgeThemeStyle

@Composable
fun EnergyMeterView(
    energy: Int,
    maxDisplayEnergy: Int = 100,
    hasAegisShield: Boolean,
    midasTurnsLeft: Int,
    theme: ForgeThemeStyle,
    onUndo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressFraction = (energy.toFloat() / maxDisplayEnergy.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progressFraction, label = "energyProgress")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Bolt,
                    contentDescription = "Energy",
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "ENERGY: $energy⚡",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }

            // Status Indicators (Shield / Midas)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (hasAegisShield) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF7C4DFF).copy(alpha = 0.35f))
                            .border(1.dp, Color(0xFF7C4DFF), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Shield, contentDescription = "Aegis Shield", tint = Color(0xFFFFD700), modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("SHIELD", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Black)
                        }
                    }
                }

                if (midasTurnsLeft > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFFD700).copy(alpha = 0.3f))
                            .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Stars, contentDescription = "Midas", tint = Color(0xFFFFD700), modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("MIDAS ($midasTurnsLeft)", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Energy Progress Bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(12.dp)
                    .clip(CircleShape)
                    .background(theme.gridBgColor)
                    .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.35f), CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF00B0FF),
                                    Color(0xFF00E5FF),
                                    Color.White
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Undo Button
            Button(
                onClick = onUndo,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (energy >= 15) theme.primaryAccent else Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier
                    .height(30.dp)
                    .testTag("undo_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Undo", tint = Color.White, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "UNDO 15⚡",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (energy >= 15) Color.White else Color.White.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}
