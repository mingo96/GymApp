package com.mintocode.rutinapp.data.repositories

import com.mintocode.rutinapp.data.daos.CalendarPhaseDao
import com.mintocode.rutinapp.data.daos.CalendarPhaseEntity
import com.mintocode.rutinapp.data.models.CalendarPhaseModel
import com.mintocode.rutinapp.sync.SyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import javax.inject.Inject

/**
 * Repository for calendar phases (named time periods on the calendar).
 *
 * Coordinates between the local Room database and the SyncManager
 * for server synchronization.
 */
class CalendarPhaseRepository @Inject constructor(
    private val calendarPhaseDao: CalendarPhaseDao,
    private val syncManager: SyncManager
) {

    /**
     * All locally stored calendar phases as a reactive Flow of domain models.
     */
    val allPhases: Flow<List<CalendarPhaseModel>> =
        calendarPhaseDao.getAllPhases().map { entities ->
            entities.map { it.toModel() }
        }

    /**
     * Gets calendar phases that overlap with the given date range.
     *
     * @param start Start of the range
     * @param end End of the range
     * @return Flow of CalendarPhaseModel list
     */
    fun getPhasesInRange(start: Date, end: Date): Flow<List<CalendarPhaseModel>> {
        return calendarPhaseDao.getPhasesInRange(start.time, end.time).map { entities ->
            entities.map { it.toModel() }
        }
    }

    /**
     * Inserts a new calendar phase locally and marks it for sync.
     *
     * @param phase The domain model to insert
     * @return The local ID assigned by Room
     */
    suspend fun insert(phase: CalendarPhaseModel): Long {
        val entity = phase.toEntity()
        return calendarPhaseDao.insertPhase(entity)
    }

    /**
     * Updates an existing calendar phase locally and marks it dirty.
     *
     * @param phase The domain model with updated data
     */
    suspend fun update(phase: CalendarPhaseModel) {
        val entity = phase.toEntity().copy(isDirty = true)
        calendarPhaseDao.updatePhase(entity)
    }

    /**
     * Deletes a calendar phase from local storage.
     *
     * @param phase The domain model to delete
     */
    suspend fun delete(phase: CalendarPhaseModel) {
        calendarPhaseDao.deletePhase(phase.toEntity())
    }

    /**
     * Gets phases not yet synced to server (serverId == 0).
     *
     * @return List of unsynced CalendarPhaseModels
     */
    suspend fun getUnsyncedPhases(): List<CalendarPhaseModel> {
        return calendarPhaseDao.getUnsyncedPhases().map { it.toModel() }
    }

    /**
     * Gets phases with local edits pending sync (isDirty, serverId > 0).
     *
     * @return List of dirty CalendarPhaseModels
     */
    suspend fun getDirtyPhases(): List<CalendarPhaseModel> {
        return calendarPhaseDao.getDirtyPhases().map { it.toModel() }
    }

    /**
     * Syncs local phases to the server and downloads new server phases.
     *
     * Steps:
     * 1. Upload unsynced (new) and dirty (edited) phases
     * 2. Update local phases with server IDs from mappings
     * 3. Download all server phases and merge into local DB
     */
    suspend fun syncPhases() {
        // 1. Upload local changes
        val unsynced = calendarPhaseDao.getUnsyncedPhases().map { it.toModel() }
        val dirty = calendarPhaseDao.getDirtyPhases().map { it.toModel() }

        val mappings = syncManager.syncCalendarPhases(
            newPhases = unsynced,
            updatedPhases = dirty
        )

        // 2. Apply server ID mappings to local new phases
        for ((localId, serverId) in mappings) {
            val entities = calendarPhaseDao.getUnsyncedPhases()
            val match = entities.find { it.localId == localId }
            if (match != null) {
                calendarPhaseDao.updatePhase(
                    match.copy(serverId = serverId, isDirty = false)
                )
            }
        }

        // 3. Clear dirty flag on synced updates
        for (dirtyPhase in dirty) {
            val entity = calendarPhaseDao.getByServerId(dirtyPhase.serverId)
            if (entity != null) {
                calendarPhaseDao.updatePhase(entity.copy(isDirty = false))
            }
        }

        // 4. Download server phases and merge
        val serverPhases = syncManager.downloadCalendarPhases()
        for (serverPhase in serverPhases) {
            val existing = calendarPhaseDao.getByServerId(serverPhase.serverId)
            if (existing != null) {
                calendarPhaseDao.updatePhase(
                    existing.copy(
                        name = serverPhase.name,
                        color = serverPhase.color,
                        startDate = serverPhase.startDate.time,
                        endDate = serverPhase.endDate.time,
                        notes = serverPhase.notes,
                        visibility = serverPhase.visibility,
                        createdByUserId = serverPhase.createdByUserId,
                        isDirty = false
                    )
                )
            } else {
                calendarPhaseDao.insertPhase(
                    CalendarPhaseEntity(
                        name = serverPhase.name,
                        color = serverPhase.color,
                        startDate = serverPhase.startDate.time,
                        endDate = serverPhase.endDate.time,
                        notes = serverPhase.notes,
                        visibility = serverPhase.visibility,
                        serverId = serverPhase.serverId,
                        createdByUserId = serverPhase.createdByUserId,
                        localId = UUID.randomUUID().toString(),
                        isDirty = false
                    )
                )
            }
        }
    }

    /**
     * Converts a domain model to its Room entity.
     *
     * @return CalendarPhaseEntity for persistence
     */
    private fun CalendarPhaseModel.toEntity(): CalendarPhaseEntity {
        return CalendarPhaseEntity(
            id = id,
            name = name,
            color = color,
            startDate = startDate.time,
            endDate = endDate.time,
            notes = notes,
            visibility = visibility,
            serverId = serverId,
            createdByUserId = createdByUserId,
            localId = localId ?: UUID.randomUUID().toString(),
            isDirty = isDirty
        )
    }
}
