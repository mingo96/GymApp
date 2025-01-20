package com.example.rutinapp.ui.theme

import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun rutinAppTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        unfocusedContainerColor = TextFieldColor,
        focusedContainerColor = TextFieldColor,
        focusedIndicatorColor = TextFieldColor,
        unfocusedIndicatorColor = TextFieldColor,
        focusedTextColor = ContentColor.copy(0.85f),
        unfocusedTextColor = ContentColor.copy(0.85f),
        focusedLabelColor = ContentColor.copy(0.5f),
        unfocusedLabelColor = ContentColor.copy(0.5f),
        cursorColor = SecondaryColor,
        disabledTextColor = ContentColor.copy(0.85f),
        disabledContainerColor = TextFieldColor,
        disabledIndicatorColor = Color.Transparent,


        )
}