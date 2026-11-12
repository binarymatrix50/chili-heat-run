package com.Sasfifu3.Wagraw.domain.repository

import com.Sasfifu3.Wagraw.domain.model.GameSettings

interface SettingsRepository {
    fun load(): GameSettings
    fun save(settings: GameSettings)
}
