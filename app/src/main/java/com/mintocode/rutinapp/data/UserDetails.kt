package com.mintocode.rutinapp.data

data class UserDetails(
    val code : String = "",
    val name: String = "",
    val isDarkTheme: Boolean = true,
    val authToken: String = "",
    val email: String = "@gmail.com"
)