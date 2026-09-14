package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/**
 * Curated color palette styles for SaveBite.
 */
enum class AppColorPalette(
    val id: String,
    val displayName: String,
    val subtitle: String,
    val primaryColor: Color,
    val primaryLight: Color,
    val primaryDark: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondaryColor: Color,
    val secondaryLight: Color,
    val secondaryDark: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val canvasLight: Color,
    val surfaceVariantLight: Color,
    val borderLight: Color,
    val canvasDark: Color,
    val surfaceDark: Color,
    val surfaceVariantDark: Color,
    val borderDark: Color
) {
    FRESH_EMERALD(
        id = "fresh_emerald",
        displayName = "Fresh Emerald",
        subtitle = "Lush botanical mint & golden harvest",
        primaryColor = Color(0xFF059669),
        primaryLight = Color(0xFF34D399),
        primaryDark = Color(0xFF064E3B),
        primaryContainer = Color(0xFFDCFCE7),
        onPrimaryContainer = Color(0xFF064E3B),
        secondaryColor = Color(0xFFF59E0B),
        secondaryLight = Color(0xFFFDE68A),
        secondaryDark = Color(0xFFB45309),
        secondaryContainer = Color(0xFFFEF3C7),
        onSecondaryContainer = Color(0xFF78350F),
        canvasLight = Color(0xFFF7FAF8),
        surfaceVariantLight = Color(0xFFEEF4F0),
        borderLight = Color(0xFFE0EAE3),
        canvasDark = Color(0xFF0C1410),
        surfaceDark = Color(0xFF13201B),
        surfaceVariantDark = Color(0xFF1A2C25),
        borderDark = Color(0xFF284439)
    ),
    SUNSET_HARVEST(
        id = "sunset_harvest",
        displayName = "Sunset Bistro",
        subtitle = "Warm artisan terracotta & golden crust",
        primaryColor = Color(0xFFE05322),
        primaryLight = Color(0xFFFB923C),
        primaryDark = Color(0xFF9A3412),
        primaryContainer = Color(0xFFFFEDD5),
        onPrimaryContainer = Color(0xFF7C2D12),
        secondaryColor = Color(0xFFD97706),
        secondaryLight = Color(0xFFFDE68A),
        secondaryDark = Color(0xFF92400E),
        secondaryContainer = Color(0xFFFEF3C7),
        onSecondaryContainer = Color(0xFF78350F),
        canvasLight = Color(0xFFFAF7F5),
        surfaceVariantLight = Color(0xFFF5EFEB),
        borderLight = Color(0xFFEBE0D8),
        canvasDark = Color(0xFF140F0D),
        surfaceDark = Color(0xFF201815),
        surfaceVariantDark = Color(0xFF2E221E),
        borderDark = Color(0xFF45332C)
    ),
    MIDNIGHT_BERRY(
        id = "midnight_berry",
        displayName = "Midnight Berry",
        subtitle = "Vibrant electric indigo & honey nectar",
        primaryColor = Color(0xFF4F46E5),
        primaryLight = Color(0xFF818CF8),
        primaryDark = Color(0xFF312E81),
        primaryContainer = Color(0xFFEEF2FF),
        onPrimaryContainer = Color(0xFF1E1B4B),
        secondaryColor = Color(0xFFF59E0B),
        secondaryLight = Color(0xFFFDE68A),
        secondaryDark = Color(0xFFB45309),
        secondaryContainer = Color(0xFFFEF3C7),
        onSecondaryContainer = Color(0xFF78350F),
        canvasLight = Color(0xFFF8F9FC),
        surfaceVariantLight = Color(0xFFEEF1F8),
        borderLight = Color(0xFFE0E5F2),
        canvasDark = Color(0xFF0E111B),
        surfaceDark = Color(0xFF161B2B),
        surfaceVariantDark = Color(0xFF20263D),
        borderDark = Color(0xFF323B5E)
    ),
    BOTANICAL_SAGE(
        id = "botanical_sage",
        displayName = "Nordic Sage",
        subtitle = "Artisan forest olive & almond earth",
        primaryColor = Color(0xFF2D6A4F),
        primaryLight = Color(0xFF52B788),
        primaryDark = Color(0xFF1B4332),
        primaryContainer = Color(0xFFD8F3DC),
        onPrimaryContainer = Color(0xFF081C15),
        secondaryColor = Color(0xFFB07D62),
        secondaryLight = Color(0xFFE6CCB2),
        secondaryDark = Color(0xFF7F5539),
        secondaryContainer = Color(0xFFF7EDE2),
        onSecondaryContainer = Color(0xFF4A3525),
        canvasLight = Color(0xFFF7FAF7),
        surfaceVariantLight = Color(0xFFEFF4EF),
        borderLight = Color(0xFFE1EAE1),
        canvasDark = Color(0xFF0F1813),
        surfaceDark = Color(0xFF17241D),
        surfaceVariantDark = Color(0xFF22352B),
        borderDark = Color(0xFF355243)
    )
}

/**
 * Display Theme Mode (System, Light, Dark).
 */
enum class AppThemeMode(val displayName: String) {
    SYSTEM("Auto"),
    LIGHT("Light"),
    DARK("Dark")
}

// ---------------------------------------------------------------------------
// Dynamic Theme Token Accessors
// These evaluate against MaterialTheme.colorScheme at composable call sites,
// enabling instantaneous app-wide reactive theming across all screens.
// ---------------------------------------------------------------------------

val SaveBiteEmerald: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primary

val SaveBiteEmeraldDark: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primaryContainer

val SaveBiteEmeraldLight: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primary

val SaveBiteContainerGreen: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primaryContainer

val SaveBiteOnContainerGreen: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onPrimaryContainer

val SaveBiteAmber: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.secondary

val SaveBiteAmberLight: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.secondaryContainer

val SaveBiteAmberDark: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSecondaryContainer

val SaveBiteCanvas: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.background

val SaveBiteSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surface

val SaveBiteSurfaceVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceVariant

val SaveBiteBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.outline

val SaveBiteTextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurface

val SaveBiteTextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

val SaveBiteTextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.outline

// Universal Badge & Status Colors
val SaveBiteBadgeRed = Color(0xFFEF4444)
val SaveBiteBadgeRedBg = Color(0xFFFEE2E2)
val SaveBiteBadgeGreen = Color(0xFF10B981)
val SaveBiteBadgeGreenBg = Color(0xFFDCFCE7)
