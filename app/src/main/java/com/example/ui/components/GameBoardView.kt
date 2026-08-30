package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.game.SwipeDirection
import com.example.game.Tile
import com.example.ui.theme.ForgeThemeStyle
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun GameBoardView(
    grid: Array<Array<Tile?>>,
    theme: ForgeThemeStyle,
    isTargeting: Boolean,
    onSwipe: (SwipeDirection) -> Unit,
    onTileClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    lastSlideDirection: SwipeDirection? = null,
    lastSlideTimestamp: Long = 0L
) {
    val nudgeOffsetX = remember { Animatable(0f) }
    val nudgeOffsetY = remember { Animatable(0f) }
    var activeDirectionVisual by remember { mutableStateOf<SwipeDirection?>(null) }
    var showDirectionIndicator by remember { mutableStateOf(false) }

    // Dynamic kinetic impulse & directional indicator on slide
    LaunchedEffect(lastSlideTimestamp) {
        if (lastSlideTimestamp > 0L && lastSlideDirection != null) {
            activeDirectionVisual = lastSlideDirection
            showDirectionIndicator = true

            val kickPx = 18f
            val targetX = when (lastSlideDirection) {
                SwipeDirection.LEFT -> -kickPx
                SwipeDirection.RIGHT -> kickPx
                else -> 0f
            }
            val targetY = when (lastSlideDirection) {
                SwipeDirection.UP -> -kickPx
                SwipeDirection.DOWN -> kickPx
                else -> 0f
            }

            coroutineScope {
                launch {
                    nudgeOffsetX.snapTo(targetX)
                    nudgeOffsetX.animateTo(
                        0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessHigh
                        )
                    )
                }
                launch {
                    nudgeOffsetY.snapTo(targetY)
                    nudgeOffsetY.animateTo(
                        0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessHigh
                        )
                    )
                }
                launch {
                    delay(300)
                    showDirectionIndicator = false
                }
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        val availableHeight = if (maxHeight != androidx.compose.ui.unit.Dp.Infinity && maxHeight > 0.dp) maxHeight else maxWidth
        val boardDim = minOf(maxWidth, availableHeight, 480.dp)
        val boardPadding = if (boardDim < 320.dp) 6.dp else 10.dp
        val cellSpacing = if (boardDim < 320.dp) 5.dp else 8.dp

        Box(
            modifier = Modifier
                .size(boardDim)
                .offset {
                    IntOffset(
                        nudgeOffsetX.value.roundToInt(),
                        nudgeOffsetY.value.roundToInt()
                    )
                }
                .shadow(16.dp, RoundedCornerShape(22.dp))
                .clip(RoundedCornerShape(22.dp))
                .background(theme.gridBgColor)
                .border(
                    width = if (isTargeting) 2.5.dp else 1.5.dp,
                    color = if (isTargeting) Color(0xFFFFD54F) else theme.primaryAccent.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(22.dp)
                )
                .pointerInput(isTargeting) {
                    if (!isTargeting) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val startPos = down.position
                            var swiped = false

                            while (!swiped) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                if (!change.pressed) break

                                val dx = change.position.x - startPos.x
                                val dy = change.position.y - startPos.y
                                val absDx = abs(dx)
                                val absDy = abs(dy)

                                val thresholdPx = 22.dp.toPx()
                                if (absDx > thresholdPx || absDy > thresholdPx) {
                                    swiped = true
                                    change.consume()
                                    if (absDx > absDy) {
                                        if (dx > 0) onSwipe(SwipeDirection.RIGHT) else onSwipe(SwipeDirection.LEFT)
                                    } else {
                                        if (dy > 0) onSwipe(SwipeDirection.DOWN) else onSwipe(SwipeDirection.UP)
                                    }
                                }
                            }
                        }
                    }
                }
                .padding(boardPadding)
                .testTag("game_board")
        ) {
            // Grid Cells & Tiles
            Column(
                verticalArrangement = Arrangement.spacedBy(cellSpacing),
                modifier = Modifier.fillMaxSize()
            ) {
                for (r in 0 until 4) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(cellSpacing),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        for (c in 0 until 4) {
                            val tile = grid[r][c]
                            TileView(
                                tile = tile,
                                theme = theme,
                                isTargeting = isTargeting,
                                onClick = { onTileClick(r, c) },
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }

            // Directional Movement Indicator Flash Banner
            AnimatedVisibility(
                visible = showDirectionIndicator && activeDirectionVisual != null,
                enter = fadeIn(animationSpec = tween(80, easing = LinearEasing)),
                exit = fadeOut(animationSpec = tween(220, easing = FastOutSlowInEasing)),
                modifier = Modifier.align(
                    when (activeDirectionVisual) {
                        SwipeDirection.UP -> Alignment.TopCenter
                        SwipeDirection.DOWN -> Alignment.BottomCenter
                        SwipeDirection.LEFT -> Alignment.CenterStart
                        SwipeDirection.RIGHT -> Alignment.CenterEnd
                        null -> Alignment.Center
                    }
                )
            ) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    theme.primaryAccent.copy(alpha = 0.85f),
                                    theme.primaryAccent.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when (activeDirectionVisual) {
                        SwipeDirection.UP -> Icons.Filled.ArrowUpward
                        SwipeDirection.DOWN -> Icons.Filled.ArrowDownward
                        SwipeDirection.LEFT -> Icons.AutoMirrored.Filled.ArrowBack
                        SwipeDirection.RIGHT -> Icons.AutoMirrored.Filled.ArrowForward
                        null -> Icons.Filled.ArrowUpward
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = "Sliding Direction",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
