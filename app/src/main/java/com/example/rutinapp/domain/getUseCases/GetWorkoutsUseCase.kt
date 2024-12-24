package com.example.rutinapp.domain.getUseCases

import android.util.Log
import com.example.rutinapp.data.models.WorkoutModel
import com.example.rutinapp.data.repositories.ExerciseRepository
import com.example.rutinapp.data.repositories.WorkoutRepository
import com.example.rutinapp.data.repositories.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetWorkoutsUseCase @Inject constructor(private val workoutRepository: WorkoutRepository, private val exerciseRepository: ExerciseRepository) {

    operator fun invoke(): Flow<List<WorkoutModel>> {
        return workoutRepository.workOuts.map {

            it.map { workoutEntity ->
                val workoutModel = workoutEntity.toModel()

                workoutModel.baseRoutine =
                    workoutRepository.getWorkoutsRoutine(workoutModel.id)?.toModel()

                val response = workoutRepository.getExercisesOfWorkout(workoutModel.id)

                val result = response.map { info ->

                    val model = info.first.toModel()

                    val sets = info.second.map {it.toModel()}.toMutableList()

                    sets.forEach { it.exercise = model }

                    Pair(model, sets)
                }
                workoutModel.exercisesAndSets = result.sortedBy { it.second.map { it.date }.max() }.toMutableList()
                return@map workoutModel
            }.sortedBy { it.date }.reversed()
        }
    }

}