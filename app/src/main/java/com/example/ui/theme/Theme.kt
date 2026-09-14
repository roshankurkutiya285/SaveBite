package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

fun buildLightColorScheme(palette: AppColorPalette): ColorScheme {
    return lightColorScheme(
        primary = palette.primaryColor,
        onPrimary = Color.White,
        primaryContainer = palette.primaryContainer,
        onPrimaryContainer = palette.onPrimaryContainer,
        secondary = palette.secondaryColor,
        onSecondary = Color.White,
        secondaryContainer = palette.secondaryContainer,
        onSecondaryContainer = palette.onSecondaryContainer,
        tertiary = palette.secondaryDark,
        onTertiary = Color.White,
        background = palette.canvasLight,
        surface = Color(0xFFFFFFFF),
        surfaceVariant = palette.surfaceVariantLight,
        onBackground = Color(0xFF131D18),
        onSurface = Color(0xFF131D18),
        onSurfaceVariant = Color(0xFF4B5563),
        outline = palette.borderLight,
        outlineVariant = palette.borderLight.copy(alpha = 0.5f)
    )
}

fun buildDarkColorScheme(palette: AppColorPalette): ColorScheme {
    return darkColorScheme(
        primary = palette.primaryLight,
        onPrimary = Color(0xFF071B13),
        primaryContainer = palette.primaryDark,
        onPrimaryContainer = palette.primaryContainer,
        secondary = palette.secondaryLight,
        onSecondary = Color(0xFF2E1700),
        secondaryContainer = palette.secondaryDark,
        onSecondaryContainer = palette.secondaryLight,
        tertiary = palette.secondaryColor,
        onTertiary = Color.Black,
        background = palette.canvasDark,
        surface = palette.surfaceDark,
        surfaceVariant = palette.surfaceVariantDark,
        onBackground = Color(0xFFF1F5F2),
        onSurface = Color(0xFFF1F5F2),
        onSurfaceVariant = Color(0xFF9CA3AF),
        outline = palette.borderDark,
        outlineVariant = palette.borderDark.copy(alpha = 0.5f)
    )
}

@Composable
fun SaveBiteTheme(
    palette: AppColorPalette = AppColorPalette.ROYAL_SAFFRON,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> buildDarkColorScheme(palette)
        else -> buildLightColorScheme(palette)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
