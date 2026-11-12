package com.Sasfifu3.Wagraw.data.repository

import com.Sasfifu3.Wagraw.data.local.ChiliPreferences
import com.Sasfifu3.Wagraw.domain.model.GameSettings
import com.Sasfifu3.Wagraw.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val preferences: ChiliPreferences) : SettingsRepository {

    override fun load(): GameSettings = GameSettings(
        heatNumbers = preferences.readBoolean(KEY_HEAT_NUMBERS, true),
        leftHandedTray = preferences.readBoolean(KEY_LEFT_TRAY, false),
        patienceAssist = preferences.readBoolean(KEY_ASSIST, false)
    )

    override fun save(settings: GameSettings) {
        preferences.writeBoolean(KEY_HEAT_NUMBERS, settings.heatNumbers)
        preferences.writeBoolean(KEY_LEFT_TRAY, settings.leftHandedTray)
        preferences.writeBoolean(KEY_ASSIST, settings.patienceAssist)
    }

    private companion object {
        const val KEY_HEAT_NUMBERS = "setting_heat_numbers"
        const val KEY_LEFT_TRAY = "setting_left_tray"
        const val KEY_ASSIST = "setting_assist"
    }
}
