package com.mintocode.rutinapp.data.models

import com.mintocode.rutinapp.data.daos.SetEntity
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
            setId = id,
            weight = weight,
            exerciseDoneId = exercise!!.id.toInt(),
            reps = reps,
            date = date.time,
            observations = observations,
            workoutDoneId = workoutDone!!.id
        )
    }
}
