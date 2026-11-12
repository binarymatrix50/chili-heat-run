package com.Sasfifu3.Wagraw.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.Sasfifu3.Wagraw.presentation.app.AppViewModel
import com.Sasfifu3.Wagraw.presentation.collection.CollectionViewModel
import com.Sasfifu3.Wagraw.presentation.game.GameViewModel
import com.Sasfifu3.Wagraw.presentation.gameover.ResultViewModel
import com.Sasfifu3.Wagraw.presentation.menu.MenuViewModel
import com.Sasfifu3.Wagraw.presentation.progress.ProgressViewModel
import com.Sasfifu3.Wagraw.presentation.routeselect.RouteSelectViewModel
import com.Sasfifu3.Wagraw.presentation.settings.SettingsViewModel
import com.Sasfifu3.Wagraw.presentation.splash.SplashViewModel

class AppViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) ->
                AppViewModel()

            modelClass.isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel()

            modelClass.isAssignableFrom(MenuViewModel::class.java) ->
                MenuViewModel(container.progressRepository)

            modelClass.isAssignableFrom(RouteSelectViewModel::class.java) ->
                RouteSelectViewModel(
                    container.progressRepository,
                    container.runSession,
                    container.generateRun
                )

            modelClass.isAssignableFrom(GameViewModel::class.java) ->
                GameViewModel(
                    container.progressRepository,
                    container.settingsRepository,
                    container.runSession,
                    container.generateRun,
                    container.resolveServe,
                    container.applyUpgrades
                )

            modelClass.isAssignableFrom(ResultViewModel::class.java) ->
                ResultViewModel(container.runSession)

            modelClass.isAssignableFrom(CollectionViewModel::class.java) ->
                CollectionViewModel(container.progressRepository, container.settingsRepository)

            modelClass.isAssignableFrom(ProgressViewModel::class.java) ->
                ProgressViewModel(container.progressRepository)

            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(container.settingsRepository, container.progressRepository)

            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }
}
