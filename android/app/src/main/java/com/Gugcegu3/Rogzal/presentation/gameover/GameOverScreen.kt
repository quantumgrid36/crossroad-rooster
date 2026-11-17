package com.Gugcegu3.Rogzal.presentation.gameover

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Gugcegu3.Rogzal.R
import com.Gugcegu3.Rogzal.core.di.AppViewModelFactory
import com.Gugcegu3.Rogzal.core.theme.BarnRed
import com.Gugcegu3.Rogzal.core.theme.CreamLight
import com.Gugcegu3.Rogzal.core.theme.CreamPaint
import com.Gugcegu3.Rogzal.core.theme.FieldSage
import com.Gugcegu3.Rogzal.core.theme.GoldMedal
import com.Gugcegu3.Rogzal.core.theme.SandDeep
import com.Gugcegu3.Rogzal.core.theme.TextOnCream
import com.Gugcegu3.Rogzal.core.ui.ContractRow
import com.Gugcegu3.Rogzal.core.ui.DuskBackground
import com.Gugcegu3.Rogzal.core.ui.DuskOverlays
import com.Gugcegu3.Rogzal.core.ui.MedalBadge
import com.Gugcegu3.Rogzal.core.ui.PrimaryButton
import com.Gugcegu3.Rogzal.core.ui.SecondaryButton
import com.Gugcegu3.Rogzal.core.ui.SkeuoPanel
import com.Gugcegu3.Rogzal.core.ui.StatItem
import com.Gugcegu3.Rogzal.core.ui.StatRow
import com.Gugcegu3.Rogzal.core.ui.WeatherChip
import com.Gugcegu3.Rogzal.domain.model.Medal
import com.Gugcegu3.Rogzal.domain.model.RunOutcome
import com.Gugcegu3.Rogzal.domain.model.RunResult
import com.Gugcegu3.Rogzal.presentation.gameover.components.ReplayPanel

@Composable
fun GameOverRoute(
    result: RunResult,
    onPlayAgain: () -> Unit,
    onNextLevel: (Int) -> Unit,
    onMenu: () -> Unit
) {
    val gameOverViewModel: GameOverViewModel = viewModel(factory = AppViewModelFactory)
    val state by gameOverViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(result) {
        gameOverViewModel.bind(result)
    }

    GameOverScreen(
        state = state,
        fallback = result,
        onPlayAgain = onPlayAgain,
        onNextLevel = onNextLevel,
        onMenu = onMenu,
        onTickChange = gameOverViewModel::setTickIndex,
        onToggleReplay = gameOverViewModel::toggleReplay
    )
}

@Composable
fun GameOverScreen(
    state: GameOverUiState,
    fallback: RunResult,
    onPlayAgain: () -> Unit,
    onNextLevel: (Int) -> Unit,
    onMenu: () -> Unit,
    onTickChange: (Int) -> Unit,
    onToggleReplay: () -> Unit
) {
    val result = state.result ?: fallback
    var stamped by remember { mutableStateOf(false) }
    LaunchedEffect(result) {
        stamped = true
    }
    val stamp by animateFloatAsState(
        targetValue = if (stamped) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 300f),
        label = "medalStamp"
    )
    val scoreState = stringResource(R.string.score_state, result.score)

    DuskBackground(overlay = DuskOverlays.result) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                DashDecor()
                Text(
                    text = stringResource(bannerRes(result.outcome)),
                    style = MaterialTheme.typography.displaySmall,
                    color = CreamLight,
                    textAlign = TextAlign.Center
                )
                DashDecor()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MedalBadge(
                        medal = result.medal,
                        size = 72.dp,
                        description = stringResource(medalDescriptionRes(result.medal)),
                        modifier = Modifier.graphicsLayer {
                            val value = 1.6f - 0.6f * stamp
                            scaleX = value
                            scaleY = value
                        }
                    )
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = stringResource(medalDescriptionRes(result.medal)),
                            style = MaterialTheme.typography.titleMedium,
                            color = CreamPaint
                        )
                        Text(
                            text = stringResource(R.string.score_value, result.score),
                            style = MaterialTheme.typography.headlineMedium,
                            color = GoldMedal,
                            modifier = Modifier.semantics {
                                liveRegion = LiveRegionMode.Polite
                                stateDescription = scoreState
                            }
                        )
                    }
                }

                SkeuoPanel(modifier = Modifier.fillMaxWidth(), contentPadding = 12.dp) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(result.levelNameRes),
                                style = MaterialTheme.typography.titleSmall,
                                color = TextOnCream
                            )
                            WeatherChip(
                                weather = result.weather,
                                contentColor = TextOnCream
                            )
                        }
                        ContractRow(
                            clauses = result.clauses,
                            showResult = true,
                            textColor = TextOnCream
                        )
                    }
                }

                StatRow(
                    items = listOf(
                        StatItem(
                            value = result.ticksUsed.toString(),
                            label = stringResource(R.string.stat_ticks),
                            accent = FieldSage,
                            iconRes = R.drawable.ic_clock,
                            visible = result.ticksUsed > 0
                        ),
                        StatItem(
                            value = result.nearMisses.toString(),
                            label = stringResource(R.string.stat_near_miss),
                            accent = BarnRed,
                            iconRes = R.drawable.ic_feather,
                            visible = result.nearMisses > 0
                        ),
                        StatItem(
                            value = state.bestScore.toString(),
                            label = stringResource(R.string.stat_best),
                            accent = GoldMedal,
                            iconRes = R.drawable.ic_medal,
                            visible = state.bestScore > 0
                        )
                    )
                )

                ReplayPanel(
                    frames = result.frames,
                    rows = result.rows,
                    barn = result.barn,
                    tickIndex = state.tickIndex,
                    playing = state.playing,
                    onTickChange = onTickChange,
                    onToggleReplay = onToggleReplay
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 14.dp)
            ) {
                PrimaryButton(
                    text = stringResource(R.string.action_play_again),
                    onClick = onPlayAgain,
                    leadingIcon = R.drawable.ic_replay,
                    height = 58.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.nextLevel > 0) {
                        SecondaryButton(
                            text = stringResource(R.string.action_next_level),
                            onClick = { onNextLevel(state.nextLevel) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    SecondaryButton(
                        text = stringResource(R.string.action_menu),
                        onClick = onMenu,
                        leadingIcon = R.drawable.icon_home,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DashDecor() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(DASH_COUNT) {
            Box(
                modifier = Modifier
                    .width(22.dp)
                    .height(4.dp)
                    .background(SandDeep.copy(alpha = 0.6f), RoundedCornerShape(2.dp))
            )
        }
    }
}

private fun bannerRes(outcome: RunOutcome): Int = when (outcome) {
    RunOutcome.DELIVERED -> R.string.result_delivered
    RunOutcome.ROUTE_COMPLETE -> R.string.result_route_complete
    RunOutcome.CLIPPED -> R.string.result_clipped
    RunOutcome.OUT_OF_TICKS -> R.string.result_out_of_ticks
}

private fun medalDescriptionRes(medal: Medal): Int = when (medal) {
    Medal.GOLD -> R.string.medal_gold
    Medal.SILVER -> R.string.medal_silver
    Medal.BRONZE -> R.string.medal_bronze
    Medal.NONE -> R.string.medal_none
}

private const val DASH_COUNT = 4
