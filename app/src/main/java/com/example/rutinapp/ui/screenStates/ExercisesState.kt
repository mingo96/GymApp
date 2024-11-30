package com.example.rutinapp.ui.screenStates

import com.example.rutinapp.newData.models.ExerciseModel

sealed interface ExercisesState{

    data object Observe : ExercisesState
    data object Creating : ExercisesState

    data class Modifying(val exerciseModel: ExerciseModel) : ExercisesState

}