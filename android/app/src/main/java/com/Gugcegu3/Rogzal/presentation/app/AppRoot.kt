package com.Gugcegu3.Rogzal.presentation.app

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Gugcegu3.Rogzal.core.di.AppViewModelFactory
import com.Gugcegu3.Rogzal.core.navigation.Screen
import com.Gugcegu3.Rogzal.presentation.game.GameRoute
import com.Gugcegu3.Rogzal.presentation.gameover.GameOverRoute
import com.Gugcegu3.Rogzal.presentation.howto.HowToPlayScreen
import com.Gugcegu3.Rogzal.presentation.levels.LevelSelectRoute
import com.Gugcegu3.Rogzal.presentation.menu.MenuRoute
import com.Gugcegu3.Rogzal.presentation.splash.SplashRoute

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        appViewModel.exitEvents.collect {
            (context as? Activity)?.finish()
        }
    }

    BackHandler(enabled = screen !is Screen.Splash) {
        appViewModel.back()
    }

    AnimatedContent(
        targetState = screen,
        modifier = Modifier.fillMaxSize(),
        transitionSpec = {
            (fadeIn(tween(420)) + slideInHorizontally(tween(360)) { full -> full / 6 })
                .togetherWith(fadeOut(tween(320)))
        },
        label = "rootScreen"
    ) { current ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (current) {
                Screen.Splash -> SplashRoute(
                    onFinished = { appViewModel.resetTo(Screen.Menu) }
                )

                Screen.Menu -> MenuRoute(
                    onPlay = { level -> appViewModel.push(Screen.Game(level, 1)) },
                    onLevels = { appViewModel.push(Screen.Levels) },
                    onHowTo = { appViewModel.push(Screen.HowTo) }
                )

                Screen.Levels -> LevelSelectRoute(
                    onPlayLevel = { level -> appViewModel.push(Screen.Game(level, 1)) },
                    onMenu = { appViewModel.resetTo(Screen.Menu) }
                )

                Screen.HowTo -> HowToPlayScreen(
                    onMenu = { appViewModel.resetTo(Screen.Menu) }
                )

                is Screen.Game -> GameRoute(
                    levelIndex = current.levelIndex,
                    attempt = current.attempt,
                    onFinished = { result -> appViewModel.replace(Screen.GameOver(result)) },
                    onMenu = { appViewModel.resetTo(Screen.Menu) }
                )

                is Screen.GameOver -> GameOverRoute(
                    result = current.result,
                    onPlayAgain = {
                        appViewModel.replace(
                            Screen.Game(current.result.levelIndex, current.result.attempt + 1)
                        )
                    },
                    onNextLevel = { level -> appViewModel.replace(Screen.Game(level, 1)) },
                    onMenu = { appViewModel.resetTo(Screen.Menu) }
                )
            }
        }
    }
}
