package com.mintocode.rutinapp.security

import com.mintocode.rutinapp.data.UserDetails

/**
 * Centralizes session data cleanup for logout flows.
 */
object SessionDataSanitizer {

    /**
     * Clears sensitive fields while preserving user preferences.
     *
     * @param userDetails Current persisted user details
     * @return Sanitized details without auth session data
     */
    fun clearSession(userDetails: UserDetails): UserDetails {
        return userDetails.copy(
            authToken = "",
            email = ""
        )
    }
}
