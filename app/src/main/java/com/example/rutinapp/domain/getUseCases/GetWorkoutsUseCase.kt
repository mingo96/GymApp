package com.example.rutinapp.domain.getUseCases

import android.util.Log
import com.example.rutinapp.data.models.WorkoutModel
import com.example.rutinapp.data.repositories.ExerciseRepository
import com.example.rutinapp.data.repositories.WorkoutRepository
import com.example.rutinapp.data.repositories.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetWorkoutsUseCase @Inject constructor(private val workoutRepository: WorkoutRepository) {

    operator fun invoke(): Flow<List<WorkoutModel>> {
        return workoutRepository.workOuts.map {

            it.map { workoutEntity ->
                val workoutModel = workoutEntity.workOut.toModel()

                workoutModel.baseRoutine =
                    workoutRepository.getWorkoutsRoutine(workoutModel.id)?.toModel()

                val response = workoutRepository.getExercisesOfWorkout(workoutModel.id)

                val result = response.map { exercise ->

                    val model = exercise.toModel()

                    val sets = workoutEntity.sets.filter { it.exerciseDoneId == exercise.exerciseId }.map { set ->
                        val setModel = set.toModel()

                        setModel.exercise = model
                        setModel.workoutDone = workoutModel.copy(exercisesAndSets = mutableListOf())

                        setModel
                    }.toMutableList()

                    Pair(model, sets)
                }

                workoutModel.exercisesAndSets = result.sortedBy { it.second.map { it.date }.max() }.toMutableList()

                workoutModel
            }.sortedBy { it.date }.reversed()
        }
    }

}