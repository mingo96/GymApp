package com.mintocode.rutinapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mintocode.rutinapp.data.api.v2.ApiV2Service
import com.mintocode.rutinapp.data.api.v2.dto.BackupCatchUpData
import com.mintocode.rutinapp.data.api.v2.dto.BackupCatchUpRequest
import com.mintocode.rutinapp.data.api.v2.dto.BackupExportData
import com.mintocode.rutinapp.data.api.v2.dto.BackupImportData
import com.mintocode.rutinapp.data.api.v2.dto.BackupImportRequest
import com.mintocode.rutinapp.data.api.v2.dto.BackupSummaryData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Possible states of a backup operation.
 */
sealed class BackupUiState {
    /** No operation running — show summary. */
    data object Idle : BackupUiState()

    /** Loading summary / export / import. */
    data object Loading : BackupUiState()

    /** Export complete with data ready. */
    data class ExportDone(val data: BackupExportData) : BackupUiState()

    /** Import complete with result counts. */
    data class ImportDone(val data: BackupImportData) : BackupUiState()

    /** Catch-up complete with diff data. */
    data class CatchUpDone(val data: BackupCatchUpData) : BackupUiState()

    /** Error. */
    data class Error(val message: String) : BackupUiState()
}

/**
 * User-selected resource toggles for backup.
 */
data class BackupSelection(
    val exercises: Boolean = true,
    val routines: Boolean = true,
    val workouts: Boolean = true
) {
    /**
     * Builds a comma-separated resources string for the API.
     *
     * @return Comma-separated resource types
     */
    fun toResourcesString(): String {
        return buildList {
            if (exercises) add("exercises")
            if (routines) add("routines")
            if (workouts) add("workouts")
        }.joinToString(",")
    }

    /**
     * Builds a list of resource names for the API.
     *
     * @return List of selected resource names
     */
    fun toResourcesList(): List<String> {
        return buildList {
            if (exercises) add("exercises")
            if (routines) add("routines")
            if (workouts) add("workouts")
        }
    }

    /**
     * Whether at least one resource is selected.
     */
    fun hasSelection(): Boolean = exercises || routines || workouts
}

/**
 * ViewModel for backup operations.
 *
 * Manages summary loading, export, import (upload), and catch-up operations.
 * All API calls are made via ApiV2Service with Hilt injection.
 *
 * @param apiV2 Retrofit API service
 */
@HiltViewModel
class BackupViewModel @Inject constructor(
    private val apiV2: ApiV2Service
) : ViewModel() {

    private val _uiState = MutableStateFlow<BackupUiState>(BackupUiState.Idle)
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    private val _summary = MutableStateFlow<BackupSummaryData?>(null)
    val summary: StateFlow<BackupSummaryData?> = _summary.asStateFlow()

    private val _selection = MutableStateFlow(BackupSelection())
    val selection: StateFlow<BackupSelection> = _selection.asStateFlow()

    /**
     * Loads the backup summary from the server.
     */
    fun loadSummary() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = BackupUiState.Loading
            try {
                val response = apiV2.getBackupSummary()
                _summary.value = response.data
                _uiState.value = BackupUiState.Idle
            } catch (e: Exception) {
                _uiState.value = BackupUiState.Error(e.message ?: "Error al cargar resumen")
            }
        }
    }

    /**
     * Toggles a resource type selection.
     *
     * @param resource Resource name: "exercises", "routines", or "workouts"
     */
    fun toggleResource(resource: String) {
        _selection.value = when (resource) {
            "exercises" -> _selection.value.copy(exercises = !_selection.value.exercises)
            "routines" -> _selection.value.copy(routines = !_selection.value.routines)
            "workouts" -> _selection.value.copy(workouts = !_selection.value.workouts)
            else -> _selection.value
        }
    }

    /**
     * Exports selected resources from the server, paginated.
     * Collects all pages and emits ExportDone when complete.
     */
    fun exportResources() {
        val sel = _selection.value
        if (!sel.hasSelection()) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = BackupUiState.Loading
            try {
                val resources = sel.toResourcesString()
                val firstPage = apiV2.exportBackup(resources = resources, page = 1, perPage = 100)
                val lastPage = firstPage.meta?.lastPage ?: 1

                // Accumulate across pages
                val allExercises = firstPage.data.exercises?.toMutableList() ?: mutableListOf()
                val allRoutines = firstPage.data.routines?.toMutableList() ?: mutableListOf()
                val allWorkouts = firstPage.data.workouts?.toMutableList() ?: mutableListOf()

                for (page in 2..lastPage) {
                    val nextPage = apiV2.exportBackup(resources = resources, page = page, perPage = 100)
                    nextPage.data.exercises?.let { allExercises.addAll(it) }
                    nextPage.data.routines?.let { allRoutines.addAll(it) }
                    nextPage.data.workouts?.let { allWorkouts.addAll(it) }
                }

                _uiState.value = BackupUiState.ExportDone(
                    BackupExportData(
                        exercises = if (allExercises.isNotEmpty()) allExercises else null,
                        routines = if (allRoutines.isNotEmpty()) allRoutines else null,
                        workouts = if (allWorkouts.isNotEmpty()) allWorkouts else null
                    )
                )
            } catch (e: Exception) {
                _uiState.value = BackupUiState.Error(e.message ?: "Error al exportar")
            }
        }
    }

    /**
     * Imports (uploads) local data to the server.
     * Uses the export → re-import pattern: first exports all selected resources,
     * then sends them back via import endpoint for upsert.
     */
    fun uploadBackup() {
        val sel = _selection.value
        if (!sel.hasSelection()) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = BackupUiState.Loading
            try {
                // Step 1: Export all selected resources from server to get full data
                val resources = sel.toResourcesString()
                val exported = apiV2.exportBackup(resources = resources, page = 1, perPage = 100)

                // Step 2: Import back (upsert)
                val importRequest = BackupImportRequest(
                    exercises = exported.data.exercises,
                    routines = exported.data.routines,
                    workouts = exported.data.workouts
                )

                val result = apiV2.importBackup(importRequest)
                _uiState.value = BackupUiState.ImportDone(result.data!!)
            } catch (e: Exception) {
                _uiState.value = BackupUiState.Error(e.message ?: "Error al subir backup")
            }
        }
    }

    /**
     * Downloads backup data from server and applies it locally.
     * Exports all selected resources to be available for local processing.
     */
    fun downloadBackup() {
        exportResources()
    }

    /**
     * Catches up on changes since a given date.
     *
     * @param sinceDate ISO 8601 date string (e.g. "2025-01-01")
     */
    fun catchUp(sinceDate: String) {
        val sel = _selection.value
        if (!sel.hasSelection()) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = BackupUiState.Loading
            try {
                val result = apiV2.catchUpBackup(
                    BackupCatchUpRequest(
                        since = sinceDate,
                        resources = sel.toResourcesList()
                    )
                )
                _uiState.value = BackupUiState.CatchUpDone(result.data!!)
            } catch (e: Exception) {
                _uiState.value = BackupUiState.Error(e.message ?: "Error al sincronizar")
            }
        }
    }

    /**
     * Resets the UI state to Idle.
     */
    fun resetState() {
        _uiState.value = BackupUiState.Idle
    }
}
