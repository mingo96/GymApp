package com.mintocode.rutinapp.data.models

import java.util.Date

/**
 * Domain model for a calendar phase.
 *
 * Calendar phases represent named time periods (e.g., "Volumen", "Definición")
 * displayed as colored spans on the calendar. They can be created by the user
 * or assigned by a trainer.
 *
 * @property id Local Room primary key
 * @property serverId Server-side ID (0 if not yet synced)
 * @property name Display name of the phase
 * @property color Hex color string (e.g., "#FF5733")
 * @property startDate Phase start date (inclusive)
 * @property endDate Phase end date (inclusive)
 * @property notes Optional notes/description
 * @property visibility "private" or "shared_with_trainers"
 * @property createdByUserId User ID who created the phase (self or trainer)
 * @property localId Temporary local identifier for sync (used before server assigns ID)
 * @property isDirty True if local changes haven't been synced yet
 */
data class CalendarPhaseModel(
    val id: Int = 0,
    var serverId: Long = 0L,
    val name: String,
    val color: String,
    val startDate: Date,
    val endDate: Date,
    val notes: String? = null,
    val visibility: String = "private",
    val createdByUserId: Long? = null,
    val localId: String? = null,
    var isDirty: Boolean = false
)
