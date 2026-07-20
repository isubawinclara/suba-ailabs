package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = StyleLavenderDark,
    secondary = StyleIndigoDark,
    tertiary = StylePinkDark,
    background = StyleBackgroundDark,
    surface = StyleSurfaceDark,
    onBackground = StyleOnBackgroundDark,
    onSurface = StyleOnSurfaceDark,
    primaryContainer = StyleLavender,
    secondaryContainer = StyleIndigo
)

private val LightColorScheme = lightColorScheme(
    primary = StyleLavender,
    secondary = StyleIndigo,
    tertiary = StylePink,
    background = StyleBackgroundLight,
    surface = StyleSurfaceLight,
    onBackground = StyleOnBackgroundLight,
    onSurface = StyleOnSurfaceLight,
    primaryContainer = ColorHelper.lighten(StyleLavender, 0.9f),
    secondaryContainer = ColorHelper.lighten(StyleIndigo, 0.9f)
)

object ColorHelper {
    fun lighten(color: androidx.compose.ui.graphics.Color, factor: Float): androidx.compose.ui.graphics.Color {
        return androidx.compose.ui.graphics.Color(
            red = color.red + (1f - color.red) * factor,
            green = color.green + (1f - color.green) * factor,
            blue = color.blue + (1f - color.blue) * factor,
            alpha = color.alpha
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // We disable dynamic color to preserve our gorgeous designer styling
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
