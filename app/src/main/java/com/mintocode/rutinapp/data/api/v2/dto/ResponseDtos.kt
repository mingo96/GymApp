package com.mintocode.rutinapp.data.api.v2.dto

import com.google.gson.annotations.SerializedName

// ============================================================================
// Generic API response wrappers matching Laravel Resource responses
// ============================================================================

/**
 * Wraps a single resource response: { "data": T }
 */
data class DataResponse<T>(
    val data: T?
)

/**
 * Wraps a paginated list response: { "data": [T], "meta": {...}, "links": {...} }
 */
data class PaginatedResponse<T>(
    val data: List<T>,
    val meta: PaginationMeta?,
    val links: PaginationLinks?
)

/**
 * Pagination metadata from Laravel's paginator.
 */
data class PaginationMeta(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("per_page") val perPage: Int,
    val total: Int,
    @SerializedName("last_page") val lastPage: Int
)

/**
 * Pagination links from Laravel's paginator.
 */
data class PaginationLinks(
    val first: String?,
    val last: String?,
    val prev: String?,
    val next: String?
)

// ============================================================================
// Auth DTOs
// ============================================================================

/**
 * Login request body matching POST /auth/login.
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Register request body matching POST /auth/register.
 */
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String
)

/**
 * Google auth request body matching POST /auth/google.
 */
data class GoogleAuthRequest(
    @SerializedName("id_token") val idToken: String
)

/**
 * Auth response from login/register/google endpoints.
 *
 * Format: { "data": { "user": {...} }, "access_token": "...", "token_type": "Bearer" }
 */
data class AuthResponse(
    val data: AuthData?,
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("token_type") val tokenType: String?
)

data class AuthData(
    val user: UserDto?
)

data class UserDto(
    val id: Long,
    val name: String,
    val email: String
)

// ============================================================================
// Exercise DTOs (matching ExerciseResource)
// ============================================================================

/**
 * Exercise DTO matching the backend ExerciseResource response.
 */
