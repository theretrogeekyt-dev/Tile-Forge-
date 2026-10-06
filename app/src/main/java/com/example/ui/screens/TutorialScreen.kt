package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.ArtifactType
import com.example.game.GameBoard
import com.example.game.PowerType
import com.example.game.SwipeDirection
import com.example.game.Tile
import com.example.game.TileType
import com.example.ui.components.GameBoardView
import com.example.ui.theme.ForgeThemeStyle
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.PowerAbilityType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class TutorialStep(
    val stepIndex: Int,
    val badgeLabel: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color
) {
    SLIDING(
        stepIndex = 1,
        badgeLabel = "LESSON 1",
        title = "Sliding Fundamentals",
        subtitle = "Master moving tiles across the 4x4 forge grid",
        icon = Icons.AutoMirrored.Filled.ArrowForward,
        accentColor = Color(0xFF00E5FF)
    ),
    COMBINING(
        stepIndex = 2,
        badgeLabel = "LESSON 2",
        title = "Combining Numbers",
        subtitle = "Collide identical numbers together to forge double values",
        icon = Icons.Filled.AutoAwesome,
        accentColor = Color(0xFFFF9100)
    ),
    ENERGY(
        stepIndex = 3,
        badgeLabel = "LESSON 3",
        title = "Energy Crystals",
        subtitle = "Gather Energy (+⚡) to charge your mythical Forge Meter",
        icon = Icons.Filled.Bolt,
        accentColor = Color(0xFF00B0FF)
    ),
    FORGE_TILES(
        stepIndex = 4,
        badgeLabel = "LESSON 4",
        title = "Using Forge Tiles",
        subtitle = "Strike Anvils (⚒️) for relics & Flame tiles (🔥) for score bonuses",
        icon = Icons.Filled.Build,
        accentColor = Color(0xFFFF4081)
    ),
    POWER_TILES(
        stepIndex = 5,
        badgeLabel = "LESSON 5",
        title = "Power Tiles & Abilities",
        subtitle = "Detonate Bomb tiles (💣) and unleash the Shatter power",
        icon = Icons.Filled.DeleteForever,
        accentColor = Color(0xFFE040FB)
    )
}

