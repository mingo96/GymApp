package com.example.rutinapp.ui.screenStates

import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.models.PlanningModel
import com.example.rutinapp.data.models.WorkoutModel

sealed interface WorkoutsScreenState {

    data class Observe(val planning: PlanningModel? = null) : WorkoutsScreenState

    data class WorkoutStarted(
        val workout: WorkoutModel,
        val otherExercises: List<ExerciseModel>,
        val setBeingCreated: SetState? = null,
        val exerciseBeingSwapped: ExerciseModel? = null,
    ) : WorkoutsScreenState

}