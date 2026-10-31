package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AmberGoldPrimary,
    onPrimary = Color(0xFF1E1405),
    primaryContainer = BronzeSurfaceElevated,
    onPrimaryContainer = AmberGlow,
    secondary = AmberGoldVariant,
    onSecondary = Color.White,
    secondaryContainer = BronzeCardDark,
    onSecondaryContainer = OnDarkTextPrimary,
    tertiary = AmberGlow,
    onTertiary = Color(0xFF261803),
    background = ObsidianBg,
    onBackground = OnDarkTextPrimary,
    surface = BronzeSurfaceDark,
    onSurface = OnDarkTextPrimary,
    surfaceVariant = BronzeCardDark,
    onSurfaceVariant = OnDarkTextSecondary,
    outline = BronzeBorderDark,
    error = MutedMicRed
)

private val LightColorScheme = lightColorScheme(
    primary = AmberGoldVariant,
    onPrimary = Color.White,
    primaryContainer = SandCardLight,
    onPrimaryContainer = Color(0xFF332004),
    secondary = AmberGoldPrimary,
    onSecondary = Color(0xFF1E1405),
    secondaryContainer = SandSurfaceLight,
    onSecondaryContainer = OnLightTextPrimary,
    tertiary = AmberGoldVariant,
    onTertiary = Color.White,
    background = ParchmentBg,
    onBackground = OnLightTextPrimary,
    surface = SandSurfaceLight,
    onSurface = OnLightTextPrimary,
    surfaceVariant = SandCardLight,
    onSurfaceVariant = OnLightTextSecondary,
    outline = SandBorderLight,
    error = MutedMicRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
