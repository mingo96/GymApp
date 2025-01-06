package com.example.rutinapp.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.rutinapp.ui.theme.ContentColor
import com.example.rutinapp.ui.theme.PrimaryColor
import com.example.rutinapp.ui.theme.SecondaryColor

data class UserDetails (
    val name: String="",
    val email: String="",
    val password: String="",
    val isDarkTheme : Boolean = true,
)