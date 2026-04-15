package com.mintocode.rutinapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Extended color tokens for fitness-specific semantics not covered by Material3 roles.
 * Access via LocalRutinAppColors.current within any composable inside RutinAppTheme.
 */
data class RutinAppExtendedColors(
    val warning: Color,
    val success: Color,
    val info: Color,
    val surfaceElevated: Color,
    val scrim: Color
)

val LocalRutinAppColors = staticCompositionLocalOf {
    RutinAppExtendedColors(
        warning = Color.Unspecified,
        success = Color.Unspecified,
        info = Color.Unspecified,
        surfaceElevated = Color.Unspecified,
        scrim = Color.Unspecified
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = RutinAppColors.Dark.primary,
    onPrimary = RutinAppColors.Dark.onPrimary,
    primaryContainer = RutinAppColors.Dark.primaryContainer,
    onPrimaryContainer = RutinAppColors.Dark.onPrimaryContainer,
    secondary = RutinAppColors.Dark.secondary,
    onSecondary = RutinAppColors.Dark.onSecondary,
    secondaryContainer = RutinAppColors.Dark.secondaryContainer,
    onSecondaryContainer = RutinAppColors.Dark.onSecondaryContainer,
    tertiary = RutinAppColors.Dark.tertiary,
    onTertiary = RutinAppColors.Dark.onTertiary,
    tertiaryContainer = RutinAppColors.Dark.tertiaryContainer,
    onTertiaryContainer = RutinAppColors.Dark.onTertiaryContainer,
    background = RutinAppColors.Dark.background,
    onBackground = RutinAppColors.Dark.onBackground,
    surface = RutinAppColors.Dark.surface,
    onSurface = RutinAppColors.Dark.onSurface,
    surfaceVariant = RutinAppColors.Dark.surfaceVariant,
    onSurfaceVariant = RutinAppColors.Dark.onSurfaceVariant,
    surfaceDim = RutinAppColors.Dark.surfaceDim,
    surfaceBright = RutinAppColors.Dark.surfaceBright,
    surfaceContainerLowest = RutinAppColors.Dark.surfaceContainerLowest,
    surfaceContainerLow = RutinAppColors.Dark.surfaceContainerLow,
    surfaceContainer = RutinAppColors.Dark.surfaceContainer,
    surfaceContainerHigh = RutinAppColors.Dark.surfaceContainerHigh,
    surfaceContainerHighest = RutinAppColors.Dark.surfaceContainerHighest,
    surfaceTint = RutinAppColors.Dark.surfaceTint,
    inverseSurface = RutinAppColors.Dark.inverseSurface,
    inverseOnSurface = RutinAppColors.Dark.inverseOnSurface,
    inversePrimary = RutinAppColors.Dark.inversePrimary,
    outline = RutinAppColors.Dark.outline,
    outlineVariant = RutinAppColors.Dark.outlineVariant,
    error = RutinAppColors.Dark.error,
    onError = RutinAppColors.Dark.onError,
    errorContainer = RutinAppColors.Dark.errorContainer,
    onErrorContainer = RutinAppColors.Dark.onErrorContainer,
    scrim = RutinAppColors.Dark.scrim
)

private val LightColorScheme = lightColorScheme(
    primary = RutinAppColors.Light.primary,
    onPrimary = RutinAppColors.Light.onPrimary,
    primaryContainer = RutinAppColors.Light.primaryContainer,
    onPrimaryContainer = RutinAppColors.Light.onPrimaryContainer,
    secondary = RutinAppColors.Light.secondary,
    onSecondary = RutinAppColors.Light.onSecondary,
    secondaryContainer = RutinAppColors.Light.secondaryContainer,
    onSecondaryContainer = RutinAppColors.Light.onSecondaryContainer,
    tertiary = RutinAppColors.Light.tertiary,
    onTertiary = RutinAppColors.Light.onTertiary,
    tertiaryContainer = RutinAppColors.Light.tertiaryContainer,
    onTertiaryContainer = RutinAppColors.Light.onTertiaryContainer,
    background = RutinAppColors.Light.background,
    onBackground = RutinAppColors.Light.onBackground,
    surface = RutinAppColors.Light.surface,
    onSurface = RutinAppColors.Light.onSurface,
    surfaceVariant = RutinAppColors.Light.surfaceVariant,
    onSurfaceVariant = RutinAppColors.Light.onSurfaceVariant,
    surfaceDim = RutinAppColors.Light.surfaceDim,
    surfaceBright = RutinAppColors.Light.surfaceBright,
    surfaceContainerLowest = RutinAppColors.Light.surfaceContainerLowest,
    surfaceContainerLow = RutinAppColors.Light.surfaceContainerLow,
    surfaceContainer = RutinAppColors.Light.surfaceContainer,
    surfaceContainerHigh = RutinAppColors.Light.surfaceContainerHigh,
    surfaceContainerHighest = RutinAppColors.Light.surfaceContainerHighest,
    surfaceTint = RutinAppColors.Light.surfaceTint,
    inverseSurface = RutinAppColors.Light.inverseSurface,
    inverseOnSurface = RutinAppColors.Light.inverseOnSurface,
    inversePrimary = RutinAppColors.Light.inversePrimary,
    outline = RutinAppColors.Light.outline,
    outlineVariant = RutinAppColors.Light.outlineVariant,
    error = RutinAppColors.Light.error,
    onError = RutinAppColors.Light.onError,
    errorContainer = RutinAppColors.Light.errorContainer,
    onErrorContainer = RutinAppColors.Light.onErrorContainer,
    scrim = RutinAppColors.Light.scrim
)

private val DarkExtendedColors = RutinAppExtendedColors(
    warning = RutinAppColors.Dark.warning,
    success = RutinAppColors.Dark.success,
    info = RutinAppColors.Dark.info,
    surfaceElevated = RutinAppColors.Dark.surfaceElevated,
    scrim = RutinAppColors.Dark.scrim
)

private val LightExtendedColors = RutinAppExtendedColors(
    warning = RutinAppColors.Light.warning,
    success = RutinAppColors.Light.success,
    info = RutinAppColors.Light.info,
    surfaceElevated = RutinAppColors.Light.surfaceElevated,
    scrim = RutinAppColors.Light.scrim
)

/**
 * RutinApp Material3 theme with full dual light/dark support.
 *
 * Uses the custom color palette defined in RutinAppColors instead of dynamic colors
 * to maintain brand consistency across all devices.
 *
 * @param darkTheme Whether to use dark theme. Controlled by user preference in DataStore.
 * @param content The composable content wrapped by the theme.
 */
@Composable
fun RutinAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    androidx.compose.runtime.CompositionLocalProvider(
        LocalRutinAppColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = RutinAppTypography,
            shapes = RutinAppShapes,
            content = content
        )
    }
}