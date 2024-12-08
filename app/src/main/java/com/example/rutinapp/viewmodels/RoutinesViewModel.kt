package com.example.rutinapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.models.RoutineModel
import com.example.rutinapp.domain.addUseCases.AddRoutineExerciseRelationUseCase
import com.example.rutinapp.domain.addUseCases.AddRoutineUseCase
import com.example.rutinapp.domain.deleteUseCases.DeleteRoutineExerciseRelationUseCase
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
import kotlin.math.min

@HiltViewModel
class RoutinesViewModel @Inject constructor(
    private val getRoutinesUseCase: GetRoutinesUseCase,
    private val createRoutineUseCase: AddRoutineUseCase,
    private val getExercisesUseCase: GetExercisesUseCase,
    private val addRoutineExerciseRelationUseCase: AddRoutineExerciseRelationUseCase,
    private val deleteRoutineExerciseRelationUseCase: DeleteRoutineExerciseRelationUseCase
) : ViewModel() {

    val routinesState: StateFlow<List<RoutineModel>> = getRoutinesUseCase().catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val exercisesState: StateFlow<List<ExerciseModel>> =
        getExercisesUseCase().catch { Error(it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _uiState: MutableLiveData<RoutinesScreenState> =
        MutableLiveData(RoutinesScreenState.Observe)

    val uiState: LiveData<RoutinesScreenState> = _uiState

    fun clickCreateRoutine() {
        _uiState.postValue(RoutinesScreenState.Creating())
        viewModelScope.launch(Dispatchers.IO) {
            exercisesState.collect {
                _uiState.postValue(RoutinesScreenState.Creating(availableExercises = it.map { Pair(it, false) }))
            }
        }
    }

    fun backToObserve() {
        _uiState.postValue(RoutinesScreenState.Observe)
    }

    fun createRoutine(name: String, targetedBodyPart: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val createdRoutine = RoutineModel(id = routinesState.value.size,name = name, targetedBodyPart = targetedBodyPart)
            createRoutineUseCase(createdRoutine)
            Log.i("RoutinesViewModel", exercisesState.value.size.toString())
            _uiState.postValue(
                relatedExercisesByBodyPart(createdRoutine)
            )
        }

    }

    fun toggleExerciseRelation(it: ExerciseModel) {

        try {
            val creatingState = _uiState.value as RoutinesScreenState.Creating

            if (creatingState.routine != null && creatingState.routine.exercises.contains(it)
                    .not()
            ) {
                viewModelScope.launch(Dispatchers.IO) {
                    addRoutineExerciseRelationUseCase(creatingState.routine, it)
                    creatingState.routine.exercises.add(it)
                    _uiState.postValue(relatedExercisesByBodyPart(creatingState.routine))
                }
            } else if (creatingState.routine != null && creatingState.routine.exercises.contains(it)) {
                viewModelScope.launch(Dispatchers.IO) {
                    deleteRoutineExerciseRelationUseCase(creatingState.routine, it)
                    creatingState.routine.exercises.remove(it)
                    _uiState.postValue(relatedExercisesByBodyPart(creatingState.routine))
                }
            }
        } catch (e: Exception) {

        }
    }

    private fun relatedExercisesByBodyPart(createdRoutine: RoutineModel): RoutinesScreenState.Creating {
        return RoutinesScreenState.Creating(
            routine = createdRoutine, availableExercises = exercisesState.value.sortedBy {
                if (createdRoutine.exercises.contains(it)) return@sortedBy 101
                if (createdRoutine.targetedBodyPart == it.targetedBodyPart) return@sortedBy 100
                var count = 0
                for (i in 0..<min(
                    it.targetedBodyPart.length, createdRoutine.targetedBodyPart.length
                )) {
                    if (createdRoutine.targetedBodyPart[i] == it.targetedBodyPart[i]) count++
                }
                count
            }.reversed().map { it to createdRoutine.exercises.contains(it) }
        )
    }

}