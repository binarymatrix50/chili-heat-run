package com.Sasfifu3.Wagraw.presentation.settings

import androidx.lifecycle.ViewModel
import com.Sasfifu3.Wagraw.core.config.GameConfig
import com.Sasfifu3.Wagraw.domain.model.GameSettings
import com.Sasfifu3.Wagraw.domain.repository.ProgressRepository
import com.Sasfifu3.Wagraw.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState(version = GameConfig.APP_VERSION))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.update { it.copy(settings = settingsRepository.load()) }
    }

    fun toggleHeatNumbers(value: Boolean) {
        persist(_uiState.value.settings.copy(heatNumbers = value))
    }

    fun toggleLeftHandedTray(value: Boolean) {
        persist(_uiState.value.settings.copy(leftHandedTray = value))
    }

    fun togglePatienceAssist(value: Boolean) {
        persist(_uiState.value.settings.copy(patienceAssist = value))
    }

    fun askReset() {
        _uiState.update { it.copy(confirmReset = true) }
    }

    fun dismissReset() {
        _uiState.update { it.copy(confirmReset = false) }
    }

    fun confirmReset() {
        progressRepository.clear()
        val defaults = GameSettings()
        settingsRepository.save(defaults)
        _uiState.value = SettingsUiState(settings = defaults, version = GameConfig.APP_VERSION)
    }

    private fun persist(settings: GameSettings) {
        settingsRepository.save(settings)
        _uiState.update { it.copy(settings = settings) }
    }
}
