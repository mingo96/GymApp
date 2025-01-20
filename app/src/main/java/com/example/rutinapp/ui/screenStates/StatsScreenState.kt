package com.example.rutinapp.ui.screenStates

import com.example.rutinapp.data.models.ExerciseModel
import java.util.Date

sealed interface StatsScreenState {

    data class Observation(val exercises: List<ExerciseModel> = emptyList()) : StatsScreenState

    data class StatsOfExercise(
        val hasBeenDone: Boolean = false,
        val exercise: ExerciseModel,
        val highestWeight: Triple<Double, Date, String> = Triple(0.0, Date(), ""),
        val timesDone: Int = 0,
        val averageWeight: Double = 0.0,
        val lastTimeDone: String = "",
        val weigths: List<Double> = emptyList(),
        val daysDone: List<Pair<String, Double>> = emptyList(),
    ) : StatsScreenState

}