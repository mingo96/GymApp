package com.mintocode.rutinapp.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

var PrimaryColor by mutableStateOf(Color(0xFF121217))
var SecondaryColor by mutableStateOf(Color(0xFF1212ED))
var TextFieldColor by mutableStateOf(Color(40, 40, 58).copy(0.5f))
var ContentColor by mutableStateOf(Color.White)