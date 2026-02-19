package com.mintocode.rutinapp.data.daos

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.mintocode.rutinapp.data.models.CalendarPhaseModel
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Room entity for calendar phases.
 *
 * Stores named time periods (e.g., "Volumen", "Definición") that
 * are displayed as colored spans on the calendar.
 */
@Entity(tableName = "CalendarPhaseEntity")
data class CalendarPhaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val color: String,
    val startDate: Long,
    val endDate: Long,
    val notes: String? = null,
    val visibility: String = "private",
    @ColumnInfo(defaultValue = "0") var serverId: Long = 0L,
    @ColumnInfo(defaultValue = "0") var createdByUserId: Long? = null,
    @ColumnInfo(defaultValue = "") var localId: String? = null,
    @ColumnInfo(defaultValue = "0") var isDirty: Boolean = false
) {
    /**
     * Converts entity to domain model.
     */
    fun toModel(): CalendarPhaseModel {
        return CalendarPhaseModel(
            id = id,
            serverId = serverId,
            name = name,
            color = color,
            startDate = Date(startDate),
            endDate = Date(endDate),
            notes = notes,
            visibility = visibility,
            createdByUserId = createdByUserId,
            localId = localId,
            isDirty = isDirty
        )
    }
}

/**
 * Room DAO for calendar phase CRUD operations.
 */
@Dao
interface CalendarPhaseDao {

    /**
     * Gets all calendar phases as a reactive Flow.
     */
    @Query("SELECT * FROM CalendarPhaseEntity ORDER BY startDate ASC")
    fun getAllPhases(): Flow<List<CalendarPhaseEntity>>

    /**
     * Gets calendar phases that overlap with a date range.
     *
     * @param startMs Start of range (epoch millis)
     * @param endMs End of range (epoch millis)
     */
    @Query("SELECT * FROM CalendarPhaseEntity WHERE startDate <= :endMs AND endDate >= :startMs ORDER BY startDate ASC")
    fun getPhasesInRange(startMs: Long, endMs: Long): Flow<List<CalendarPhaseEntity>>

    /**
     * Gets phases that haven't been synced yet (serverId == 0).
     */
    @Query("SELECT * FROM CalendarPhaseEntity WHERE serverId = 0")
    suspend fun getUnsyncedPhases(): List<CalendarPhaseEntity>

    /**
     * Gets phases with local changes (isDirty == true, serverId > 0).
     */
    @Query("SELECT * FROM CalendarPhaseEntity WHERE isDirty = 1 AND serverId > 0")
    suspend fun getDirtyPhases(): List<CalendarPhaseEntity>

    /**
     * Finds a phase by its server ID.
     */
    @Query("SELECT * FROM CalendarPhaseEntity WHERE serverId = :serverId LIMIT 1")
    suspend fun getByServerId(serverId: Long): CalendarPhaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhase(phase: CalendarPhaseEntity): Long

    @Update
    suspend fun updatePhase(phase: CalendarPhaseEntity)

    @Delete
    suspend fun deletePhase(phase: CalendarPhaseEntity)

    @Query("DELETE FROM CalendarPhaseEntity WHERE serverId = :serverId")
    suspend fun deleteByServerId(serverId: Long)

    @Query("DELETE FROM CalendarPhaseEntity")
    suspend fun deleteAll()
}
