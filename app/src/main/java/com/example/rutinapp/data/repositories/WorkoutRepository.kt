package com.example.rutinapp.data.repositories

import android.util.Log
import com.example.rutinapp.data.daos.ExerciseDao
import com.example.rutinapp.data.daos.ExerciseEntity
import com.example.rutinapp.data.daos.RoutineDao
import com.example.rutinapp.data.daos.RoutineEntity
import com.example.rutinapp.data.daos.SetDao
import com.example.rutinapp.data.daos.SetEntity
import com.example.rutinapp.data.daos.WorkOutDao
import com.example.rutinapp.data.daos.WorkOutEntity
import com.example.rutinapp.data.daos.WorkOutWithSets
import com.example.rutinapp.data.daos.WorkoutRoutineEntity
import com.example.rutinapp.data.daos.WorkoutRoutinesDao
import com.example.rutinapp.data.models.WorkoutModel
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

fun WorkOutEntity.toModel(): WorkoutModel {
    return WorkoutModel(
        id = this.workOutId, date = Date(this.date), title = this.title
    )
}

fun WorkoutModel.toEntity(): WorkOutEntity {
    return WorkOutEntity(
        workOutId = this.id, date = this.date.time, title = this.title
    )
}

class WorkoutRepository @Inject constructor(
    private val workoutDao: WorkOutDao,
    private val setDao: SetDao,
    private val routineDao: RoutineDao,
    private val workoutRoutinesDao: WorkoutRoutinesDao,
    private val exerciseDao: ExerciseDao
) {

    val workOuts: Flow<List<WorkOutWithSets>> = workoutDao.get10MoreRecent()

    suspend fun addWorkout(workOutEntity: WorkOutEntity):Int {
        return workoutDao.addWorkOut(workOutEntity).toInt()
    }

    suspend fun addWorkoutRoutineRelation(workOutEntity: WorkOutEntity, routineEntity: RoutineEntity){
        val workout = workoutDao.getByDate(workOutEntity.date)

        workoutRoutinesDao.insert(WorkoutRoutineEntity(workoutId = workout.workOutId, routineId = routineEntity.routineId))
    }

    suspend fun getWorkoutsRoutine(id: Int): RoutineEntity? {
        val relation = workoutRoutinesDao.getByWorkoutId(id) ?: return null
        return routineDao.getFromId(relation.routineId)
    }

    suspend fun getExercisesOfWorkout(id: Int):List<ExerciseEntity>{
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

}