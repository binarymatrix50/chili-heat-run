package com.Sasfifu3.Wagraw.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object RouteSelect : Screen
    data object Game : Screen
    data object Result : Screen
    data object Collection : Screen
    data object Progress : Screen
    data object Settings : Screen
}
