package com.example.rutinapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.models.RoutineModel
import com.example.rutinapp.data.models.WorkoutModel
import com.example.rutinapp.domain.getUseCases.GetExercisesUseCase
import com.example.rutinapp.domain.getUseCases.GetRoutinesUseCase
import com.example.rutinapp.domain.getUseCases.GetWorkoutsUseCase
import com.example.rutinapp.ui.screenStates.WorkoutsScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    getWorkoutsUseCase: GetWorkoutsUseCase,
    getRoutinesUseCase: GetRoutinesUseCase,
    getExercisesUseCase: GetExercisesUseCase
) : ViewModel() {

    val workouts: StateFlow<List<WorkoutModel>> = getWorkoutsUseCase().catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    val routines: StateFlow<List<RoutineModel>> = getRoutinesUseCase().catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val exercises: Flow<List<ExerciseModel>> = getExercisesUseCase().catch { Error(it) }

    private val _workoutScreenStates: MutableLiveData<WorkoutsScreenState> =
        MutableLiveData(WorkoutsScreenState.Observe)

    val workoutScreenStates: LiveData<WorkoutsScreenState> = _workoutScreenStates

    fun startFromRoutine(routine: RoutineModel) {

        Log.d("WorkoutsViewModel", "startFromRoutine: $routine")
        viewModelScope.launch(Dispatchers.IO) {

            val momentOfCreation = Date.from(Instant.now())

            val newWorkout = WorkoutModel(
                date = momentOfCreation,
                title = routine.name + " " + momentOfCreation.toGMTString(),
                baseRoutine = routine,
                exercisesAndSets = routine.exercises.map { Pair(it, emptyList()) }
            )

            val availableExercises = exercises.first().filter { it !in routine.exercises }

            _workoutScreenStates.postValue(
                WorkoutsScreenState.WorkoutStarted(
                    workout = newWorkout, otherExercises = availableExercises
                )
            )
        }
    }

    fun addSet(exercise: ExerciseModel) {

    }

}
