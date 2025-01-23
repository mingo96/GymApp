package com.mintocode.rutinapp.domain.getUseCases

import com.mintocode.rutinapp.data.models.WorkoutModel
import com.mintocode.rutinapp.data.repositories.ExerciseRepository
import com.mintocode.rutinapp.data.repositories.WorkoutRepository
import com.mintocode.rutinapp.data.repositories.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetWorkoutsUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository
) {

    operator fun invoke(): Flow<List<WorkoutModel>> {
        return workoutRepository.workOuts.map {

            it.map { workoutEntity ->
                val workoutModel = workoutEntity.workOut.toModel()

                workoutModel.baseRoutine =
                    workoutRepository.getWorkoutsRoutine(workoutModel.id)?.toModel()

                val response = workoutRepository.getExercisesOfWorkout(workoutModel.id)

                val result = response.map { exercise ->

                    val model = exercise.toModel().apply {
                        equivalentExercises =
                            exerciseRepository.getRelatedExercises(this.id).map { it.toModel() }
                    }

                    val sets =
                        workoutEntity.sets.filter { it.exerciseDoneId == exercise.exerciseId }
                            .map { set ->
                                val setModel = set.toModel()

                                setModel.exercise = model
                                setModel.workoutDone =
                                    workoutModel.copy(exercisesAndSets = mutableListOf())

                                setModel
                            }.toMutableList()

                    Pair(model, sets)
                }

                workoutModel.exercisesAndSets =
                    result.sortedBy { it.second.map { it.date }.max() }.toMutableList()

                workoutModel
            }.sortedBy { it.date }.reversed()
        }
    }

}