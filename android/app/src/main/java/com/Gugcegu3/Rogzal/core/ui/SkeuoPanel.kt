package com.Gugcegu3.Rogzal.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Gugcegu3.Rogzal.core.theme.CreamPaint
import com.Gugcegu3.Rogzal.core.theme.DuskShadow
import com.Gugcegu3.Rogzal.core.theme.Sand

@Composable
fun SkeuoPanel(
    modifier: Modifier = Modifier,
    fill: Color = CreamPaint,
    borderColor: Color = Sand,
    radius: Dp = 18.dp,
    elevation: Dp = 10.dp,
    contentPadding: Dp = 14.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(radius)
    Box(
        modifier = modifier
            .shadow(elevation, shape, ambientColor = DuskShadow, spotColor = DuskShadow)
            .clip(shape)
            .background(fill)
            .border(BorderStroke(2.dp, borderColor), shape)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = radius / 2)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(modifier = Modifier.padding(contentPadding), content = content)
    }
}
