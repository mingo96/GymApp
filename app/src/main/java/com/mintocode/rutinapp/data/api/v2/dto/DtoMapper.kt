package com.mintocode.rutinapp.data.api.v2.dto

import com.mintocode.rutinapp.data.models.CalendarPhaseModel
import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.models.PlanningExerciseModel
import com.mintocode.rutinapp.data.models.PlanningGrantModel
import com.mintocode.rutinapp.data.models.PlanningModel
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.data.models.SetModel
import com.mintocode.rutinapp.data.models.TrainerRelationModel
import com.mintocode.rutinapp.data.models.WorkoutModel
import com.mintocode.rutinapp.data.models.WorkoutVisibilityGrantModel
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
            realId = dto.id,
            date = parseDate(dto.date),
            title = dto.title,
            isFinished = dto.isFinished,
            isDirty = false
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
     * Note: The routine reference has realId set but local id = 0.
     * Caller must resolve the local routineId before persisting.
     * Includes planning exercises and trainer metadata if present.
     */
    fun toPlanningModel(dto: PlanningDto): PlanningModel {
        return PlanningModel(
            id = 0,
            realId = dto.id,
            date = parseDate(dto.date),
            statedRoutine = dto.routine?.let { toRoutineModel(it) },
            statedBodyPart = dto.bodyPart,
            reminderTime = dto.reminderTime,
            isDirty = false,
            planningExercises = dto.planningExercises?.map { toPlanningExerciseModel(it) } ?: emptyList(),
            createdByUserId = dto.createdByUserId,
            derivedFromPlanningId = dto.derivedFromPlanningId
        )
    }

    /**
     * Converts a local PlanningModel to a SyncPlanningCreate DTO.
     *
     * Uses the routine's server realId. If the routine has not been
     * synced (realId == 0), sends null to avoid referencing a non-existent
     * server resource. Includes planning exercises if present.
     */
    fun toSyncPlanningCreate(model: PlanningModel): SyncPlanningCreate {
        val routineServerId = model.statedRoutine?.realId?.toLong()
        return SyncPlanningCreate(
            localId = model.id.toString(),
            date = toDateString(model.date),
            routineId = if (routineServerId != null && routineServerId != 0L) routineServerId else null,
            bodyPart = model.statedBodyPart,
            reminderTime = model.reminderTime,
            planningExercises = model.planningExercises.takeIf { it.isNotEmpty() }?.map {
                SyncPlanningExercise(
                    exerciseId = it.exerciseId,
                    expectationText = it.expectationText,
                    position = it.position,
                    notes = it.notes
                )
            }
        )
    }

    // ========================================================================
    // Planning Exercise mapping
    // ========================================================================

    /**
     * Maps a PlanningExerciseDto from the API to a PlanningExerciseModel.
     */
    fun toPlanningExerciseModel(dto: PlanningExerciseDto): PlanningExerciseModel {
        return PlanningExerciseModel(
            id = dto.id,
            exerciseId = dto.exerciseId,
            exerciseName = dto.exercise?.name ?: "",
            expectationText = dto.expectationText,
            position = dto.position,
            notes = dto.notes
        )
    }

    // ========================================================================
    // Calendar Phase mapping
    // ========================================================================

    /**
     * Maps a CalendarPhaseDto from the API to a local CalendarPhaseModel.
     */
    fun toCalendarPhaseModel(dto: CalendarPhaseDto): CalendarPhaseModel {
        return CalendarPhaseModel(
            id = 0,
            serverId = dto.id,
            name = dto.name,
            color = dto.color,
            startDate = parseDate(dto.startDate),
            endDate = parseDate(dto.endDate),
            notes = dto.notes,
            visibility = dto.visibility ?: "private",
            createdByUserId = dto.createdByUserId,
            isDirty = false
        )
    }

    /**
     * Converts a local CalendarPhaseModel to a SyncCalendarPhaseCreate DTO.
     */
    fun toSyncCalendarPhaseCreate(model: CalendarPhaseModel): SyncCalendarPhaseCreate {
        return SyncCalendarPhaseCreate(
            localId = model.id.toString(),
            name = model.name,
            color = model.color,
            startDate = toDateString(model.startDate),
            endDate = toDateString(model.endDate),
            notes = model.notes,
            visibility = model.visibility
        )
    }

    /**
     * Converts a local CalendarPhaseModel to a SyncCalendarPhaseUpdate DTO.
     */
    fun toSyncCalendarPhaseUpdate(model: CalendarPhaseModel): SyncCalendarPhaseUpdate {
        return SyncCalendarPhaseUpdate(
            id = model.serverId,
            name = model.name,
            color = model.color,
            startDate = toDateString(model.startDate),
            endDate = toDateString(model.endDate),
            notes = model.notes,
            visibility = model.visibility,
            updatedAt = toIsoString()
        )
    }

    // ========================================================================
    // Trainer data mapping
    // ========================================================================

    /**
     * Maps a TrainerClientRelationDto to a domain TrainerRelationModel.
     */
    fun toTrainerRelationModel(dto: TrainerClientRelationDto): TrainerRelationModel {
        return TrainerRelationModel(
            id = dto.id,
            trainerUserId = dto.trainerUserId,
            clientUserId = dto.clientUserId,
            status = dto.status,
            notes = dto.notes
        )
    }

    /**
     * Maps a PlanningGrantDto to a domain PlanningGrantModel.
     */
    fun toPlanningGrantModel(dto: PlanningGrantDto): PlanningGrantModel {
        return PlanningGrantModel(
            id = dto.id,
            clientUserId = dto.clientUserId,
            trainerUserId = dto.trainerUserId,
            accessType = dto.accessType,
            dateFrom = dto.dateFrom,
            dateTo = dto.dateTo,
            isActive = dto.isActive
        )
    }

    /**
     * Maps a WorkoutVisibilityGrantDto to a domain WorkoutVisibilityGrantModel.
     */
    fun toWorkoutVisibilityGrantModel(dto: WorkoutVisibilityGrantDto): WorkoutVisibilityGrantModel {
        return WorkoutVisibilityGrantModel(
            id = dto.id,
            clientUserId = dto.clientUserId,
            trainerUserId = dto.trainerUserId,
            canViewResults = dto.canViewResults,
            dateFrom = dto.dateFrom,
            dateTo = dto.dateTo,
            isActive = dto.isActive
        )
    }
}
