package com.mintocode.rutinapp.data.models

data class ExerciseModel(
    var id: String = "",
    var name: String,
    var description: String,
    var targetedBodyPart: String,
    var equivalentExercises: List<ExerciseModel> = emptyList(),
    var setsAndReps: String = "",
    var observations: String = ""
)