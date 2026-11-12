package com.Sasfifu3.Wagraw.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ChiliColorScheme = darkColorScheme(
    primary = AccentChili,
    onPrimary = TextPrimary,
    secondary = AccentEmber,
    onSecondary = TextPrimary,
    tertiary = AccentFresh,
    onTertiary = BgDeep,
    background = BgDeep,
    onBackground = TextPrimary,
    surface = SurfaceClay,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceAlt,
    onSurfaceVariant = TextSecondary,
    error = DangerRed,
    onError = TextPrimary,
    outline = TextTertiary
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ChiliColorScheme,
        typography = AppTypography,
        content = content
    )
}
