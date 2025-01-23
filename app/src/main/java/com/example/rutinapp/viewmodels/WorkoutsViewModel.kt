package com.example.rutinapp.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.models.PlanningModel
import com.example.rutinapp.data.models.RoutineModel
import com.example.rutinapp.data.models.SetModel
import com.example.rutinapp.data.models.WorkoutModel
import com.example.rutinapp.domain.addUseCases.AddSetUseCase
import com.example.rutinapp.domain.addUseCases.AddWorkoutUseCase
import com.example.rutinapp.domain.deleteUseCases.DeleteSetUseCase
import com.example.rutinapp.domain.deleteUseCases.DeleteWorkoutUseCase
import com.example.rutinapp.domain.getUseCases.GetExercisesUseCase
import com.example.rutinapp.domain.getUseCases.GetRelatedExercisesByBodyPartUseCase
import com.example.rutinapp.domain.getUseCases.GetRoutinesUseCase
import com.example.rutinapp.domain.getUseCases.GetTodaysPlanningUseCase
import com.example.rutinapp.domain.getUseCases.GetWorkoutsUseCase
import com.example.rutinapp.domain.updateUseCases.UpdateSetUseCase
import com.example.rutinapp.domain.updateUseCases.UpdateWorkoutUseCase
import com.example.rutinapp.ui.screenStates.SetState
import com.example.rutinapp.ui.screenStates.WorkoutsScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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
    getTodaysPlanning: GetTodaysPlanningUseCase,
    private val addWorkoutUseCase: AddWorkoutUseCase,
    private val addSetUseCase: AddSetUseCase,
    private val updateSetUseCase: UpdateSetUseCase,
    private val deleteSetUseCase: DeleteSetUseCase,
    private val deleteWorkoutUseCase: DeleteWorkoutUseCase,
    private val updateWorkoutUseCase: UpdateWorkoutUseCase,
    private val getRelatedExercisesByBodyPartUseCase : GetRelatedExercisesByBodyPartUseCase,
) : ViewModel() {

    lateinit var exercisesViewModel: ExercisesViewModel

    private lateinit var adsViewModel: AdViewModel

    private val todaysPlanning: StateFlow<PlanningModel?> = getTodaysPlanning().map {
        refreshPlanning(it)
        it
    }.catch { Error(it) }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val workouts: StateFlow<List<WorkoutModel>> = getWorkoutsUseCase().catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val routines: StateFlow<List<RoutineModel>> = getRoutinesUseCase().catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val exercises: Flow<List<ExerciseModel>> = getExercisesUseCase().map {
        try {
            val state = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted

            val availableExercises =
                it.filter { it.id !in state.workout.exercisesAndSets.map { it.first.id } }
                    .toMutableList()

            if (state.workout.baseRoutine != null) {
                availableExercises.removeIf {
                    it.id in state.workout.baseRoutine!!.exercises.toSet().map { it.id }
                }
            }
            _workoutScreenStates.postValue(
                WorkoutsScreenState.WorkoutStarted(
                    workout = state.workout,
                    otherExercises = availableExercises,
                    setBeingCreated = state.setBeingCreated
                )
            )
        } catch (_: Exception) {

        }
        it
    }.catch { Error(it) }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _workoutScreenStates: MutableLiveData<WorkoutsScreenState> =
        MutableLiveData(WorkoutsScreenState.Observe(todaysPlanning.value))

    val workoutScreenStates: LiveData<WorkoutsScreenState> = _workoutScreenStates

    val currentDate: StateFlow<Long> = flow {
        while (true) {
            delay(1000)
            emit(Date().time)
        }

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Date().time)

    fun provideAdsViewModel(adViewModel: AdViewModel) {
        adsViewModel = adViewModel
    }

    fun startFromRoutine(routine: RoutineModel) {

        viewModelScope.launch(Dispatchers.IO) {

            adsViewModel.callRandomAd()

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

            adsViewModel.callRandomAd()

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
                currentState.workout, currentState.otherExercises, SetState.CreatingSet(newSet)
            )

        )

    }

    fun saveSet(weight: Double, reps: Int, observations: String) {

        val currentState = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted

        if (currentState.setBeingCreated!! is SetState.CreatingSet) {
            createSet(currentState, weight, reps, observations)
        } else {
            updateSet(currentState, weight, reps, observations)
        }
    }

    private fun createSet(
        currentState: WorkoutsScreenState.WorkoutStarted,
        weight: Double,
        reps: Int,
        observations: String
    ) {

        val setState = currentState.setBeingCreated as SetState.CreatingSet
        setState.set.apply {
            this.weight = weight
            this.reps = reps
            this.observations = observations
        }

        currentState.workout.exercisesAndSets.find { it.first == setState.set.exercise }!!.second += setState.set

        viewModelScope.launch(Dispatchers.IO) {
            setState.set.id = addSetUseCase(setState.set)
            _workoutScreenStates.postValue(
                WorkoutsScreenState.WorkoutStarted(
                    workout = currentState.workout, otherExercises = currentState.otherExercises
                )
            )
        }
    }

    private fun updateSet(
        currentState: WorkoutsScreenState.WorkoutStarted,
        weight: Double,
        reps: Int,
        observations: String
    ) {

        val setState = currentState.setBeingCreated as SetState.OptionsOfSet
        setState.set.apply {
            this.weight = weight
            this.reps = reps
            this.observations = observations
        }
        viewModelScope.launch(Dispatchers.IO) {
            updateSetUseCase(setState.set)
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

            adsViewModel.callRandomAd()

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

            _workoutScreenStates.postValue(
                WorkoutsScreenState.WorkoutStarted(
                    workout = workout, otherExercises = availableExercises
                )
            )
        }
    }

    fun backToObserve() {
        try {

            adsViewModel

            val actualState = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted

            if (actualState.workout.exercisesAndSets.find { it.second.isNotEmpty() } == null) {
                viewModelScope.launch(Dispatchers.IO) {
                    deleteWorkoutUseCase(actualState.workout)
                }
            }
        } catch (e: Exception) {
            //state is not WorkoutStarted
        }

        _workoutScreenStates.postValue(WorkoutsScreenState.Observe())
        refreshPlanning()
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

    fun setOptionsClicked(set: SetModel) {

        val actualState = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted

        val setState = SetState.OptionsOfSet(set)

        _workoutScreenStates.postValue(
            WorkoutsScreenState.WorkoutStarted(
                actualState.workout, actualState.otherExercises, setState
            )
        )
    }

    fun clickEditSet(set: SetModel) {

    }

    fun deleteSet(set: SetModel) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteSetUseCase(set)
            val actualState = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted
            actualState.workout.exercisesAndSets.find { it.first.id == set.exercise!!.id }!!.second -= set
            _workoutScreenStates.postValue(
                WorkoutsScreenState.WorkoutStarted(
                    actualState.workout, actualState.otherExercises, null
                )
            )
        }
    }

    fun cancelExerciseSwap() {
        val actualState = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted
        _workoutScreenStates.postValue(
            actualState.copy(exerciseBeingSwapped = null)
        )
    }

    fun startSwappingExercise(exercise: ExerciseModel) {
        val actualState = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted

        _workoutScreenStates.postValue(
            actualState.copy(exerciseBeingSwapped = exercise)
        )
    }

    fun swapExerciseBeingSwapped(newExercise: ExerciseModel, context: Context) {
        val actualState = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted

        val newListOfExercises = actualState.workout.exercisesAndSets.toMutableList()

        if (newExercise.id !in actualState.workout.exercisesAndSets.map { it.first.id }) {

            newListOfExercises[newListOfExercises.indexOfFirst { it.first.id == actualState.exerciseBeingSwapped!!.id }] =
                Pair(newExercise, mutableListOf())
            _workoutScreenStates.postValue(
                actualState.copy(
                    workout = actualState.workout.copy(exercisesAndSets = newListOfExercises),
                    otherExercises = actualState.otherExercises + actualState.exerciseBeingSwapped!! - newExercise,
                    exerciseBeingSwapped = null,
                )
            )
        } else {
            Toast.makeText(context, "Ese ejercicio ya esta en el entrenamiento", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun searchExercise(name: String) {
        val actualState = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted

        viewModelScope.launch {

            var newListOfExercises = exercises.first()
                .filter { it.name.contains(name, true) || it.targetedBodyPart.contains(name, true) }
                .toMutableList()

            newListOfExercises =
                newListOfExercises.filter { it.id !in actualState.workout.exercisesAndSets.map { it.first.id } }
                    .toMutableList()

            if (actualState.workout.baseRoutine != null) {
                newListOfExercises =
                    newListOfExercises.filter { it.id !in actualState.workout.baseRoutine!!.exercises.map { it.id } }
                        .toMutableList()
            }

            _workoutScreenStates.postValue(
                actualState.copy(otherExercises = newListOfExercises)
            )
        }

    }

    fun finishTraining() {

        adsViewModel.callRandomAd()

        val actualState = _workoutScreenStates.value as WorkoutsScreenState.WorkoutStarted

        viewModelScope.launch(Dispatchers.IO) {
            updateWorkoutUseCase(actualState.workout.copy(isFinished = true))
            backToObserve()
            refreshPlanning()
        }

    }

    fun refreshPlanning(planning: PlanningModel? = null) {

        try {

            if (planning != null) {
                _workoutScreenStates.value as WorkoutsScreenState.Observe
                _workoutScreenStates.value = WorkoutsScreenState.Observe(planning)
            } else {
                viewModelScope.launch {
                    val newPlanning = todaysPlanning.first()
                    println(newPlanning)

                    try {
                        _workoutScreenStates.value as WorkoutsScreenState.Observe
                        _workoutScreenStates.postValue( WorkoutsScreenState.Observe(newPlanning))
                    }catch (_:Exception){

                    }
                }
            }
        } catch (_: ClassCastException) {

        }
    }

    fun startFromStatedBodyPart() {
        try {

            adsViewModel.callRandomAd()

            val momentOfCreation = Date.from(Instant.now())

            val actualState = _workoutScreenStates.value as WorkoutsScreenState.Observe

            val statedBodyPart = actualState.planning!!.statedBodyPart!!

            viewModelScope.launch {

                val selectedExercises = getRelatedExercisesByBodyPartUseCase(statedBodyPart)

                println(selectedExercises.size)

                val newWorkout = WorkoutModel(
                    date = momentOfCreation,
                    title = "Entreno del " + momentOfCreation.toInstant().toString().substring(0, 10),
                    baseRoutine = null,
                    exercisesAndSets = selectedExercises.map {
                        Pair<ExerciseModel, MutableList<SetModel>>(
                            it, mutableListOf()
                        )
                    }.toMutableList()
                )

                val availableExercises = exercises.first().filter { it.id !in selectedExercises.map { it.id } }

                newWorkout.id = addWorkoutUseCase(workout = newWorkout)

                _workoutScreenStates.postValue(
                    WorkoutsScreenState.WorkoutStarted(
                        workout = newWorkout, otherExercises = availableExercises
                    )
                )

            }

        } catch (e: Exception) {

        }
    }

}
