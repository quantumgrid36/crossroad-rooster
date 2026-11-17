package com.Gugcegu3.Rogzal.presentation.game.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.Gugcegu3.Rogzal.R
import com.Gugcegu3.Rogzal.core.theme.BarnRed
import com.Gugcegu3.Rogzal.core.theme.CreamPaint
import com.Gugcegu3.Rogzal.core.theme.FieldSageDeep
import com.Gugcegu3.Rogzal.core.theme.SandDeep
import com.Gugcegu3.Rogzal.core.theme.TextMuted
import com.Gugcegu3.Rogzal.core.theme.TextOnCream
import com.Gugcegu3.Rogzal.domain.model.LanePreview

@Composable
fun GameStrip(
    ribbon: List<LanePreview>,
    ribbonEnabled: Boolean,
    signsLeft: Int,
    signBudget: Int,
    patienceLeft: Int,
    patienceTotal: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CreamPaint.copy(alpha = 0.94f),
        border = BorderStroke(2.dp, SandDeep),
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                if (ribbonEnabled) {
                    RhythmRibbon(lanes = ribbon)
                } else {
                    Text(
                        text = stringResource(R.string.ribbon_hidden),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
            StripDivider()
            CounterSegment(
                iconRes = R.drawable.ic_sign,
                value = signsLeft,
                total = signBudget,
                description = stringResource(R.string.strip_signs)
            )
            StripDivider()
            CounterSegment(
                iconRes = R.drawable.ic_feather,
                value = patienceLeft,
                total = patienceTotal,
                description = stringResource(R.string.strip_patience)
            )
        }
    }
}

@Composable
private fun StripDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(32.dp)
            .background(SandDeep)
    )
}

@Composable
private fun CounterSegment(iconRes: Int, value: Int, total: Int, description: String) {
    val state = stringResource(R.string.counter_state, description, value, total)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .semantics { stateDescription = state }
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = BarnRed,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = stringResource(R.string.counter_value, value, total),
            style = MaterialTheme.typography.titleSmall,
            color = TextOnCream
        )
    }
}

@Composable
fun RhythmRibbon(lanes: List<LanePreview>, modifier: Modifier = Modifier) {
    if (lanes.isEmpty()) return
    Column(
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = modifier
    ) {
        lanes.forEach { lane ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 10.dp, height = 3.dp)
                        .background(
                            if (lane.heavy) BarnRed else FieldSageDeep,
                            RoundedCornerShape(2.dp)
                        )
                )
                lane.pips.forEach { occupied ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = when {
                                    lane.hidden -> SandDeep
                                    occupied -> BarnRed
                                    else -> CreamPaint
                                },
                                shape = CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = if (lane.hidden) TextMuted else SandDeep,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}
