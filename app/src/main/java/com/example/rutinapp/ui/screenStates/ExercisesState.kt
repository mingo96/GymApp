package com.example.rutinapp.ui.screenStates

import com.example.rutinapp.data.models.ExerciseModel

sealed interface ExercisesState {

    data class Observe(val exercise: ExerciseModel? = null) : ExercisesState
    data object Creating : ExercisesState

    data class Modifying(
        val exerciseModel: ExerciseModel,
        val relatedExercises: List<ExerciseModel>
    ) : ExercisesState

    data class AddingRelations(
        val exerciseModel: ExerciseModel,
        val possibleValues: List<ExerciseModel>
    ) : ExercisesState

    data class SearchingForExercise(val possibleValues: List<ExerciseModel>) : ExercisesState

}