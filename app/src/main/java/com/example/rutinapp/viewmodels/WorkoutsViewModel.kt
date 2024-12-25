package com.example.rutinapp.viewmodels

import android.util.Log
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
import kotlinx.coroutines.flow.map
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

    lateinit var exercisesViewModel: ExercisesViewModel

    val workouts: StateFlow<List<WorkoutModel>> = getWorkoutsUseCase().catch { Error(it) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    val routines: StateFlow<List<RoutineModel>> = getRoutinesUseCase().catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val exercises: Flow<List<ExerciseModel>> = getExercisesUseCase().map  {
        try {
            val state = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted

            val availableExercises = it
                .filter { it.id !in state.workout.exercisesAndSets.map { it.first.id } }
                .toMutableList()

            if (state.workout.baseRoutine != null) {
                availableExercises.removeIf { it.id in state.workout.baseRoutine!!.exercises.toSet().map { it.id } }
            }
            _workoutScreenStates.postValue(
                WorkoutsScreenState.WorkoutStarted(
                    workout = state.workout, otherExercises = availableExercises, setBeingCreated = state.setBeingCreated
                )
            )
        } catch (_: Exception) {

        }
        it
    }.catch { Error(it) }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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

            newWorkout.id = addWorkoutUseCase(workout = newWorkout)

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


            newWorkout.id = addWorkoutUseCase(workout = newWorkout)


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

        currentState.setBeingCreated!!.apply {
            this.weight = weight
            this.reps = reps
            this.observations = observations
        }

        currentState.workout.exercisesAndSets.find { it.first == currentState.setBeingCreated.exercise }!!.second += currentState.setBeingCreated

        viewModelScope.launch(Dispatchers.IO) {
            currentState.setBeingCreated.id = addSetUseCase(currentState.setBeingCreated)
            _workoutScreenStates.postValue(
                WorkoutsScreenState.WorkoutStarted(
                    workout = currentState.workout, otherExercises = currentState.otherExercises
                )
            )
        }

    }

    fun addExerciseToWorkout(exercise: ExerciseModel) {

        val currentState = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted

        _workoutScreenStates.postValue(
            WorkoutsScreenState.WorkoutStarted(
                currentState.workout.copy(
                    exercisesAndSets = (currentState.workout.exercisesAndSets + Pair(
                        exercise, mutableListOf()
                    )).toMutableList()
                ), currentState.otherExercises - exercise
            )
        )
    }

    fun cancelSetEditing() {
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

    fun continueWorkout(workout: WorkoutModel) {
        viewModelScope.launch(Dispatchers.IO) {

            val routine = routines.value.find { it.id == workout.baseRoutine?.id }

            workout.baseRoutine = routine

            var availableExercises =
                (exercises.first() - workout.exercisesAndSets.map { it.first }.toSet())

            if (routine?.exercises?.isNotEmpty() == true) {
                availableExercises =
                    availableExercises.filter { it.id !in routine.exercises.map { it.id } }


                workout.exercisesAndSets += routine.exercises.filter { it.id !in workout.exercisesAndSets.map { it.first.id } }
                    .map { it to mutableListOf() }
            }

            Log.d("WOEXES", workout.exercisesAndSets.joinToString { it.first.id + " " })

            _workoutScreenStates.postValue(
                WorkoutsScreenState.WorkoutStarted(
                    workout = workout, otherExercises = availableExercises
                )
            )
        }
    }

    fun backToObserve() {
        try {

            val actualState = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted

            if (actualState.workout.exercisesAndSets.find { it.second.isNotEmpty() } == null) {
                viewModelScope.launch(Dispatchers.IO) {
                    deleteWorkoutUseCase(actualState.workout)
                }
            }
        } catch (e: Exception) {
            //state is not WorkoutStarted
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

    fun editSetClicked(set: SetModel) {

        val actualState = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted

        _workoutScreenStates.postValue(
            WorkoutsScreenState.WorkoutStarted(
                actualState.workout, actualState.otherExercises, set
            )
        )
    }

}
