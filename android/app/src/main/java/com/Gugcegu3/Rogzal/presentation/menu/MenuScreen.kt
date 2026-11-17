package com.Gugcegu3.Rogzal.presentation.menu

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Gugcegu3.Rogzal.R
import com.Gugcegu3.Rogzal.core.di.AppViewModelFactory
import com.Gugcegu3.Rogzal.core.theme.CreamLight
import com.Gugcegu3.Rogzal.core.theme.CreamPaint
import com.Gugcegu3.Rogzal.core.theme.DuskShadow
import com.Gugcegu3.Rogzal.core.theme.FieldSage
import com.Gugcegu3.Rogzal.core.theme.GoldMedal
import com.Gugcegu3.Rogzal.core.theme.Sand
import com.Gugcegu3.Rogzal.core.theme.TextMuted
import com.Gugcegu3.Rogzal.core.theme.TextOnCream
import com.Gugcegu3.Rogzal.core.ui.ContractRow
import com.Gugcegu3.Rogzal.core.ui.DuskBackground
import com.Gugcegu3.Rogzal.core.ui.DuskOverlays
import com.Gugcegu3.Rogzal.core.ui.PrimaryButton
import com.Gugcegu3.Rogzal.core.ui.SecondaryButton
import com.Gugcegu3.Rogzal.core.ui.SkeuoPanel
import com.Gugcegu3.Rogzal.core.ui.StatItem
import com.Gugcegu3.Rogzal.core.ui.StatRow
import com.Gugcegu3.Rogzal.core.ui.WeatherChip
import com.Gugcegu3.Rogzal.presentation.dialog.SettingsDialog
import com.Gugcegu3.Rogzal.core.theme.BarnRed

@Composable
fun MenuRoute(
    onPlay: (Int) -> Unit,
    onLevels: () -> Unit,
    onHowTo: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = AppViewModelFactory)
    val state by menuViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        menuViewModel.refresh()
    }
    MenuScreen(
        state = state,
        onPlay = { onPlay(state.nextLevel) },
        onLevels = onLevels,
        onHowTo = onHowTo,
        onOpenSettings = menuViewModel::openSettings,
        onCloseSettings = menuViewModel::closeSettings,
        onTickMs = menuViewModel::setTickMs,
        onRibbon = menuViewModel::setRibbon,
        onContrast = menuViewModel::setHighContrast,
        onConfirmGo = menuViewModel::setConfirmGo
    )
}

@Composable
fun MenuScreen(
    state: MenuUiState,
    onPlay: () -> Unit,
    onLevels: () -> Unit,
    onHowTo: () -> Unit,
    onOpenSettings: () -> Unit,
    onCloseSettings: () -> Unit,
    onTickMs: (Int) -> Unit,
    onRibbon: (Boolean) -> Unit,
    onContrast: (Boolean) -> Unit,
    onConfirmGo: (Boolean) -> Unit
) {
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        appeared = true
    }
    val reveal by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(420, easing = FastOutSlowInEasing),
        label = "menuReveal"
    )

    DuskBackground(overlay = DuskOverlays.menu) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            MenuArt(
                totalParcels = state.totalParcels,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            MenuSheet(
                state = state,
                reveal = reveal,
                onPlay = onPlay,
                onLevels = onLevels,
                onHowTo = onHowTo,
                onOpenSettings = onOpenSettings
            )
        }
    }

    if (state.settingsVisible) {
        SettingsDialog(
            settings = state.settings,
            onTickMs = onTickMs,
            onRibbon = onRibbon,
            onContrast = onContrast,
            onConfirmGo = onConfirmGo,
            onDismiss = onCloseSettings
        )
    }
}

@Composable
private fun MenuArt(totalParcels: Int, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to DuskShadow.copy(alpha = 0.10f),
                        1f to CreamLight.copy(alpha = 0.92f)
                    )
                )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(38.dp)
            )
            if (totalParcels > 0) {
                SkeuoPanel(radius = 14.dp, elevation = 6.dp, contentPadding = 8.dp) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.sprite_4),
                            contentDescription = stringResource(R.string.cd_parcel),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = totalParcels.toString(),
                            style = MaterialTheme.typography.titleSmall,
                            color = TextOnCream
                        )
                    }
                }
            }
        }
        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = stringResource(R.string.cd_rooster),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 18.dp, bottom = 16.dp)
                .size(104.dp)
        )
        Image(
            painter = painterResource(R.drawable.sprite_5),
            contentDescription = stringResource(R.string.cd_barn),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 18.dp, bottom = 16.dp)
                .size(92.dp)
        )
    }
}

@Composable
private fun MenuSheet(
    state: MenuUiState,
    reveal: Float,
    onPlay: () -> Unit,
    onLevels: () -> Unit,
    onHowTo: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = CreamLight,
        shadowElevation = 18.dp,
        border = BorderStroke(2.dp, Sand),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = reveal
                translationY = (1f - reveal) * 120f
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.headlineMedium,
                color = TextOnCream,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.app_tagline),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                textAlign = TextAlign.Center
            )

            StatRow(
                items = listOf(
                    StatItem(
                        value = state.nextLevel.toString(),
                        label = stringResource(R.string.stat_level),
                        accent = FieldSage,
                        iconRes = R.drawable.ic_route
                    ),
                    StatItem(
                        value = state.medals.toString(),
                        label = stringResource(R.string.stat_medals),
                        accent = GoldMedal,
                        iconRes = R.drawable.ic_medal,
                        visible = state.medals > 0
                    ),
                    StatItem(
                        value = state.bestScore.toString(),
                        label = stringResource(R.string.stat_best),
                        accent = BarnRed,
                        iconRes = R.drawable.ic_clock,
                        visible = state.bestScore > 0
                    )
                )
            )

            SkeuoPanel(
                modifier = Modifier.fillMaxWidth(),
                fill = CreamPaint,
                radius = 18.dp,
                elevation = 8.dp,
                contentPadding = 12.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.next_contract),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        WeatherChip(weather = state.weather, contentColor = TextOnCream)
                    }
                    ContractRow(
                        clauses = state.clauses,
                        showResult = false,
                        textColor = TextOnCream
                    )
                }
            }

            Text(
                text = stringResource(R.string.menu_hint),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                textAlign = TextAlign.Center
            )

            PrimaryButton(
                text = stringResource(R.string.action_play),
                onClick = onPlay,
                leadingIcon = R.drawable.ic_play_arrow,
                height = 60.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SecondaryButton(
                    text = stringResource(R.string.action_levels),
                    onClick = onLevels,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.action_how_to_play),
                    onClick = onHowTo,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.action_settings),
                    onClick = onOpenSettings,
                    modifier = Modifier.weight(1f)
                )
            }
            Box(modifier = Modifier.height(2.dp))
        }
    }
}
