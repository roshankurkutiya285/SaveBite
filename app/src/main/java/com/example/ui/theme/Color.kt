package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/**
 * Curated color palette styles celebrating Indian culinary vibrancy & aesthetics.
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
    ROYAL_SAFFRON(
        id = "royal_saffron",
        displayName = "Royal Saffron",
        subtitle = "Aromatic Kesari saffron & turmeric marigold",
        primaryColor = Color(0xFFE05322), // Saffron / Kesari
        primaryLight = Color(0xFFFB923C),
        primaryDark = Color(0xFF9A3412),
        primaryContainer = Color(0xFFFFEDD5),
        onPrimaryContainer = Color(0xFF7C2D12),
        secondaryColor = Color(0xFFD97706), // Marigold amber
        secondaryLight = Color(0xFFFDE68A),
        secondaryDark = Color(0xFF92400E),
        secondaryContainer = Color(0xFFFEF3C7),
        onSecondaryContainer = Color(0xFF78350F),
        canvasLight = Color(0xFFFAF7F4),
        surfaceVariantLight = Color(0xFFF5EFEB),
        borderLight = Color(0xFFEBE0D8),
        canvasDark = Color(0xFF140F0D),
        surfaceDark = Color(0xFF201815),
        surfaceVariantDark = Color(0xFF2E221E),
        borderDark = Color(0xFF45332C)
    ),
    PEACOCK_EMERALD(
        id = "peacock_emerald",
        displayName = "Peacock & Tulsi",
        subtitle = "Sacred botanical mint & royal peacock teal",
        primaryColor = Color(0xFF0D7A53),
        primaryLight = Color(0xFF34D399),
        primaryDark = Color(0xFF064E3B),
        primaryContainer = Color(0xFFDCFCE7),
        onPrimaryContainer = Color(0xFF064E3B),
        secondaryColor = Color(0xFFE05322),
        secondaryLight = Color(0xFFFED7AA),
        secondaryDark = Color(0xFFC2410C),
        secondaryContainer = Color(0xFFFFEDD5),
        onSecondaryContainer = Color(0xFF7C2D12),
        canvasLight = Color(0xFFF7FAF8),
        surfaceVariantLight = Color(0xFFEEF4F0),
        borderLight = Color(0xFFE0EAE3),
        canvasDark = Color(0xFF0C1410),
        surfaceDark = Color(0xFF13201B),
        surfaceVariantDark = Color(0xFF1A2C25),
        borderDark = Color(0xFF284439)
    ),
    KOLKATA_TERRACOTTA(
        id = "kolkata_terracotta",
        displayName = "Kulhad Terracotta",
        subtitle = "Artisanal clay chai & fragrant roasted spices",
        primaryColor = Color(0xFFB43E19),
        primaryLight = Color(0xFFF97316),
        primaryDark = Color(0xFF7C2D12),
        primaryContainer = Color(0xFFFFEAD9),
        onPrimaryContainer = Color(0xFF67220C),
        secondaryColor = Color(0xFF0F766E),
        secondaryLight = Color(0xFF99F6E4),
        secondaryDark = Color(0xFF115E59),
        secondaryContainer = Color(0xFFCCFBF1),
        onSecondaryContainer = Color(0xFF134E4A),
        canvasLight = Color(0xFFFAF6F2),
        surfaceVariantLight = Color(0xFFF3ECE6),
        borderLight = Color(0xFFE6DBD3),
        canvasDark = Color(0xFF15100E),
        surfaceDark = Color(0xFF231B18),
        surfaceVariantDark = Color(0xFF322723),
        borderDark = Color(0xFF4A3A35)
    ),
    MITHAI_ROSE(
        id = "mithai_rose",
        displayName = "Mithai Gulab",
        subtitle = "Royal Gulab Jamun crimson & pistachio gold",
        primaryColor = Color(0xFFBE185D),
        primaryLight = Color(0xFFF472B6),
        primaryDark = Color(0xFF831843),
        primaryContainer = Color(0xFFFCE7F3),
        onPrimaryContainer = Color(0xFF700732),
        secondaryColor = Color(0xFFD97706),
        secondaryLight = Color(0xFFFDE68A),
        secondaryDark = Color(0xFF92400E),
        secondaryContainer = Color(0xFFFEF3C7),
        onSecondaryContainer = Color(0xFF78350F),
        canvasLight = Color(0xFFFAF7F8),
        surfaceVariantLight = Color(0xFFF5EDF0),
        borderLight = Color(0xFFEBDFE4),
        canvasDark = Color(0xFF160F13),
        surfaceDark = Color(0xFF241820),
        surfaceVariantDark = Color(0xFF34222E),
        borderDark = Color(0xFF4E3345)
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
val SaveBiteBadgeRed = Color(0xFFDC2626)
val SaveBiteBadgeRedBg = Color(0xFFFEE2E2)
val SaveBiteBadgeGreen = Color(0xFF16A34A)
val SaveBiteBadgeGreenBg = Color(0xFFDCFCE7)
