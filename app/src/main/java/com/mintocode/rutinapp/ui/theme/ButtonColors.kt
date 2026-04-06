package com.mintocode.rutinapp.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Primary action button colors using the app's Material3 color scheme.
 * Use for main CTA buttons (e.g., "Start Workout", "Save Exercise").
 */
@Composable
fun rutinAppButtonsColours(): ButtonColors {
    return ButtonDefaults.buttonColors(
        contentColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.primary,
        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    )
}

/**
 * Text button colors for secondary actions (e.g., "Cancel", "Skip").
 */
@Composable
fun rutinAppTextButtonColors(): ButtonColors {
    return ButtonDefaults.textButtonColors(
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    )
}