package com.mintocode.rutinapp.data.api.v2

import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Test

class HttpLoggingPolicyTest {

    @Test
    fun usesBodyLoggingInDebugBuilds() {
        val level = HttpLoggingPolicy.resolve(isDebug = true)

        assertEquals(HttpLoggingInterceptor.Level.BODY, level)
    }

    @Test
    fun disablesLoggingInReleaseBuilds() {
        val level = HttpLoggingPolicy.resolve(isDebug = false)

        assertEquals(HttpLoggingInterceptor.Level.NONE, level)
    }
}
