package com.mintocode.rutinapp.data.models

import com.mintocode.rutinapp.data.api.classes.Exercise

data class ExerciseModel(
    var id: String = "",
    var name: String,
    var description: String,
    var targetedBodyPart: String,
    var equivalentExercises: List<ExerciseModel> = emptyList(),
    var setsAndReps: String = "",
    var observations: String = ""
){
    fun toAPIModel() = Exercise(
        exerciseId = id.toLong(),
        exerciseName = name,
        exerciseDescription = description,
        targetedBodyPart = targetedBodyPart
    )
}