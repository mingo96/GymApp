package com.example.rutinapp.newData.repositories

import com.example.rutinapp.newData.daos.ExerciseEntity
import com.example.rutinapp.newData.daos.RoutineDao
import com.example.rutinapp.newData.daos.RoutineEntity
import com.example.rutinapp.newData.daos.RoutineExerciseDao
import com.example.rutinapp.newData.daos.RoutineExerciseEntity
import com.example.rutinapp.newData.models.RoutineModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

fun RoutineEntity.toModel(): RoutineModel {
    return RoutineModel(
        id = this.routineId,
        name = this.name,
        targetedBodyPart = this.targetedBodyPart,
        exercises = mutableListOf()
    )
}

fun RoutineModel.toEntity(): RoutineEntity {
    return RoutineEntity(
        routineId = this.id,
        name = this.name,
        targetedBodyPart = this.targetedBodyPart
    )
}

class RoutineRepository @Inject constructor(
    private val routineDao: RoutineDao,
    private val routineExerciseDao: RoutineExerciseDao,
    private val exerciseRepository: ExerciseRepository
) {

    val routines: Flow<List<RoutineEntity>> = routineDao.getAll()

    suspend fun getExercisesForRoutine(routineId: Long): List<ExerciseEntity> {
        val relatedExercises = routineExerciseDao.getRoutineExercisesByRoutineId(routineId).first()
            .map { it.exerciseId }
        return relatedExercises.map { exerciseRepository.getExercise(it.toString()) }
    }

    suspend fun addRoutine(routine: RoutineEntity) {
        routineDao.addRoutine(routine)
    }

    suspend fun relateExerciseToRoutine(routineId: Long, exerciseId: Long) {
        routineExerciseDao.addRoutineExercise(RoutineExerciseEntity(routineId, exerciseId))
    }

}