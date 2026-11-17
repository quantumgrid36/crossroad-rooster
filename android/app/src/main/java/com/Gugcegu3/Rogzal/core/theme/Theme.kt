package com.Gugcegu3.Rogzal.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CrossroadColors = lightColorScheme(
    primary = BarnRed,
    onPrimary = CreamLight,
    primaryContainer = BarnRedLight,
    onPrimaryContainer = CreamLight,
    secondary = FieldSage,
    onSecondary = DuskDeep,
    secondaryContainer = FieldSageDeep,
    onSecondaryContainer = CreamLight,
    tertiary = GoldMedal,
    onTertiary = DuskDeep,
    background = DuskShadow,
    onBackground = CreamLight,
    surface = CreamLight,
    onSurface = TextOnCream,
    surfaceVariant = CreamPaint,
    onSurfaceVariant = TextMuted,
    outline = SandDeep,
    outlineVariant = Sand,
    error = BarnRedDark,
    onError = CreamLight,
    scrim = DuskDeep
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CrossroadColors,
        typography = AppTypography,
        content = content
    )
}
