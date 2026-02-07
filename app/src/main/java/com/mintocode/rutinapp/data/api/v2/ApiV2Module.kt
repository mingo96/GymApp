package com.mintocode.rutinapp.data.api.v2

import com.mintocode.rutinapp.data.BASE_URL
import com.mintocode.rutinapp.data.UserDetails
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Hilt module providing Retrofit + OkHttp + ApiV2Service for API v2.
 *
 * Replaces the old ApiV1Module. The AuthInterceptor automatically adds
 * the Bearer token from UserDetails if available, so individual endpoints
 * do not need to accept an Authorization header parameter.
 */
@Module
@InstallIn(SingletonComponent::class)
object ApiV2Module {

    /**
     * Interceptor that auto-attaches the Sanctum Bearer token
     * from the current UserDetails to every request.
     */
    private class AuthInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val token = UserDetails.actualValue?.authToken
            return if (!token.isNullOrBlank()) {
                val req = original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .header("Accept", "application/json")
                    .build()
                chain.proceed(req)
            } else {
                val req = original.newBuilder()
                    .header("Accept", "application/json")
                    .build()
                chain.proceed(req)
            }
        }
    }

    /**
     * Provides a configured OkHttpClient with logging and auth interceptors.
     */
    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor())
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Provides a Retrofit instance configured for API v2.
     */
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * Provides the ApiV2Service Retrofit implementation.
     */
    @Provides
    @Singleton
    fun provideApiV2(retrofit: Retrofit): ApiV2Service =
        retrofit.create(ApiV2Service::class.java)
}
