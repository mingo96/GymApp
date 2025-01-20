package com.example.rutinapp.data.models

data class RoutineModel(
    var id: Int = 0,
    var name: String,
    var targetedBodyPart: String,
    var exercises: MutableList<ExerciseModel> = mutableListOf()
)