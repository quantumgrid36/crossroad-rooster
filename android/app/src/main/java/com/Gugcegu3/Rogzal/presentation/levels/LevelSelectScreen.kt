package com.Gugcegu3.Rogzal.presentation.levels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Gugcegu3.Rogzal.R
import com.Gugcegu3.Rogzal.core.di.AppViewModelFactory
import com.Gugcegu3.Rogzal.core.theme.BarnRed
import com.Gugcegu3.Rogzal.core.theme.CreamPaint
import com.Gugcegu3.Rogzal.core.theme.FieldSage
import com.Gugcegu3.Rogzal.core.theme.GoldMedal
import com.Gugcegu3.Rogzal.core.theme.SandDeep
import com.Gugcegu3.Rogzal.core.theme.TextMuted
import com.Gugcegu3.Rogzal.core.theme.TextOnCream
import com.Gugcegu3.Rogzal.core.ui.DuskBackground
import com.Gugcegu3.Rogzal.core.ui.DuskOverlays
import com.Gugcegu3.Rogzal.core.ui.MedalBadge
import com.Gugcegu3.Rogzal.core.ui.ScreenHeader
import com.Gugcegu3.Rogzal.core.ui.SecondaryButton
import com.Gugcegu3.Rogzal.core.ui.SkeuoPanel
import com.Gugcegu3.Rogzal.domain.model.LevelProgress
import com.Gugcegu3.Rogzal.domain.model.Medal

@Composable
fun LevelSelectRoute(
    onPlayLevel: (Int) -> Unit,
    onMenu: () -> Unit
) {
    val levelsViewModel: LevelSelectViewModel = viewModel(factory = AppViewModelFactory)
    val state by levelsViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        levelsViewModel.refresh()
    }
    LevelSelectScreen(state = state, onPlayLevel = onPlayLevel, onMenu = onMenu)
}

@Composable
fun LevelSelectScreen(
    state: LevelSelectUiState,
    onPlayLevel: (Int) -> Unit,
    onMenu: () -> Unit
) {
    DuskBackground(overlay = DuskOverlays.plain) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.levels_title),
                subtitle = stringResource(
                    R.string.counter_value,
                    state.medalPoints,
                    state.medalMax
                ),
                trailing = {
                    Icon(
                        painter = painterResource(R.drawable.ic_medal),
                        contentDescription = null,
                        tint = GoldMedal,
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
            if (state.fresh) {
                Text(
                    text = stringResource(R.string.levels_fresh_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = SandDeep,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(state.levels) { level ->
                    LevelTile(level = level, onPlayLevel = onPlayLevel)
                }
            }
            SecondaryButton(
                text = stringResource(R.string.action_menu),
                onClick = onMenu,
                leadingIcon = R.drawable.icon_home,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 14.dp)
            )
        }
    }
}

@Composable
private fun LevelTile(level: LevelProgress, onPlayLevel: (Int) -> Unit) {
    val lockedLabel = stringResource(
        if (level.unlocked) R.string.state_unlocked else R.string.state_locked
    )
    SkeuoPanel(
        fill = if (level.unlocked) CreamPaint else SandDeep.copy(alpha = 0.45f),
        borderColor = if (level.unlocked) SandDeep else TextMuted.copy(alpha = 0.4f),
        radius = 18.dp,
        elevation = if (level.unlocked) 8.dp else 0.dp,
        contentPadding = 8.dp,
        modifier = Modifier
            .aspectRatio(1f)
            .graphicsLayer { alpha = if (level.unlocked) 1f else 0.6f }
            .semantics { stateDescription = lockedLabel }
            .clickable(enabled = level.unlocked) { onPlayLevel(level.spec.index) }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (level.unlocked) {
                Text(
                    text = level.spec.index.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextOnCream
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = stringResource(level.spec.nameRes),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                repeat(level.spec.laneCount) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(
                                if (level.unlocked) FieldSage else TextMuted,
                                CircleShape
                            )
                    )
                }
                if (level.medal != Medal.NONE) {
                    MedalBadge(
                        medal = level.medal,
                        size = 18.dp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            if (level.bestScore > 0) {
                Text(
                    text = level.bestScore.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = BarnRed,
                    maxLines = 1
                )
            }
        }
    }
}
