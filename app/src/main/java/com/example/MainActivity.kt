package com.example

import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.game.GameModeType
import com.example.ui.screens.GameScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TileForgeApp(activity = this)
            }
        }
    }
}

@Composable
fun TileForgeApp(activity: ComponentActivity) {
    val gameViewModel: GameViewModel = viewModel()
    val gameStats by gameViewModel.gameStats.collectAsState()
    val uiState by gameViewModel.uiState.collectAsState()

    var currentScreen by remember { mutableStateOf("main_menu") }

    // Immersive Mode Controller
    val window = activity.window
    val insetsController = remember(window) {
        WindowCompat.getInsetsController(window, window.decorView)
    }

    LaunchedEffect(uiState.isImmersiveModeEnabled) {
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        if (uiState.isImmersiveModeEnabled) {
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    DisposableEffect(uiState.isImmersiveModeEnabled) {
        val listener = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
            if (hasFocus && uiState.isImmersiveModeEnabled) {
                insetsController.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
            }
        }
        val decorView = window.decorView
        decorView.viewTreeObserver.addOnWindowFocusChangeListener(listener)
        onDispose {
            decorView.viewTreeObserver.removeOnWindowFocusChangeListener(listener)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        when (currentScreen) {
            "main_menu" -> {
                MainMenuScreen(
                    viewModel = gameViewModel,
                    onStartGame = { currentScreen = "game" },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            "game" -> {
                GameScreen(
                    viewModel = gameViewModel,
                    onNavigateBack = {
                        gameViewModel.dismissAllDialogs()
                        currentScreen = "main_menu"
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
