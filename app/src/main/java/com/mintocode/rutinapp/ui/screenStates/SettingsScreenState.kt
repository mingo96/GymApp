package com.mintocode.rutinapp.ui.screenStates

sealed interface SettingsScreenState {
    data object UserData : SettingsScreenState
    data class LogIn(val isRegister : Boolean=false, val userMail : String="") : SettingsScreenState
}