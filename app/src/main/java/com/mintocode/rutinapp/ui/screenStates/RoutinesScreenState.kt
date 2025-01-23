package com.mintocode.rutinapp.ui.screenStates

import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.models.RoutineModel

sealed interface RoutinesScreenState {

    data object Overview : RoutinesScreenState

    data class Observe(val routine: RoutineModel) : RoutinesScreenState

    data object Creating : RoutinesScreenState

    /**when [positionOfScreen] is true we are editing name and description of routine, false is exercises*/
    data class Editing(
        val routine: RoutineModel,
        val availableExercises: List<ExerciseModel> = emptyList(),
        val positionOfScreen: Boolean = true,
        val selectedExercise: ExerciseModel? = null
    ) : RoutinesScreenState

}