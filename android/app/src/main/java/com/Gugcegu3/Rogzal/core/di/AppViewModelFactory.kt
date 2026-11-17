package com.Gugcegu3.Rogzal.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.Gugcegu3.Rogzal.presentation.app.AppViewModel
import com.Gugcegu3.Rogzal.presentation.game.GameViewModel
import com.Gugcegu3.Rogzal.presentation.gameover.GameOverViewModel
import com.Gugcegu3.Rogzal.presentation.levels.LevelSelectViewModel
import com.Gugcegu3.Rogzal.presentation.menu.MenuViewModel
import com.Gugcegu3.Rogzal.presentation.splash.SplashViewModel

object AppViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel()
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(
                ServiceLocator.campaign,
                ServiceLocator.settings
            )
            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(
                ServiceLocator.settings,
                ServiceLocator.generateLayout,
                ServiceLocator.advanceTick,
                ServiceLocator.projectBoard,
                ServiceLocator.evaluateRun
            )
            modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(
                ServiceLocator.campaign,
                ServiceLocator.settings
            )
            modelClass.isAssignableFrom(LevelSelectViewModel::class.java) -> LevelSelectViewModel(
                ServiceLocator.campaign
            )
            else -> throw IllegalArgumentException(UNKNOWN_MODEL + modelClass.name)
        }
        @Suppress("UNCHECKED_CAST")
        return created as T
    }

    private const val UNKNOWN_MODEL = "Unknown ViewModel: "
}
