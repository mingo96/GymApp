package com.example.rutinapp.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.models.RoutineModel
import com.example.rutinapp.domain.getUseCases.GetExercisesUseCase
import com.example.rutinapp.domain.getUseCases.GetRoutinesUseCase
import com.example.rutinapp.domain.getUseCases.GetSetsOfExerciseUseCase
import com.example.rutinapp.ui.screenStates.StatsScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    getRoutinesUseCase: GetRoutinesUseCase,
    getExercisesUseCase: GetExercisesUseCase,
    private val getSetsUseCase: GetSetsOfExerciseUseCase
) : ViewModel() {

    val exercisesState: StateFlow<List<ExerciseModel>> = getExercisesUseCase().map {
        if (_uiState.value is StatsScreenState.Observation) {
            _uiState.postValue(StatsScreenState.Observation(it))
        }
        it
    }.catch { Error(it) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val routinesState: StateFlow<List<RoutineModel>> = getRoutinesUseCase().catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState: MutableLiveData<StatsScreenState> =
        MutableLiveData(StatsScreenState.Observation(exercisesState.value))

    val uiState: LiveData<StatsScreenState> = _uiState

    fun backToObservation() {
        _uiState.postValue(StatsScreenState.Observation(exercisesState.value))
    }

    @SuppressLint("SimpleDateFormat")
    fun selectExerciseForStats(exerciseModel: ExerciseModel) {

        viewModelScope.launch(Dispatchers.IO) {

            val setsDone = getSetsUseCase(exerciseModel)

            if (setsDone.isNotEmpty()) {

                val maxWeight =
                    setsDone.map { Triple(it.weight, it.date, it.observations) }.maxBy { it.first }

                val timesDone = setsDone.size

                val avgWeight = setsDone.sumOf { it.weight } / timesDone

                val mostWeightOnASet =
                    setsDone.map { Triple(it.weight * it.reps, it.date, it.observations) }
                        .maxBy { it.first }

                val lastTimeDone = setsDone.maxOf { it.date }

                val newState = StatsScreenState.StatsOfExercise(
                    true,
                    exerciseModel,
                    maxWeight,
                    timesDone,
                    avgWeight,
                    mostWeightOnASet,
                    SimpleDateFormat("dd MMMM yyyy : HH:mm:ss").format(lastTimeDone)
                )

                _uiState.postValue(newState)
            } else {
                _uiState.postValue(StatsScreenState.StatsOfExercise(exercise = exerciseModel))
            }
        }
    }

    fun searchExercise(name: String) {
        if (_uiState.value is StatsScreenState.Observation) {
            val newList = exercisesState.value.filter { it.name.contains(name) }
            _uiState.postValue(StatsScreenState.Observation(newList))
        }
    }

}