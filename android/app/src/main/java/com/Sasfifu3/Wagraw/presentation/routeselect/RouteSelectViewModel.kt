package com.Sasfifu3.Wagraw.presentation.routeselect

import androidx.lifecycle.ViewModel
import com.Sasfifu3.Wagraw.core.config.GameConfig
import com.Sasfifu3.Wagraw.data.repository.RunSessionStore
import com.Sasfifu3.Wagraw.data.sample.SampleData
import com.Sasfifu3.Wagraw.domain.model.RouteStop
import com.Sasfifu3.Wagraw.domain.repository.ProgressRepository
import com.Sasfifu3.Wagraw.domain.usecase.GenerateRunUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RouteSelectViewModel(
    private val progressRepository: ProgressRepository,
    private val runSession: RunSessionStore,
    private val generateRun: GenerateRunUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RouteSelectUiState())
    val uiState: StateFlow<RouteSelectUiState> = _uiState.asStateFlow()

    private var seed = 1L
    private var reroll = 0

    init {
        refresh()
    }

    fun refresh() {
        val progress = progressRepository.load()
        seed = (progress.stats.runs + 1) * 7919L
        _uiState.update {
            it.copy(
                spiceTokens = progress.spiceTokens,
                runs = progress.stats.runs
            )
        }
        if (_uiState.value.chosen.isEmpty()) {
            resetRoute()
        } else {
            refreshOptions()
        }
    }

    fun resetRoute() {
        reroll = 0
        val opening = SampleData.stops.first()
        _uiState.update { it.copy(chosen = listOf(opening), rerollsLeft = 1, lockedNotice = false) }
        refreshOptions()
    }

    fun pick(stop: RouteStop) {
        val state = _uiState.value
        if (stop.unlockRuns > state.runs) {
            _uiState.update { it.copy(lockedNotice = true) }
            return
        }
        if (state.complete) return
        reroll = 0
        _uiState.update { it.copy(chosen = it.chosen + stop, lockedNotice = false) }
        refreshOptions()
    }

    fun shuffle() {
        val state = _uiState.value
        if (state.rerollsLeft <= 0 || state.complete) return
        reroll += 1
        _uiState.update { it.copy(rerollsLeft = it.rerollsLeft - 1, lockedNotice = false) }
        refreshOptions()
    }

    fun confirmRoute() {
        val state = _uiState.value
        val route = if (state.complete) state.chosen else SampleData.defaultRoute
        runSession.setRoute(route)
    }

    private fun refreshOptions() {
        val state = _uiState.value
        if (state.chosen.size >= GameConfig.ROUTE_STOPS) {
            _uiState.update { it.copy(options = emptyList()) }
            return
        }
        val taken = state.chosen.map { it.id }.toSet()
        val offer = generateRun.offerForSlot(
            pool = SampleData.stops,
            taken = taken,
            seed = seed,
            slot = state.chosen.size,
            reroll = reroll
        )
        _uiState.update { current ->
            current.copy(
                options = offer.map { StopOptionUi(it, it.unlockRuns > current.runs) }
            )
        }
    }
}
