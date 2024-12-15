package com.example.rutinapp.ui.screenStates

import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.models.RoutineModel

sealed interface RoutinesScreenState {

    data object Overview : RoutinesScreenState

    data class Observe(val routine: RoutineModel) : RoutinesScreenState

    data class Creating(
        val routine: RoutineModel? = null,
        val availableExercises: List<Pair<ExerciseModel, Boolean>>? = null
    ) : RoutinesScreenState

    /**when [positionOfScreen] is true we are editing name and description of routine, false is exercises*/
    data class Editing(
        val routine: RoutineModel,
        val availableExercises: List<ExerciseModel> = emptyList(),
        val positionOfScreen: Boolean = true,
        val selectedExercise: ExerciseModel? = null
    ) : RoutinesScreenState

}