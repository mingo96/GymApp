package com.example.rutinapp.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.rutinapp.ui.theme.SecondaryColor

@Composable
fun rutinAppButtonsColours(): ButtonColors {
    return ButtonDefaults.buttonColors(
        contentColor = Color.White,
        containerColor = SecondaryColor,
        disabledContainerColor = Color.White,
        disabledContentColor = SecondaryColor
    )
}