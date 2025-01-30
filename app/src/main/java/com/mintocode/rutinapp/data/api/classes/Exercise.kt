package com.mintocode.rutinapp.data.api.classes

import com.mintocode.rutinapp.data.models.ExerciseModel

class Exercise(
    val exerciseId: Long = 0,
    var exerciseName: String = "",
    var exerciseDescription: String = "",
    var targetedBodyPart: String = "",
    var userId: Long = 0
) {
    fun toExerciseModel() = ExerciseModel(
        id = exerciseId.toString(),
        name = exerciseName,
        description = exerciseDescription,
        targetedBodyPart = targetedBodyPart
    )
}