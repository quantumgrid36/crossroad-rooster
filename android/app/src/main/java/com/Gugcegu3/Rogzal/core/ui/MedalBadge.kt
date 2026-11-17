package com.Gugcegu3.Rogzal.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Gugcegu3.Rogzal.R
import com.Gugcegu3.Rogzal.core.theme.BronzeMedal
import com.Gugcegu3.Rogzal.core.theme.GoldMedal
import com.Gugcegu3.Rogzal.core.theme.SilverMedal
import com.Gugcegu3.Rogzal.core.theme.TextMuted
import com.Gugcegu3.Rogzal.domain.model.Medal

fun medalColor(medal: Medal): Color = when (medal) {
    Medal.GOLD -> GoldMedal
    Medal.SILVER -> SilverMedal
    Medal.BRONZE -> BronzeMedal
    Medal.NONE -> TextMuted
}

@Composable
fun MedalBadge(
    medal: Medal,
    size: Dp,
    modifier: Modifier = Modifier,
    description: String? = null
) {
    val tint = medalColor(medal)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .background(tint.copy(alpha = if (medal == Medal.NONE) 0.08f else 0.18f), CircleShape)
            .border(2.dp, tint.copy(alpha = if (medal == Medal.NONE) 0.35f else 0.9f), CircleShape)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_medal),
            contentDescription = description,
            tint = tint.copy(alpha = if (medal == Medal.NONE) 0.4f else 1f),
            modifier = Modifier.size(size * 0.56f)
        )
    }
}
