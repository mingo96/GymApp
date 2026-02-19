package com.mintocode.rutinapp.data.api.v2.dto

import com.google.gson.annotations.SerializedName

// ============================================================================
// Sync Exercise DTOs (matching backend SyncExercisesRequest / SyncController response)
// ============================================================================

/**
 * Request body for POST /sync/exercises.
 *
 * Sends local changes to the server: new exercises, updated exercises, and deleted IDs.
 */
data class SyncExercisesRequest(
    @SerializedName("client_timestamp") val clientTimestamp: String,
    val created: List<SyncExerciseCreate> = emptyList(),
    val updated: List<SyncExerciseUpdate> = emptyList(),
    @SerializedName("deleted_ids") val deletedIds: List<Long> = emptyList()
)

data class SyncExerciseCreate(
    @SerializedName("local_id") val localId: String,
    val name: String,
    val description: String? = null,
    @SerializedName("targeted_body_part") val targetedBodyPart: String? = null,
    val observations: String? = null,
    @SerializedName("is_public") val isPublic: Boolean = false
)

data class SyncExerciseUpdate(
    val id: Long,
    val name: String? = null,
    val description: String? = null,
    @SerializedName("targeted_body_part") val targetedBodyPart: String? = null,
    val observations: String? = null,
    @SerializedName("is_public") val isPublic: Boolean? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

/**
 * Response from POST /sync/exercises wrapped in { data: SyncExercisesData }.
 */
data class SyncExercisesData(
    @SerializedName("created_mappings") val createdMappings: List<IdMapping>,
    @SerializedName("server_updates") val serverUpdates: List<ExerciseDto>,
    @SerializedName("server_created") val serverCreated: List<ExerciseDto>,
    @SerializedName("confirmed_deletions") val confirmedDeletions: List<Long>
)

// ============================================================================
// Sync Routine DTOs
// ============================================================================

/**
 * Request body for POST /sync/routines.
 */
data class SyncRoutinesRequest(
    @SerializedName("client_timestamp") val clientTimestamp: String,
    val created: List<SyncRoutineCreate> = emptyList(),
    val updated: List<SyncRoutineUpdate> = emptyList(),
    @SerializedName("deleted_ids") val deletedIds: List<Long> = emptyList()
)

data class SyncRoutineCreate(
    @SerializedName("local_id") val localId: String,
    val name: String,
    val description: String? = null,
    @SerializedName("targeted_body_part") val targetedBodyPart: String? = null,
    @SerializedName("is_public") val isPublic: Boolean = false,
    val exercises: List<SyncRoutineExercise> = emptyList()
)

data class SyncRoutineUpdate(
    val id: Long,
    val name: String? = null,
    val description: String? = null,
    @SerializedName("targeted_body_part") val targetedBodyPart: String? = null,
    @SerializedName("is_public") val isPublic: Boolean? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    val exercises: List<SyncRoutineExercise>? = null
)

data class SyncRoutineExercise(
    @SerializedName("exercise_server_id") val exerciseServerId: Long,
    @SerializedName("stated_sets_and_reps") val statedSetsAndReps: String? = null,
    val observations: String? = null,
    val order: Int? = null
)

/**
 * Response from POST /sync/routines wrapped in { data: SyncRoutinesData }.
 */
data class SyncRoutinesData(
    @SerializedName("created_mappings") val createdMappings: List<IdMapping>,
    @SerializedName("server_updates") val serverUpdates: List<RoutineDto>,
    @SerializedName("server_created") val serverCreated: List<RoutineDto>,
    @SerializedName("confirmed_deletions") val confirmedDeletions: List<Long>
)

// ============================================================================
// Sync Workout DTOs
// ============================================================================

/**
 * Request body for POST /sync/workouts.
 */
data class SyncWorkoutsRequest(
    @SerializedName("client_timestamp") val clientTimestamp: String,
    val created: List<SyncWorkoutCreate> = emptyList(),
    val updated: List<SyncWorkoutUpdate> = emptyList(),
    @SerializedName("deleted_ids") val deletedIds: List<Long> = emptyList()
)

data class SyncWorkoutCreate(
    @SerializedName("local_id") val localId: String,
    val title: String,
    val notes: String? = null,
    val date: String,
    @SerializedName("is_finished") val isFinished: Boolean = false,
    @SerializedName("routine_ids") val routineIds: List<Long>? = null,
    val sets: List<SyncWorkoutSet> = emptyList()
)

data class SyncWorkoutUpdate(
    val id: Long,
    val title: String? = null,
    val notes: String? = null,
    val date: String? = null,
    @SerializedName("is_finished") val isFinished: Boolean? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("routine_ids") val routineIds: List<Long>? = null,
    val sets: List<SyncWorkoutSet>? = null
)

data class SyncWorkoutSet(
    @SerializedName("exercise_server_id") val exerciseServerId: Long,
    val weight: Double? = null,
    val repetitions: Int? = null,
    val time: Int? = null,
    val distance: Double? = null,
    val difficulty: Int? = null,
    val notes: String? = null,
    val order: Int? = null
)

/**
 * Response from POST /sync/workouts wrapped in { data: SyncWorkoutsData }.
 */
data class SyncWorkoutsData(
    @SerializedName("created_mappings") val createdMappings: List<IdMapping>,
    @SerializedName("server_updates") val serverUpdates: List<WorkoutDto>,
    @SerializedName("server_created") val serverCreated: List<WorkoutDto>,
    @SerializedName("confirmed_deletions") val confirmedDeletions: List<Long>
)

// ============================================================================
// Sync Planning DTOs
// ============================================================================

/**
 * Request body for POST /sync/planning.
 */
data class SyncPlanningRequest(
    @SerializedName("client_timestamp") val clientTimestamp: String,
    val created: List<SyncPlanningCreate> = emptyList(),
    val updated: List<SyncPlanningUpdate> = emptyList(),
    @SerializedName("deleted_ids") val deletedIds: List<Long> = emptyList()
)

data class SyncPlanningCreate(
    @SerializedName("local_id") val localId: String,
    val date: String,
    @SerializedName("routine_id") val routineId: Long? = null,
    @SerializedName("body_part") val bodyPart: String? = null,
    @SerializedName("reminder_time") val reminderTime: String? = null,
    @SerializedName("planning_exercises") val planningExercises: List<SyncPlanningExercise>? = null
)

data class SyncPlanningUpdate(
    val id: Long,
    val date: String? = null,
    @SerializedName("routine_id") val routineId: Long? = null,
    @SerializedName("body_part") val bodyPart: String? = null,
    @SerializedName("reminder_time") val reminderTime: String? = null,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("planning_exercises") val planningExercises: List<SyncPlanningExercise>? = null
)

/**
 * Exercise expectation within a planning sync payload.
 */
data class SyncPlanningExercise(
    @SerializedName("exercise_id") val exerciseId: Long,
    @SerializedName("expectation_text") val expectationText: String? = null,
    val position: Int? = null,
    val notes: String? = null
)

/**
 * Response from POST /sync/planning wrapped in { data: SyncPlanningData }.
 */
data class SyncPlanningData(
    @SerializedName("created_mappings") val createdMappings: List<IdMapping>,
    @SerializedName("server_updates") val serverUpdates: List<PlanningDto>,
    @SerializedName("server_created") val serverCreated: List<PlanningDto>,
    @SerializedName("confirmed_deletions") val confirmedDeletions: List<Long>
)

// ============================================================================
// Shared DTOs
// ============================================================================

/**
 * Local ID to server ID mapping returned by sync endpoints.
 */
data class IdMapping(
    @SerializedName("local_id") val localId: String,
    @SerializedName("server_id") val serverId: Long
)

// ============================================================================
// Sync Calendar Phase DTOs (matching backend SyncCalendarPhasesRequest / response)
// ============================================================================

/**
 * Request body for POST /sync/calendar-phases.
 *
 * Sends local calendar phase changes to the server.
 */
data class SyncCalendarPhasesRequest(
    @SerializedName("client_timestamp") val clientTimestamp: String,
    val created: List<SyncCalendarPhaseCreate> = emptyList(),
    val updated: List<SyncCalendarPhaseUpdate> = emptyList(),
    @SerializedName("deleted_ids") val deletedIds: List<Long> = emptyList()
)

data class SyncCalendarPhaseCreate(
    @SerializedName("local_id") val localId: String,
    val name: String,
    val color: String,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    val notes: String? = null,
    val visibility: String = "private"
)

data class SyncCalendarPhaseUpdate(
    val id: Long,
    val name: String? = null,
    val color: String? = null,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("end_date") val endDate: String? = null,
    val notes: String? = null,
    val visibility: String? = null,
    @SerializedName("updated_at") val updatedAt: String
)

/**
 * Response from POST /sync/calendar-phases wrapped in { data: SyncCalendarPhasesData }.
 */
data class SyncCalendarPhasesData(
    @SerializedName("created_mappings") val createdMappings: List<IdMapping>,
    @SerializedName("server_updates") val serverUpdates: List<CalendarPhaseDto>,
    @SerializedName("server_created") val serverCreated: List<CalendarPhaseDto>,
    @SerializedName("confirmed_deletions") val confirmedDeletions: List<Long>
)

// ============================================================================
// Sync Trainer Data DTOs (matching backend SyncTrainerDataRequest / response)
// ============================================================================

/**
 * Request body for POST /sync/trainer-data.
 *
 * This endpoint is Server → Client (read-only). The client just sends
 * its last known timestamp and receives all changes since then.
 */
data class SyncTrainerDataRequest(
    @SerializedName("client_timestamp") val clientTimestamp: String
)

/**
 * Response from POST /sync/trainer-data wrapped in { data: SyncTrainerDataData }.
 */
data class SyncTrainerDataData(
    val relations: List<TrainerClientRelationDto>,
    @SerializedName("planning_grants") val planningGrants: List<PlanningGrantDto>,
    @SerializedName("workout_grants") val workoutGrants: List<WorkoutVisibilityGrantDto>
)
