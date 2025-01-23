package com.mintocode.rutinapp.data.models

import com.mintocode.rutinapp.data.daos.PlanningEntity
import java.util.Date

data class PlanningModel(
    val id: Int,
    val date: Date,
    var statedRoutine: RoutineModel? = null,
    var statedBodyPart: String? = null
) {
    fun toEntity(): PlanningEntity {
        return PlanningEntity(
            id = id,
            date = date.time,
            routineId = statedRoutine?.id,
            bodyPart = statedBodyPart
        )
    }
}