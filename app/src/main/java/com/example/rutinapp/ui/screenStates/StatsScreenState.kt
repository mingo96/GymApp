package com.example.rutinapp.ui.screenStates

import com.example.rutinapp.data.models.ExerciseModel
import java.util.Date

sealed interface StatsScreenState {

    data object Observation : StatsScreenState

    data class StatsOfExercise(
        val exercise: ExerciseModel,
        val highestWeight: Triple<Double, Date, String> = Triple(0.0, Date(), ""),
        val timesDone: Int=0,
        val averageWeight: Double=0.0,
        val mostWeigthOnASet : Triple<Double, Date, String> = Triple(0.0, Date(), ""),
        val lastTimeDone : String=""
    ) : StatsScreenState

}