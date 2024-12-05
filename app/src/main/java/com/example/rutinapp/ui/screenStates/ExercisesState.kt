package com.example.rutinapp.ui.screenStates

import com.example.rutinapp.newData.models.ExerciseModel

sealed interface ExercisesState{

    data class Observe(val exercise : ExerciseModel? = null) : ExercisesState
    data object Creating : ExercisesState

    data class Modifying(val exerciseModel: ExerciseModel) : ExercisesState

}