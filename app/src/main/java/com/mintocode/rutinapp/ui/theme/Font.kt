package com.mintocode.rutinapp.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.mintocode.rutinapp.R

/**
 * Space Grotesk — used for headlines, display text, and navigation labels.
 * Variable font supporting weights 300–700.
 */
val SpaceGroteskFont = FontFamily(
    Font(R.font.space_grotesk, FontWeight.Light),
    Font(R.font.space_grotesk, FontWeight.Normal),
    Font(R.font.space_grotesk, FontWeight.Medium),
    Font(R.font.space_grotesk, FontWeight.SemiBold),
    Font(R.font.space_grotesk, FontWeight.Bold)
)

/**
 * Manrope — used for body text, labels, and UI elements.
 * Variable font supporting weights 200–800.
 */
val ManropeFont = FontFamily(
    Font(R.font.manrope, FontWeight.Light),
    Font(R.font.manrope, FontWeight.Normal),
    Font(R.font.manrope, FontWeight.Medium),
    Font(R.font.manrope, FontWeight.SemiBold),
    Font(R.font.manrope, FontWeight.Bold),
    Font(R.font.manrope, FontWeight.ExtraBold)
)

/** Kept for backwards compatibility — maps to SpaceGroteskFont. */
val RutinAppFont = SpaceGroteskFont