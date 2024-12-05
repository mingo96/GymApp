package com.example.rutinapp.ui.theme

import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun rutinAppTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        unfocusedContainerColor = TextFieldColor.copy(0.5f),
        focusedContainerColor = TextFieldColor.copy(0.5f),
        focusedIndicatorColor = TextFieldColor.copy(0.5f),
        unfocusedIndicatorColor = TextFieldColor.copy(0.5f),
        focusedTextColor = Color.LightGray.copy(0.85f),
        unfocusedTextColor = Color.LightGray.copy(0.85f),
        focusedLabelColor = Color.White.copy(0.5f),
        unfocusedLabelColor = Color.White.copy(0.5f),
        cursorColor = SecondaryColor,
        disabledTextColor = Color.LightGray.copy(0.85f),
        disabledContainerColor = TextFieldColor.copy(0.5f)

    )
}