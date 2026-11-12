package com.Sasfifu3.Wagraw.presentation.gameover

import com.Sasfifu3.Wagraw.domain.model.RunEndReason

data class ResultUiState(
    val reason: RunEndReason = RunEndReason.ROUTE_SERVED,
    val stopsCleared: Int = 0,
    val totalStops: Int = 0,
    val stopNameRes: Int = 0,
    val coinsEarned: Int = 0,
    val perfectServes: Int = 0,
    val bestStreak: Int = 0,
    val spiceEarned: Int = 0,
    val assisted: Boolean = false,
    val newBest: Boolean = false,
    val nextRecipeNameRes: Int? = null,
    val hasResult: Boolean = false
)
