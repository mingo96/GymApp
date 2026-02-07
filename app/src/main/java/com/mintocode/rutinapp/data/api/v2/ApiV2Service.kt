package com.mintocode.rutinapp.data.api.v2

import com.mintocode.rutinapp.data.api.v2.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit service interface for RutinApp API v2.
 *
 * All endpoints are relative to BASE_URL (https://rutynapp.com/api/v2/).
 * The AuthInterceptor in ApiV2Module automatically adds the Bearer token header.
 *
 * @see <a href="docs/api-spec.yaml">API Specification</a>
 */
interface ApiV2Service {

    // ========================================================================
    // Auth endpoints
    // ========================================================================

    /**
     * Login with email and password.
     *
     * @return AuthResponse with access_token and user data
     */
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    /**
     * Register a new user account.
     *
     * @return AuthResponse with access_token and user data
     */
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    /**
     * Authenticate or register with Google Sign-In.
     *
     * @param body Contains the Google ID token (not Firebase token)
     * @return AuthResponse with access_token and user data
     */
    @POST("auth/google")
    suspend fun googleAuth(@Body body: GoogleAuthRequest): AuthResponse

    /**
     * Get the currently authenticated user's profile.
     *
     * @return DataResponse wrapping UserDto
     */
    @GET("auth/me")
    suspend fun me(): DataResponse<UserDto>

    /**
     * Logout the current user (revokes token).
     */
    @POST("auth/logout")
    suspend fun logout(): MessageResponse

    // ========================================================================
    // Exercises CRUD
    // ========================================================================

    /**
     * List exercises visible to the authenticated user (own + public).
     *
     * @param mineOnly If true, only return user's own exercises
     * @param bodyPart Filter by body part
     * @param search Search by name
     * @param page Page number for pagination
     * @param perPage Items per page
     * @return Paginated list of exercises
     */
    @GET("exercises")
    suspend fun getExercises(
        @Query("mine_only") mineOnly: Boolean? = null,
        @Query("body_part") bodyPart: String? = null,
        @Query("search") search: String? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null
    ): PaginatedResponse<ExerciseDto>

    /**
     * Get a single exercise by ID.
     */
    @GET("exercises/{id}")
    suspend fun getExercise(@Path("id") id: Long): DataResponse<ExerciseDto>

    /**
     * Create a new exercise.
     */
    @POST("exercises")
    suspend fun createExercise(@Body body: CreateExerciseRequest): DataResponse<ExerciseDto>

    /**
     * Update an existing exercise.
     */
    @PUT("exercises/{id}")
    suspend fun updateExercise(
        @Path("id") id: Long,
        @Body body: UpdateExerciseRequest
    ): DataResponse<ExerciseDto>

    /**
     * Delete an exercise (soft delete).
     */
    @DELETE("exercises/{id}")
    suspend fun deleteExercise(@Path("id") id: Long): Response<Unit>

    // ========================================================================
    // Routines CRUD
    // ========================================================================

    /**
     * List routines visible to the authenticated user (own + public).
     *
     * @param mineOnly If true, only return user's own routines
     * @return Paginated list of routines
     */
    @GET("routines")
    suspend fun getRoutines(
        @Query("mine_only") mineOnly: Boolean? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null
    ): PaginatedResponse<RoutineDto>

    /**
     * Get a single routine by ID (includes exercises).
     */
    @GET("routines/{id}")
    suspend fun getRoutine(@Path("id") id: Long): DataResponse<RoutineDto>

    /**
     * Create a new routine.
     */
    @POST("routines")
    suspend fun createRoutine(@Body body: CreateRoutineRequest): DataResponse<RoutineDto>

    /**
     * Update an existing routine.
     */
    @PUT("routines/{id}")
    suspend fun updateRoutine(
        @Path("id") id: Long,
        @Body body: UpdateRoutineRequest
    ): DataResponse<RoutineDto>

    /**
     * Delete a routine (soft delete).
     */
    @DELETE("routines/{id}")
    suspend fun deleteRoutine(@Path("id") id: Long): Response<Unit>

    /**
     * Add an exercise to a routine.
     */
    @POST("routines/{id}/exercises")
    suspend fun addExerciseToRoutine(
        @Path("id") routineId: Long,
        @Body body: AddRoutineExerciseRequest
    ): Response<Unit>

    /**
     * Remove an exercise from a routine.
     */
    @DELETE("routines/{id}/exercises/{exerciseId}")
    suspend fun removeExerciseFromRoutine(
        @Path("id") routineId: Long,
        @Path("exerciseId") exerciseId: Long
    ): Response<Unit>

    // ========================================================================
    // Workouts CRUD
    // ========================================================================

    /**
     * List the authenticated user's workouts.
     *
     * @param fromDate Filter from date (ISO format)
     * @param toDate Filter to date (ISO format)
     * @param isFinished Filter by completion status
     */
    @GET("workouts")
    suspend fun getWorkouts(
        @Query("from_date") fromDate: String? = null,
        @Query("to_date") toDate: String? = null,
        @Query("is_finished") isFinished: Boolean? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null
    ): PaginatedResponse<WorkoutDto>

    /**
     * Get a single workout by ID (includes routines and sets).
     */
    @GET("workouts/{id}")
    suspend fun getWorkout(@Path("id") id: Long): DataResponse<WorkoutDto>

    /**
     * Create a new workout.
     */
    @POST("workouts")
    suspend fun createWorkout(@Body body: CreateWorkoutRequest): DataResponse<WorkoutDto>

    /**
     * Update an existing workout.
     */
    @PUT("workouts/{id}")
    suspend fun updateWorkout(
        @Path("id") id: Long,
        @Body body: UpdateWorkoutRequest
    ): DataResponse<WorkoutDto>

    /**
     * Delete a workout.
     */
    @DELETE("workouts/{id}")
    suspend fun deleteWorkout(@Path("id") id: Long): Response<Unit>

    /**
     * Add a set to a workout.
     */
    @POST("workouts/{id}/sets")
    suspend fun addSetToWorkout(
        @Path("id") workoutId: Long,
        @Body body: CreateSetRequest
    ): DataResponse<SetDto>

    /**
     * Mark a workout as finished.
     */
    @POST("workouts/{id}/finish")
    suspend fun finishWorkout(@Path("id") id: Long): DataResponse<WorkoutDto>

    // ========================================================================
    // Sets CRUD
    // ========================================================================

    /**
     * Update an existing set.
     */
    @PUT("sets/{id}")
    suspend fun updateSet(
        @Path("id") id: Long,
        @Body body: UpdateSetRequest
    ): DataResponse<SetDto>

    /**
     * Delete a set.
     */
    @DELETE("sets/{id}")
    suspend fun deleteSet(@Path("id") id: Long): Response<Unit>

    // ========================================================================
    // Planning CRUD
    // ========================================================================

    /**
     * List planning entries for the authenticated user.
     *
     * @param fromDate Start date filter (YYYY-MM-DD)
     * @param toDate End date filter (YYYY-MM-DD)
     */
    @GET("planning")
    suspend fun getPlanning(
        @Query("from_date") fromDate: String? = null,
        @Query("to_date") toDate: String? = null
    ): PaginatedResponse<PlanningDto>

    /**
     * Get today's planning entries.
     */
    @GET("planning/today")
    suspend fun getTodayPlanning(): PaginatedResponse<PlanningDto>

    /**
     * Create a new planning entry.
     */
    @POST("planning")
    suspend fun createPlanning(@Body body: CreatePlanningRequest): DataResponse<PlanningDto>

    /**
     * Update a planning entry.
     */
    @PUT("planning/{id}")
    suspend fun updatePlanning(
        @Path("id") id: Long,
        @Body body: UpdatePlanningRequest
    ): DataResponse<PlanningDto>

    /**
     * Delete a planning entry.
     */
    @DELETE("planning/{id}")
    suspend fun deletePlanning(@Path("id") id: Long): Response<Unit>

    // ========================================================================
    // Sync endpoints
    // ========================================================================

    /**
     * Bidirectional sync of exercises.
     *
     * Sends local changes and receives server changes since last sync.
     */
    @POST("sync/exercises")
    suspend fun syncExercises(@Body body: SyncExercisesRequest): DataResponse<SyncExercisesData>

    /**
     * Bidirectional sync of routines.
     */
    @POST("sync/routines")
    suspend fun syncRoutines(@Body body: SyncRoutinesRequest): DataResponse<SyncRoutinesData>

    /**
     * Bidirectional sync of workouts (includes sets).
     */
    @POST("sync/workouts")
    suspend fun syncWorkouts(@Body body: SyncWorkoutsRequest): DataResponse<SyncWorkoutsData>

    /**
     * Bidirectional sync of planning entries.
     */
    @POST("sync/planning")
    suspend fun syncPlanning(@Body body: SyncPlanningRequest): DataResponse<SyncPlanningData>

    // ========================================================================
    // Statistics
    // ========================================================================

    /**
     * Get user's stats summary.
     */
    @GET("stats/summary")
    suspend fun getStatsSummary(): DataResponse<StatsSummaryDto>

    // ========================================================================
    // Ad Codes
    // ========================================================================

    /**
     * Redeem an ad reduction code.
     */
    @POST("ad-codes/redeem")
    suspend fun redeemAdCode(@Body body: RedeemAdCodeRequest): DataResponse<AdCodeDto>

    /**
     * Check if ads should be shown for the current user.
     */
    @GET("ad-codes/check/should-show-ad")
    suspend fun shouldShowAd(): ShouldShowAdResponse

    // ========================================================================
    // Device / FCM
    // ========================================================================

    /**
     * Register or update the user's FCM push token.
     */
    @POST("device/register")
    suspend fun registerFcmToken(@Body body: RegisterFcmTokenRequest): MessageResponse

    /**
     * Remove the user's FCM push token.
     */
    @DELETE("device/unregister")
    suspend fun unregisterFcmToken(): MessageResponse

    /**
     * Update push notification preferences.
     */
    @PUT("device/preferences")
    suspend fun updateDevicePreferences(@Body body: DevicePreferencesRequest): MessageResponse
}
