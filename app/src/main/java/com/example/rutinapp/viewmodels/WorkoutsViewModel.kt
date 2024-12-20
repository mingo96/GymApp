package com.example.rutinapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.models.RoutineModel
import com.example.rutinapp.data.models.SetModel
import com.example.rutinapp.data.models.WorkoutModel
import com.example.rutinapp.domain.addUseCases.AddSetUseCase
import com.example.rutinapp.domain.addUseCases.AddWorkoutUseCase
import com.example.rutinapp.domain.deleteUseCases.DeleteSetUseCase
import com.example.rutinapp.domain.deleteUseCases.DeleteWorkoutUseCase
import com.example.rutinapp.domain.getUseCases.GetExercisesUseCase
import com.example.rutinapp.domain.getUseCases.GetRoutinesUseCase
import com.example.rutinapp.domain.getUseCases.GetWorkoutIdByDateUseCase
import com.example.rutinapp.domain.getUseCases.GetWorkoutsUseCase
import com.example.rutinapp.domain.updateUseCases.UpdateSetUseCase
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
    getExercisesUseCase: GetExercisesUseCase,
    private val addWorkoutUseCase: AddWorkoutUseCase,
    private val addSetUseCase: AddSetUseCase,
    private val updateSetUseCase: UpdateSetUseCase,
    private val deleteSetUseCase: DeleteSetUseCase,
    private val getWorkoutIdByDateUseCase: GetWorkoutIdByDateUseCase,
    private val deleteWorkoutUseCase: DeleteWorkoutUseCase
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

        viewModelScope.launch(Dispatchers.IO) {

            val momentOfCreation = Date.from(Instant.now())

            val newWorkout = WorkoutModel(
                date = momentOfCreation,
                title = routine.name + " " + momentOfCreation.toInstant().toString()
                    .substring(0, 10),
                baseRoutine = routine,
                exercisesAndSets = routine.exercises.map {
                    Pair<ExerciseModel, MutableList<SetModel>>(
                        it, mutableListOf()
                    )
                }.toMutableList()
            )

            val availableExercises =
                exercises.first().filter { it.id !in routine.exercises.map { it.id } }

            addWorkoutUseCase(workout = newWorkout)

            newWorkout.id =
                getWorkoutIdByDateUseCase(newWorkout.date.toString())
            _workoutScreenStates.postValue(
                WorkoutsScreenState.WorkoutStarted(
                    workout = newWorkout, otherExercises = availableExercises
                )
            )
        }
    }

    fun startFromEmpty() {
        viewModelScope.launch(Dispatchers.IO) {
            val momentOfCreation = Date.from(Instant.now())
            val newWorkout = WorkoutModel(
                date = momentOfCreation,
                title = "Entreno del " + momentOfCreation.toInstant().toString().substring(0, 10),
                baseRoutine = null
            )

            val availableExercises = exercises.first()

            addWorkoutUseCase(workout = newWorkout)

            newWorkout.id =
                getWorkoutIdByDateUseCase(newWorkout.date.toString())

            _workoutScreenStates.postValue(
                WorkoutsScreenState.WorkoutStarted(
                    workout = newWorkout, otherExercises = availableExercises
                )
            )
        }
    }

    fun addSetClicked(exercise: ExerciseModel) {

        val currentState = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted

        val momentOfCreation = Date.from(Instant.now())

        val newSet = SetModel(
            weight = 0.0,
            reps = 0,
            date = momentOfCreation,
            observations = "",
            exercise = exercise,
            workoutDone = currentState.workout
        )

        _workoutScreenStates.postValue(
            WorkoutsScreenState.WorkoutStarted(
                currentState.workout, currentState.otherExercises, newSet
            )

        )


    }

    fun createSet(weight: Double, reps: Int, observations: String) {

        val currentState = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted

        val momentOfCreation = Date.from(Instant.now())

        val newSet = SetModel(
            weight = weight,
            reps = reps,
            date = momentOfCreation,
            observations = observations,
            exercise = currentState.setBeingCreated!!.exercise,
            workoutDone = currentState.workout
        )
        currentState.workout.exercisesAndSets.find { it.first == newSet.exercise }!!.second += newSet

        viewModelScope.launch(Dispatchers.IO) {
            addSetUseCase(newSet)
            _workoutScreenStates.postValue(
                WorkoutsScreenState.WorkoutStarted(
                    workout = currentState.workout, otherExercises = currentState.otherExercises
                )
            )
        }

    }

    fun addExerciseToWorkout(exercise: ExerciseModel) {

        val currentState = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted

        currentState.workout.exercisesAndSets += Pair(exercise, mutableListOf())

        _workoutScreenStates.postValue(
            WorkoutsScreenState.WorkoutStarted(
                currentState.workout, currentState.otherExercises - exercise
            )
        )
    }

    fun cancelSetCreation() {
        val currentState = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted

        _workoutScreenStates.postValue(
            WorkoutsScreenState.WorkoutStarted(
                currentState.workout, currentState.otherExercises
            )
        )
    }

    fun removeExerciseFromRoutine(exercise: ExerciseModel) {

        val currentState = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted

        val foundData = currentState.workout.exercisesAndSets.find { it.first.id == exercise.id }

        if (foundData != null) {

            if (foundData.second.isNotEmpty()) {
                viewModelScope.launch(Dispatchers.IO) {
                    foundData.second.forEach {
                        deleteSetUseCase(it)
                    }
                }
            }

            currentState.workout.exercisesAndSets -= foundData
        }

        _workoutScreenStates.postValue(
            WorkoutsScreenState.WorkoutStarted(
                currentState.workout,
                currentState.otherExercises + exercise,
                currentState.setBeingCreated
            )
        )
    }

    fun backToObserve() {
        val actualState = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted

        if(actualState.workout.exercisesAndSets.isEmpty()){
            viewModelScope.launch(Dispatchers.IO) {
                deleteWorkoutUseCase(actualState.workout)
            }
        }

        _workoutScreenStates.postValue(WorkoutsScreenState.Observe)
    }

    fun moveExercise(first: ExerciseModel, upOrDown: Boolean) {

        val actualState = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted

        val index = actualState.workout.exercisesAndSets.indexOfFirst { it.first.id == first.id }
        val indexToItem = index to actualState.workout.exercisesAndSets[index]

        val desiredIndex = if (upOrDown) index - 1 else index + 1
        val desiredIndexToOriginalItem =
            desiredIndex to actualState.workout.exercisesAndSets[desiredIndex]

        val newList = actualState.workout.exercisesAndSets.withIndex().map {
            if (it.index == index) desiredIndexToOriginalItem.second else if (it.index == desiredIndex) indexToItem.second else it.value
        }.toMutableList()

        _workoutScreenStates.postValue(
            WorkoutsScreenState.WorkoutStarted(
                actualState.workout.copy(exercisesAndSets = newList),
                actualState.otherExercises,
                actualState.setBeingCreated
            )
        )
    }

}
