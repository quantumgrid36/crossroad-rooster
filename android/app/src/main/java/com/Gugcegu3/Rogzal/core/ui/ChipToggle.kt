package com.Gugcegu3.Rogzal.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.Gugcegu3.Rogzal.core.theme.BarnRed
import com.Gugcegu3.Rogzal.core.theme.CreamLight
import com.Gugcegu3.Rogzal.core.theme.CreamPaint
import com.Gugcegu3.Rogzal.core.theme.SandDeep
import com.Gugcegu3.Rogzal.core.theme.TextOnCream

@Composable
fun ChipToggle(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    stateLabel: String? = null
) {
    val shape = RoundedCornerShape(14.dp)
    val fill by animateColorAsState(
        targetValue = if (selected) BarnRed else CreamPaint,
        label = "chipFill"
    )
    val content = if (selected) CreamLight else TextOnCream
    val semanticsModifier = stateLabel?.let { label ->
        Modifier.semantics { stateDescription = label }
    } ?: Modifier
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .heightIn(min = 48.dp)
            .graphicsLayer { alpha = if (enabled) 1f else 0.45f }
            .clip(shape)
            .background(fill)
            .border(BorderStroke(2.dp, if (selected) BarnRed else SandDeep), shape)
            .then(semanticsModifier)
    ) {
        TextButton(
            onClick = onClick,
            enabled = enabled,
            shape = shape,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = content,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (!enabled) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(2.dp)
                    .background(SandDeep)
            )
        }
    }
}
