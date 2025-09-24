package com.mintocode.rutinapp.data.api.classes

/**
 * Legacy DTO; scheduled for removal after migrating to dedicated v1 routine endpoints.
 */

import com.mintocode.rutinapp.data.models.RoutineModel

data class Routine(
    val id: Int,
    val name: String,
    val targetedBodyPart: String,
    val exercises: List<Exercise>,
    val isFromThisUser: Boolean,
    val realId: Int
) {
    fun toModel():RoutineModel=
        RoutineModel(
            id = this.id,
            name = this.name,
            targetedBodyPart = this.targetedBodyPart,
            exercises = this.exercises.map { it.toModel() }.toMutableList(),
            isFromThisUser = this.isFromThisUser,
        )
}