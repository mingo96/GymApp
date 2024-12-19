package com.example.rutinapp.data.models

import java.util.Date

data class WorkoutModel(
    val id: Int=0,
    var baseRoutine : RoutineModel? = null,
    var exercisesAndSets: List<Pair<ExerciseModel, List<SetModel>>> = listOf(),
    val date: Date,
    var title: String
){
}
