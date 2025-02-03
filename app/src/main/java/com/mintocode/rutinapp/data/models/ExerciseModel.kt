package com.mintocode.rutinapp.data.models

import com.mintocode.rutinapp.data.api.classes.Exercise
import com.mintocode.rutinapp.data.daos.ExerciseEntity

data class ExerciseModel(
    var id: String = "0",
    var realId: Long = 0L,
    var name: String,
    var description: String,
    var targetedBodyPart: String,
    var equivalentExercises: List<ExerciseModel> = emptyList(),
    var setsAndReps: String = "",
    var observations: String = "",
    val isFromThisUser : Boolean = true
){

    fun toEntity(): ExerciseEntity{
        return ExerciseEntity(
            exerciseId = id.toIntOrNull()?:0,
            exerciseName = name,
            exerciseDescription = description,
            targetedBodyPart = targetedBodyPart,
            realId = realId.toInt(),
            )
    }

    fun toAPIModel(): Exercise {
        return Exercise(
            id = id,
            realId = realId,
            name = name,
            description = description,
            targetedBodyPart = targetedBodyPart,
            equivalentExercises = equivalentExercises.map { it.id.toInt() },
            setsAndReps = setsAndReps,
            observations = observations,
            isFromThisUser = isFromThisUser
        )
    }
}