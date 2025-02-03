package com.mintocode.rutinapp.data.api.classes

import com.mintocode.rutinapp.data.models.ExerciseModel

data class Exercise(

    var id: String = "0",
    var realId: Long = 0L,
    var name: String,
    var description: String,
    var targetedBodyPart: String,
    var equivalentExercises: List<Int> = emptyList(),
    var setsAndReps: String = "",
    var observations: String = "",
    val isFromThisUser : Boolean = true
) {
    fun toModel(): ExerciseModel {
        return ExerciseModel(
            id = id,
            realId = realId,
            name = name,
            description = description,
            targetedBodyPart = targetedBodyPart,
            setsAndReps = setsAndReps,
            observations = observations,
            isFromThisUser = isFromThisUser
            )
    }
}