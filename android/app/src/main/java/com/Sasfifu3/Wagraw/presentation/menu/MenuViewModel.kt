package com.Sasfifu3.Wagraw.presentation.menu

import androidx.lifecycle.ViewModel
import com.Sasfifu3.Wagraw.data.sample.SampleData
import com.Sasfifu3.Wagraw.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MenuViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val progress = progressRepository.load()
        val stats = progress.stats
        _uiState.update {
            it.copy(
                coins = progress.coins,
                spiceTokens = progress.spiceTokens,
                runs = stats.runs,
                bestScore = progressRepository.bestScore(),
                routeNumber = stats.runs + 1,
                unlockedStops = SampleData.stops.count { stop -> stop.unlockRuns <= stats.runs },
                totalStops = SampleData.stops.size,
                unlockedRecipes = progress.unlockedRecipes.size,
                totalRecipes = SampleData.recipes.size,
                lastStops = stats.lastStops,
                lastCoins = stats.lastCoins,
                hasRuns = stats.runs > 0
            )
        }
    }
}
