package com.mintocode.rutinapp.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mintocode.rutinapp.data.models.PlanningModel
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.domain.addUseCases.AddPlanningUseCase
import com.mintocode.rutinapp.domain.getUseCases.DAY_IN_MILLIS
import com.mintocode.rutinapp.domain.getUseCases.GetPlanningsUseCase
import com.mintocode.rutinapp.domain.getUseCases.GetRoutinesUseCase
import com.mintocode.rutinapp.domain.updateUseCases.UpdatePlanningUseCase
import com.mintocode.rutinapp.ui.screenStates.FieldBeingEdited
import com.mintocode.rutinapp.ui.screenStates.MainScreenState
import com.mintocode.rutinapp.utils.toSimpleDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getPlanningsUseCase: GetPlanningsUseCase,
    private val addPlanningUseCase: AddPlanningUseCase,
    private val updatePlanningUseCase: UpdatePlanningUseCase,
    private val getRoutinesUseCase: GetRoutinesUseCase
) : ViewModel() {

    private var _planningsFlow: StateFlow<List<PlanningModel>> =
        getPlanningsUseCase(
            Pair(
                Date().toSimpleDate(),
                Date(Date().time + DAY_IN_MILLIS * 7)
            )
        ).catch { Error(it) }
            .map {
                delay(1000)
                _todaysPlanning.postValue(it.find { it.date.toSimpleDate().time == Date().toSimpleDate().time }
                    ?: PlanningModel(id = 0, date = Date().toSimpleDate()))
                _plannings.postValue(it)
                it
            }.stateIn(
                viewModelScope, SharingStarted.Eagerly, emptyList()
            )

    private val _plannings: MutableLiveData<List<PlanningModel>> = MutableLiveData(emptyList())

    var plannings: LiveData<List<PlanningModel>> = _plannings

    private val _todaysPlanning: MutableLiveData<PlanningModel> = MutableLiveData()

    val todaysPlanning: LiveData<PlanningModel> = _todaysPlanning

    private val _uiState: MutableLiveData<MainScreenState> =
        MutableLiveData(MainScreenState.Observation)

    val uiState: LiveData<MainScreenState> = _uiState

    fun changeDates(selectedStartMillis: Long?, selectedEndDateMillis: Long?) {
        if (selectedStartMillis == null || selectedEndDateMillis == null) return

        if (selectedEndDateMillis > selectedStartMillis) {
            _planningsFlow =
                getPlanningsUseCase(Date(selectedStartMillis) to Date(selectedEndDateMillis)).catch {
                    Error(it)
                }.map {
                    _plannings.postValue(it)
                    it
                }.stateIn(
                    viewModelScope, SharingStarted.Eagerly, emptyList()
                )
        }
    }

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

    fun saveBodypart(it: String, context: Context) {
        if (it.isEmpty()) {
            Toast.makeText(context, "Debes escribir una parte del cuerpo", Toast.LENGTH_SHORT)
                .show()
            return
        }
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