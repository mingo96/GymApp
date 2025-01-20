package com.example.rutinapp.data.models

import com.example.rutinapp.data.daos.SetEntity
import java.util.Date

data class SetModel(
    var id: Int = 0,
    var weight: Double,
    var exercise: ExerciseModel?,
    var workoutDone: WorkoutModel?,
    var reps: Int,
    val date: Date,
    var observations: String
) {
    fun toEntity(): SetEntity {
        return SetEntity(
            weight = weight,
            exerciseDoneId = exercise!!.id.toInt(),
            reps = reps,
            date = date.toString(),
            observations = observations,
            workoutDoneId = workoutDone!!.id
        )
    }
}
