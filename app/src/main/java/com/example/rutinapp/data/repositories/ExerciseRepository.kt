package com.example.rutinapp.data.repositories

import com.example.rutinapp.data.daos.ExerciseDao
import com.example.rutinapp.data.daos.ExerciseEntity
import com.example.rutinapp.data.daos.ExerciseToExerciseDao
import com.example.rutinapp.data.daos.ExerciseToExerciseEntity
import com.example.rutinapp.data.models.ExerciseModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


fun ExerciseEntity.toModel() = ExerciseModel(
    this.exerciseId.toString(),
    this.exerciseName,
    this.exerciseDescription,
    this.targetedBodyPart,
    emptyList()
)

fun ExerciseModel.toEntity() = ExerciseEntity(
    this.id.toIntOrNull() ?: 0,
    this.name,
    this.description,
    this.targetedBodyPart
)

class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao, private val exerciseToExerciseDao: ExerciseToExerciseDao
) {

    val allExercises: Flow<List<ExerciseEntity>> = exerciseDao.getAllAsFlow()

    suspend fun getRelatedExercises(id: String): List<ExerciseEntity> {
        val relatedOnes = exerciseToExerciseDao.getRelatedExercises(id.toInt())

        val result = mutableListOf<ExerciseEntity>()

        if (relatedOnes.isNotEmpty()) {
            val ids =
                relatedOnes.map { if (it.exercise1Id == id.toInt()) it.exercise2Id else it.exercise1Id }
            ids.distinct().forEach { result.add(exerciseDao.getById(it)) }
        }

        return result
    }

    suspend fun getExercise(id: String): ExerciseEntity {
        return exerciseDao.getById(id.toInt())
    }

    suspend fun addExercise(exercise: ExerciseEntity) {
        exerciseDao.insert(
            ExerciseEntity(
                exerciseName = exercise.exerciseName,
                exerciseDescription = exercise.exerciseDescription,
                targetedBodyPart = exercise.targetedBodyPart
            )
        )
    }

    suspend fun relateExercises(exercise1: ExerciseEntity, exercise2: ExerciseEntity) {
        exerciseToExerciseDao.insert(
            ExerciseToExerciseEntity(
                exercise1Id = exercise1.exerciseId,
                exercise2Id = exercise2.exerciseId
            )
        )
    }

    suspend fun unRelateExercises(exercise1: ExerciseEntity, exercise2: ExerciseEntity) {
        exerciseToExerciseDao.delete(
            ExerciseToExerciseEntity(
                exercise1Id = exercise1.exerciseId,
                exercise2Id = exercise2.exerciseId
            )
        )
    }

    suspend fun updateExercise(exerciseToUpdate: ExerciseEntity) {
        exerciseDao.update(exerciseToUpdate)
    }

}

