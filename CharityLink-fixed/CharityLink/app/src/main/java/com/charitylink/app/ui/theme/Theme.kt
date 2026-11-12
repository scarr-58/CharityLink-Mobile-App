package com.charitylink.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Emerald500,
    onPrimary = White,
    primaryContainer = Emerald100,
    onPrimaryContainer = ForestGreen,
    secondary = ForestGreen,
    onSecondary = White,
    secondaryContainer = Emerald50,
    onSecondaryContainer = MidGreen,
    background = PageLight,
    onBackground = InkLight,
    surface = White,
    onSurface = InkLight,
    onSurfaceVariant = Color(0xFF5E6E64),
    surfaceContainer = White
)

private val DarkColors = darkColorScheme(
    primary = Emerald400,
    onPrimary = ForestGreen,
    primaryContainer = MidGreen,
    onPrimaryContainer = Emerald100,
    secondary = LogoGreen,
    onSecondary = ForestGreen,
    secondaryContainer = MidGreen,
    onSecondaryContainer = Emerald100,
    background = DarkBg,
    onBackground = White,
    surface = DarkCard,
    onSurface = White,
    onSurfaceVariant = Color(0xFFA3B5AA),
    surfaceContainer = DarkSurface
)

@Composable
fun CharityLinkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}