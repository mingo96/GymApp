package com.example.rutinapp.data.repositories

import com.example.rutinapp.data.daos.ExerciseEntity
import com.example.rutinapp.data.daos.RoutineDao
import com.example.rutinapp.data.daos.RoutineEntity
import com.example.rutinapp.data.daos.RoutineExerciseDao
import com.example.rutinapp.data.daos.RoutineExerciseEntity
import com.example.rutinapp.data.models.RoutineModel
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
        routineId = this.id, name = this.name, targetedBodyPart = this.targetedBodyPart
    )
}

class RoutineRepository @Inject constructor(
    private val routineDao: RoutineDao,
    private val routineExerciseDao: RoutineExerciseDao,
    private val exerciseRepository: ExerciseRepository
) {

    val routines: Flow<List<RoutineEntity>> = routineDao.getAllAsFlow()

    suspend fun getExercisesForRoutine(routineId: Long): List<ExerciseEntity> {
        val relatedExercises = routineExerciseDao.getRoutineExercisesByRoutineId(routineId).first()
            .map { it.exerciseId }

        return relatedExercises.map { exerciseRepository.getExercise(it.toString()) }
    }

    suspend fun getExercisesOrder(routineId: Long): List<Triple<Int, String, String>> {
        val relatedExercises = routineExerciseDao.getRoutineExercisesByRoutineId(routineId).first()
            .map { Triple(it.exerciseId, it.statedSetsAndReps, it.observations) }
        return relatedExercises
    }

    suspend fun addRoutine(routine: RoutineEntity): Int {
        return routineDao.addRoutine(routine).toInt()
    }

    suspend fun relateExerciseToRoutine(routineid: Int, exerciseId: Int) {

        routineExerciseDao.addRoutineExercise(
            RoutineExerciseEntity(
                routineid, exerciseId
            )
        )
    }

    suspend fun deleteRoutineExerciseRelation(routine: Int, exerciseId: Int) {

        routineExerciseDao.deleteRoutineExercise(routine, exerciseId)

    }

    suspend fun updateRoutineExerciseRelation(relation: RoutineExerciseEntity) {
        routineExerciseDao.updateRoutineExercise(relation)
    }

    suspend fun getRoutineById(routineId: Int): RoutineEntity {
        return routineDao.getFromId(routineId)
    }

}