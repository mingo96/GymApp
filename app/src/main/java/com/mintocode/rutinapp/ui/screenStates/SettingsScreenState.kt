package com.mintocode.rutinapp.ui.screenStates

sealed interface SettingsScreenState {
    data object UserData : SettingsScreenState
    data class LogIn(val isRegister: Boolean = false, val userMail: String = "") : SettingsScreenState

    /**
     * 2FA challenge during login — user must enter TOTP code.
     *
     * @param twoFactorToken Temporary token from the login response
     * @param email Email of the user (for display)
     */
    data class TwoFactorChallenge(
        val twoFactorToken: String,
        val email: String
    ) : SettingsScreenState
}