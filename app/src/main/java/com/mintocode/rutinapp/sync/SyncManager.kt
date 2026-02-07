package com.mintocode.rutinapp.sync

import android.util.Log
import com.mintocode.rutinapp.data.api.v2.ApiV2Service
import com.mintocode.rutinapp.data.api.v2.dto.*
import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.models.PlanningModel
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.data.models.WorkoutModel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Orchestrates bidirectional synchronization with the RutinApp API v2.
 *
 * Handles sync for exercises, routines, workouts, and planning entries.
 * Uses the v2 sync endpoints which follow a unified request/response format:
 * - Request: { client_timestamp, created[], updated[], deleted_ids[] }
 * - Response: { data: { created_mappings[], server_updates[], server_created[], confirmed_deletions[] } }
 */
@Singleton
class SyncManager @Inject constructor(
    private val api: ApiV2Service
) {
    companion object {
        private const val TAG = "SyncManager"
    }

    // ========================================================================
    // Exercise sync
    // ========================================================================

    /**
     * Syncs new/dirty exercises to the server.
     *
     * Sends locally created exercises and returns local_id → server_id mappings.
     *
     * @param exercises List of new exercises with isDirty=true
     * @return List of (localId, serverId) pairs for newly created exercises
     */
    suspend fun syncNewExercises(exercises: List<ExerciseModel>): List<Pair<String, Long>> {
        if (exercises.isEmpty()) return emptyList()
        SyncStateHolder.start()
        val body = SyncExercisesRequest(
            clientTimestamp = DtoMapper.toIsoString(),
            created = exercises.map { DtoMapper.toSyncExerciseCreate(it) }
        )
        return try {
            val res = api.syncExercises(body)
            SyncStateHolder.success()
            res.data?.createdMappings?.map { it.localId to it.serverId } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "syncNewExercises failed", e)
            SyncStateHolder.fail(e.message)
            emptyList()
        }
    }

    /**
     * Syncs updated exercises to the server.
     *
     * @param exercises List of exercises with local changes (realId > 0)
     */
    suspend fun syncUpdatedExercises(exercises: List<ExerciseModel>) {
        if (exercises.isEmpty()) return
        SyncStateHolder.start()
        val body = SyncExercisesRequest(
            clientTimestamp = DtoMapper.toIsoString(),
            updated = exercises.map { DtoMapper.toSyncExerciseUpdate(it) }
        )
        try {
            api.syncExercises(body)
            SyncStateHolder.success()
        } catch (e: Exception) {
            Log.e(TAG, "syncUpdatedExercises failed", e)
            SyncStateHolder.fail(e.message)
        }
    }

    /**
     * Downloads the user's exercises from the server (own exercises only).
     *
     * Uses the paginated GET /exercises?mine_only=true endpoint.
     *
     * @return List of ExerciseModels from server marked as isFromThisUser=true
     */
    suspend fun downloadMyExercises(): List<ExerciseModel> {
        SyncStateHolder.start()
        return try {
            val res = api.getExercises(mineOnly = true, perPage = 200)
            val mapped = res.data.map { DtoMapper.toExerciseModel(it) }
            SyncStateHolder.success()
            mapped
        } catch (e: Exception) {
            Log.e(TAG, "downloadMyExercises failed", e)
            SyncStateHolder.fail(e.message)
            emptyList()
        }
    }

    /**
     * Downloads public exercises from other users.
     *
     * Uses GET /exercises (returns own + public). Filters out own exercises client-side.
     *
     * @return List of ExerciseModels from server marked as isFromThisUser=false
     */
    suspend fun downloadOtherExercises(): List<ExerciseModel> {
        SyncStateHolder.start()
        return try {
            val res = api.getExercises(perPage = 200)
            val mapped = res.data
                .filter { !it.isMine }
                .map { DtoMapper.toExerciseModel(it) }
            SyncStateHolder.success()
            mapped
        } catch (e: Exception) {
            Log.e(TAG, "downloadOtherExercises failed", e)
            SyncStateHolder.fail(e.message)
            emptyList()
        }
    }

    // ========================================================================
    // Routine sync
    // ========================================================================

    /**
     * Syncs new/dirty routines to the server.
     *
     * @param routines List of new routines with isDirty=true
     * @return List of (localId, serverId) pairs
     */
    suspend fun syncNewRoutines(routines: List<RoutineModel>): List<Pair<String, Long>> {
        if (routines.isEmpty()) return emptyList()
        SyncStateHolder.start()
        val body = SyncRoutinesRequest(
            clientTimestamp = DtoMapper.toIsoString(),
            created = routines.map { DtoMapper.toSyncRoutineCreate(it) }
        )
        return try {
            val res = api.syncRoutines(body)
            SyncStateHolder.success()
            res.data?.createdMappings?.map { it.localId to it.serverId } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "syncNewRoutines failed", e)
            SyncStateHolder.fail(e.message)
            emptyList()
        }
    }

    /**
     * Downloads the user's routines from the server.
     *
     * @return List of RoutineModels from server
     */
    suspend fun downloadMyRoutines(): List<RoutineModel> {
        SyncStateHolder.start()
        return try {
            val res = api.getRoutines(mineOnly = true, perPage = 200)
            val mapped = res.data.map { DtoMapper.toRoutineModel(it) }
            SyncStateHolder.success()
            mapped
        } catch (e: Exception) {
            Log.e(TAG, "downloadMyRoutines failed", e)
            SyncStateHolder.fail(e.message)
            emptyList()
        }
    }

    /**
     * Downloads public routines from other users.
     *
     * @return List of RoutineModels from server marked as isFromThisUser=false
     */
    suspend fun downloadOtherRoutines(): List<RoutineModel> {
        SyncStateHolder.start()
        return try {
            val res = api.getRoutines(perPage = 200)
            val mapped = res.data
                .filter { !it.isMine }
                .map { DtoMapper.toRoutineModel(it) }
            SyncStateHolder.success()
            mapped
        } catch (e: Exception) {
            Log.e(TAG, "downloadOtherRoutines failed", e)
            SyncStateHolder.fail(e.message)
            emptyList()
        }
    }

    // ========================================================================
    // Workout sync
    // ========================================================================

    /**
     * Syncs new local workouts (with their sets) to the server.
     *
     * @param workouts List of workouts to sync
     * @return List of (localId, serverId) pairs
     */
    suspend fun syncNewWorkouts(workouts: List<WorkoutModel>): List<Pair<String, Long>> {
        if (workouts.isEmpty()) return emptyList()
        SyncStateHolder.start()
        val body = SyncWorkoutsRequest(
            clientTimestamp = DtoMapper.toIsoString(),
            created = workouts.map { DtoMapper.toSyncWorkoutCreate(it) }
        )
        return try {
            val res = api.syncWorkouts(body)
            SyncStateHolder.success()
            res.data?.createdMappings?.map { it.localId to it.serverId } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "syncNewWorkouts failed", e)
            SyncStateHolder.fail(e.message)
            emptyList()
        }
    }

    /**
     * Downloads the user's workouts from the server.
     *
     * @return List of WorkoutModels from server
     */
    suspend fun downloadMyWorkouts(): List<WorkoutModel> {
        SyncStateHolder.start()
        return try {
            val res = api.getWorkouts(perPage = 200)
            val mapped = res.data.map { DtoMapper.toWorkoutModel(it) }
            SyncStateHolder.success()
            mapped
        } catch (e: Exception) {
            Log.e(TAG, "downloadMyWorkouts failed", e)
            SyncStateHolder.fail(e.message)
            emptyList()
        }
    }

    // ========================================================================
    // Planning sync
    // ========================================================================

    /**
     * Syncs new local planning entries to the server.
     *
     * @param plannings List of planning entries to sync
     * @return List of (localId, serverId) pairs
     */
    suspend fun syncNewPlannings(plannings: List<PlanningModel>): List<Pair<String, Long>> {
        if (plannings.isEmpty()) return emptyList()
        SyncStateHolder.start()
        val body = SyncPlanningRequest(
            clientTimestamp = DtoMapper.toIsoString(),
            created = plannings.map { DtoMapper.toSyncPlanningCreate(it) }
        )
        return try {
            val res = api.syncPlanning(body)
            SyncStateHolder.success()
            res.data?.createdMappings?.map { it.localId to it.serverId } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "syncNewPlannings failed", e)
            SyncStateHolder.fail(e.message)
            emptyList()
        }
    }

    /**
     * Downloads the user's planning entries from the server.
     *
     * @return List of PlanningModels from server
     */
    suspend fun downloadMyPlanning(): List<PlanningModel> {
        SyncStateHolder.start()
        return try {
            val res = api.getPlanning()
            val mapped = res.data.map { DtoMapper.toPlanningModel(it) }
            SyncStateHolder.success()
            mapped
        } catch (e: Exception) {
            Log.e(TAG, "downloadMyPlanning failed", e)
            SyncStateHolder.fail(e.message)
            emptyList()
        }
    }

    // ========================================================================
    // Full sync (convenience)
    // ========================================================================

    /**
     * Result of a full sync operation across all entity types.
     */
    data class FullSyncResult(
        val exerciseMappings: List<Pair<String, Long>> = emptyList(),
        val routineMappings: List<Pair<String, Long>> = emptyList(),
        val workoutMappings: List<Pair<String, Long>> = emptyList(),
        val planningMappings: List<Pair<String, Long>> = emptyList(),
        val errors: List<String> = emptyList()
    )

    /**
     * Performs a full sync of all entity types.
     *
     * Order matters: exercises first (routines reference them),
     * then routines (workouts reference them), then workouts, then planning.
     *
     * @param dirtyExercises Local exercises needing sync
     * @param dirtyRoutines Local routines needing sync
     * @param dirtyWorkouts Local workouts needing sync
     * @param dirtyPlannings Local planning entries needing sync
     */
    suspend fun fullSync(
        dirtyExercises: List<ExerciseModel> = emptyList(),
        dirtyRoutines: List<RoutineModel> = emptyList(),
        dirtyWorkouts: List<WorkoutModel> = emptyList(),
        dirtyPlannings: List<PlanningModel> = emptyList()
    ): FullSyncResult {
        val errors = mutableListOf<String>()

        val exMappings = try {
            syncNewExercises(dirtyExercises)
        } catch (e: Exception) {
            errors.add("Exercise sync: ${e.message}")
            emptyList()
        }

        val rtMappings = try {
            syncNewRoutines(dirtyRoutines)
        } catch (e: Exception) {
            errors.add("Routine sync: ${e.message}")
            emptyList()
        }

        val woMappings = try {
            syncNewWorkouts(dirtyWorkouts)
        } catch (e: Exception) {
            errors.add("Workout sync: ${e.message}")
            emptyList()
        }

        val plMappings = try {
            syncNewPlannings(dirtyPlannings)
        } catch (e: Exception) {
            errors.add("Planning sync: ${e.message}")
            emptyList()
        }

        return FullSyncResult(
            exerciseMappings = exMappings,
            routineMappings = rtMappings,
            workoutMappings = woMappings,
            planningMappings = plMappings,
            errors = errors
        )
    }
}
