package com.Gugcegu3.Rogzal.presentation.gameover.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Gugcegu3.Rogzal.R
import com.Gugcegu3.Rogzal.core.config.GameConfig
import com.Gugcegu3.Rogzal.core.theme.BarnRed
import com.Gugcegu3.Rogzal.core.theme.SandDeep
import com.Gugcegu3.Rogzal.core.theme.TextMuted
import com.Gugcegu3.Rogzal.core.theme.TextOnCream
import com.Gugcegu3.Rogzal.core.ui.SkeuoPanel
import com.Gugcegu3.Rogzal.domain.model.Cell
import com.Gugcegu3.Rogzal.domain.model.RowKind
import com.Gugcegu3.Rogzal.domain.model.TickEvent
import com.Gugcegu3.Rogzal.domain.model.TickFrame
import com.Gugcegu3.Rogzal.presentation.game.components.BoardCanvas
import kotlin.math.floor
import kotlin.math.min

@Composable
fun ReplayPanel(
    frames: List<TickFrame>,
    rows: List<RowKind>,
    barn: Cell,
    tickIndex: Int,
    playing: Boolean,
    onTickChange: (Int) -> Unit,
    onToggleReplay: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (frames.isEmpty()) return
    val safeIndex = tickIndex.coerceIn(0, frames.lastIndex)
    val frame = frames[safeIndex]
    SkeuoPanel(modifier = modifier.fillMaxWidth(), contentPadding = 12.dp) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.replay_title),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            BoxWithConstraints(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                val frameInset = 6.dp
                val boardWidth = (maxWidth - 16.dp).coerceAtMost(240.dp)
                val unit = floor(((boardWidth - frameInset * 2) / GameConfig.COLS).value)
                val cell = min(unit, 26f).coerceAtLeast(14f).dp
                BoardCanvas(
                    rows = rows,
                    cols = GameConfig.COLS,
                    cell = cell,
                    frame = frameInset,
                    rooster = frame.rooster,
                    vehicles = frame.vehicles,
                    parcels = frame.parcels,
                    barn = barn,
                    shadowCells = emptyList(),
                    signs = emptyList(),
                    route = emptyList(),
                    start = frame.rooster,
                    carrying = frame.carrying,
                    highContrast = false,
                    collisionCell = if (frame.event == TickEvent.COLLISION) frame.rooster else null,
                    stepMs = 0,
                    animated = false,
                    showBadges = false,
                    onCellTap = null
                )
            }
            if (frames.size > 1) {
                Slider(
                    value = safeIndex.toFloat(),
                    onValueChange = { value -> onTickChange(value.toInt()) },
                    valueRange = 0f..frames.lastIndex.toFloat(),
                    steps = (frames.size - 2).coerceAtLeast(0),
                    colors = SliderDefaults.colors(
                        thumbColor = BarnRed,
                        activeTrackColor = BarnRed,
                        inactiveTrackColor = SandDeep,
                        activeTickColor = SandDeep,
                        inactiveTickColor = SandDeep
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Text(
                text = stringResource(
                    R.string.replay_caption,
                    frame.tick,
                    laneLabel(frame.laneIndex),
                    stringResource(eventLabelRes(frame.event))
                ),
                style = MaterialTheme.typography.labelSmall,
                color = TextOnCream,
                textAlign = TextAlign.Center
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onToggleReplay) {
                    Text(
                        text = stringResource(
                            if (playing) R.string.action_hold else R.string.action_replay
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = BarnRed,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                }
            }
            Box(modifier = Modifier.padding(1.dp))
        }
    }
}

private fun laneLabel(laneIndex: Int): String = when (laneIndex) {
    0 -> "A"
    1 -> "B"
    2 -> "C"
    3 -> "D"
    else -> "-"
}

private fun eventLabelRes(event: TickEvent): Int = when (event) {
    TickEvent.START -> R.string.event_start
    TickEvent.MOVE -> R.string.event_move
    TickEvent.WAIT -> R.string.event_wait
    TickEvent.PICKUP -> R.string.event_pickup
    TickEvent.DELIVER -> R.string.event_deliver
    TickEvent.NEAR_MISS -> R.string.event_near_miss
    TickEvent.COLLISION -> R.string.event_collision
    TickEvent.FREEZE -> R.string.event_freeze
    TickEvent.RECOVER -> R.string.event_recover
}
