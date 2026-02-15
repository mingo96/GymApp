package com.mintocode.rutinapp.data.models

import com.mintocode.rutinapp.data.daos.PlanningEntity
import java.util.Date

data class PlanningModel(
    val id: Int,
    var realId: Long = 0L,
    val date: Date,
    var statedRoutine: RoutineModel? = null,
    var statedBodyPart: String? = null,
    var reminderTime: String? = null,
    var isDirty: Boolean = false
) {
    /**
     * Converts model to Room entity.
     *
     * Uses statedRoutine?.id (local PK) as routineId FK.
     * A routineId of 0 means the routine is not resolved locally
     * and will be stored as null to avoid FK violations.
     */
    fun toEntity(): PlanningEntity {
        val resolvedRoutineId = statedRoutine?.id?.takeIf { it != 0 }
        return PlanningEntity(
            id = id,
            date = date.time,
            routineId = resolvedRoutineId,
            bodyPart = statedBodyPart,
            realId = realId.toInt(),
            isDirty = isDirty,
            reminderTime = reminderTime
        )
    }
}