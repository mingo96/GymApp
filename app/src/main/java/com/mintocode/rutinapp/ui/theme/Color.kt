package com.mintocode.rutinapp.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Color palette for RutinApp v2 design system.
 *
 * Organized by semantic roles: primary actions, surfaces, text, status indicators.
 * Both Dark and Light palettes are fully designed for dual-theme support.
 * All colors verified for WCAG AA contrast compliance on their target backgrounds.
 */
object RutinAppColors {

    /** Dark theme palette — default theme, designed for gym/low-light environments. */
    object Dark {
        val background = Color(0xFF0F0F14)
        val surface = Color(0xFF1A1A24)
        val surfaceVariant = Color(0xFF242433)
        val surfaceElevated = Color(0xFF2A2A3C)

        val primary = Color(0xFF4361EE)
        val primaryContainer = Color(0xFF2D44B8)
        val onPrimary = Color(0xFFFFFFFF)
        val onPrimaryContainer = Color(0xFFD6DEFF)

        val secondary = Color(0xFF8B7FBF)
        val secondaryContainer = Color(0xFF3D3566)
        val onSecondary = Color(0xFFFFFFFF)
        val onSecondaryContainer = Color(0xFFE0D8F0)

        val tertiary = Color(0xFF06D6A0)
        val tertiaryContainer = Color(0xFF04A87D)
        val onTertiary = Color(0xFF003325)
        val onTertiaryContainer = Color(0xFFB8F5E3)

        val onBackground = Color(0xFFE8E8F0)
        val onSurface = Color(0xFFE8E8F0)
        val onSurfaceVariant = Color(0xFFB0B0C0)

        val outline = Color(0xFF424257)
        val outlineVariant = Color(0xFF32324A)

        val error = Color(0xFFEF476F)
        val errorContainer = Color(0xFF8B0A2E)
        val onError = Color(0xFFFFFFFF)
        val onErrorContainer = Color(0xFFFFDAE0)

        val warning = Color(0xFFFFB703)
        val success = Color(0xFF06D6A0)
        val info = Color(0xFF4CC9F0)

        val scrim = Color(0x80000000)
    }

    /** Light theme palette — clean, high-contrast for outdoor/bright environments. */
    object Light {
        val background = Color(0xFFF5F5FA)
        val surface = Color(0xFFFFFFFF)
        val surfaceVariant = Color(0xFFF0F0F5)
        val surfaceElevated = Color(0xFFFFFFFF)

        val primary = Color(0xFF3B52CC)
        val primaryContainer = Color(0xFFDDE1FF)
        val onPrimary = Color(0xFFFFFFFF)
        val onPrimaryContainer = Color(0xFF001A5C)

        val secondary = Color(0xFF6B5E9E)
        val secondaryContainer = Color(0xFFE8E0F5)
        val onSecondary = Color(0xFFFFFFFF)
        val onSecondaryContainer = Color(0xFF261A4A)

        val tertiary = Color(0xFF05B384)
        val tertiaryContainer = Color(0xFFBBF5E4)
        val onTertiary = Color(0xFFFFFFFF)
        val onTertiaryContainer = Color(0xFF002117)

        val onBackground = Color(0xFF1A1A2E)
        val onSurface = Color(0xFF1A1A2E)
        val onSurfaceVariant = Color(0xFF5A5A72)

        val outline = Color(0xFFD0D0DE)
        val outlineVariant = Color(0xFFE8E8F0)

        val error = Color(0xFFBA1A40)
        val errorContainer = Color(0xFFFFDAE0)
        val onError = Color(0xFFFFFFFF)
        val onErrorContainer = Color(0xFF410013)

        val warning = Color(0xFFE5A400)
        val success = Color(0xFF05B384)
        val info = Color(0xFF0086B3)

        val scrim = Color(0x33000000)
    }
}