package com.example.rutinapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rutinapp.data.models.PlanningModel
import com.example.rutinapp.data.models.RoutineModel
import com.example.rutinapp.domain.addUseCases.AddPlanningUseCase
import com.example.rutinapp.domain.getUseCases.GetPlanningsUseCase
import com.example.rutinapp.domain.getUseCases.GetRoutinesUseCase
import com.example.rutinapp.domain.updateUseCases.UpdatePlanningUseCase
import com.example.rutinapp.ui.screenStates.FieldBeingEdited
import com.example.rutinapp.ui.screenStates.MainScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    getPlanningsUseCase: GetPlanningsUseCase,
    private val addPlanningUseCase: AddPlanningUseCase,
    private val updatePlanningUseCase: UpdatePlanningUseCase,
    private val getRoutinesUseCase: GetRoutinesUseCase
) : ViewModel() {

    val plannings: StateFlow<List<PlanningModel>> =
        getPlanningsUseCase().catch { Error(it) }.stateIn(
            viewModelScope, SharingStarted.Eagerly, emptyList()
        )

    private val _uiState: MutableLiveData<MainScreenState> =
        MutableLiveData(MainScreenState.Observation)

    val uiState: LiveData<MainScreenState> = _uiState

    fun planningClicked(it: PlanningModel) {
        val destination =
            if (it.statedRoutine != null) FieldBeingEdited.ROUTINE else if (it.statedBodyPart != null) FieldBeingEdited.BODYPART else FieldBeingEdited.NONE
        _uiState.postValue(MainScreenState.PlanningOnMainFocus(it, destination))
    }

    fun backToObservation() {
        _uiState.postValue(MainScreenState.Observation)
    }

    fun selectBodypartClicked() {
        val actualState = _uiState.value as MainScreenState.PlanningOnMainFocus


        _uiState.postValue(actualState.copy(fieldBeingEdited = FieldBeingEdited.BODYPART))

    }

    fun selectRoutineClicked() {
        val actualState = _uiState.value as MainScreenState.PlanningOnMainFocus
        viewModelScope.launch {

            val routines = getRoutinesUseCase().first()

            _uiState.postValue(
                actualState.copy(
                    fieldBeingEdited = FieldBeingEdited.ROUTINE, availableRoutines = routines
                )
            )
        }
    }

    fun saveBodypart(it: String) {
        val actualState = _uiState.value as MainScreenState.PlanningOnMainFocus

        val planning = actualState.planningModel.copy(statedBodyPart = it, statedRoutine = null)

        viewModelScope.launch(Dispatchers.IO) {
            if (planning.id == 0) addPlanningUseCase(planning)
            else updatePlanningUseCase(planning)
            backToObservation()
        }

    }

    fun backToSelection() {

        val actualState = _uiState.value as MainScreenState.PlanningOnMainFocus

        _uiState.postValue(actualState.copy(fieldBeingEdited = FieldBeingEdited.NONE))
    }

    fun saveRoutine(it: RoutineModel) {
        val actualState = _uiState.value as MainScreenState.PlanningOnMainFocus

        val planning = actualState.planningModel.copy(statedRoutine = it, statedBodyPart = null)

        viewModelScope.launch(Dispatchers.IO) {
            if (planning.id == 0) addPlanningUseCase(planning)
            else updatePlanningUseCase(planning)
            backToObservation()
        }
    }


}