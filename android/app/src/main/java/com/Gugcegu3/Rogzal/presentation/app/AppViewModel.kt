package com.Gugcegu3.Rogzal.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Gugcegu3.Rogzal.core.navigation.Screen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val backStack = mutableListOf<Screen>()

    private val exitRequests = Channel<Unit>(Channel.CONFLATED)
    val exitEvents = exitRequests.receiveAsFlow()

    fun push(target: Screen) {
        val current = _screen.value
        if (current !is Screen.Splash) backStack.add(current)
        _screen.value = target
    }

    fun replace(target: Screen) {
        _screen.value = target
    }

    fun resetTo(target: Screen) {
        backStack.clear()
        _screen.value = target
    }

    fun back() {
        if (backStack.isEmpty()) {
            viewModelScope.launch { exitRequests.send(Unit) }
            return
        }
        val previous = backStack.removeAt(backStack.lastIndex)
        _screen.value = previous
    }
}