@Composable
fun TutorialScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit,
    onFinishTutorial: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val theme = uiState.activeTheme
    val coroutineScope = rememberCoroutineScope()

    // Tutorial Board state
    val tutorialBoard = remember { GameBoard().apply { preventRandomSpawns = true } }
    var currentStep by remember { mutableStateOf(TutorialStep.SLIDING) }
    var stepSubPhase by remember { mutableIntStateOf(0) }
    var stepCompleted by remember { mutableStateOf(false) }
    var showGraduationDialog by remember { mutableStateOf(false) }
    var hintMessage by remember { mutableStateOf<String?>(null) }
    var activePowerAbility by remember { mutableStateOf(PowerAbilityType.NONE) }
    var completedStepsSet by remember { mutableStateOf(setOf<TutorialStep>()) }

    // Board trigger to force recomposition
    var boardRevision by remember { mutableIntStateOf(0) }

    BackHandler {
        if (showGraduationDialog) {
            showGraduationDialog = false
        } else {
            onNavigateBack()
        }
    }

    // Helper to initialize board for a specific step
    fun loadStepLayout(step: TutorialStep) {
        stepCompleted = false
        stepSubPhase = 0
        hintMessage = null
        activePowerAbility = PowerAbilityType.NONE

        when (step) {
            TutorialStep.SLIDING -> {
                tutorialBoard.setupCustomGrid(
                    listOf(
                        Tile(value = 2, row = 1, col = 0)
                    ),
                    startingEnergy = 0
                )
            }
            TutorialStep.COMBINING -> {
                tutorialBoard.setupCustomGrid(
                    listOf(
                        Tile(value = 2, row = 1, col = 0),
                        Tile(value = 2, row = 1, col = 2)
                    ),
                    startingEnergy = 0
                )
            }
            TutorialStep.ENERGY -> {
                tutorialBoard.setupCustomGrid(
                    listOf(
                        Tile(type = TileType.ENERGY_CRYSTAL, energyBonus = 25, row = 2, col = 1),
                        Tile(type = TileType.ENERGY_CRYSTAL, energyBonus = 25, row = 2, col = 3)
                    ),
                    startingEnergy = 0
                )
            }
            TutorialStep.FORGE_TILES -> {
                tutorialBoard.setupCustomGrid(
                    listOf(
                        Tile(type = TileType.FORGE_ANVIL, row = 1, col = 1),
                        Tile(value = 8, row = 1, col = 3),
                        Tile(type = TileType.FORGE_FLAME, value = 8, row = 2, col = 1),
                        Tile(value = 8, row = 2, col = 3)
                    ),
                    startingEnergy = 20
                )
            }
            TutorialStep.POWER_TILES -> {
                tutorialBoard.setupCustomGrid(
                    listOf(
                        Tile(value = 4, powerType = PowerType.BOMB, row = 1, col = 1),
                        Tile(value = 4, row = 1, col = 2),
                        Tile(type = TileType.OBSTACLE, row = 2, col = 2),
                        Tile(type = TileType.OBSTACLE, row = 0, col = 2)
                    ),
                    startingEnergy = 40
                )
            }
        }
        boardRevision++
    }

    // Load step on change
    LaunchedEffect(currentStep) {
        loadStepLayout(currentStep)
    }

    // Clear hint after delay
    LaunchedEffect(hintMessage) {
        if (hintMessage != null) {
            delay(2800)
            hintMessage = null
        }
    }

    // Swipe Handler for Tutorial
    fun onTutorialSwipe(direction: SwipeDirection) {
        if (stepCompleted) return

        if (direction != SwipeDirection.RIGHT && currentStep != TutorialStep.POWER_TILES) {
            hintMessage = "👉 Hint: Try swiping RIGHT to guide the tiles to merge!"
            viewModel.audioEngine.playSlideSound()
            tutorialBoard.slide(direction)
            boardRevision++
            return
        }

        when (currentStep) {
            TutorialStep.SLIDING -> {
                val result = tutorialBoard.slide(direction)
                boardRevision++
                if (result.moved) {
                    viewModel.audioEngine.playSlideSound()
                    stepCompleted = true
                    completedStepsSet = completedStepsSet + TutorialStep.SLIDING
                }
            }

            TutorialStep.COMBINING -> {
                val result = tutorialBoard.slide(direction)
                boardRevision++
                if (result.mergesCount > 0) {
                    viewModel.audioEngine.playMergeSound(4)
                    if (stepSubPhase == 0) {
                        // Sub-phase 1: Spawns another 4 to combine into 8
                        stepSubPhase = 1
                        tutorialBoard.setTileAt(1, 0, Tile(value = 4, row = 1, col = 0))
                        boardRevision++
                        hintMessage = "Awesome! Now swipe RIGHT again to combine (4 + 4 = 8)!"
                    } else {
                        viewModel.audioEngine.playMergeSound(8)
                        stepCompleted = true
                        completedStepsSet = completedStepsSet + TutorialStep.COMBINING
                    }
                }
            }

            TutorialStep.ENERGY -> {
                val result = tutorialBoard.slide(direction)
                boardRevision++
                if (result.energyGained > 0) {
                    viewModel.audioEngine.playEnergySound()
                    stepCompleted = true
                    completedStepsSet = completedStepsSet + TutorialStep.ENERGY
                }
            }

            TutorialStep.FORGE_TILES -> {
                val result = tutorialBoard.slide(direction)
                boardRevision++
                if (result.mergesCount > 0 || result.artifactForged != null) {
                    viewModel.audioEngine.playForgeSound()
                    stepCompleted = true
                    completedStepsSet = completedStepsSet + TutorialStep.FORGE_TILES
                }
            }

            TutorialStep.POWER_TILES -> {
                if (stepSubPhase == 0) {
                    val result = tutorialBoard.slide(direction)
                    boardRevision++
                    if (result.mergesCount > 0) {
                        viewModel.audioEngine.playPowerSound()
                        stepSubPhase = 1
                        // Spawn a slag tile for Shatter practice
                        tutorialBoard.setTileAt(3, 1, Tile(type = TileType.OBSTACLE, row = 3, col = 1))
                        tutorialBoard.addEnergy(20)
                        boardRevision++
                        hintMessage = "💥 Bomb detonated! Now tap 'Shatter' below, then tap the remaining rock (🪨)!"
                    }
                } else {
                    tutorialBoard.slide(direction)
                    boardRevision++
                }
            }
        }
    }

    // Tile Click Handler (for Shatter)
    fun onTutorialTileClick(r: Int, c: Int) {
        if (currentStep == TutorialStep.POWER_TILES && stepSubPhase == 1 && activePowerAbility == PowerAbilityType.SHATTER) {
            val tile = tutorialBoard.grid[r][c]
            if (tile != null) {
                val success = tutorialBoard.shatterTile(r, c)
                if (success) {
                    viewModel.audioEngine.playPowerSound()
                    activePowerAbility = PowerAbilityType.NONE
                    stepCompleted = true
                    completedStepsSet = completedStepsSet + TutorialStep.POWER_TILES
                    boardRevision++
                }
            }
        }
    }

    // Pulse animation for guided swipe cues
    val infiniteTransition = rememberInfiniteTransition(label = "tutorialHandPulse")
    val handNudge by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "handNudge"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF130D1E),
                        Color(0xFF0C0814),
                        Color(0xFF07050A)
                    )
                )
            )
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val isLandscape = screenWidth > screenHeight

        if (isLandscape) {
            // Adaptive Landscape Two-Column Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(if (uiState.isImmersiveModeEnabled) WindowInsets.displayCutout else WindowInsets.safeDrawing)
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Instructions, Step Navigator & Energy/Ability Bar
                Column(
                    modifier = Modifier
                        .weight(1.05f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        TutorialTopBar(
                            currentStep = currentStep,
                            onNavigateBack = onNavigateBack,
                            onResetStep = { loadStepLayout(currentStep) }
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        TutorialStepSelector(
                            currentStep = currentStep,
                            completedSteps = completedStepsSet,
                            onSelectStep = { step -> currentStep = step }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TutorialInstructionCard(
                            step = currentStep,
                            subPhase = stepSubPhase,
                            stepCompleted = stepCompleted,
                            hintMessage = hintMessage
                        )
                    }

                    // Energy & Power Bar if relevant
                    TutorialEnergyAndPowerBar(
                        step = currentStep,
                        subPhase = stepSubPhase,
                        energy = tutorialBoard.energy,
                        activeAbility = activePowerAbility,
                        onSelectShatter = {
                            activePowerAbility = if (activePowerAbility == PowerAbilityType.SHATTER) PowerAbilityType.NONE else PowerAbilityType.SHATTER
                        }
                    )
                }

                // Right Column: Interactive Board with Swipe Cues
                Box(
                    modifier = Modifier
                        .weight(1.15f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    TutorialBoardContainer(
                        grid = tutorialBoard.grid,
                        theme = theme,
                        isTargeting = activePowerAbility != PowerAbilityType.NONE,
                        handNudge = handNudge,
                        stepCompleted = stepCompleted,
                        onSwipe = { dir -> onTutorialSwipe(dir) },
                        onTileClick = { r, c -> onTutorialTileClick(r, c) },
                        lastSlideDirection = tutorialBoard.lastSlideDirection,
                        lastSlideTimestamp = tutorialBoard.lastSlideTimestamp
                    )
                }
            }
        } else {
            // Adaptive Portrait Layout
            val isShortScreen = screenHeight < 720.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 480.dp)
                    .align(Alignment.Center)
                    .windowInsetsPadding(if (uiState.isImmersiveModeEnabled) WindowInsets.displayCutout else WindowInsets.safeDrawing)
                    .padding(horizontal = 10.dp, vertical = if (isShortScreen) 6.dp else 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top: Header & Step Progress
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TutorialTopBar(
                        currentStep = currentStep,
                        onNavigateBack = onNavigateBack,
                        onResetStep = { loadStepLayout(currentStep) }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    TutorialStepSelector(
                        currentStep = currentStep,
                        completedSteps = completedStepsSet,
                        onSelectStep = { step -> currentStep = step }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    TutorialInstructionCard(
                        step = currentStep,
                        subPhase = stepSubPhase,
                        stepCompleted = stepCompleted,
                        hintMessage = hintMessage
                    )
                }

                // Middle: Interactive Game Board
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    contentAlignment = Alignment.Center
                ) {
                    TutorialBoardContainer(
                        grid = tutorialBoard.grid,
                        theme = theme,
                        isTargeting = activePowerAbility != PowerAbilityType.NONE,
                        handNudge = handNudge,
                        stepCompleted = stepCompleted,
                        onSwipe = { dir -> onTutorialSwipe(dir) },
                        onTileClick = { r, c -> onTutorialTileClick(r, c) },
                        lastSlideDirection = tutorialBoard.lastSlideDirection,
                        lastSlideTimestamp = tutorialBoard.lastSlideTimestamp
                    )
                }

                // Bottom: Power Abilities (if Step 5) & Energy Indicator
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TutorialEnergyAndPowerBar(
                        step = currentStep,
                        subPhase = stepSubPhase,
                        energy = tutorialBoard.energy,
                        activeAbility = activePowerAbility,
                        onSelectShatter = {
                            activePowerAbility = if (activePowerAbility == PowerAbilityType.SHATTER) PowerAbilityType.NONE else PowerAbilityType.SHATTER
                        }
                    )

                    Spacer(modifier = Modifier.height(if (isShortScreen) 2.dp else 6.dp))
                }
            }
        }

        // Positive Reinforcement Modal Card on Step Completion
        AnimatedVisibility(
            visible = stepCompleted,
            enter = fadeIn(tween(180)) + scaleIn(tween(240)),
            exit = fadeOut(tween(160)) + scaleOut(tween(200)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            TutorialSuccessModal(
                step = currentStep,
                isLastStep = currentStep == TutorialStep.POWER_TILES,
                onContinue = {
                    val nextStepIndex = currentStep.stepIndex + 1
                    val nextStep = TutorialStep.values().find { it.stepIndex == nextStepIndex }
                    if (nextStep != null) {
                        currentStep = nextStep
                    } else {
                        // Complete tutorial
                        viewModel.completeTutorial()
                        viewModel.audioEngine.playForgeSound()
                        showGraduationDialog = true
                    }
                },
                onReplayStep = { loadStepLayout(currentStep) }
            )
        }

        // Final Academy Graduation Celebration Dialog
        if (showGraduationDialog) {
            TutorialGraduationDialog(
                onStartEndless = {
                    showGraduationDialog = false
                    onFinishTutorial()
                },
                onReplayTutorial = {
                    showGraduationDialog = false
                    currentStep = TutorialStep.SLIDING
                },
                onDismiss = {
                    showGraduationDialog = false
                    onNavigateBack()
                }
            )
        }
    }
}

