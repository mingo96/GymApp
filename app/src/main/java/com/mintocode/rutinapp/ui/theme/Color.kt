package com.mintocode.rutinapp.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Kinetic Precision color palette for RutinApp.
 *
 * Follows Material3 tonal palette conventions with full surface container hierarchy.
 * Dark palette optimized for gym/low-light environments.
 * Light palette for outdoor/bright environments.
 * Colors sourced from Stitch KP design system.
 */
object RutinAppColors {

    /** Dark theme palette — Kinetic Precision dark mode. */
    object Dark {
        val background = Color(0xFF131318)
        val surface = Color(0xFF131318)
        val surfaceDim = Color(0xFF131318)
        val surfaceBright = Color(0xFF39393E)
        val surfaceContainerLowest = Color(0xFF0E0E13)
        val surfaceContainerLow = Color(0xFF1B1B20)
        val surfaceContainer = Color(0xFF1F1F24)
        val surfaceContainerHigh = Color(0xFF2A292F)
        val surfaceContainerHighest = Color(0xFF35343A)
        val surfaceVariant = Color(0xFF35343A)
        val surfaceElevated = Color(0xFF2A292F)
        val surfaceTint = Color(0xFFBAC3FF)

        val primary = Color(0xFFBAC3FF)
        val primaryContainer = Color(0xFF4361EE)
        val onPrimary = Color(0xFF00218D)
        val onPrimaryContainer = Color(0xFFF4F2FF)
        val primaryFixed = Color(0xFFDEE1FF)
        val primaryFixedDim = Color(0xFFBAC3FF)
        val onPrimaryFixed = Color(0xFF001159)
        val onPrimaryFixedVariant = Color(0xFF0031C4)
        val inversePrimary = Color(0xFF2E4EDC)

        val secondary = Color(0xFFD2BBFF)
        val secondaryContainer = Color(0xFF6800E4)
        val onSecondary = Color(0xFF3E008E)
        val onSecondaryContainer = Color(0xFFD2BBFF)
        val secondaryFixed = Color(0xFFEADDFF)
        val secondaryFixedDim = Color(0xFFD2BBFF)
        val onSecondaryFixed = Color(0xFF25005A)
        val onSecondaryFixedVariant = Color(0xFF5900C6)

        val tertiary = Color(0xFF27E0A9)
        val tertiaryContainer = Color(0xFF007F5D)
        val onTertiary = Color(0xFF003827)
        val onTertiaryContainer = Color(0xFFCDFFE7)
        val tertiaryFixed = Color(0xFF54FDC4)
        val tertiaryFixedDim = Color(0xFF27E0A9)
        val onTertiaryFixed = Color(0xFF002116)
        val onTertiaryFixedVariant = Color(0xFF00513B)

        val onBackground = Color(0xFFE4E1E9)
        val onSurface = Color(0xFFE4E1E9)
        val onSurfaceVariant = Color(0xFFC4C5D7)
        val inverseSurface = Color(0xFFE4E1E9)
        val inverseOnSurface = Color(0xFF303035)

        val outline = Color(0xFF8E8FA1)
        val outlineVariant = Color(0xFF444655)

        val error = Color(0xFFFFB4AB)
        val errorContainer = Color(0xFF93000A)
        val onError = Color(0xFF690005)
        val onErrorContainer = Color(0xFFFFDAD6)

        val warning = Color(0xFFFFB703)
        val success = Color(0xFF27E0A9)
        val info = Color(0xFF4CC9F0)

        val scrim = Color(0x80000000)
    }

    /** Light theme palette — Kinetic Precision light mode. */
    object Light {
        val background = Color(0xFFFCF8FF)
        val surface = Color(0xFFFCF8FF)
        val surfaceDim = Color(0xFFDCD8E0)
        val surfaceBright = Color(0xFFFCF8FF)
        val surfaceContainerLowest = Color(0xFFFFFFFF)
        val surfaceContainerLow = Color(0xFFF7F2FA)
        val surfaceContainer = Color(0xFFF1ECF4)
        val surfaceContainerHigh = Color(0xFFEBE6EE)
        val surfaceContainerHighest = Color(0xFFE5E1E9)
        val surfaceVariant = Color(0xFFE5E1E9)
        val surfaceElevated = Color(0xFFFFFFFF)
        val surfaceTint = Color(0xFF4361EE)

        val primary = Color(0xFF3B52CC)
        val primaryContainer = Color(0xFFDDE1FF)
        val onPrimary = Color(0xFFFFFFFF)
        val onPrimaryContainer = Color(0xFF001159)
        val primaryFixed = Color(0xFFDEE1FF)
        val primaryFixedDim = Color(0xFFBAC3FF)
        val onPrimaryFixed = Color(0xFF001159)
        val onPrimaryFixedVariant = Color(0xFF0031C4)
        val inversePrimary = Color(0xFFBAC3FF)

        val secondary = Color(0xFF6B5E9E)
        val secondaryContainer = Color(0xFFE8E0F5)
        val onSecondary = Color(0xFFFFFFFF)
        val onSecondaryContainer = Color(0xFF25005A)
        val secondaryFixed = Color(0xFFEADDFF)
        val secondaryFixedDim = Color(0xFFD2BBFF)
        val onSecondaryFixed = Color(0xFF25005A)
        val onSecondaryFixedVariant = Color(0xFF5900C6)

        val tertiary = Color(0xFF05B384)
        val tertiaryContainer = Color(0xFFBBF5E4)
        val onTertiary = Color(0xFFFFFFFF)
        val onTertiaryContainer = Color(0xFF002117)
        val tertiaryFixed = Color(0xFF54FDC4)
        val tertiaryFixedDim = Color(0xFF27E0A9)
        val onTertiaryFixed = Color(0xFF002116)
        val onTertiaryFixedVariant = Color(0xFF00513B)

        val onBackground = Color(0xFF1B1B21)
        val onSurface = Color(0xFF1B1B21)
        val onSurfaceVariant = Color(0xFF46464F)
        val inverseSurface = Color(0xFF303035)
        val inverseOnSurface = Color(0xFFF3EFF7)

        val outline = Color(0xFF777680)
        val outlineVariant = Color(0xFFC7C5D0)

        val error = Color(0xFFBA1A40)
        val errorContainer = Color(0xFFFFDAD6)
        val onError = Color(0xFFFFFFFF)
        val onErrorContainer = Color(0xFF410002)

        val warning = Color(0xFFE5A400)
        val success = Color(0xFF05B384)
        val info = Color(0xFF0086B3)

        val scrim = Color(0x33000000)
    }
}