package com.example.rutinapp.newData.models

import java.util.Date

data class TrainingModel(
    val id: Int,
    val baseRoutine : RoutineModel? = null,
    var exercises: List<ExerciseModel> = listOf(),
    val date: Date,
    var title: String
){
    init {
        exercises = baseRoutine?.exercises ?: listOf()
    }
}
