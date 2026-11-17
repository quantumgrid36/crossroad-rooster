package com.Gugcegu3.Rogzal.presentation.howto

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.Gugcegu3.Rogzal.R
import com.Gugcegu3.Rogzal.core.theme.Asphalt
import com.Gugcegu3.Rogzal.core.theme.BarnRed
import com.Gugcegu3.Rogzal.core.theme.CreamPaint
import com.Gugcegu3.Rogzal.core.theme.FieldSage
import com.Gugcegu3.Rogzal.core.theme.TextMuted
import com.Gugcegu3.Rogzal.core.theme.TextOnCream
import com.Gugcegu3.Rogzal.core.ui.DuskBackground
import com.Gugcegu3.Rogzal.core.ui.DuskOverlays
import com.Gugcegu3.Rogzal.core.ui.ScreenHeader
import com.Gugcegu3.Rogzal.core.ui.SecondaryButton
import com.Gugcegu3.Rogzal.core.ui.SkeuoPanel
import com.Gugcegu3.Rogzal.data.sample.SampleData

@Composable
fun HowToPlayScreen(onMenu: () -> Unit) {
    DuskBackground(overlay = DuskOverlays.plain) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(title = stringResource(R.string.howto_title))
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                SampleData.howToSteps.forEach { step ->
                    SkeuoPanel(modifier = Modifier.fillMaxWidth(), contentPadding = 12.dp) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BarnRed.copy(alpha = 0.14f))
                            ) {
                                Icon(
                                    painter = painterResource(step.iconRes),
                                    contentDescription = null,
                                    tint = BarnRed,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = stringResource(step.titleRes),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextOnCream
                                )
                                Text(
                                    text = stringResource(step.bodyRes),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
                SkeuoPanel(modifier = Modifier.fillMaxWidth(), contentPadding = 12.dp) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.howto_diagram_title),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        MiniDiagram()
                    }
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
private fun MiniDiagram() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
    ) {
        val unit = size.height / 3f
        val originX = (size.width - unit * 3f) / 2f
        for (row in 0 until 3) {
            for (col in 0 until 3) {
                val topLeft = Offset(originX + col * unit, row * unit)
                drawRect(
                    color = if (row == 1) Asphalt else FieldSage,
                    topLeft = topLeft,
                    size = Size(unit, unit)
                )
            }
        }
        drawLine(
            color = CreamPaint,
            start = Offset(originX + unit * 1.5f, unit * 2.6f),
            end = Offset(originX + unit * 1.5f, unit * 0.4f),
            strokeWidth = 7f,
            cap = StrokeCap.Round
        )
        drawCircle(
            color = BarnRed,
            radius = unit * 0.18f,
            center = Offset(originX + unit * 1.5f, unit * 1.5f)
        )
    }
}
