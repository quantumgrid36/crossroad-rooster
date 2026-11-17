package com.Gugcegu3.Rogzal.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.Gugcegu3.Rogzal.R
import com.Gugcegu3.Rogzal.core.config.GameConfig
import com.Gugcegu3.Rogzal.core.theme.BarnRed
import com.Gugcegu3.Rogzal.core.theme.CreamLight
import com.Gugcegu3.Rogzal.core.theme.Sand
import com.Gugcegu3.Rogzal.core.theme.SandDeep
import com.Gugcegu3.Rogzal.core.theme.TextMuted
import com.Gugcegu3.Rogzal.core.theme.TextOnCream
import com.Gugcegu3.Rogzal.core.ui.ChipToggle
import com.Gugcegu3.Rogzal.domain.model.GameSettings

@Composable
fun SettingsDialog(
    settings: GameSettings,
    onTickMs: (Int) -> Unit,
    onRibbon: (Boolean) -> Unit,
    onContrast: (Boolean) -> Unit,
    onConfirmGo: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        containerColor = CreamLight,
        titleContentColor = TextOnCream,
        textContentColor = TextMuted,
        title = {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineMedium,
                color = TextOnCream
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.action_done),
                    style = MaterialTheme.typography.labelLarge,
                    color = BarnRed
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = stringResource(R.string.settings_tick_speed),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipToggle(
                        text = stringResource(R.string.speed_calm),
                        selected = settings.tickMs == GameConfig.TICK_CALM_MS,
                        onClick = { onTickMs(GameConfig.TICK_CALM_MS) },
                        modifier = Modifier.weight(1f)
                    )
                    ChipToggle(
                        text = stringResource(R.string.speed_standard),
                        selected = settings.tickMs == GameConfig.TICK_STANDARD_MS,
                        onClick = { onTickMs(GameConfig.TICK_STANDARD_MS) },
                        modifier = Modifier.weight(1f)
                    )
                    ChipToggle(
                        text = stringResource(R.string.speed_brisk),
                        selected = settings.tickMs == GameConfig.TICK_BRISK_MS,
                        onClick = { onTickMs(GameConfig.TICK_BRISK_MS) },
                        modifier = Modifier.weight(1f)
                    )
                }
                SettingSwitch(
                    label = stringResource(R.string.settings_ribbon),
                    hint = stringResource(R.string.settings_ribbon_hint),
                    checked = settings.ribbon,
                    onCheckedChange = onRibbon
                )
                SettingSwitch(
                    label = stringResource(R.string.settings_contrast),
                    hint = stringResource(R.string.settings_contrast_hint),
                    checked = settings.highContrast,
                    onCheckedChange = onContrast
                )
                SettingSwitch(
                    label = stringResource(R.string.settings_confirm_go),
                    hint = stringResource(R.string.settings_confirm_go_hint),
                    checked = settings.confirmGo,
                    onCheckedChange = onConfirmGo
                )
            }
        }
    )
}

@Composable
private fun SettingSwitch(
    label: String,
    hint: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val state = if (checked) {
        stringResource(R.string.state_on)
    } else {
        stringResource(R.string.state_off)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .padding(vertical = 2.dp)
            .semantics { stateDescription = state }
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = TextOnCream
            )
            Text(
                text = hint,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CreamLight,
                checkedTrackColor = BarnRed,
                checkedBorderColor = BarnRed,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = Sand,
                uncheckedBorderColor = SandDeep
            )
        )
    }
}
