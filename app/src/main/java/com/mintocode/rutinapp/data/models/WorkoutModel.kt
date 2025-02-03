package com.mintocode.rutinapp.data.models

import com.mintocode.rutinapp.data.daos.WorkOutEntity
import java.util.Date

data class WorkoutModel(
    var id: Int = 0,
    var baseRoutine: RoutineModel? = null,
    var exercisesAndSets: MutableList<Pair<ExerciseModel, MutableList<SetModel>>> = mutableListOf(),
    val date: Date,
    var title: String,
    var isFinished: Boolean = false
){
    fun toEntity(): WorkOutEntity {
        return WorkOutEntity(
            workOutId = this.id, date = this.date.time, title = this.title, isFinished = isFinished
        )
    }
}