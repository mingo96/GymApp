package com.mintocode.rutinapp.security

import com.mintocode.rutinapp.data.UserDetails
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionDataSanitizerTest {

    @Test
    fun clearsSensitiveSessionFields() {
        val original = UserDetails(
            code = "abc123",
            name = "Eloy",
            isDarkTheme = false,
            authToken = "secret-token",
            email = "user@test.com",
            floatingWidgetEnabled = true
        )

        val sanitized = SessionDataSanitizer.clearSession(original)

        assertEquals("", sanitized.authToken)
        assertEquals("", sanitized.email)
        assertEquals("Eloy", sanitized.name)
        assertEquals("abc123", sanitized.code)
        assertTrue(sanitized.floatingWidgetEnabled)
    }
}
