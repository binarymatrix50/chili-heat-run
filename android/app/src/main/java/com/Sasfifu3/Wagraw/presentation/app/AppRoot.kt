package com.Sasfifu3.Wagraw.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Sasfifu3.Wagraw.core.di.rememberAppFactory
import com.Sasfifu3.Wagraw.core.navigation.Screen
import com.Sasfifu3.Wagraw.presentation.collection.CollectionScreen
import com.Sasfifu3.Wagraw.presentation.game.GameScreen
import com.Sasfifu3.Wagraw.presentation.gameover.ResultScreen
import com.Sasfifu3.Wagraw.presentation.menu.MenuScreen
import com.Sasfifu3.Wagraw.presentation.progress.ProgressScreen
import com.Sasfifu3.Wagraw.presentation.routeselect.RouteSelectScreen
import com.Sasfifu3.Wagraw.presentation.settings.SettingsScreen
import com.Sasfifu3.Wagraw.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val factory = rememberAppFactory()
    val appViewModel: AppViewModel = viewModel(factory = factory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()
    val runKey by appViewModel.runKey.collectAsStateWithLifecycle()

    BackHandler(enabled = canGoBack) {
        appViewModel.back()
    }

    AnimatedContent(
        targetState = screen,
        transitionSpec = {
            (fadeIn(tween(320)) + slideInVertically(tween(320)) { full -> full / 12 })
                .togetherWith(fadeOut(tween(260)))
        },
        modifier = Modifier.fillMaxSize(),
        label = "appScreen"
    ) { target ->
        when (target) {
            Screen.Splash -> SplashScreen(
                onFinished = { appViewModel.navigate(Screen.Menu) }
            )

            Screen.Menu -> MenuScreen(
                onPlay = { appViewModel.startRun() },
                onRouteSelect = { appViewModel.navigate(Screen.RouteSelect) },
                onProgress = { appViewModel.navigate(Screen.Progress) },
                onCollection = { appViewModel.navigate(Screen.Collection) },
                onSettings = { appViewModel.navigate(Screen.Settings) }
            )

            Screen.RouteSelect -> RouteSelectScreen(
                onBack = { appViewModel.back() },
                onStartRoute = { appViewModel.startRun() }
            )

            Screen.Game -> GameScreen(
                runKey = runKey,
                onFinished = { appViewModel.showResult() },
                onMenu = { appViewModel.openMenu() },
                onSettings = { appViewModel.navigate(Screen.Settings) }
            )

            Screen.Result -> ResultScreen(
                onPlayAgain = { appViewModel.startRun() },
                onCollection = { appViewModel.navigate(Screen.Collection) },
                onMenu = { appViewModel.openMenu() }
            )

            Screen.Collection -> CollectionScreen(
                onBack = { appViewModel.back() }
            )

            Screen.Progress -> ProgressScreen(
                onBack = { appViewModel.back() }
            )

            Screen.Settings -> SettingsScreen(
                onBack = { appViewModel.back() }
            )
        }
    }
}
