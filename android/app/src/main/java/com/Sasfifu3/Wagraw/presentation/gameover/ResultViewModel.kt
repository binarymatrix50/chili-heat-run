package com.Sasfifu3.Wagraw.presentation.gameover

import androidx.lifecycle.ViewModel
import com.Sasfifu3.Wagraw.data.repository.RunSessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ResultViewModel(
    private val runSession: RunSessionStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val result = runSession.lastResult ?: return
        _uiState.value = ResultUiState(
            reason = result.reason,
            stopsCleared = result.stopsCleared,
            totalStops = result.totalStops,
            stopNameRes = result.stopNameRes,
            coinsEarned = result.coinsEarned,
            perfectServes = result.perfectServes,
            bestStreak = result.bestStreak,
            spiceEarned = result.spiceEarned,
            assisted = result.assisted,
            newBest = result.newBest,
            nextRecipeNameRes = result.nextRecipeNameRes,
            hasResult = true
        )
    }
}
