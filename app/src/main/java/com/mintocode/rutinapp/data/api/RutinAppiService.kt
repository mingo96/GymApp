package com.mintocode.rutinapp.data.api

import com.google.gson.annotations.SerializedName
import com.mintocode.rutinapp.data.BASE_URL
import com.mintocode.rutinapp.data.api.classes.User
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

private val retrofit =
    Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(
        BASE_URL
    ).build()


interface RutinAppiService {

    @POST("auth/newuser")
    suspend fun createUser(@Body user: User): User



}

object Rutinappi {
    val retrofitService: RutinAppiService by lazy {
        retrofit.create(RutinAppiService::class.java)
    }
}