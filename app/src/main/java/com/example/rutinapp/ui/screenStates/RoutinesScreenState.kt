package com.example.rutinapp.ui.screenStates

import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.models.RoutineModel

sealed interface RoutinesScreenState {

    data object Observe : RoutinesScreenState

    data class Creating(val routine: RoutineModel? = null, val availableExercises : List<Pair<ExerciseModel, Boolean>>? = null) : RoutinesScreenState

    data class Editing(val routine: RoutineModel) : RoutinesScreenState

}