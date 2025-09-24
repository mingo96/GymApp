package com.mintocode.rutinapp.sync

import com.mintocode.rutinapp.data.api.v1.ApiV1Service
import com.mintocode.rutinapp.data.api.v1.dto.*
import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.models.RoutineModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val api: ApiV1Service
) {
    suspend fun syncNewExercises(exercises: List<ExerciseModel>): List<Pair<String, Long>> {
        if (exercises.isEmpty()) return emptyList()
        SyncStateHolder.start()
        val body = SyncExercisesRequest(
            exercises = exercises.map {
                SyncExerciseInput(
                    local_id = it.id.toString(),
                    name = it.name,
                    description = it.description,
                    targeted_body_part = it.targetedBodyPart
                )
            }
        )
        return try {
            val res = api.syncExercises(bearer = "", body = body) // interceptor añade token
            SyncStateHolder.success()
            res.data?.synced_exercises?.map { it.local_id to it.server_id } ?: emptyList()
        } catch (e: Exception) {
            SyncStateHolder.fail(e.message)
            emptyList()
        }
    }

    suspend fun syncNewRoutines(routines: List<RoutineModel>): List<Pair<String, Long>> {
        if (routines.isEmpty()) return emptyList()
        SyncStateHolder.start()
        val body = SyncRoutinesRequest(
            routines = routines.map {
                SyncRoutineInput(
                    local_id = it.id.toString(),
                    name = it.name,
                    description = null,
                    targeted_body_part = it.targetedBodyPart
                )
            }
        )
        return try {
            val res = api.syncRoutines(bearer = "", body = body)
            SyncStateHolder.success()
            res.data?.synced_routines?.map { it.local_id to it.server_id } ?: emptyList()
        } catch (e: Exception) {
            SyncStateHolder.fail(e.message)
            emptyList()
        }
    }

    // Download current user's exercises from server (authoritative list)
    suspend fun downloadMyExercises(): List<ExerciseModel> {
        SyncStateHolder.start()
        return try {
            val res = api.getMyExercises(bearer = "")
            val list = res.data?.exercises.orEmpty()
            val mapped = list.map {
                ExerciseModel(
                    id = "0", // local id will be generated if inserted; keep 0 placeholder
                    realId = it.id,
                    name = it.name,
                    description = it.description ?: "",
                    targetedBodyPart = it.targeted_body_part ?: "",
                    observations = it.observations ?: "",
                    isFromThisUser = true,
                    equivalentExercises = emptyList(),
                    setsAndReps = ""
                )
            }
            SyncStateHolder.success()
            mapped
        } catch (e: Exception) {
            SyncStateHolder.fail(e.message)
            emptyList()
        }
    }

    // Future: downloadOtherUsersExercises() -> usar endpoint cuando exista
    suspend fun downloadOtherExercises(): List<ExerciseModel> {
        SyncStateHolder.start()
        return try {
            val res = api.getOtherExercises(bearer = "")
            val list = res.data?.exercises.orEmpty()
            val mapped = list.map {
                ExerciseModel(
                    id = "0",
                    realId = it.id,
                    name = it.name,
                    description = it.description ?: "",
                    targetedBodyPart = it.targeted_body_part ?: "",
                    observations = it.observations ?: "",
                    isFromThisUser = false,
                    equivalentExercises = emptyList(),
                    setsAndReps = ""
                )
            }
            SyncStateHolder.success()
            mapped
        } catch (e: Exception) {
            SyncStateHolder.fail(e.message)
            emptyList()
        }
    }

    // Download current user's routines
    suspend fun downloadMyRoutines(token: String): List<RoutineModel> {
        SyncStateHolder.start()
        return try {
            val res = api.getMyRoutines(bearer = "Bearer $token")
            val list = res.data?.routines.orEmpty()
            val mapped = list.map {
                RoutineModel(
                    name = it.name,
                    targetedBodyPart = it.targeted_body_part ?: "",
                    realId = it.id.toInt(),
                    isFromThisUser = true
                )
            }
            SyncStateHolder.success()
            mapped
        } catch (e: Exception) {
            SyncStateHolder.fail(e.message)
            emptyList()
        }
    }

    suspend fun downloadOtherRoutines(token: String): List<RoutineModel> {
        SyncStateHolder.start()
        return try {
            val res = api.getOtherRoutines(bearer = "Bearer $token")
            val list = res.data?.routines.orEmpty()
            val mapped = list.map {
                RoutineModel(
                    name = it.name,
                    targetedBodyPart = it.targeted_body_part ?: "",
                    realId = it.id.toInt(),
                    isFromThisUser = false
                )
            }
            SyncStateHolder.success()
            mapped
    } catch (e: Exception) {
            SyncStateHolder.fail(e.message)
            emptyList()
        }
    }
}
