package com.example.rutinapp.ui.screenStates

import com.example.rutinapp.data.models.SetModel

sealed interface SetState {

    data class OptionsOfSet(val set: SetModel) : SetState

    data class CreatingSet(val set: SetModel) : SetState

}