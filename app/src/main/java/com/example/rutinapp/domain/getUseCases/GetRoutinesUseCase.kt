package com.example.rutinapp.domain.getUseCases

import com.example.rutinapp.newData.models.RoutineModel
import com.example.rutinapp.newData.repositories.ExerciseRepository
import com.example.rutinapp.newData.repositories.RoutineRepository
import com.example.rutinapp.newData.repositories.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRoutinesUseCase @Inject constructor(
    private val routinesRepository: RoutineRepository,
    private val exerciseRepository: ExerciseRepository
) {
    operator fun invoke(): Flow<List<RoutineModel>> {
        return routinesRepository.routines.map { items ->
            items.map { routineEntity ->
                routineEntity.toModel().apply {
                    exercises = routinesRepository.getExercisesForRoutine(routineEntity.routineId.toLong())
                        .map { it.toModel() }.toMutableList()

                    exercises.forEach {
                        it.equivalentExercises =
                            exerciseRepository.getRelatedExercises(it.id).map { it.toModel() }
                    }

                }
            }
        }
    }
}