/**
 * Header bar for the tutorial with title, step counter, reset & back buttons
 */
@Composable
private fun TutorialTopBar(
    currentStep: TutorialStep,
    onNavigateBack: () -> Unit,
    onResetStep: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .testTag("tutorial_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Exit Tutorial",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.School,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "FORGE ACADEMY",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "Lesson ${currentStep.stepIndex} of 5 • ${currentStep.title}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.65f)
                )
            }
        }

        // Reset step button to retry anytime
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF1E1428),
            modifier = Modifier
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                .clickable { onResetStep() }
                .testTag("tutorial_reset_step_btn")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Restart Lesson",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "RETRY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Step navigation pill row allowing instant jump or review of any lesson
 */
@Composable
private fun TutorialStepSelector(
    currentStep: TutorialStep,
    completedSteps: Set<TutorialStep>,
    onSelectStep: (TutorialStep) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        TutorialStep.values().forEach { step ->
            val isCurrent = step == currentStep
            val isDone = completedSteps.contains(step)

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when {
                    isCurrent -> step.accentColor.copy(alpha = 0.25f)
                    isDone -> Color(0xFF233825)
                    else -> Color(0xFF1B1428)
                },
                modifier = Modifier
                    .border(
                        width = if (isCurrent) 1.5.dp else 1.dp,
                        color = when {
                            isCurrent -> step.accentColor
                            isDone -> Color(0xFF4CAF50).copy(alpha = 0.6f)
                            else -> Color.White.copy(alpha = 0.12f)
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelectStep(step) }
                    .testTag("tutorial_step_tab_${step.stepIndex}")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    if (isDone) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Completed",
                            tint = Color(0xFF81C784),
                            modifier = Modifier.size(12.dp)
                        )
                    } else {
                        Text(
                            text = "${step.stepIndex}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isCurrent) step.accentColor else Color.White.copy(alpha = 0.5f)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when (step) {
                            TutorialStep.SLIDING -> "Slide"
                            TutorialStep.COMBINING -> "Merge"
                            TutorialStep.ENERGY -> "Energy"
                            TutorialStep.FORGE_TILES -> "Forge"
                            TutorialStep.POWER_TILES -> "Powers"
                        },
                        fontSize = 10.5.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        color = if (isCurrent) Color.White else Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

/**
 * Floating objective & step instruction card
 */
@Composable
private fun TutorialInstructionCard(
    step: TutorialStep,
    subPhase: Int,
    stepCompleted: Boolean,
    hintMessage: String?
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E142B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.2.dp,
                Brush.horizontalGradient(
                    listOf(
                        step.accentColor,
                        step.accentColor.copy(alpha = 0.4f)
                    )
                ),
                RoundedCornerShape(14.dp)
            )
            .testTag("tutorial_instruction_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            step.accentColor.copy(alpha = 0.15f),
                            Color(0xFF1E142B)
                        )
                    )
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(step.accentColor.copy(alpha = 0.25f))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = step.badgeLabel,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            color = step.accentColor,
                            letterSpacing = 0.8.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = step.title,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (stepCompleted) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF4CAF50).copy(alpha = 0.25f))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "COMPLETED ✓",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF81C784)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            // Objective text
            val objectiveText = when (step) {
                TutorialStep.SLIDING -> "👉 Swipe RIGHT to slide the tile across the forge grid!"
                TutorialStep.COMBINING -> if (subPhase == 0) "👉 Swipe RIGHT to slam matching numbers together (2 + 2 = 4)!" else "👉 Swipe RIGHT again to combine 4 + 4 = 8!"
                TutorialStep.ENERGY -> "👉 Swipe RIGHT to merge Energy Crystals (+⚡) & charge your meter!"
                TutorialStep.FORGE_TILES -> "👉 Swipe RIGHT to strike the Anvil (⚒️) into the tile and forge an Artifact!"
                TutorialStep.POWER_TILES -> if (subPhase == 0) "👉 Swipe RIGHT to merge the Bomb Tile (💣4) and blast obstacles!" else "⚡ Tap 'Shatter' below, then tap the highlighted Slag Rock (🪨)!"
            }

            Text(
                text = objectiveText,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (stepCompleted) Color(0xFF81C784) else Color(0xFFFFD54F)
            )

            if (hintMessage != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = hintMessage,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF80D8FF)
                )
            }
        }
    }
}

