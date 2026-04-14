package com.mintocode.rutinapp.data.api.v2

import okhttp3.logging.HttpLoggingInterceptor

/**
 * Resolves the HTTP logging level according to build type.
 *
 * Debug builds keep BODY logs for local debugging.
 * Release builds disable network logging to avoid leaking sensitive data.
 *
 * @param isDebug Whether the current build is a debug build
 * @return The logging level to apply to OkHttp interceptor
 */
object HttpLoggingPolicy {

    fun resolve(isDebug: Boolean): HttpLoggingInterceptor.Level {
        return if (isDebug) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }
}
