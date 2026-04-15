package com.mintocode.rutinapp.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Primary action button colors using KP theme (primaryContainer/onPrimaryContainer).
 * Use for main CTA buttons (e.g., "Start Workout", "Save Exercise").
 */
@Composable
fun rutinAppButtonsColours(): ButtonColors {
    return ButtonDefaults.buttonColors(
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    )
}

/**
 * Text button colors for secondary actions using KP theme.
 */
@Composable
fun rutinAppTextButtonColors(): ButtonColors {
    return ButtonDefaults.textButtonColors(
        contentColor = MaterialTheme.colorScheme.primaryContainer,
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    )
}