package com.Sasfifu3.Wagraw.presentation.settings

import com.Sasfifu3.Wagraw.domain.model.GameSettings

data class SettingsUiState(
    val settings: GameSettings = GameSettings(),
    val confirmReset: Boolean = false,
    val version: String = ""
)
