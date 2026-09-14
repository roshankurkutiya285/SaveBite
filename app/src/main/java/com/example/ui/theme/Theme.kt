package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SaveBiteEmeraldLight,
    onPrimary = Color.White,
    primaryContainer = SaveBiteEmeraldDark,
    onPrimaryContainer = SaveBiteContainerGreen,
    secondary = SaveBiteAmber,
    onSecondary = Color.Black,
    secondaryContainer = SaveBiteAmberDark,
    onSecondaryContainer = SaveBiteAmberLight,
    background = Color(0xFF0F1714),
    surface = Color(0xFF16221E),
    onBackground = Color(0xFFECEFEA),
    onSurface = Color(0xFFECEFEA),
    surfaceVariant = Color(0xFF22332C),
    outline = Color(0xFF3B5047)
)

private val LightColorScheme = lightColorScheme(
    primary = SaveBiteEmerald,
    onPrimary = Color.White,
    primaryContainer = SaveBiteContainerGreen,
    onPrimaryContainer = SaveBiteOnContainerGreen,
    secondary = SaveBiteAmber,
    onSecondary = Color.White,
    secondaryContainer = SaveBiteAmberLight,
    onSecondaryContainer = SaveBiteAmberDark,
    background = SaveBiteCanvas,
    surface = SaveBiteSurface,
    surfaceVariant = SaveBiteSurfaceVariant,
    onBackground = SaveBiteTextPrimary,
    onSurface = SaveBiteTextPrimary,
    outline = SaveBiteBorder
)

@Composable
fun SaveBiteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent SaveBite branding across Android OS versions
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
