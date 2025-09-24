package com.mintocode.rutinapp.data.api.v1.dto

// Request bodies for sync endpoints

data class SyncExercisesRequest(
    val exercises: List<SyncExerciseInput>
)

data class SyncExerciseInput(
    val local_id: String,
    val name: String,
    val description: String,
    val targeted_body_part: String
)

data class SyncRoutinesRequest(
    val routines: List<SyncRoutineInput>
)

data class SyncRoutineInput(
    val local_id: String,
    val name: String,
    val description: String?, // backend currently ignores? placeholder
    val targeted_body_part: String
)

// Responses

data class SyncExercisesData(
    val synced_exercises: List<SyncedExercise>
)

data class SyncedExercise(
    val local_id: String,
    val server_id: Long,
    val created: Boolean
)

data class SyncRoutinesData(
    val synced_routines: List<SyncedRoutine>
)

data class SyncedRoutine(
    val local_id: String,
    val server_id: Long,
    val created: Boolean
)
