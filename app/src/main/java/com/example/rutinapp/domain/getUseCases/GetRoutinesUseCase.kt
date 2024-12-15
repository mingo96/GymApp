package com.example.rutinapp.domain.getUseCases

import com.example.rutinapp.data.models.RoutineModel
import com.example.rutinapp.data.repositories.ExerciseRepository
import com.example.rutinapp.data.repositories.RoutineRepository
import com.example.rutinapp.data.repositories.toModel
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
                    exercises =
                        routinesRepository.getExercisesForRoutine(routineEntity.routineId.toLong())
                            .map { it.toModel() }.toMutableList()

                    exercises.forEach {
                        it.equivalentExercises =
                            exerciseRepository.getRelatedExercises(it.id).map { it.toModel() }
                    }

                    val orderOfIds = routinesRepository.getExercisesOrder(routineEntity.routineId.toLong())

                    exercises.forEach {exercise ->
                        val relation = orderOfIds.find { it.first == exercise.id.toInt() }!!
                        exercise.setsAndReps =relation.second
                        exercise.observations = relation.third
                    }
                }
            }
        }
    }
}