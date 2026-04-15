package com.mintocode.rutinapp.data.api.v2.dto

import com.google.gson.annotations.SerializedName

// ============================================================================
// Backup Summary
// ============================================================================

/**
 * Resource summary with count and last modification date.
 */
data class BackupResourceSummary(
    val count: Int,
    @SerializedName("last_modified_at") val lastModifiedAt: String?
)

/**
 * Full backup summary response: { data: { exercises: {...}, routines: {...}, workouts: {...} } }
 */
data class BackupSummaryData(
    val exercises: BackupResourceSummary,
    val routines: BackupResourceSummary,
    val workouts: BackupResourceSummary
)

// ============================================================================
// Backup Export
// ============================================================================

/**
 * Exported exercise with all fields for backup.
 */
data class BackupExerciseDto(
    val id: Long,
    val name: String,
    val description: String?,
    @SerializedName("targeted_body_part") val targetedBodyPart: String?,
    val observations: String?,
    @SerializedName("is_public") val isPublic: Boolean,
    @SerializedName("reps_type") val repsType: String?,
    @SerializedName("weight_type") val weightType: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

/**
 * Routine exercise pivot data for backup.
 */
data class BackupRoutineExerciseDto(
    @SerializedName("exercise_id") val exerciseId: Long,
    val order: Int?,
    @SerializedName("stated_sets_and_reps") val statedSetsAndReps: String?,
    val observations: String?
)

/**
 * Exported routine with nested exercises for backup.
 */
data class BackupRoutineDto(
    val id: Long,
    val name: String,
    val description: String?,
    @SerializedName("targeted_body_part") val targetedBodyPart: String?,
    @SerializedName("is_public") val isPublic: Boolean,
    val exercises: List<BackupRoutineExerciseDto>?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

/**
 * Exported set for backup.
 */
data class BackupSetDto(
    val id: Long,
    @SerializedName("exercise_id") val exerciseId: Long,
    val weight: Double?,
    val repetitions: Int?,
    val time: Int?,
    val distance: Double?,
    val difficulty: Int?,
    val notes: String?,
    val order: Int?
)

/**
 * Exported workout with nested sets for backup.
 */
data class BackupWorkoutDto(
    val id: Long,
    val title: String,
    val notes: String?,
    val date: String,
    @SerializedName("is_finished") val isFinished: Boolean,
    @SerializedName("routine_ids") val routineIds: List<Long>?,
    val sets: List<BackupSetDto>?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

/**
 * Export response data containing resource arrays.
 */
data class BackupExportData(
    val exercises: List<BackupExerciseDto>?,
    val routines: List<BackupRoutineDto>?,
    val workouts: List<BackupWorkoutDto>?
)

/**
 * Full export response: { data: {...}, meta: {...} }
 */
data class BackupExportResponse(
    val data: BackupExportData,
    val meta: PaginationMeta?
)

// ============================================================================
// Backup Import
// ============================================================================

/**
 * Import request body sent to POST /backup/import.
 */
data class BackupImportRequest(
    val exercises: List<BackupExerciseDto>? = null,
    val routines: List<BackupRoutineDto>? = null,
    val workouts: List<BackupWorkoutDto>? = null
)

/**
 * Import result per resource.
 */
data class BackupImportResourceResult(
    val created: Int,
    val updated: Int,
    val skipped: Int
)

/**
 * Full import response: { data: { exercises: {...}, routines: {...}, workouts: {...} } }
 */
data class BackupImportData(
    val exercises: BackupImportResourceResult?,
    val routines: BackupImportResourceResult?,
    val workouts: BackupImportResourceResult?
)

// ============================================================================
// Backup Catch-up
// ============================================================================

/**
 * Catch-up request body sent to POST /backup/catch-up.
 */
data class BackupCatchUpRequest(
    val since: String,
    val resources: List<String>
)

/**
 * Catch-up data per resource.
 */
data class BackupCatchUpResourceData(
    val updated: List<BackupExerciseDto>?,
    @SerializedName("deleted_ids") val deletedIds: List<Long>?
)

/**
 * Full catch-up response: { data: { exercises: {...}, routines: {...}, workouts: {...} } }
 */
data class BackupCatchUpData(
    val exercises: BackupCatchUpResourceData?,
    val routines: BackupCatchUpResourceData?,
    val workouts: BackupCatchUpResourceData?
)
