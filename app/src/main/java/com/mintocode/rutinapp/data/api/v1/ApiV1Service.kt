package com.mintocode.rutinapp.data.api.v1

import com.mintocode.rutinapp.data.api.v1.dto.*
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.GET

interface ApiV1Service {

    @POST("sync/exercises")
    suspend fun syncExercises(
        @Header("Authorization") bearer: String,
        @Body body: SyncExercisesRequest
    ): Envelope<SyncExercisesData>

    @POST("sync/routines")
    suspend fun syncRoutines(
        @Header("Authorization") bearer: String,
        @Body body: SyncRoutinesRequest
    ): Envelope<SyncRoutinesData>

    // Fetch current user's exercises (server authoritative list)
    @GET("exercises/mine")
    suspend fun getMyExercises(
        @Header("Authorization") bearer: String
    ): Envelope<ExercisesMineData>

    @GET("exercises/others")
    suspend fun getOtherExercises(
        @Header("Authorization") bearer: String
    ): Envelope<ExercisesMineData>

    @GET("routines/mine")
    suspend fun getMyRoutines(
        @Header("Authorization") bearer: String
    ): Envelope<RoutinesMineData>

    @GET("routines/others")
    suspend fun getOtherRoutines(
        @Header("Authorization") bearer: String
    ): Envelope<RoutinesMineData>

    // Auth endpoints (envelope returns access_token at root along with data.user)
    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequest
    ): AuthEnvelope

    @POST("auth/register")
    suspend fun register(
        @Body body: RegisterRequest
    ): AuthEnvelope
}
