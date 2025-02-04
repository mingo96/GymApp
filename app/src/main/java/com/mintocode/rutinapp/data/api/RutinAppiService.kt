package com.mintocode.rutinapp.data.api

import com.mintocode.rutinapp.data.BASE_URL
import com.mintocode.rutinapp.data.api.classes.Exercise
import com.mintocode.rutinapp.data.api.classes.Routine
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
import retrofit2.http.PUT
import java.util.concurrent.TimeUnit


private val retrofit =
    Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(
        BASE_URL
    ).client(client()).build()

fun client(): OkHttpClient {
    val client = OkHttpClient.Builder().readTimeout(120, TimeUnit.SECONDS).writeTimeout(120, TimeUnit.SECONDS).connectTimeout(120, TimeUnit.SECONDS)
    return client.build()
}

interface RutinAppiService {

    @POST("auth/newuser")
    suspend fun createUser(@Body user: User): User

    @POST("exercises/newexercise")
    suspend fun createExercise(
        @Body exercise: Exercise, @Header("Authorization") token: String
    ): Response<Exercise>

    @GET("exercises/myexercises")
    suspend fun getExercises(@Header("Authorization") token: String): Response<List<Exercise>>

    @PUT("exercises/updateexercise")
    suspend fun updateExercise(@Body toAPIModel: Exercise, @Header("Authorization")authToken: String): Response<Exercise>

    @POST("routines/newroutine")
    suspend fun createRoutine(
        @Body routine: Routine, @Header("Authorization") token: String
    ): Response<Routine>

    @GET("routines/myroutines")
    suspend fun getRoutines(@Header("Authorization") token: String): Response<List<Routine>>

    @POST("routines/editroutine")
    suspend fun updateRoutine(
        @Body routine: Routine, @Header("Authorization") token: String
    ): Response<Routine>

}

object Rutinappi {
    val retrofitService: RutinAppiService by lazy {
        retrofit.create(RutinAppiService::class.java)
    }
}