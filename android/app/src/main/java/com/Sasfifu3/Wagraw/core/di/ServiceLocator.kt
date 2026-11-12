package com.Sasfifu3.Wagraw.core.di

import android.content.Context
import com.Sasfifu3.Wagraw.data.local.ChiliPreferences
import com.Sasfifu3.Wagraw.data.repository.ProgressRepositoryImpl
import com.Sasfifu3.Wagraw.data.repository.RunSessionStore
import com.Sasfifu3.Wagraw.data.repository.SettingsRepositoryImpl
import com.Sasfifu3.Wagraw.domain.repository.ProgressRepository
import com.Sasfifu3.Wagraw.domain.repository.SettingsRepository
import com.Sasfifu3.Wagraw.domain.usecase.ApplyUpgradesUseCase
import com.Sasfifu3.Wagraw.domain.usecase.GenerateRunUseCase
import com.Sasfifu3.Wagraw.domain.usecase.ResolveServeUseCase

class AppContainer(context: Context) {

    private val preferences = ChiliPreferences(context)

    val progressRepository: ProgressRepository = ProgressRepositoryImpl(preferences)
    val settingsRepository: SettingsRepository = SettingsRepositoryImpl(preferences)
    val runSession = RunSessionStore()
    val generateRun = GenerateRunUseCase()
    val resolveServe = ResolveServeUseCase()
    val applyUpgrades = ApplyUpgradesUseCase()
}

object ServiceLocator {

    private var container: AppContainer? = null

    fun init(context: Context) {
        if (container == null) {
            container = AppContainer(context.applicationContext)
        }
    }

    fun container(): AppContainer = checkNotNull(container)
}
