package com.mintocode.rutinapp.data.models

import com.mintocode.rutinapp.data.daos.PlanningEntity
import java.util.Date

data class PlanningModel(
    val id: Int,
    var realId: Long = 0L,
    val date: Date,
    var statedRoutine: RoutineModel? = null,
    var statedBodyPart: String? = null,
    var isDirty: Boolean = false
) {
    fun toEntity(): PlanningEntity {
        return PlanningEntity(
            id = id,
            date = date.time,
            routineId = statedRoutine?.id,
            bodyPart = statedBodyPart,
            realId = realId.toInt(),
            isDirty = isDirty
        )
    }
}