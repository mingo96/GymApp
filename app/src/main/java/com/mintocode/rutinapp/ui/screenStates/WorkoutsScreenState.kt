package com.mintocode.rutinapp.ui.screenStates

import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.models.PlanningModel
import com.mintocode.rutinapp.data.models.WorkoutModel

sealed interface WorkoutsScreenState {

    data class Observe(val planning: PlanningModel? = null) : WorkoutsScreenState

    data class WorkoutStarted(
        val workout: WorkoutModel,
        val otherExercises: List<ExerciseModel>,
        val setBeingCreated: SetState? = null,
        val exerciseBeingSwapped: ExerciseModel? = null,
    ) : WorkoutsScreenState

}