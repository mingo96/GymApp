package com.example.rutinapp.domain.getUseCases

import com.example.rutinapp.data.models.WorkoutModel
import com.example.rutinapp.data.repositories.WorkoutRepository
import com.example.rutinapp.data.repositories.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetWorkoutsUseCase @Inject constructor(private val workoutRepository: WorkoutRepository) {

    operator fun invoke(): Flow<List<WorkoutModel>> {
        return workoutRepository.workOuts.map {
            it.map { workoutEntity ->
                val workoutModel = workoutEntity.toModel()

                workoutModel.baseRoutine =
                    workoutRepository.getWorkoutsRoutine(workoutModel.id)?.toModel()

                val response = workoutRepository.getExercisesOfWorkout(workoutModel.id)

                val result = response.map { info ->

                    val model = info.first.toModel()

                    val sets = info.second.map {it.toModel()}

                    Pair(model, sets)
                }
                workoutModel.exercisesAndSets = result
                return@map workoutModel
            }
        }
    }

}