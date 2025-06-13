package com.android.core.core_design_system

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


data class ThemeColorsTemplate(
    private val darkTheme: Boolean, val light: ColorScheme, val dark: ColorScheme
) {
    val colors: ColorScheme
        get() {
            return when (darkTheme) {
                true -> dark
                false -> light
            }
        }
}

private val LightColors = lightColorScheme()

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFF3366),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF000000),

    secondary = Color(0xFF547AFF),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFA8B9FF),
    onSecondaryContainer = Color(0xFF00133E),

    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF212121),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF212121),

    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF),

    outline = Color(0xFFBDBDBD)
)

@Composable
fun themeColors(darkTheme: Boolean): ThemeColorsTemplate {
    return ThemeColorsTemplate(darkTheme, LightColors, DarkColors)
}
