package com.example.rutinapp.ui.screenStates

import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.models.SetModel
import com.example.rutinapp.data.models.WorkoutModel

sealed interface WorkoutsScreenState {
    data object Observe : WorkoutsScreenState

    data class WorkoutStarted(val workout: WorkoutModel, val otherExercises: List<ExerciseModel>, val setBeingCreated : SetModel? = null) : WorkoutsScreenState

}