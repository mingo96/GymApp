package com.example.rutinapp.ui.theme

import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun rutinappCardColors(): CardColors {
    return CardDefaults.cardColors(
        containerColor = TextFieldColor,

    )
}