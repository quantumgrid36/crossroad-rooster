package com.Gugcegu3.Rogzal.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.Gugcegu3.Rogzal.R
import com.Gugcegu3.Rogzal.core.theme.FieldSage
import com.Gugcegu3.Rogzal.core.theme.GoldMedal
import com.Gugcegu3.Rogzal.core.theme.SandDeep
import com.Gugcegu3.Rogzal.domain.model.Weather

fun weatherIcon(weather: Weather): Int = when (weather) {
    Weather.CLEAR -> R.drawable.ic_sun
    Weather.RAIN -> R.drawable.ic_rain
    Weather.FOG -> R.drawable.ic_fog
    Weather.WIND -> R.drawable.ic_wind
}

fun weatherLabelRes(weather: Weather): Int = when (weather) {
    Weather.CLEAR -> R.string.weather_clear
    Weather.RAIN -> R.string.weather_rain
    Weather.FOG -> R.string.weather_fog
    Weather.WIND -> R.string.weather_wind
}

fun weatherAccent(weather: Weather): Color = when (weather) {
    Weather.CLEAR -> GoldMedal
    Weather.RAIN -> FieldSage
    Weather.FOG -> SandDeep
    Weather.WIND -> FieldSage
}

@Composable
fun WeatherChip(
    weather: Weather,
    modifier: Modifier = Modifier,
    contentColor: Color = SandDeep
) {
    val accent = weatherAccent(weather)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .background(accent.copy(alpha = 0.16f), RoundedCornerShape(10.dp))
            .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(
            painter = painterResource(weatherIcon(weather)),
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = stringResource(weatherLabelRes(weather)),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            maxLines = 1
        )
    }
}
