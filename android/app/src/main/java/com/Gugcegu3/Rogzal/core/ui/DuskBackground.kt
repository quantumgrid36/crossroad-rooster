package com.Gugcegu3.Rogzal.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.Gugcegu3.Rogzal.R
import com.Gugcegu3.Rogzal.core.theme.DuskDeep
import com.Gugcegu3.Rogzal.core.theme.DuskMid
import com.Gugcegu3.Rogzal.core.theme.DuskShadow

object DuskOverlays {
    val splash = listOf(
        0f to DuskShadow.copy(alpha = 0.88f),
        0.55f to DuskMid.copy(alpha = 0.92f),
        1f to DuskDeep.copy(alpha = 0.96f)
    )
    val menu = listOf(
        0f to DuskShadow.copy(alpha = 0.18f),
        0.46f to DuskShadow.copy(alpha = 0.34f),
        1f to DuskDeep.copy(alpha = 0.72f)
    )
    val game = listOf(
        0f to DuskShadow.copy(alpha = 0.62f),
        0.5f to DuskMid.copy(alpha = 0.40f),
        1f to DuskShadow.copy(alpha = 0.72f)
    )
    val result = listOf(
        0f to DuskDeep.copy(alpha = 0.80f),
        1f to DuskShadow.copy(alpha = 0.92f)
    )
    val plain = listOf(
        0f to DuskShadow.copy(alpha = 0.74f),
        1f to DuskDeep.copy(alpha = 0.88f)
    )
}

@Composable
fun DuskBackground(
    overlay: List<Pair<Float, Color>>,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(*overlay.toTypedArray()))
        )
        content()
    }
}
