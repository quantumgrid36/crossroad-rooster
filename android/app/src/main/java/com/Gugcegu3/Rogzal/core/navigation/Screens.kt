package com.Gugcegu3.Rogzal.core.navigation

import com.Gugcegu3.Rogzal.domain.model.RunResult

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object Levels : Screen
    data object HowTo : Screen
    data class Game(val levelIndex: Int, val attempt: Int) : Screen
    data class GameOver(val result: RunResult) : Screen
}
