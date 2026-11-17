package com.Gugcegu3.Rogzal.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.Gugcegu3.Rogzal.R
import com.Gugcegu3.Rogzal.core.theme.BarnRed
import com.Gugcegu3.Rogzal.core.theme.CreamLight
import com.Gugcegu3.Rogzal.core.theme.TextMuted
import com.Gugcegu3.Rogzal.core.theme.TextOnCream
import com.Gugcegu3.Rogzal.core.ui.PrimaryButton
import com.Gugcegu3.Rogzal.core.ui.SecondaryButton

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onResume,
        shape = RoundedCornerShape(22.dp),
        containerColor = CreamLight,
        title = {
            Text(
                text = stringResource(R.string.pause_title),
                style = MaterialTheme.typography.headlineMedium,
                color = TextOnCream
            )
        },
        text = {
            Text(
                text = stringResource(R.string.pause_body),
                style = MaterialTheme.typography.bodyLarge,
                color = TextMuted
            )
        },
        confirmButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                PrimaryButton(
                    text = stringResource(R.string.action_resume),
                    onClick = onResume,
                    height = 52.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                SecondaryButton(
                    text = stringResource(R.string.action_restart),
                    onClick = onRestart,
                    modifier = Modifier.fillMaxWidth()
                )
                SecondaryButton(
                    text = stringResource(R.string.action_menu),
                    onClick = onMenu,
                    leadingIcon = R.drawable.icon_home,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Composable
fun ConfirmGoDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        containerColor = CreamLight,
        title = {
            Text(
                text = stringResource(R.string.confirm_go_title),
                style = MaterialTheme.typography.headlineMedium,
                color = TextOnCream
            )
        },
        text = {
            Text(
                text = stringResource(R.string.confirm_go_body),
                style = MaterialTheme.typography.bodyLarge,
                color = TextMuted
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.action_go),
                    style = MaterialTheme.typography.labelLarge,
                    color = BarnRed
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.action_back),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextMuted
                )
            }
        }
    )
}
