package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.ArtifactType
import com.example.game.PowerType
import com.example.game.Tile
import com.example.game.TileType
import com.example.ui.theme.ForgeThemeStyle

@Composable
fun TileView(
    tile: Tile?,
    theme: ForgeThemeStyle,
    isTargeting: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEligibleTarget = isTargeting && tile != null && tile.type != TileType.ARTIFACT

    // Spawn & Merge animations
    val isMerged = tile?.mergedInTurn == true

    val scaleAnim by animateFloatAsState(
        targetValue = when {
            tile == null -> 0.94f
            isMerged -> 1.0f
            else -> 1.0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "tileScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val targetingPulse by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "targetingPulse"
    )

    val highTileGlow by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "highTileGlow"
    )

    if (tile == null) {
        Box(
            modifier = modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(theme.gridCellBgColor)
        )
        return
    }

    val tileBrush = getTileBackgroundBrush(tile, theme)
    val textColor = getTileTextColor(tile)

    val finalScale = when {
        isEligibleTarget -> scaleAnim * targetingPulse
        isMerged -> scaleAnim * 1.06f
        else -> scaleAnim
    }

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .scale(finalScale)
            .shadow(
                elevation = when {
                    tile.type == TileType.ARTIFACT -> 12.dp
                    tile.value >= 1024 -> 10.dp
                    tile.value >= 256 -> 6.dp
                    tile.value >= 32 -> 4.dp
                    else -> 2.dp
                },
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(tileBrush)
            .border(
                width = if (isEligibleTarget) 2.5.dp else if (tile.type == TileType.ARTIFACT || tile.value >= 1024 || isMerged) 2.dp else 1.dp,
                color = when {
                    isEligibleTarget -> Color(0xFFFFEB3B)
                    isMerged -> Color(0xFFFFD700)
                    tile.type == TileType.ARTIFACT -> Color(0xFFFFD700).copy(alpha = highTileGlow)
                    tile.value >= 2048 -> Color(0xFFFFD700).copy(alpha = highTileGlow)
                    tile.value >= 512 -> theme.primaryAccent.copy(alpha = 0.8f)
                    else -> Color.White.copy(alpha = 0.16f)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                enabled = isEligibleTarget,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(2.dp)
            .testTag("tile_${tile.row}_${tile.col}"),
        contentAlignment = Alignment.Center
    ) {
        val tileSize = maxWidth
        val iconSize = (tileSize * 0.36f).coerceIn(14.dp, 28.dp)
        val textMultiplier = (tileSize.value / 64f).coerceIn(0.7f, 1.4f)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            when (tile.type) {
                TileType.ENERGY_CRYSTAL -> {
                    Icon(
                        imageVector = Icons.Filled.Bolt,
                        contentDescription = "Energy Crystal",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(iconSize)
                    )
                    Text(
                        text = "+${tile.energyBonus}⚡",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = (13f * textMultiplier).sp
                    )
                }
                TileType.FORGE_ANVIL -> {
                    Icon(
                        imageVector = Icons.Filled.Build,
                        contentDescription = "Forge Anvil",
                        tint = Color(0xFFFF4081),
                        modifier = Modifier.size(iconSize)
                    )
                    Text(
                        text = "ANVIL",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = (11f * textMultiplier).sp,
                        letterSpacing = 1.sp
                    )
                }
                TileType.FORGE_FLAME -> {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = "Flame Tile",
                        tint = Color(0xFFFF3D00),
                        modifier = Modifier.size(iconSize)
                    )
                    Text(
                        text = tile.value.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = (16f * textMultiplier).sp
                    )
                }
                TileType.OBSTACLE -> {
                    Text(
                        text = "🪨",
                        fontSize = (22f * textMultiplier).sp
                    )
                    Text(
                        text = "SLAG",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = (9f * textMultiplier).sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                TileType.ARTIFACT -> {
                    val artifactIcon = when (tile.artifactType) {
                        ArtifactType.AEGIS_SHIELD -> Icons.Filled.Shield
                        ArtifactType.MIDAS_RING -> Icons.Filled.Star
                        ArtifactType.IGNIS_EMBER -> Icons.Filled.LocalFireDepartment
                        ArtifactType.CHRONOS_RELIC -> Icons.Filled.Bolt
                        null -> Icons.Filled.Shield
                    }
                    Icon(
                        imageVector = artifactIcon,
                        contentDescription = "Artifact",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(iconSize)
                    )
                    Text(
                        text = tile.displayLabel,
                        color = Color(0xFFFFE082),
                        fontWeight = FontWeight.Black,
                        fontSize = (9f * textMultiplier).sp,
                        textAlign = TextAlign.Center,
                        lineHeight = (11f * textMultiplier).sp
                    )
                }
                TileType.STANDARD -> {
                    val baseFontSize = when {
                        tile.value < 100 -> 22f
                        tile.value < 1000 -> 18f
                        tile.value < 10000 -> 14f
                        else -> 11f
                    }

                    if (tile.powerType != PowerType.NONE) {
                        Text(
                            text = when (tile.powerType) {
                                PowerType.FREEZE -> "❄️"
                                PowerType.BOMB -> "💣"
                                PowerType.DUPLICATOR -> "✨"
                                PowerType.MULTIPLIER_2X -> "2X"
                                else -> ""
                            },
                            fontSize = (10f * textMultiplier).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Yellow
                        )
                    }

                    Text(
                        text = tile.value.toString(),
                        color = textColor,
                        fontWeight = FontWeight.Black,
                        fontSize = (baseFontSize * textMultiplier).sp
                    )
                }
            }
        }
    }
}

fun getTileBackgroundBrush(tile: Tile, theme: ForgeThemeStyle): Brush {
    if (tile.type == TileType.ARTIFACT) {
        return Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFD700),
                Color(0xFFFF8F00),
                Color(0xFFE65100)
            )
        )
    }

    if (tile.type == TileType.ENERGY_CRYSTAL) {
        return Brush.linearGradient(
            colors = listOf(
                Color(0xFF00E5FF),
                Color(0xFF0091EA),
                Color(0xFF006064)
            )
        )
    }

    if (tile.type == TileType.FORGE_ANVIL) {
        return Brush.linearGradient(
            colors = listOf(
                Color(0xFFFF4081),
                Color(0xFFC2185B),
                Color(0xFF880E4F)
            )
        )
    }

    if (tile.type == TileType.FORGE_FLAME) {
        return Brush.linearGradient(
            colors = listOf(
                Color(0xFFFF9100),
                Color(0xFFFF3D00),
                Color(0xFFDD2C00)
            )
        )
    }

    if (tile.type == TileType.OBSTACLE) {
        return Brush.linearGradient(
            colors = listOf(
                Color(0xFF424242),
                Color(0xFF212121),
                Color(0xFF141414)
            )
        )
    }

    val baseColor = theme.tileColors[tile.value] ?: theme.primaryAccent
    return Brush.linearGradient(
        colors = listOf(
            baseColor.copy(alpha = 0.95f),
            baseColor,
            baseColor.copy(alpha = 0.85f)
        )
    )
}

fun getTileTextColor(tile: Tile): Color {
    if (tile.type != TileType.STANDARD) return Color.White
    return if (tile.value in listOf(2, 4)) Color(0xFF2D2438) else Color.White
}
