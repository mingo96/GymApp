package com.example.rutinapp.data.models

import java.util.Date

data class WorkoutModel(
    var id: Int=0,
    var baseRoutine : RoutineModel? = null,
    var exercisesAndSets: MutableList<Pair<ExerciseModel, MutableList<SetModel>>> = mutableListOf(),
    val date: Date,
    var title: String
){
}
