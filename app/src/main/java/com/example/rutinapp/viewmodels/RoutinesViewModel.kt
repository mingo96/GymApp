package com.example.rutinapp.viewmodels

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
import com.example.rutinapp.domain.updateUseCases.UpdateRoutineExerciseRelationUseCase
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
    getRoutinesUseCase: GetRoutinesUseCase,
    private val createRoutineUseCase: AddRoutineUseCase,
    getExercisesUseCase: GetExercisesUseCase,
    private val addRoutineExerciseRelationUseCase: AddRoutineExerciseRelationUseCase,
    private val deleteRoutineExerciseRelationUseCase: DeleteRoutineExerciseRelationUseCase,
    private val updateRoutineExerciseRelationUseCase: UpdateRoutineExerciseRelationUseCase
) : ViewModel() {

    val routines: StateFlow<List<RoutineModel>> = getRoutinesUseCase().catch {Error(it)}
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val exercisesState: StateFlow<List<ExerciseModel>> =
        getExercisesUseCase().catch { Error(it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _uiState: MutableLiveData<RoutinesScreenState> =
        MutableLiveData(RoutinesScreenState.Overview)

    val uiState: LiveData<RoutinesScreenState> = _uiState

    fun clickObserveRoutine(routine: RoutineModel) {
        _uiState.postValue(RoutinesScreenState.Observe(routine))
    }

    fun clickCreateRoutine() {
        _uiState.postValue(RoutinesScreenState.Creating())
        viewModelScope.launch(Dispatchers.IO) {
            exercisesState.collect {
                _uiState.postValue(RoutinesScreenState.Creating(availableExercises = it.map {
                    Pair(
                        it, false
                    )
                }))
            }
        }
    }

    fun backToObserve() {
        _uiState.postValue(RoutinesScreenState.Overview)
    }

    fun createRoutine(name: String, targetedBodyPart: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val createdRoutine = RoutineModel(
                id = routines.value.size, name = name, targetedBodyPart = targetedBodyPart
            )
            createRoutineUseCase(createdRoutine)

            _uiState.postValue(
                RoutinesScreenState.Creating(createdRoutine,
                    relatedExercisesByBodyPart(createdRoutine).reversed().map { it to false })
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
                    _uiState.postValue(
                        RoutinesScreenState.Creating(creatingState.routine,
                            availableExercises = relatedExercisesByBodyPart(creatingState.routine).reversed()
                                .map { it to creatingState.routine.exercises.contains(it) })
                    )
                }
            } else if (creatingState.routine != null && creatingState.routine.exercises.contains(it)) {
                viewModelScope.launch(Dispatchers.IO) {
                    deleteRoutineExerciseRelationUseCase(creatingState.routine, it)
                    creatingState.routine.exercises.remove(it)
                    _uiState.postValue(
                        RoutinesScreenState.Creating(creatingState.routine,
                            availableExercises = relatedExercisesByBodyPart(creatingState.routine).reversed()
                                .map { it to creatingState.routine.exercises.contains(it) })
                    )
                }
            }
        } catch (_: Exception) {

        }
    }

    private fun relatedExercisesByBodyPart(createdRoutine: RoutineModel): List<ExerciseModel> {

        return exercisesState.value.sortedBy {
            if (createdRoutine.exercises.contains(it)) return@sortedBy 101 + createdRoutine.exercises.indexOf(
                it
            )
            if (createdRoutine.targetedBodyPart == it.targetedBodyPart) return@sortedBy 100
            return@sortedBy nameSimilarity(createdRoutine.targetedBodyPart, it.targetedBodyPart)
        }.reversed()
    }

    private fun nameSimilarity(word1: String, word2: String): Int {

        var count = 0
        for (i in 0..<min(
            word2.length, word1.length
        )) {
            if (word1[i] == word2[i]) count++
        }
        return count
    }

    fun clickEditRoutine(routine: RoutineModel) {

        _uiState.postValue(
            RoutinesScreenState.Editing(routine = routine,
                positionOfScreen = true,
                availableExercises = relatedExercisesByBodyPart(routine).filter { it.id !in routine.exercises.map { it.id } })
        )

    }

    fun toggleEditingState(comesFromExercises : Boolean = false) {
        val actualState = _uiState.value as RoutinesScreenState.Editing
        _uiState.postValue(
            RoutinesScreenState.Editing(
                actualState.routine,
                positionOfScreen = !actualState.positionOfScreen,
                availableExercises = relatedExercisesByBodyPart(actualState.routine).filter { it.id !in actualState.routine.exercises.map { it.id } },
                selectedExercise = if(comesFromExercises) actualState.selectedExercise else null
            )
        )
    }

    fun changeExercisePresenceOnRoutine() {
        val actualState = _uiState.value as RoutinesScreenState.Editing

        if (actualState.selectedExercise != null) {
            viewModelScope.launch(Dispatchers.IO) {
                if (actualState.routine.exercises.contains(actualState.selectedExercise)) {
                    deleteRoutineExerciseRelationUseCase(
                        actualState.routine, actualState.selectedExercise
                    )
                    actualState.routine.exercises -= actualState.selectedExercise
                } else {
                    addRoutineExerciseRelationUseCase(
                        actualState.routine, actualState.selectedExercise
                    )
                    actualState.routine.exercises += actualState.selectedExercise
                }
                _uiState.postValue(
                    RoutinesScreenState.Editing(routine = actualState.routine,
                        positionOfScreen = false,
                        availableExercises = relatedExercisesByBodyPart(actualState.routine).filter { it.id !in actualState.routine.exercises.map { it.id } })
                )

            }
        }

    }

    fun selectExercise(exercise: ExerciseModel) {
        val actualState = _uiState.value as RoutinesScreenState.Editing
        _uiState.postValue(
            RoutinesScreenState.Editing(
                routine = actualState.routine,
                availableExercises = actualState.availableExercises,
                selectedExercise = if (actualState.selectedExercise == exercise) null else exercise,
                positionOfScreen = actualState.positionOfScreen
            )
        )
    }

    fun updateRoutineExerciseRelation(setsAndReps: String, observations: String) {
        val actualState = _uiState.value as RoutinesScreenState.Editing

        if (actualState.selectedExercise != null && (actualState.selectedExercise.setsAndReps != setsAndReps || actualState.selectedExercise.observations != observations)) {
            viewModelScope.launch(
                Dispatchers.IO
            ) {
                updateRoutineExerciseRelationUseCase(
                    actualState.routine,
                    actualState.selectedExercise.apply {
                        this.setsAndReps = setsAndReps
                        this.observations = observations
                    })

            }
        }
        _uiState.postValue(
            RoutinesScreenState.Editing(
                actualState.routine,
                actualState.availableExercises,
                actualState.positionOfScreen,
                null
            )
        )
        toggleEditingState(true)
    }

}