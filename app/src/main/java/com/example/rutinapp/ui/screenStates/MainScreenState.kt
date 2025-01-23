package com.example.rutinapp.ui.screenStates

import com.example.rutinapp.data.models.PlanningModel
import com.example.rutinapp.data.models.RoutineModel

sealed interface MainScreenState {

    data object Observation : MainScreenState

    data class PlanningOnMainFocus(
        val planningModel: PlanningModel,
        val fieldBeingEdited: FieldBeingEdited = FieldBeingEdited.NONE,
        val availableRoutines: List<RoutineModel> = listOf()
    ) : MainScreenState

}

enum class FieldBeingEdited {
    NONE, BODYPART, ROUTINE
}