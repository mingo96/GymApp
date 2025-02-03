package com.mintocode.rutinapp.data.repositories

import com.mintocode.rutinapp.data.daos.ExerciseDao
import com.mintocode.rutinapp.data.daos.ExerciseEntity
import com.mintocode.rutinapp.data.daos.RoutineDao
import com.mintocode.rutinapp.data.daos.RoutineEntity
import com.mintocode.rutinapp.data.daos.SetDao
import com.mintocode.rutinapp.data.daos.WorkOutDao
import com.mintocode.rutinapp.data.daos.WorkOutEntity
import com.mintocode.rutinapp.data.daos.WorkOutWithSets
import com.mintocode.rutinapp.data.daos.WorkoutRoutineEntity
import com.mintocode.rutinapp.data.daos.WorkoutRoutinesDao
import com.mintocode.rutinapp.data.models.WorkoutModel
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class WorkoutRepository @Inject constructor(
    private val workoutDao: WorkOutDao,
    private val setDao: SetDao,
    private val routineDao: RoutineDao,
    private val workoutRoutinesDao: WorkoutRoutinesDao,
    private val exerciseDao: ExerciseDao
) {

    val workOuts: Flow<List<WorkOutWithSets>> = workoutDao.get10MoreRecent()

    suspend fun addWorkout(workOutEntity: WorkOutEntity): Int {
        return workoutDao.addWorkOut(workOutEntity).toInt()
    }

    suspend fun addWorkoutRoutineRelation(
        workOutEntity: WorkOutEntity,
        routineEntity: RoutineEntity
    ) {
        val workout = workoutDao.getByDate(workOutEntity.date)

        workoutRoutinesDao.insert(
            WorkoutRoutineEntity(
                workoutId = workout.workOutId,
                routineId = routineEntity.routineId
            )
        )
    }

    suspend fun getWorkoutsRoutine(id: Int): RoutineEntity? {
        val relation = workoutRoutinesDao.getByWorkoutId(id) ?: return null
        return routineDao.getFromId(relation.routineId)
    }

    suspend fun getExercisesOfWorkout(id: Int): List<ExerciseEntity> {
        val sets = setDao.getByWorkoutId(id).map { it.exerciseDoneId }.distinct()

        return sets.map {
            exerciseDao.getById(it)
        }
    }

    suspend fun getWorkOutFromDate(date: Long): WorkOutEntity {
        return workoutDao.getByDate(date)
    }

    suspend fun deleteWorkout(workout: WorkOutEntity) {
        workoutDao.delete(workout)
    }

    suspend fun updateWorkout(workOut: WorkOutEntity) {
        workoutDao.update(workOut)
    }

}