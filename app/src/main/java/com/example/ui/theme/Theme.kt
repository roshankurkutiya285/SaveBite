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
        onPrimary = Color(0xFF0F172A),
        primaryContainer = palette.primaryDark,
        onPrimaryContainer = Color(0xFFF1F5F9),
        secondary = palette.secondaryLight,
        onSecondary = Color(0xFF1E1000),
        secondaryContainer = palette.secondaryDark,
        onSecondaryContainer = Color(0xFFFEF3C7),
        tertiary = palette.secondaryColor,
        onTertiary = Color.White,
        background = Color(0xFF0F172A),
        surface = Color(0xFF1E293B),
        surfaceVariant = Color(0xFF334155),
        onBackground = Color(0xFFF8FAFC),
        onSurface = Color(0xFFF8FAFC),
        onSurfaceVariant = Color(0xFFCBD5E1),
        outline = Color(0xFF475569),
        outlineVariant = Color(0xFF64748B)
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
