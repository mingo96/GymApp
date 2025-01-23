package com.mintocode.rutinapp.ui.screenStates

import com.mintocode.rutinapp.data.models.SetModel

sealed interface SetState {

    data class OptionsOfSet(val set: SetModel) : SetState

    data class CreatingSet(val set: SetModel) : SetState

}