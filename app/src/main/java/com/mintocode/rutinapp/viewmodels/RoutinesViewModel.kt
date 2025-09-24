package com.mintocode.rutinapp.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mintocode.rutinapp.data.UserDetails
import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.domain.addUseCases.AddRoutineExerciseRelationUseCase
import com.mintocode.rutinapp.domain.addUseCases.AddRoutineUseCase
import com.mintocode.rutinapp.domain.deleteUseCases.DeleteRoutineExerciseRelationUseCase
import com.mintocode.rutinapp.domain.getUseCases.GetExercisesUseCase
import com.mintocode.rutinapp.domain.getUseCases.GetRoutinesUseCase
import com.mintocode.rutinapp.domain.updateUseCases.UpdateRoutineExerciseRelationUseCase
import com.mintocode.rutinapp.domain.updateUseCases.UpdateRoutineUseCase
import com.mintocode.rutinapp.ui.screenStates.RoutinesScreenState
import com.mintocode.rutinapp.sync.SyncManager
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
    private val updateRoutineExerciseRelationUseCase: UpdateRoutineExerciseRelationUseCase,
    private val updateRoutineUseCase: UpdateRoutineUseCase,
    private val syncManager: SyncManager
) : ViewModel() {

    val routines: StateFlow<List<RoutineModel>> = getRoutinesUseCase().catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val exercisesState: StateFlow<List<ExerciseModel>> =
        getExercisesUseCase().catch { Error(it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _uiState: MutableLiveData<RoutinesScreenState> =
        MutableLiveData(RoutinesScreenState.Overview)

    val uiState: LiveData<RoutinesScreenState> = _uiState

    private val _showOthers = MutableLiveData(false)
    val showOthers: LiveData<Boolean> = _showOthers

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    @Volatile private var lastActionAt: Long = 0
    private fun debounceWindow(): Long = 600
    private fun canAct(): Boolean {
        val now = System.currentTimeMillis()
        return if (now - lastActionAt > debounceWindow()) { lastActionAt = now; true } else false
    }

    // Insert routines from remote if new (by realId)
    private fun insertDownloaded(routinesRemote: List<RoutineModel>) {
        if (routinesRemote.isEmpty()) return
        val existing = routines.value.associateBy { it.realId }
        routinesRemote.forEach { r ->
            if (r.realId != 0 && existing[r.realId] == null) {
                // mark not dirty (already server authoritative)
                viewModelScope.launch(Dispatchers.IO) { createRoutineUseCase(r) }
            } else if (r.realId != 0) {
                val current = existing[r.realId]
                if (current != null && (current.name != r.name || current.targetedBodyPart != r.targetedBodyPart)) {
                    current.name = r.name
                    current.targetedBodyPart = r.targetedBodyPart
                    viewModelScope.launch(Dispatchers.IO) { updateRoutineUseCase(current) }
                }
            }
        }
    }

    fun downloadMyRoutines() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = UserDetails.actualValue?.authToken ?: return@launch
            if (token.isBlank()) return@launch
            _isLoading.postValue(true)
            try {
                val remote = syncManager.downloadMyRoutines(token)
                insertDownloaded(remote)
            } catch (e: Exception) { }
            finally { _isLoading.postValue(false) }
        }
    }

    fun downloadOtherRoutines() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = UserDetails.actualValue?.authToken ?: return@launch
            if (token.isBlank()) return@launch
            _isLoading.postValue(true)
            try {
                val remote = syncManager.downloadOtherRoutines(token)
                insertDownloaded(remote)
            } catch (e: Exception) { }
            finally { _isLoading.postValue(false) }
        }
    }

    fun toggleShowOthers() {
        val newValue = !(_showOthers.value ?: false)
        _showOthers.postValue(newValue)
        if (newValue) downloadOtherRoutines() else downloadMyRoutines()
    }

    fun showOthers() {
    if (_showOthers.value == true || !canAct()) return
        _showOthers.postValue(true)
        downloadOtherRoutines()
    }

    fun showMine() {
    if (_showOthers.value == false || !canAct()) return
        _showOthers.postValue(false)
        downloadMyRoutines()
    }

    fun clickObserveRoutine(routine: RoutineModel) {
        _uiState.postValue(RoutinesScreenState.Observe(routine))
    }

    fun clickCreateRoutine() {
        _uiState.postValue(RoutinesScreenState.Creating)
        viewModelScope.launch(Dispatchers.IO) {
            exercisesState.collect {
                _uiState.postValue(RoutinesScreenState.Creating)
            }
        }
    }

    fun backToObserve() {
        _uiState.postValue(RoutinesScreenState.Overview)
    }

    fun createRoutine(name: String, targetedBodyPart: String, context: Context) {

        if (name.isEmpty() || targetedBodyPart.isEmpty()) {
            Toast.makeText(
                context, "Rellene todos los campos", Toast.LENGTH_SHORT
            ).show()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val createdRoutine = RoutineModel(
                name = name, targetedBodyPart = targetedBodyPart
            ).apply { this.isDirty = true }
            createdRoutine.id = createRoutineUseCase(createdRoutine)

            _uiState.postValue(
                RoutinesScreenState.Editing(
                    createdRoutine,
                    availableExercises = relatedExercisesByBodyPart(createdRoutine),
                    positionOfScreen = false
                )
            )
            persistRoutine(createdRoutine)

        }

    }

    private fun relatedExercisesByBodyPart(createdRoutine: RoutineModel): List<ExerciseModel> {

        return exercisesState.value.sortedBy {
            if (createdRoutine.exercises.contains(it)) return@sortedBy 101 + createdRoutine.exercises.indexOf(
                it
            )
            if (createdRoutine.targetedBodyPart == it.targetedBodyPart) return@sortedBy 100
            return@sortedBy createdRoutine.targetedBodyPart.nameSimilarity(it.targetedBodyPart)
        }.reversed()
    }

    fun clickEditRoutine(routine: RoutineModel) {

        _uiState.postValue(
            RoutinesScreenState.Editing(routine = routine,
                positionOfScreen = true,
                availableExercises = relatedExercisesByBodyPart(routine).filter { it.id !in routine.exercises.map { it.id } })
        )

    }

    fun toggleEditingState(comesFromExercises: Boolean = false) {
        val actualState = _uiState.value as RoutinesScreenState.Editing
        _uiState.postValue(
            RoutinesScreenState.Editing(
                actualState.routine,
                positionOfScreen = !actualState.positionOfScreen,
                availableExercises = relatedExercisesByBodyPart(actualState.routine).filter { it.id !in actualState.routine.exercises.map { it.id } },
                selectedExercise = if (comesFromExercises) actualState.selectedExercise else null
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
                    actualState.routine.isDirty = true
                }
                _uiState.postValue(
                    RoutinesScreenState.Editing(
                        routine = actualState.routine,
                        positionOfScreen = false,
                        availableExercises = relatedExercisesByBodyPart(actualState.routine).filter { it.id !in actualState.routine.exercises.map { it.id } },
                        selectedExercise = actualState.selectedExercise
                    )
                )

                persistRoutine(actualState.routine)


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
                updateRoutineExerciseRelationUseCase(actualState.routine,
                    actualState.selectedExercise.apply {
                        this.setsAndReps = setsAndReps
                        this.observations = observations
                    })
                persistRoutine(actualState.routine)
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

    fun editRoutine(name: String, targetedBodyPart: String, context: Context) {
        if (name.isNotEmpty() && targetedBodyPart.isNotEmpty()) {
            val actualState = _uiState.value as RoutinesScreenState.Editing
            viewModelScope.launch(Dispatchers.IO) {
                actualState.routine.isDirty = true
                updateRoutineUseCase(
                    actualState.routine.copy(targetedBodyPart = targetedBodyPart, name = name)
                )
                persistRoutine(actualState.routine)
            }
        } else {
            Toast.makeText(context, "Rellene todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun persistRoutine(routine: RoutineModel) {

    // Offline-first: immediate remote persistence removed. syncPendingRoutines will batch create.

    }

    /** Offline-first sync: send all local routines without a realId */
    fun syncPendingRoutines() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = UserDetails.actualValue?.authToken ?: return@launch
            if (token.isBlank()) return@launch
            val pending = routines.value.filter { it.realId == 0 && it.isDirty }
            if (pending.isEmpty()) return@launch
            try {
                val mappings = syncManager.syncNewRoutines(pending)
                if (mappings.isNotEmpty()) {
                    val mapByLocal = mappings.toMap()
                    pending.forEach { r ->
                        mapByLocal[r.id.toString()]?.let { serverId ->
                            r.realId = serverId.toInt()
                            r.isDirty = false
                            updateRoutineUseCase(r)
                        }
                    }
                }
            } catch (_: Exception) { }
        }
    }

}

fun String.nameSimilarity(word2: String): Int {

    var count = 0
    for (i in 0..<min(
        word2.length, this.length
    )) {
        if (this[i] == word2[i]) count++
    }
    return count
}
