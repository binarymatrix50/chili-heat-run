package com.Sasfifu3.Wagraw.presentation.menu

data class MenuUiState(
    val coins: Int = 0,
    val spiceTokens: Int = 0,
    val runs: Int = 0,
    val bestScore: Int = 0,
    val routeNumber: Int = 1,
    val unlockedStops: Int = 0,
    val totalStops: Int = 0,
    val unlockedRecipes: Int = 0,
    val totalRecipes: Int = 0,
    val lastStops: Int = 0,
    val lastCoins: Int = 0,
    val hasRuns: Boolean = false
)
