package com.mintocode.rutinapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * RutinApp v2 shape tokens.
 *
 * Consistent corner radii across all components:
 * - extraSmall (4dp): chips, badges
 * - small (8dp): text fields, small cards
 * - medium (12dp): cards, dialogs
 * - large (16dp): bottom sheets, large cards
 * - extraLarge (28dp): FABs, modals
 */
val RutinAppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)
