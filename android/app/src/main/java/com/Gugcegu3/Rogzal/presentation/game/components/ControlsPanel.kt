package com.Gugcegu3.Rogzal.presentation.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Gugcegu3.Rogzal.R
import com.Gugcegu3.Rogzal.core.theme.CreamLight
import com.Gugcegu3.Rogzal.core.theme.TextMuted
import com.Gugcegu3.Rogzal.core.ui.ChipToggle
import com.Gugcegu3.Rogzal.core.ui.PrimaryButton
import com.Gugcegu3.Rogzal.core.ui.SecondaryButton
import com.Gugcegu3.Rogzal.presentation.game.GamePhase
import com.Gugcegu3.Rogzal.presentation.game.InputMode

@Composable
fun ControlsPanel(
    mode: InputMode,
    phase: GamePhase,
    signsLeft: Int,
    routeLength: Int,
    canUndo: Boolean,
    onMode: (InputMode) -> Unit,
    onUndo: () -> Unit,
    onClear: () -> Unit,
    onGo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val planning = phase == GamePhase.PLANNING
    Surface(
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        color = CreamLight.copy(alpha = 0.96f),
        shadowElevation = 16.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ChipToggle(
                    text = stringResource(R.string.mode_route),
                    selected = mode == InputMode.ROUTE,
                    onClick = { onMode(InputMode.ROUTE) },
                    enabled = planning,
                    stateLabel = stringResource(
                        if (mode == InputMode.ROUTE) R.string.state_on else R.string.state_off
                    ),
                    modifier = Modifier.weight(1f)
                )
                ChipToggle(
                    text = stringResource(R.string.mode_sign),
                    selected = mode == InputMode.SIGN,
                    onClick = { onMode(InputMode.SIGN) },
                    enabled = planning && signsLeft > 0,
                    stateLabel = stringResource(
                        if (mode == InputMode.SIGN) R.string.state_on else R.string.state_off
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SecondaryButton(
                    text = stringResource(R.string.action_undo),
                    onClick = onUndo,
                    enabled = planning && canUndo,
                    leadingIcon = R.drawable.ic_undo,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.action_clear),
                    onClick = onClear,
                    enabled = planning && (canUndo || routeLength > 0),
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                text = stringResource(R.string.game_hint),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            PrimaryButton(
                text = stringResource(
                    if (phase == GamePhase.RUNNING) R.string.action_hold else R.string.action_go
                ),
                onClick = onGo,
                leadingIcon = if (phase == GamePhase.RUNNING) null else R.drawable.ic_play_arrow,
                height = 58.dp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
