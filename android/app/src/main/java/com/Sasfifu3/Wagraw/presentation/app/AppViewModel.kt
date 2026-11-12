package com.Sasfifu3.Wagraw.presentation.app

import androidx.lifecycle.ViewModel
import com.Sasfifu3.Wagraw.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val _runKey = MutableStateFlow(0)
    val runKey: StateFlow<Int> = _runKey.asStateFlow()

    private val backStack = ArrayDeque<Screen>()

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current != Screen.Splash && current != target) {
            backStack.addLast(current)
        }
        _screen.value = target
        syncBack()
    }

    fun startRun() {
        backStack.clear()
        backStack.addLast(Screen.Menu)
        _runKey.value = _runKey.value + 1
        _screen.value = Screen.Game
        syncBack()
    }

    fun showResult() {
        backStack.clear()
        backStack.addLast(Screen.Menu)
        _screen.value = Screen.Result
        syncBack()
    }

    fun openMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
        syncBack()
    }

    fun back() {
        val previous = backStack.removeLastOrNull() ?: return
        _screen.value = previous
        syncBack()
    }

    private fun syncBack() {
        _canGoBack.value = backStack.isNotEmpty()
    }
}
