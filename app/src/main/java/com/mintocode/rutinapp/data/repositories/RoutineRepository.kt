package com.mintocode.rutinapp.data.repositories

import com.mintocode.rutinapp.data.daos.ExerciseEntity
import com.mintocode.rutinapp.data.daos.RoutineDao
import com.mintocode.rutinapp.data.daos.RoutineEntity
import com.mintocode.rutinapp.data.daos.RoutineExerciseDao
import com.mintocode.rutinapp.data.daos.RoutineExerciseEntity
import com.mintocode.rutinapp.data.daos.RoutineWithExercises
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RoutineRepository @Inject constructor(
    private val routineDao: RoutineDao,
    private val routineExerciseDao: RoutineExerciseDao,
    private val exerciseRepository: ExerciseRepository
) {

    val routines: Flow<List<RoutineWithExercises>> = routineDao.allWithRelations()

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
        if (routineExerciseDao.existsRoutineExercise(
                relation.routineId,
                relation.exerciseId
            )
        ) routineExerciseDao.updateRoutineExercise(relation)
        else
            routineExerciseDao.addRoutineExercise(relation)
    }

    suspend fun getRoutineById(routineId: Int): RoutineEntity {
        return routineDao.getFromId(routineId)
    }

    suspend fun updateRoutine(entity: RoutineEntity) {
        routineDao.updateRoutine(entity)
    }

}