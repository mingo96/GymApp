package com.mintocode.rutinapp.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mintocode.rutinapp.data.UserDetails
import com.mintocode.rutinapp.data.models.CalendarPhaseModel
import com.mintocode.rutinapp.data.models.PlanningGrantModel
import com.mintocode.rutinapp.data.models.PlanningModel
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.data.models.TrainerRelationModel
import com.mintocode.rutinapp.data.models.WorkoutVisibilityGrantModel
import com.mintocode.rutinapp.data.repositories.CalendarPhaseRepository
import com.mintocode.rutinapp.domain.addUseCases.AddPlanningUseCase
import com.mintocode.rutinapp.domain.getUseCases.DAY_IN_MILLIS
import com.mintocode.rutinapp.domain.getUseCases.GetPlanningsUseCase
import com.mintocode.rutinapp.domain.getUseCases.GetRoutinesUseCase
import com.mintocode.rutinapp.domain.updateUseCases.UpdatePlanningUseCase
import com.mintocode.rutinapp.ui.screenStates.FieldBeingEdited
import com.mintocode.rutinapp.ui.screenStates.MainScreenState
import com.mintocode.rutinapp.sync.SyncManager
import com.mintocode.rutinapp.utils.toSimpleDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getPlanningsUseCase: GetPlanningsUseCase,
    private val addPlanningUseCase: AddPlanningUseCase,
    private val updatePlanningUseCase: UpdatePlanningUseCase,
    private val getRoutinesUseCase: GetRoutinesUseCase,
    private val syncManager: SyncManager,
    private val calendarPhaseRepository: CalendarPhaseRepository
) : ViewModel() {

    companion object {
        private const val TAG = "MainScreenViewModel"
    }

    private var _planningsFlow: StateFlow<List<PlanningModel>> =
        getPlanningsUseCase(
            Pair(
                Date().toSimpleDate(),
                Date(Date().time + DAY_IN_MILLIS * 7)
            )
        ).catch { Error(it) }
            .map {
                delay(1000)
                _todaysPlanning.postValue(it.find { it.date.toSimpleDate().time == Date().toSimpleDate().time }
                    ?: PlanningModel(id = 0, date = Date().toSimpleDate()))
                _plannings.postValue(it)
                it
            }.stateIn(
                viewModelScope, SharingStarted.Eagerly, emptyList()
            )

    private val _plannings: MutableLiveData<List<PlanningModel>> = MutableLiveData(emptyList())

    var plannings: LiveData<List<PlanningModel>> = _plannings

    private val _todaysPlanning: MutableLiveData<PlanningModel> = MutableLiveData()

    val todaysPlanning: LiveData<PlanningModel> = _todaysPlanning

    private val _uiState: MutableLiveData<MainScreenState> =
        MutableLiveData(MainScreenState.Observation)

    val uiState: LiveData<MainScreenState> = _uiState

    /**
     * Calendar phases from the local Room database, observed as a reactive Flow.
     */
    val calendarPhases: StateFlow<List<CalendarPhaseModel>> =
        calendarPhaseRepository.allPhases
            .catch { Log.e(TAG, "Error loading calendar phases", it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /**
     * Trainer relations (read-only from server).
     */
    private val _trainerRelations = MutableLiveData<List<TrainerRelationModel>>(emptyList())
    val trainerRelations: LiveData<List<TrainerRelationModel>> = _trainerRelations

    /**
     * Planning grants (read-only from server).
     */
    private val _planningGrants = MutableLiveData<List<PlanningGrantModel>>(emptyList())
    val planningGrants: LiveData<List<PlanningGrantModel>> = _planningGrants

    /**
     * Workout visibility grants (read-only from server).
     */
    private val _workoutGrants = MutableLiveData<List<WorkoutVisibilityGrantModel>>(emptyList())
    val workoutGrants: LiveData<List<WorkoutVisibilityGrantModel>> = _workoutGrants

    /**
     * Auto-refresh silencioso al entrar a la pantalla.
     *
     * Sube plannings no sincronizados y descarga los del servidor sin mostrar toast.
     * Resuelve las referencias de rutinas (realId → localId) para evitar
     * violaciones de FK al insertar plannings descargados.
     */
    fun autoSync() {
        val token = UserDetails.actualValue?.authToken
        if (token.isNullOrBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Esperar hasta 3s a que se carguen los plannings desde Room
                val allPlannings = withTimeoutOrNull(3000L) {
                    _planningsFlow.first { it.isNotEmpty() }
                } ?: (_plannings.value ?: emptyList())

                // 1. Subir plannings no sincronizados (realId == 0 implica nunca subido)
                // Incluimos todos los que no tienen realId, no solo los dirty,
                // para sincronizar plannings creados antes de que existiera isDirty.
                val unsynced = allPlannings.filter { it.realId == 0L }
                if (unsynced.isNotEmpty()) {
                    val mappings = syncManager.syncNewPlannings(unsynced)
                    mappings.forEach { (localId, serverId) ->
                        val local = unsynced.find { it.id.toString() == localId }
                        if (local != null) {
                            local.realId = serverId
                            local.isDirty = false
                            updatePlanningUseCase(local)
                        }
                    }
                }

                // 2. Descargar plannings del servidor y merge
                val remote = syncManager.downloadMyPlanning()
                if (remote.isNotEmpty()) {
                    // Refrescar lista tras posible upload
                    val currentPlannings = _plannings.value ?: allPlannings
                    val existingByReal = currentPlannings
                        .filter { it.realId != 0L }
                        .associateBy { it.realId }

                    // Construir mapa realId → localId para resolver rutinas
                    val routines = getRoutinesUseCase().first()
                    val routineByRealId = routines.associateBy { it.realId }

                    remote.forEach { incoming ->
                        if (incoming.realId != 0L && existingByReal[incoming.realId] == null) {
                            // Resolver routineId local desde realId del servidor
                            if (incoming.statedRoutine != null) {
                                val localRoutine = routineByRealId[incoming.statedRoutine!!.realId]
                                if (localRoutine != null) {
                                    incoming.statedRoutine!!.id = localRoutine.id
                                } else {
                                    // Rutina no sincronizada localmente, guardar sin rutina
                                    incoming.statedRoutine = null
                                }
                            }
                            incoming.isDirty = false
                            addPlanningUseCase(incoming)
                        }
                    }
                }

                // 3. Sync calendar phases (upload local + download server)
                try {
                    calendarPhaseRepository.syncPhases()
                } catch (e: Exception) {
                    Log.e(TAG, "Calendar phase sync failed", e)
                }

                // 4. Download trainer data (read-only)
                try {
                    val (relations, grants, wGrants) = syncManager.syncTrainerData()
                    _trainerRelations.postValue(relations)
                    _planningGrants.postValue(grants)
                    _workoutGrants.postValue(wGrants)
                } catch (e: Exception) {
                    Log.e(TAG, "Trainer data sync failed", e)
                }
            } catch (_: Exception) { }
        }
    }

    fun changeDates(selectedStartMillis: Long?, selectedEndDateMillis: Long?) {
        if (selectedStartMillis == null || selectedEndDateMillis == null) return

        if (selectedEndDateMillis > selectedStartMillis) {
            _planningsFlow =
                getPlanningsUseCase(Date(selectedStartMillis) to Date(selectedEndDateMillis)).catch {
                    Error(it)
                }.map {
                    _plannings.postValue(it)
                    it
                }.stateIn(
                    viewModelScope, SharingStarted.Eagerly, emptyList()
                )
        }
    }

    fun planningClicked(it: PlanningModel) {
        val destination =
            if (it.statedRoutine != null) FieldBeingEdited.ROUTINE else if (it.statedBodyPart != null) FieldBeingEdited.BODYPART else FieldBeingEdited.NONE
        _uiState.postValue(MainScreenState.PlanningOnMainFocus(it, destination))
    }

    fun backToObservation() {
        _uiState.postValue(MainScreenState.Observation)
    }

    fun selectBodypartClicked() {
        val actualState = _uiState.value as MainScreenState.PlanningOnMainFocus

        _uiState.postValue(actualState.copy(fieldBeingEdited = FieldBeingEdited.BODYPART))

    }

    fun selectRoutineClicked() {
        val actualState = _uiState.value as MainScreenState.PlanningOnMainFocus
        viewModelScope.launch {

            val routines = getRoutinesUseCase().first()

            _uiState.postValue(
                actualState.copy(
                    fieldBeingEdited = FieldBeingEdited.ROUTINE, availableRoutines = routines
                )
            )
        }
    }

    fun saveBodypart(it: String, context: Context) {
        if (it.isEmpty()) {
            Toast.makeText(context, "Debes escribir una parte del cuerpo", Toast.LENGTH_SHORT)
                .show()
            return
        }
        val actualState = _uiState.value as MainScreenState.PlanningOnMainFocus

        val planning = actualState.planningModel.copy(statedBodyPart = it, statedRoutine = null, isDirty = true)

        viewModelScope.launch(Dispatchers.IO) {
            if (planning.id == 0) addPlanningUseCase(planning)
            else updatePlanningUseCase(planning)
            backToObservation()
        }

    }

    fun backToSelection() {

        val actualState = _uiState.value as MainScreenState.PlanningOnMainFocus

        _uiState.postValue(actualState.copy(fieldBeingEdited = FieldBeingEdited.NONE))
    }

    fun saveRoutine(it: RoutineModel) {
        val actualState = _uiState.value as MainScreenState.PlanningOnMainFocus

        val planning = actualState.planningModel.copy(statedRoutine = it, statedBodyPart = null, isDirty = true)

        viewModelScope.launch(Dispatchers.IO) {
            if (planning.id == 0) addPlanningUseCase(planning)
            else updatePlanningUseCase(planning)
            backToObservation()
        }
    }


}