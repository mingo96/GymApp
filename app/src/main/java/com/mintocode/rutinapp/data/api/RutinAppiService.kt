package com.mintocode.rutinapp.data.api

import com.mintocode.rutinapp.data.BASE_URL
import com.mintocode.rutinapp.data.api.classes.Exercise
import com.mintocode.rutinapp.data.api.classes.User
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


private val retrofit =
    Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(
        BASE_URL
    ).build()


interface RutinAppiService {

    @POST("auth/newuser")
    suspend fun createUser(@Body user: User): User

    @POST("exercises/newexercise")
    suspend fun createExercise(
        @Body exercise: Exercise, @Header("Authorization") token: String
    ): Response<Exercise>

    @GET("exercises/myexercises")
    suspend fun getExercises(@Header("Authorization") token: String): Response<List<Exercise>>

}

object Rutinappi {
    val retrofitService: RutinAppiService by lazy {
        retrofit.create(RutinAppiService::class.java)
    }
}