/**
 * Container around GameBoardView with tutorial animated arrow cues
 */
@Composable
private fun TutorialBoardContainer(
    grid: Array<Array<Tile?>>,
    theme: ForgeThemeStyle,
    isTargeting: Boolean,
    handNudge: Float,
    stepCompleted: Boolean,
    onSwipe: (SwipeDirection) -> Unit,
    onTileClick: (Int, Int) -> Unit,
    lastSlideDirection: SwipeDirection?,
    lastSlideTimestamp: Long
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        GameBoardView(
            grid = grid,
            theme = theme,
            isTargeting = isTargeting,
            onSwipe = onSwipe,
            onTileClick = onTileClick,
            lastSlideDirection = lastSlideDirection,
            lastSlideTimestamp = lastSlideTimestamp,
            modifier = Modifier.fillMaxWidth()
        )

        // Animated Swipe Gesture Cue (Arrow & Guide Hand)
        if (!stepCompleted && !isTargeting) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(handNudge.roundToInt(), 0) }
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF000000).copy(alpha = 0.55f))
                    .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SWIPE RIGHT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD700),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Swipe Right",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Energy meter and power buttons relevant for Tutorial lessons
 */
@Composable
private fun TutorialEnergyAndPowerBar(
    step: TutorialStep,
    subPhase: Int,
    energy: Int,
    activeAbility: PowerAbilityType,
    onSelectShatter: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Energy Meter Status Pill
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1B1428),
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = if (energy > 0) Color(0xFF00E5FF).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Bolt,
                    contentDescription = "Energy",
                    tint = if (energy > 0) Color(0xFF00E5FF) else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "FORGE ENERGY: ${energy}⚡",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (energy > 0) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.5f)
                )
            }
        }

        // Power Ability Bar for Lesson 5
        if (step == TutorialStep.POWER_TILES && subPhase == 1) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (activeAbility == PowerAbilityType.SHATTER) Color(0xFFFF5252).copy(alpha = 0.35f) else Color(0xFF1E1528),
                    modifier = Modifier
                        .border(
                            width = if (activeAbility == PowerAbilityType.SHATTER) 2.dp else 1.dp,
                            color = if (activeAbility == PowerAbilityType.SHATTER) Color(0xFFFF5252) else Color(0xFFFF5252).copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelectShatter() }
                        .testTag("tutorial_shatter_ability_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteForever,
                            contentDescription = "Shatter",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (activeAbility == PowerAbilityType.SHATTER) "TAP ROCK TO SHATTER!" else "SELECT SHATTER (20⚡)",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Positive reinforcement modal card shown when completing each tutorial lesson
 */
@Composable
private fun TutorialSuccessModal(
    step: TutorialStep,
    isLastStep: Boolean,
    onContinue: () -> Unit,
    onReplayStep: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F162E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        modifier = Modifier
            .widthIn(max = 380.dp)
            .fillMaxWidth(0.92f)
            .border(
                2.dp,
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFFFD700),
                        step.accentColor
                    )
                ),
                RoundedCornerShape(20.dp)
            )
            .testTag("tutorial_success_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF2B1D3F),
                            Color(0xFF191024)
                        )
                    )
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Trophy / Star Celebration Emblem
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFFFFD700).copy(alpha = 0.35f),
                                Color(0xFF7C4DFF).copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFFD700),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Success",
                            tint = Color(0xFF140D1F),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val (praiseTitle, explanation) = when (step) {
                TutorialStep.SLIDING -> Pair(
                    "Masterful Slide!",
                    "When you swipe, every unblocked tile slides together until it strikes the boundary or another tile. Use this kinetic momentum to control the board!"
                )
                TutorialStep.COMBINING -> Pair(
                    "Fusion Mastery!",
                    "Matching numbers combine to double their power (2+2=4, 4+4=8, 8+8=16...). Keep combining tiles to forge the legendary 2048 tile and beyond!"
                )
                TutorialStep.ENERGY -> Pair(
                    "Energy Harvested (+65⚡)!",
                    "Energy Crystals charge your Forge Meter. Energy fuels kinetic abilities (Shatter, Duplicate, Transmute) and lets you Undo mistakes when trapped!"
                )
                TutorialStep.FORGE_TILES -> Pair(
                    "Legendary Artifact Forged!",
                    "Forge Anvils (⚒️) fuse with tiles to craft powerful Artifacts like Aegis Shield (which rescues you from game over) or Midas Ring! Forge Flames (🔥) multiply score!"
                )
                TutorialStep.POWER_TILES -> Pair(
                    "Complete Combat Mastery!",
                    "Bomb tiles obliterate surrounding blocks on collision, and active abilities like Shatter let you purge slag obstacles whenever you need space!"
                )
            }

            Text(
                text = praiseTitle,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFD700),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = explanation,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.88f),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onReplayStep,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "REPLAY",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Button(
                    onClick = onContinue,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00)),
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("tutorial_continue_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isLastStep) "GRADUATE 🎓" else "NEXT LESSON",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Academy Graduation celebratory dialog
 */
@Composable
private fun TutorialGraduationDialog(
    onStartEndless: () -> Unit,
    onReplayTutorial: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1228)),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        modifier = Modifier
            .widthIn(max = 420.dp)
            .fillMaxWidth(0.94f)
            .border(
                2.dp,
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFD700),
                        Color(0xFFFF6D00),
                        Color(0xFF7C4DFF)
                    )
                ),
                RoundedCornerShape(22.dp)
            )
            .testTag("tutorial_graduation_dialog")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF2C1940),
                            Color(0xFF160E22)
                        )
                    )
                )
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Academy Seal Emblem
            Surface(
                shape = CircleShape,
                color = Color(0xFFFFD700),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.School,
                        contentDescription = "Graduate",
                        tint = Color(0xFF1F142D),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "ACADEMY GRADUATE!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.2.sp
            )

            Text(
                text = "🏆 Master Smith Certification Awarded",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Skills checklist summary
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF160E22),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    GraduationSkillRow(label = "Sliding Dynamics", note = "Fluid multi-tile movement")
                    GraduationSkillRow(label = "Number Synthesis", note = "2048 power merging")
                    GraduationSkillRow(label = "Energy Collection", note = "Charged meter & move undo")
                    GraduationSkillRow(label = "Forge Relics & Flames", note = "Anvil artifact forging")
                    GraduationSkillRow(label = "Power Abilities", note = "Bomb detonation & Shatter")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "🎁 +50⚡ Energy added to your profile! Apprentice Smith achievement unlocked.",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF00E5FF),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onStartEndless,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("grad_start_endless_btn")
                ) {
                    Text(
                        text = "START PLAYING ENDLESS MODE",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onReplayTutorial,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "REPLAY TUTORIAL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "MAIN MENU",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GraduationSkillRow(label: String, note: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "• $note",
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.55f)
        )
    }
}
