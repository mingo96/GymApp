package com.mintocode.rutinapp.data

data class UserDetails(
    val code : String = "",
    val name: String = "",
    val isDarkTheme: Boolean = true,
    val authToken: String = "",
    val email: String = "@gmail.com",
    val floatingWidgetEnabled: Boolean = false
){
    companion object{

        var actualValue : UserDetails? = null

    }
}