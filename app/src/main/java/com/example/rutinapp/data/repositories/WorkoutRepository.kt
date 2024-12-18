package com.example.rutinapp.data.repositories

import com.example.rutinapp.data.daos.ExerciseDao
import com.example.rutinapp.data.daos.ExerciseEntity
import com.example.rutinapp.data.daos.RoutineDao
import com.example.rutinapp.data.daos.RoutineEntity
import com.example.rutinapp.data.daos.SetDao
import com.example.rutinapp.data.daos.SetEntity
import com.example.rutinapp.data.daos.SetsWorkOutDao
import com.example.rutinapp.data.daos.WorkOutDao
import com.example.rutinapp.data.daos.WorkOutEntity
import com.example.rutinapp.data.daos.WorkoutRoutinesDao
import com.example.rutinapp.data.models.WorkoutModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Date
import javax.inject.Inject

fun WorkOutEntity.toModel(): WorkoutModel {
    return WorkoutModel(
        id = this.workOutId, date = Date(this.date), title = this.title
    )
}

fun WorkoutModel.toEntity(): WorkOutEntity {
    return WorkOutEntity(
        workOutId = this.id, date = this.date.toString(), title = this.title
    )
}

class WorkoutRepository @Inject constructor(
    private val workoutDao: WorkOutDao,
    private val setsWorkoutDao: SetsWorkOutDao,
    private val setDao: SetDao,
    private val routineDao: RoutineDao,
    private val workoutRoutinesDao: WorkoutRoutinesDao,
    private val exerciseDao: ExerciseDao
) {

    val workOuts: Flow<List<WorkOutEntity>> = workoutDao.getAll()

    suspend fun addWorkout(workOutEntity: WorkOutEntity) {
        workoutDao.addWorkOut(workOutEntity)
    }

    suspend fun getWorkoutsRoutine(id: Int): RoutineEntity? {
        val relation = workoutRoutinesDao.getByWorkoutId(id)
        return routineDao.getAllAsFlow().first().find { it.routineId == relation.routineId }
    }

    //returns the exercise itself, a string with the sets and reps, and a list of the observations
    suspend fun getExercisesOfWorkout(id: Int): List<Pair<ExerciseEntity, List<SetEntity>>> {
        val sets = setsWorkoutDao.getByWorOutId(id).map {
            setDao.getById(it.setId)
        }
        val exercises = sets.map { set ->
            exerciseDao.getById(set.exerciseDoneId)
        }.distinct()

        val response = mutableListOf<Pair<ExerciseEntity, List<SetEntity>>>()

        exercises.forEach { exercise ->
            val setsOfThisExercise = sets.filter { it.exerciseDoneId == exercise.exerciseId }

            response.add(
                Pair(
                    exercise, setsOfThisExercise
                )
            )
        }
        return response.toList()

    }

}