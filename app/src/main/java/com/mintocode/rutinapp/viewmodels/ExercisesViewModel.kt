package com.mintocode.rutinapp.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mintocode.rutinapp.data.UserDetails
import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.domain.addUseCases.AddExerciseUseCase
import com.mintocode.rutinapp.domain.addUseCases.AddExercisesRelationUseCase
import com.mintocode.rutinapp.domain.deleteUseCases.DeleteExerciseRelationUseCase
import com.mintocode.rutinapp.domain.getUseCases.GetExercisesUseCase
import com.mintocode.rutinapp.domain.updateUseCases.UpdateExerciseUseCase
import com.mintocode.rutinapp.ui.screenStates.ExercisesState
import com.mintocode.rutinapp.sync.SyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExercisesViewModel @Inject constructor(
    private val addExerciseUseCase: AddExerciseUseCase,
    private val getExercisesUseCase: GetExercisesUseCase,
    private val addExerciseRelationUseCase: AddExercisesRelationUseCase,
    private val deleteExerciseRelationUseCase: DeleteExerciseRelationUseCase,
    private val updateExerciseUseCase: UpdateExerciseUseCase,
    private val syncManager: SyncManager
) : ViewModel() {

    val exercisesState: StateFlow<List<ExerciseModel>> = getExercisesUseCase().catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState: MutableLiveData<ExercisesState> =
        MutableLiveData(ExercisesState.Observe())

    val uiState: LiveData<ExercisesState> = _uiState

    private val _showOthers = MutableLiveData(false)
    val showOthers: LiveData<Boolean> = _showOthers

    // Loading flag for remote fetches
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Simple debounce timestamp (ms)
    @Volatile private var lastActionAt: Long = 0
    private fun debounceWindow(): Long = 600 // ms
    private fun canAct(): Boolean {
        val now = System.currentTimeMillis()
        return if (now - lastActionAt > debounceWindow()) {
            lastActionAt = now; true
        } else false
    }

    // Internal reusable downloader (no context side-effects)
    private fun downloadMyExercisesInternal() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = UserDetails.actualValue?.authToken ?: return@launch
            if (token.isBlank()) return@launch
            _isLoading.postValue(true)
            try {
                val remote = syncManager.downloadMyExercises()
                if (remote.isNotEmpty()) {
                    val existingByReal = exercisesState.value.associateBy { it.realId }
                    remote.forEach { incoming ->
                        if (incoming.realId != 0L) {
                            val existing = existingByReal[incoming.realId]
                            if (existing == null) {
                                addExerciseUseCase(incoming)
                            } else {
                                // Merge if changed
                                if (existing.name != incoming.name ||
                                    existing.description != incoming.description ||
                                    existing.targetedBodyPart != incoming.targetedBodyPart ||
                                    existing.observations != incoming.observations
                                ) {
                                    existing.name = incoming.name
                                    existing.description = incoming.description
                                    existing.targetedBodyPart = incoming.targetedBodyPart
                                    existing.observations = incoming.observations
                                    updateExerciseUseCase(existing)
                                }
                            }
                        }
                    }
                }
            } catch (_: Exception) { }
            finally { _isLoading.postValue(false) }
        }
    }

    // Public variant with optional toast if token blank
    fun downloadMyExercises(context: Context) {
        val token = UserDetails.actualValue?.authToken
        if (token.isNullOrBlank()) {
            Toast.makeText(context, "Token vacío", Toast.LENGTH_SHORT).show()
            return
        }
        downloadMyExercisesInternal()
    }

    // Download others' exercises (always refresh on call)
    fun downloadOtherExercises() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = UserDetails.actualValue?.authToken ?: return@launch
            if (token.isBlank()) return@launch
            _isLoading.postValue(true)
            try {
                val remote = syncManager.downloadOtherExercises()
                if (remote.isNotEmpty()) {
                    val existingByReal = exercisesState.value.associateBy { it.realId }
                    remote.forEach { incoming ->
                        if (incoming.realId != 0L) {
                            val existing = existingByReal[incoming.realId]
                            if (existing == null) {
                                addExerciseUseCase(incoming)
                            } else {
                                if (existing.name != incoming.name ||
                                    existing.description != incoming.description ||
                                    existing.targetedBodyPart != incoming.targetedBodyPart ||
                                    existing.observations != incoming.observations
                                ) {
                                    existing.name = incoming.name
                                    existing.description = incoming.description
                                    existing.targetedBodyPart = incoming.targetedBodyPart
                                    existing.observations = incoming.observations
                                    updateExerciseUseCase(existing)
                                }
                            }
                        }
                    }
                }
            } catch (_: Exception) { }
            finally { _isLoading.postValue(false) }
        }
    }

    fun writeOnExerciseName(name: String) {

        if (uiState.value is ExercisesState.SearchingForExercise){
            localSearch(name)
        }else{
            searchOnDB(name)
        }

    }

    private fun localSearch(name: String){
        _uiState.postValue(
            ExercisesState.SearchingForExercise(exercisesState.value.filter {
                it.name.lowercase(Locale.getDefault())
                    .contains(name, true) || it.targetedBodyPart.contains(name, true)
            })
        )
    }

    fun addExercise(name: String, description: String, targetedBodyPart: String, context: Context) {
        if (name.isNotEmpty() && description.isNotEmpty() && targetedBodyPart.isNotEmpty()) viewModelScope.launch(
            context = Dispatchers.IO
        ) {
            val exercise = ExerciseModel(
                name = name, description = description, targetedBodyPart = targetedBodyPart
            ).apply { this.isDirty = true }
            addExerciseUseCase(
                exercise
            )
            _uiState.postValue(ExercisesState.Observe())

            // Offline-first: no immediate remote create; syncPendingExercises will batch.

        }
        else Toast.makeText(context, "Faltan campos por rellenar", Toast.LENGTH_SHORT).show()
    }

    fun fetchExercises(context: Context) { // reutilizamos el botón existente
        downloadMyExercises(context)
    }

    fun toggleExercisesRelation(exercise: ExerciseModel) {
        viewModelScope.launch(context = Dispatchers.IO) {
            if (_uiState.value is ExercisesState.AddingRelations) {
                val actualExercise =
                    (_uiState.value as ExercisesState.AddingRelations).exerciseModel
                if (!actualExercise.equivalentExercises.map { it.id }.contains(exercise.id)) {
                    addExerciseRelationUseCase(actualExercise, exercise)
                    actualExercise.equivalentExercises += exercise
                    _uiState.postValue(
                        ExercisesState.Modifying(
                            actualExercise, actualExercise.equivalentExercises
                        )
                    )
                } else {
                    //honestly i dont know how would this happen
                }
            } else {
                if (_uiState.value is ExercisesState.Modifying) {

                    val actualExercise = (_uiState.value as ExercisesState.Modifying).exerciseModel

                    deleteExerciseRelationUseCase(actualExercise, exercise)
                    actualExercise.equivalentExercises -= exercise

                    _uiState.postValue(
                        ExercisesState.Modifying(
                            actualExercise, actualExercise.equivalentExercises
                        )
                    )

                } else {
                    //i dont even know how would this happen
                }
            }
        }
    }

    fun clickToEdit(selected: ExerciseModel) {
        _uiState.postValue(ExercisesState.Modifying(selected, selected.equivalentExercises))
    }

    /**this will only be reached during edit state*/
    fun clickToAddRelatedExercises(context: Context) {
        try {
            assert(_uiState.value is ExercisesState.Modifying)
            val selected = (_uiState.value as ExercisesState.Modifying).exerciseModel
            _uiState.postValue(
                ExercisesState.AddingRelations(selected,
                    exercisesState.value.filter { it != selected && it.id !in selected.equivalentExercises.map { it.id } })
            )
        } catch (error: AssertionError) {
            Toast.makeText(context, "You are not in edit mode", Toast.LENGTH_SHORT).show()
        }
    }

    fun backToObserve() {
        _uiState.postValue(ExercisesState.Observe())
    }

    fun updateExercise(
        name: String, description: String, targetedBodyPart: String, context: Context
    ) {
        if (name.isNotEmpty() && description.isNotEmpty() && targetedBodyPart.isNotEmpty())

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    assert(_uiState.value is ExercisesState.Modifying)
                    val selected = (_uiState.value as ExercisesState.Modifying).exerciseModel
                    selected.name = name
                    selected.description = description
                    selected.targetedBodyPart = targetedBodyPart
                    selected.isDirty = true
                    updateExerciseUseCase(selected)
                    _uiState.postValue(ExercisesState.Observe(selected))
                    // Remote update removed (offline-first). Mark dirty if future upsert added.

                } catch (error: AssertionError) {
                    //i dont know how would this happen
                }
            }
        else Toast.makeText(context, "Faltan campos por rellenar", Toast.LENGTH_SHORT).show()
    }

    fun clickToCreate() {
        _uiState.postValue(ExercisesState.Creating)
    }

    fun clickToObserve(exercise: ExerciseModel) {
        _uiState.postValue(ExercisesState.Observe(exercise))
    }

    fun uploadExercise(exercise: ExerciseModel) { /* deprecated - use syncPendingExercises */ }

    fun changeToUploadedExercises() {
        viewModelScope.launch {
            if (_uiState.value !is ExercisesState.ExploringExercises) {
            _uiState.postValue(ExercisesState.ExploringExercises(emptyList()))
            }else{
                _uiState.postValue(ExercisesState.Observe())
            }
        }
    }

    /** Offline-first sync: send all local exercises without a realId to server */
    fun syncPendingExercises() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = UserDetails.actualValue?.authToken ?: return@launch
            if (token.isBlank()) return@launch
            val pending = exercisesState.value.filter { it.realId == 0L && it.isDirty }
            if (pending.isEmpty()) return@launch
            try {
                val mappings = syncManager.syncNewExercises(pending)
                if (mappings.isNotEmpty()) {
                    val mapByLocal = mappings.toMap()
                    pending.forEach { ex ->
                        mapByLocal[ex.id]?.let { serverId ->
                            ex.realId = serverId
                            ex.isDirty = false
                            updateExerciseUseCase(ex)
                        }
                    }
                }
            } catch (_: Exception) { }
        }
    }

    private fun searchOnDB(text: String) {
        viewModelScope.launch {

            // Remote search disabled; fallback to localSearch invoked by user typing.
        }
    }

    fun saveExercise(exercise: ExerciseModel) {

        viewModelScope.launch(Dispatchers.IO) {

            if (exercise.realId == 0L) return@launch

            // Related exercises fetch disabled (no server endpoint in offline-first scope).
            _uiState.postValue(ExercisesState.Observe())
        }

    }

    // Toggle view; always refresh corresponding remote list
    fun toggleShowOthers(context: Context) {
        val newValue = !(_showOthers.value ?: false)
        if (_uiState.value !is ExercisesState.Observe) {
            _uiState.postValue(ExercisesState.Observe())
        }
        _showOthers.postValue(newValue)
        if (newValue) {
            // Showing others
            downloadOtherExercises()
        } else {
            // Refresh own list to capture possible changes
            downloadMyExercises(context)
        }
    }

    // Explicit actions (avoid relying only on toggle for UI with separate buttons)
    fun showOthers(context: Context) {
    if (!canAct()) return
    if (_uiState.value !is ExercisesState.Observe) {
            _uiState.postValue(ExercisesState.Observe())
        }
        _showOthers.postValue(true)
        downloadOtherExercises()
    }

    fun showMine(context: Context) {
    if (!canAct()) return
    if (_uiState.value !is ExercisesState.Observe) {
            _uiState.postValue(ExercisesState.Observe())
        }
        _showOthers.postValue(false)
        downloadMyExercises(context)
    }

    val visibleExercises: StateFlow<List<ExerciseModel>> = exercisesState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun currentVisible(): List<ExerciseModel> {
        val base = exercisesState.value
        return if (showOthers.value == true) base.filter { !it.isFromThisUser } else base.filter { it.isFromThisUser }
    }

}