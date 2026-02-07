package com.mintocode.rutinapp.data.api.v2.dto

import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.models.PlanningModel
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.data.models.SetModel
import com.mintocode.rutinapp.data.models.WorkoutModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Maps between API v2 DTOs and local domain models.
 *
 * Centralizes all mapping logic to keep it DRY and testable.
 */
object DtoMapper {

    private val isoFormat: SimpleDateFormat
        get() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

    private val dateOnlyFormat: SimpleDateFormat
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

    // ========================================================================
    // Timestamp helpers
    // ========================================================================

    /**
     * Formats a Date as an ISO 8601 timestamp string.
     */
    fun toIsoString(date: Date = Date()): String = isoFormat.format(date)

    /**
     * Formats a Date as a YYYY-MM-DD date string.
     */
    fun toDateString(date: Date): String = dateOnlyFormat.format(date)

    /**
     * Parses an ISO 8601 timestamp string to a Date.
     * Falls back to date-only format, then to current time on failure.
     */
    fun parseDate(dateString: String?): Date {
        if (dateString.isNullOrBlank()) return Date()
        return try {
            isoFormat.parse(dateString) ?: Date()
        } catch (_: Exception) {
            try {
                dateOnlyFormat.parse(dateString) ?: Date()
            } catch (_: Exception) {
                Date()
            }
        }
    }

    // ========================================================================
    // Exercise mapping
    // ========================================================================

    /**
     * Maps an ExerciseDto from the API to a local ExerciseModel.
     *
     * @param dto The API exercise DTO
     * @param localId Optional local ID to preserve (defaults to "0")
     */
    fun toExerciseModel(dto: ExerciseDto, localId: String = "0"): ExerciseModel {
        return ExerciseModel(
            id = localId,
            realId = dto.id,
            name = dto.name,
            description = dto.description ?: "",
            targetedBodyPart = dto.targetedBodyPart ?: "",
            observations = dto.observations ?: "",
            isFromThisUser = dto.isMine,
            equivalentExercises = emptyList(),
            setsAndReps = "",
            isDirty = false
        )
    }

    /**
     * Converts a local ExerciseModel to a SyncExerciseCreate DTO for sync upload.
     */
    fun toSyncExerciseCreate(model: ExerciseModel): SyncExerciseCreate {
        return SyncExerciseCreate(
            localId = model.id,
            name = model.name,
            description = model.description.ifBlank { null },
            targetedBodyPart = model.targetedBodyPart.ifBlank { null },
            observations = model.observations.ifBlank { null }
        )
    }

    /**
     * Converts a local ExerciseModel to a SyncExerciseUpdate DTO.
     */
    fun toSyncExerciseUpdate(model: ExerciseModel): SyncExerciseUpdate {
        return SyncExerciseUpdate(
            id = model.realId,
            name = model.name,
            description = model.description.ifBlank { null },
            targetedBodyPart = model.targetedBodyPart.ifBlank { null },
            observations = model.observations.ifBlank { null }
        )
    }

    // ========================================================================
    // Routine mapping
    // ========================================================================

    /**
     * Maps a RoutineDto from the API to a local RoutineModel.
     */
    fun toRoutineModel(dto: RoutineDto): RoutineModel {
        return RoutineModel(
            name = dto.name,
            targetedBodyPart = dto.targetedBodyPart ?: "",
            realId = dto.id.toInt(),
            isFromThisUser = dto.isMine,
            exercises = dto.exercises?.map { toExerciseModel(it) }?.toMutableList() ?: mutableListOf(),
            isDirty = false
        )
    }

    /**
     * Converts a local RoutineModel to a SyncRoutineCreate DTO.
     */
    fun toSyncRoutineCreate(model: RoutineModel): SyncRoutineCreate {
        return SyncRoutineCreate(
            localId = model.id.toString(),
            name = model.name,
            targetedBodyPart = model.targetedBodyPart.ifBlank { null },
            exercises = model.exercises.filter { it.realId > 0 }.mapIndexed { index, ex ->
                SyncRoutineExercise(
                    exerciseServerId = ex.realId,
                    statedSetsAndReps = ex.setsAndReps.ifBlank { null },
                    order = index
                )
            }
        )
    }

    // ========================================================================
    // Workout mapping
    // ========================================================================

    /**
     * Maps a WorkoutDto from the API to a local WorkoutModel.
     *
     * Note: Sets and exercise linkage need to be resolved separately
     * since the local model uses object references.
     */
    fun toWorkoutModel(dto: WorkoutDto): WorkoutModel {
        return WorkoutModel(
            date = parseDate(dto.date),
            title = dto.title,
            isFinished = dto.isFinished
        )
    }

    /**
     * Converts a local WorkoutModel to a SyncWorkoutCreate DTO.
     */
    fun toSyncWorkoutCreate(model: WorkoutModel): SyncWorkoutCreate {
        val allSets = model.exercisesAndSets.flatMap { (exercise, sets) ->
            sets.mapIndexed { index, set ->
                SyncWorkoutSet(
                    exerciseServerId = exercise.realId,
                    weight = set.weight,
                    repetitions = set.reps,
                    notes = set.observations.ifBlank { null },
                    order = index
                )
            }
        }
        return SyncWorkoutCreate(
            localId = model.id.toString(),
            title = model.title,
            date = toIsoString(model.date),
            isFinished = model.isFinished,
            routineIds = model.baseRoutine?.let { listOf(it.realId.toLong()) },
            sets = allSets
        )
    }

    // ========================================================================
    // Planning mapping
    // ========================================================================

    /**
     * Maps a PlanningDto from the API to a local PlanningModel.
     *
     * Note: The routine reference needs to be resolved separately.
     */
    fun toPlanningModel(dto: PlanningDto): PlanningModel {
        return PlanningModel(
            id = dto.id.toInt(),
            date = parseDate(dto.date),
            statedRoutine = dto.routine?.let { toRoutineModel(it) },
            statedBodyPart = dto.bodyPart
        )
    }

    /**
     * Converts a local PlanningModel to a SyncPlanningCreate DTO.
     */
    fun toSyncPlanningCreate(model: PlanningModel): SyncPlanningCreate {
        return SyncPlanningCreate(
            localId = model.id.toString(),
            date = toDateString(model.date),
            routineId = model.statedRoutine?.realId?.toLong(),
            bodyPart = model.statedBodyPart
        )
    }
}
