package com.example.rutinapp.data.repositories

import android.util.Log
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

    val routines: Flow<List<RoutineEntity>> = routineDao.getAll()

    suspend fun getExercisesForRoutine(routineId: Long): List<ExerciseEntity> {
        val relatedExercises = routineExerciseDao.getRoutineExercisesByRoutineId(routineId).first()
            .map { it.exerciseId }
        return relatedExercises.map { exerciseRepository.getExercise(it.toString()) }
    }

    suspend fun addRoutine(routine: RoutineEntity) {
        routineDao.addRoutine(routine)
    }

    suspend fun relateExerciseToRoutine(routineid : Int, exerciseId: Int, position: Int) {
        Log.i("relateExerciseToRoutine", "relateExerciseToRoutine: $routineid $exerciseId $position")
        routineExerciseDao.addRoutineExercise(
            RoutineExerciseEntity(
                routineid, exerciseId, position
            )
        )
    }

    suspend fun deleteRoutineExerciseRelation(routine : Int, exerciseId: Int, position: Int) {

        routineExerciseDao.deleteRoutineExercise(
            RoutineExerciseEntity(
                routine, exerciseId, position
            )
        )

    }

}