data class ExerciseDto(
    val id: Long,
    val name: String,
    val description: String?,
    @SerializedName("targeted_body_part") val targetedBodyPart: String?,
    @SerializedName("targeted_body_part_label") val targetedBodyPartLabel: String?,
    val observations: String?,
    @SerializedName("is_public") val isPublic: Boolean,
    @SerializedName("is_mine") val isMine: Boolean,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("related_exercises_count") val relatedExercisesCount: Int?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

/**
 * Request body for creating an exercise (POST /exercises).
 */
data class CreateExerciseRequest(
    val name: String,
    val description: String? = null,
    @SerializedName("targeted_body_part") val targetedBodyPart: String? = null,
    val observations: String? = null,
    @SerializedName("is_public") val isPublic: Boolean = false
)

/**
 * Request body for updating an exercise (PUT /exercises/{id}).
 */
data class UpdateExerciseRequest(
    val name: String? = null,
    val description: String? = null,
    @SerializedName("targeted_body_part") val targetedBodyPart: String? = null,
    val observations: String? = null,
    @SerializedName("is_public") val isPublic: Boolean? = null
)

// ============================================================================
// Routine DTOs (matching RoutineResource)
// ============================================================================

/**
 * Routine DTO matching the backend RoutineResource response.
 */
data class RoutineDto(
    val id: Long,
    val name: String,
    val description: String?,
    @SerializedName("targeted_body_part") val targetedBodyPart: String?,
    @SerializedName("targeted_body_part_label") val targetedBodyPartLabel: String?,
    @SerializedName("is_public") val isPublic: Boolean,
    @SerializedName("is_mine") val isMine: Boolean,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("exercises_count") val exercisesCount: Int?,
    val exercises: List<ExerciseDto>?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

/**
 * Request body for creating a routine (POST /routines).
 */
data class CreateRoutineRequest(
    val name: String,
    val description: String? = null,
    @SerializedName("targeted_body_part") val targetedBodyPart: String? = null,
    @SerializedName("is_public") val isPublic: Boolean = false
)

/**
 * Request body for updating a routine (PUT /routines/{id}).
 */
data class UpdateRoutineRequest(
    val name: String? = null,
    val description: String? = null,
    @SerializedName("targeted_body_part") val targetedBodyPart: String? = null,
    @SerializedName("is_public") val isPublic: Boolean? = null
)

/**
 * Request body for adding an exercise to a routine (POST /routines/{id}/exercises).
 */
data class AddRoutineExerciseRequest(
    @SerializedName("exercise_id") val exerciseId: Long,
    @SerializedName("stated_sets_and_reps") val statedSetsAndReps: String = "3x10",
    val observations: String? = null,
    val position: Int? = null
)

// ============================================================================
// Workout DTOs (matching WorkoutResource)
// ============================================================================

/**
 * Workout DTO matching the backend WorkoutResource response.
 */
data class WorkoutDto(
    val id: Long,
    @SerializedName("user_id") val userId: Long,
    val title: String,
    val notes: String?,
    val date: String,
    @SerializedName("is_finished") val isFinished: Boolean,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    val routines: List<RoutineDto>?,
    val sets: List<SetDto>?,
    @SerializedName("total_volume") val totalVolume: Double?,
    @SerializedName("sets_count") val setsCount: Int?
)

/**
 * Request body for creating a workout (POST /workouts).
 */
data class CreateWorkoutRequest(
    val title: String,
    val notes: String? = null,
    val date: String? = null,
    @SerializedName("routine_ids") val routineIds: List<Long>? = null
)

/**
 * Request body for updating a workout (PUT /workouts/{id}).
 */
data class UpdateWorkoutRequest(
    val title: String? = null,
    val notes: String? = null,
    @SerializedName("is_finished") val isFinished: Boolean? = null
)

// ============================================================================
// Set DTOs (matching SetResource)
// ============================================================================

/**
 * Set DTO matching the backend SetResource response.
 */
data class SetDto(
    val id: Long,
    @SerializedName("workout_id") val workoutId: Long,
    @SerializedName("exercise_id") val exerciseId: Long,
    val weight: Double?,
    val repetitions: Int?,
    val time: Int?,
    val distance: Double?,
    val difficulty: Int?,
    val notes: String?,
    val order: Int?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    val exercise: ExerciseDto?,
    val volume: Double?
)

/**
 * Request body for creating a set (POST /workouts/{id}/sets).
 */
data class CreateSetRequest(
    @SerializedName("exercise_id") val exerciseId: Long,
    val weight: Double? = null,
    val repetitions: Int? = null,
    val time: Int? = null,
    val distance: Double? = null,
    val difficulty: Int? = null,
    val notes: String? = null,
    val order: Int? = null
)

/**
 * Request body for updating a set (PUT /sets/{id}).
 */
data class UpdateSetRequest(
    val weight: Double? = null,
    val repetitions: Int? = null,
    val time: Int? = null,
    val distance: Double? = null,
    val difficulty: Int? = null,
    val notes: String? = null,
    val order: Int? = null
)

// ============================================================================
// Planning DTOs (matching PlanningResource)
// ============================================================================

/**
 * Planning DTO matching the backend PlanningResource response.
 */
data class PlanningDto(
    val id: Long,
    @SerializedName("user_id") val userId: Long,
    val date: String,
    @SerializedName("body_part") val bodyPart: String?,
    val routine: RoutineDto?,
    @SerializedName("reminder_time") val reminderTime: String?,
    @SerializedName("created_by_user_id") val createdByUserId: Long?,
    @SerializedName("derived_from_planning_id") val derivedFromPlanningId: Long?,
    @SerializedName("planning_exercises") val planningExercises: List<PlanningExerciseDto>?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

/**
 * Request body for creating a planning entry (POST /planning).
 */
data class CreatePlanningRequest(
    val date: String,
    @SerializedName("routine_id") val routineId: Long? = null,
    @SerializedName("body_part") val bodyPart: String? = null,
    @SerializedName("reminder_time") val reminderTime: String? = null
)

/**
 * Request body for updating a planning entry (PUT /planning/{id}).
 */
data class UpdatePlanningRequest(
    val date: String? = null,
    @SerializedName("routine_id") val routineId: Long? = null,
    @SerializedName("body_part") val bodyPart: String? = null,
    @SerializedName("reminder_time") val reminderTime: String? = null
)

// ============================================================================
// Statistics DTOs
// ============================================================================

/**
 * Stats summary response from GET /stats/summary.
 */
data class StatsSummaryDto(
    @SerializedName("total_workouts") val totalWorkouts: Int,
    @SerializedName("total_sets") val totalSets: Int,
    @SerializedName("total_volume") val totalVolume: Double,
    @SerializedName("avg_workout_duration") val avgWorkoutDuration: Int?,
    @SerializedName("most_trained_part") val mostTrainedPart: String?
)

// ============================================================================
// Ad Code DTOs
// ============================================================================

/**
 * Request body for redeeming an ad code (POST /ad-codes/redeem).
 */
data class RedeemAdCodeRequest(
    val code: String
)

/**
 * Response for ad code redemption.
 */
data class AdCodeDto(
    val id: Long,
    val code: String,
    @SerializedName("discount_percentage") val discountPercentage: Int?,
    @SerializedName("valid_until") val validUntil: String?,
    @SerializedName("redeemed_at") val redeemedAt: String?
)

/**
 * Response for checking whether to show ads.
 */
data class ShouldShowAdResponse(
    @SerializedName("should_show_ad") val shouldShowAd: Boolean
)

// ============================================================================
// Simple message response
// ============================================================================

/**
 * Generic message response from the API (e.g. logout, error messages).
 */
data class MessageResponse(
    val message: String?
)

// ============================================================================
// Device / FCM DTOs
// ============================================================================

/**
 * Request body for registering a FCM token (POST /device/register).
 */
data class RegisterFcmTokenRequest(
    @SerializedName("fcm_token") val fcmToken: String,
    @SerializedName("push_enabled") val pushEnabled: Boolean = true
)

/**
 * Request body for updating device notification preferences (PUT /device/preferences).
 */
data class DevicePreferencesRequest(
    @SerializedName("push_enabled") val pushEnabled: Boolean,
    @SerializedName("email_notifications_enabled") val emailNotificationsEnabled: Boolean? = null
)

// ============================================================================
// Notification DTOs
// ============================================================================

/**
 * DTO representing a notification from the backend (GET /notifications).
 */
data class NotificationDto(
    val id: Long,
    @SerializedName("user_id") val userId: Long,
    val title: String,
    val body: String?,
    val type: String,
    val data: Map<String, String>?,
    @SerializedName("read_at") val readAt: String?,
    @SerializedName("is_read") val isRead: Boolean,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

/**
 * Response for unread count endpoint (GET /notifications/unread-count).
 */
data class UnreadCountResponse(
    @SerializedName("unread_count") val unreadCount: Int
)

// ============================================================================
// Planning Exercise DTOs
// ============================================================================

/**
 * DTO for an exercise expectation within a planning entry.
 *
 * Matches the inline object in PlanningResource->planning_exercises.
 */
data class PlanningExerciseDto(
    val id: Long,
    @SerializedName("exercise_id") val exerciseId: Long,
    @SerializedName("expectation_text") val expectationText: String?,
    val position: Int,
    val notes: String?,
    val exercise: PlanningExerciseExerciseDto?
)

/**
 * Minimal exercise info nested within PlanningExerciseDto.
 */
data class PlanningExerciseExerciseDto(
    val id: Long,
    val name: String
)

// ============================================================================
// Calendar Phase DTOs (matching CalendarPhaseResource)
// ============================================================================

/**
 * Calendar phase DTO matching the backend CalendarPhaseResource response.
 *
 * Represents a named time period displayed on the calendar (e.g., "Volumen", "Definición").
 */
data class CalendarPhaseDto(
    val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("created_by_user_id") val createdByUserId: Long?,
    val name: String,
    val color: String,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    val notes: String?,
    val visibility: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

// ============================================================================
// Trainer DTOs (matching backend Resources)
// ============================================================================

/**
 * Trainer–client relationship DTO matching TrainerClientRelationResource.
 *
 * Read-only from client perspective; managed via dedicated endpoints.
 */
data class TrainerClientRelationDto(
    val id: Long,
    @SerializedName("trainer_user_id") val trainerUserId: Long,
    @SerializedName("client_user_id") val clientUserId: Long,
    val status: String,
    @SerializedName("invited_via_code_id") val invitedViaCodeId: Long?,
    val notes: String?,
    @SerializedName("local_id") val localId: String?,
    @SerializedName("synced_at") val syncedAt: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

/**
 * Planning grant DTO matching PlanningGrantResource.
 *
 * Represents permission for a trainer to view/edit client's planning.
 */
data class PlanningGrantDto(
    val id: Long,
    @SerializedName("client_user_id") val clientUserId: Long,
    @SerializedName("trainer_user_id") val trainerUserId: Long,
    @SerializedName("access_type") val accessType: String,
    @SerializedName("date_from") val dateFrom: String?,
    @SerializedName("date_to") val dateTo: String?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

/**
 * Workout visibility grant DTO matching WorkoutVisibilityGrantResource.
 *
 * Represents permission for a trainer to view client's workout results.
 */
data class WorkoutVisibilityGrantDto(
    val id: Long,
    @SerializedName("client_user_id") val clientUserId: Long,
    @SerializedName("trainer_user_id") val trainerUserId: Long,
    @SerializedName("can_view_results") val canViewResults: Boolean,
    @SerializedName("date_from") val dateFrom: String?,
    @SerializedName("date_to") val dateTo: String?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

// ============================================================================
// Trainer management request/response DTOs
// ============================================================================

/**
 * Request body for redeeming a trainer invite code (POST /trainer/invite-codes/redeem).
 */
data class RedeemInviteCodeRequest(
    val code: String
)

/**
 * Request body for creating a planning grant (POST /my-trainers/{relation}/grants/planning).
 */
data class CreatePlanningGrantRequest(
    @SerializedName("access_type") val accessType: String,
    @SerializedName("date_from") val dateFrom: String? = null,
    @SerializedName("date_to") val dateTo: String? = null
)

/**
 * Request body for creating a workout visibility grant.
 */
data class CreateWorkoutVisibilityGrantRequest(
    @SerializedName("can_view_results") val canViewResults: Boolean = false,
    @SerializedName("date_from") val dateFrom: String? = null,
    @SerializedName("date_to") val dateTo: String? = null
)

/**
 * Request body for planning duplication preview (POST /planning/duplicate/preview).
 */
data class DuplicatePreviewRequest(
    @SerializedName("planning_ids") val planningIds: List<Long>,
    @SerializedName("target_user_id") val targetUserId: Long,
    @SerializedName("date_offset") val dateOffset: Int? = null
)

/**
 * Response from planning duplication preview.
 */
data class DuplicatePreviewResponse(
    val preview: List<DuplicatePreviewItem>,
    @SerializedName("target_user") val targetUser: UserDto
)

data class DuplicatePreviewItem(
    @SerializedName("original_id") val originalId: Long,
    @SerializedName("original_date") val originalDate: String,
    @SerializedName("new_date") val newDate: String,
    @SerializedName("body_part") val bodyPart: String?,
    @SerializedName("routine_name") val routineName: String?
)

/**
 * Request body for executing planning duplication (POST /planning/duplicate).
 */
data class DuplicateExecuteRequest(
    @SerializedName("planning_ids") val planningIds: List<Long>,
    @SerializedName("target_user_id") val targetUserId: Long,
    @SerializedName("date_offset") val dateOffset: Int? = null
)

/**
 * Response for exercise last-mark endpoint (GET /exercises/{id}/last-mark).
 */
data class LastMarkDto(
    @SerializedName("exercise_id") val exerciseId: Long,
    @SerializedName("last_weight") val lastWeight: Double?,
    @SerializedName("last_reps") val lastReps: Int?,
    @SerializedName("last_date") val lastDate: String?,
    @SerializedName("max_weight") val maxWeight: Double?
)
