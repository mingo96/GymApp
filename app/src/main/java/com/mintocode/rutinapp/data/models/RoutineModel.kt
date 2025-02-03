package com.mintocode.rutinapp.data.models

import com.mintocode.rutinapp.data.daos.RoutineEntity

data class RoutineModel(
    var id: Int = 0,
    var name: String,
    var targetedBodyPart: String,
    var exercises: MutableList<ExerciseModel> = mutableListOf(),
    var realId: Int = 0,
    var isFromThisUser : Boolean = true
){
    fun toEntity(): RoutineEntity {
        return RoutineEntity(
            routineId = this.id, name = this.name, targetedBodyPart = this.targetedBodyPart,
            realId = this.realId, isFromThisUser = this.isFromThisUser
        )
    }
}