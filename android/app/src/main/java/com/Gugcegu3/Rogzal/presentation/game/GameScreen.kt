package com.Gugcegu3.Rogzal.presentation.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Gugcegu3.Rogzal.R
import com.Gugcegu3.Rogzal.core.config.GameConfig
import com.Gugcegu3.Rogzal.core.di.AppViewModelFactory
import com.Gugcegu3.Rogzal.core.theme.BarnRed
import com.Gugcegu3.Rogzal.core.theme.CreamLight
import com.Gugcegu3.Rogzal.core.theme.CreamPaint
import com.Gugcegu3.Rogzal.core.theme.SandDeep
import com.Gugcegu3.Rogzal.core.theme.TextOnCream
import com.Gugcegu3.Rogzal.core.ui.DuskBackground
import com.Gugcegu3.Rogzal.core.ui.DuskOverlays
import com.Gugcegu3.Rogzal.core.ui.ScreenHeader
import com.Gugcegu3.Rogzal.domain.model.Cell
import com.Gugcegu3.Rogzal.domain.model.RunResult
import com.Gugcegu3.Rogzal.presentation.dialog.ConfirmGoDialog
import com.Gugcegu3.Rogzal.presentation.dialog.PauseDialog
import com.Gugcegu3.Rogzal.presentation.game.components.BoardCanvas
import com.Gugcegu3.Rogzal.presentation.game.components.ControlsPanel
import com.Gugcegu3.Rogzal.presentation.game.components.GameStrip
import kotlin.math.floor
import kotlin.math.min

@Composable
fun GameRoute(
    levelIndex: Int,
    attempt: Int,
    onFinished: (RunResult) -> Unit,
    onMenu: () -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(factory = AppViewModelFactory)
    val state by gameViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(levelIndex, attempt) {
        gameViewModel.prepare(levelIndex, attempt)
    }
    LaunchedEffect(Unit) {
        gameViewModel.events.collect { event ->
            when (event) {
                is GameEvent.Finished -> onFinished(event.result)
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose { gameViewModel.hold() }
    }

    GameScreen(
        state = state,
        onCellTap = gameViewModel::onCellTap,
        onMode = gameViewModel::setMode,
        onUndo = gameViewModel::undo,
        onClear = gameViewModel::clearPlan,
        onGo = gameViewModel::onGoPressed,
        onPause = gameViewModel::openPause,
        onResume = gameViewModel::resumeFromPause,
        onRestart = gameViewModel::restartLevel,
        onMenu = onMenu,
        onConfirmGo = gameViewModel::confirmGo,
        onDismissConfirm = gameViewModel::dismissConfirm
    )
}

@Composable
fun GameScreen(
    state: GameUiState,
    onCellTap: (Cell) -> Unit,
    onMode: (InputMode) -> Unit,
    onUndo: () -> Unit,
    onClear: () -> Unit,
    onGo: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit,
    onConfirmGo: () -> Unit,
    onDismissConfirm: () -> Unit
) {
    val tickState = stringResource(R.string.tick_state, state.tick, state.tickTarget)
    DuskBackground(overlay = DuskOverlays.game) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.level_number, state.levelIndex),
                subtitle = if (state.levelNameRes != 0) {
                    stringResource(state.levelNameRes)
                } else {
                    null
                },
                leading = {
                    IconButton(onClick = onPause, modifier = Modifier.size(48.dp)) {
                        Image(
                            painter = painterResource(R.drawable.icon_pause),
                            contentDescription = stringResource(R.string.action_pause),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                },
                trailing = {
                    Text(
                        text = stringResource(
                            R.string.counter_value,
                            state.tick,
                            state.tickTarget
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = CreamPaint,
                        modifier = Modifier.semantics {
                            liveRegion = LiveRegionMode.Polite
                            stateDescription = tickState
                        }
                    )
                }
            )

            BoxWithConstraints(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                val layout = state.layout
                if (layout == null) {
                    CircularProgressIndicator(color = CreamLight)
                } else {
                    val frame = 8.dp
                    val maxBoardWidth = (maxWidth - 32.dp).coerceAtMost(372.dp)
                    val widthUnit = floor(((maxBoardWidth - frame * 2) / GameConfig.COLS).value)
                    val heightUnit = floor(((maxHeight - frame * 2) / layout.rows.size).value)
                    val cell = min(widthUnit, heightUnit).coerceAtLeast(22f).dp
                    BoardCanvas(
                        rows = layout.rows,
                        cols = GameConfig.COLS,
                        cell = cell,
                        frame = frame,
                        rooster = state.rooster,
                        vehicles = state.vehicles,
                        parcels = state.parcels,
                        barn = layout.barn,
                        shadowCells = layout.shadowCells,
                        signs = state.signs,
                        route = state.route,
                        start = layout.start,
                        carrying = state.carrying,
                        highContrast = state.highContrast,
                        collisionCell = state.collisionCell,
                        stepMs = state.tickMs - 80,
                        animated = state.phase == GamePhase.RUNNING,
                        showBadges = state.phase == GamePhase.PLANNING,
                        onCellTap = onCellTap
                    )
                }
                if (state.windBanner) {
                    WindBanner(modifier = Modifier.align(Alignment.TopCenter))
                }
            }

            GameStrip(
                ribbon = state.ribbon,
                ribbonEnabled = state.ribbonEnabled,
                signsLeft = state.signsLeft,
                signBudget = state.signBudget,
                patienceLeft = state.patienceLeft,
                patienceTotal = GameConfig.PATIENCE_TOKENS,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Box(modifier = Modifier.size(10.dp))

            ControlsPanel(
                mode = state.mode,
                phase = state.phase,
                signsLeft = state.signsLeft,
                routeLength = state.routeLength,
                canUndo = state.route.isNotEmpty() || state.signs.isNotEmpty(),
                onMode = onMode,
                onUndo = onUndo,
                onClear = onClear,
                onGo = onGo
            )
        }
    }

    if (state.pauseVisible) {
        PauseDialog(
            onResume = onResume,
            onRestart = onRestart,
            onMenu = onMenu
        )
    }
    if (state.confirmVisible) {
        ConfirmGoDialog(
            onConfirm = onConfirmGo,
            onDismiss = onDismissConfirm
        )
    }
}

@Composable
private fun WindBanner(modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CreamPaint,
        shadowElevation = 8.dp,
        modifier = modifier.padding(top = 6.dp)
    ) {
        Text(
            text = stringResource(R.string.wind_banner),
            style = MaterialTheme.typography.labelSmall,
            color = TextOnCream,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
