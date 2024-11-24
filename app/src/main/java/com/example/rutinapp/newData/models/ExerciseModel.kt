package com.example.rutinapp.newData.models

data class ExerciseModel(
    var id: String = "",
    var name: String,
    var description: String,
    var targetedBodyPart: String,
    var equivalentExercises: List<ExerciseModel> = emptyList()
) {

}