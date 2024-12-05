package com.example.rutinapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.models.RoutineModel
import com.example.rutinapp.domain.addUseCases.AddRoutineUseCase
import com.example.rutinapp.domain.getUseCases.GetExercisesUseCase
import com.example.rutinapp.domain.getUseCases.GetRoutinesUseCase
import com.example.rutinapp.ui.screenStates.RoutinesScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutinesViewModel @Inject constructor(
    private val getRoutinesUseCase: GetRoutinesUseCase,
    private val createRoutineUseCase: AddRoutineUseCase,
    private val getExercisesUseCase: GetExercisesUseCase
) : ViewModel() {

    val routinesState: StateFlow<List<RoutineModel>> = getRoutinesUseCase().catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val exercisesState: StateFlow<List<ExerciseModel>> =
        getExercisesUseCase().catch { Error(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState: MutableLiveData<RoutinesScreenState> =
        MutableLiveData(RoutinesScreenState.Observe)

    val uiState: LiveData<RoutinesScreenState> = _uiState

    fun clickCreateRoutine() {
        _uiState.postValue(RoutinesScreenState.Creating())
    }

    fun backToObserve() {
        _uiState.postValue(RoutinesScreenState.Observe)
    }

    fun createRoutine(name: String, targetedBodyPart: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val createdRoutine = RoutineModel(name = name, targetedBodyPart = targetedBodyPart)
            createRoutineUseCase(createdRoutine)
            _uiState.postValue(RoutinesScreenState.Creating(routine = createdRoutine, availableExercises = exercisesState.value))
        }

    }

}