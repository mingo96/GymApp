package com.mintocode.rutinapp.domain.getUseCases

import android.util.Log
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.data.repositories.ExerciseRepository
import com.mintocode.rutinapp.data.repositories.RoutineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRoutinesUseCase @Inject constructor(
    private val routinesRepository: RoutineRepository,
    private val exerciseRepository: ExerciseRepository
) {
    operator fun invoke(): Flow<List<RoutineModel>> {
        return routinesRepository.routines.map { items ->
            val result = items.map { routineWithExercises ->
                val routine = routineWithExercises.routine.toModel()

                routine.apply {

                    exercises =
                        routineWithExercises.exerciseRelations.map { exerciseRepository.getExercise(it.exerciseId.toString()).toModel() }.toMutableList()

                    exercises.forEach {
                        it.equivalentExercises =
                            exerciseRepository.getRelatedExercises(it.id).map { it.toModel() }
                    }

                    val orderOfIds =
                        routinesRepository.getExercisesOrder(routine.id.toLong())

                    exercises.forEach { exercise ->
                        val relation = orderOfIds.find { it.first == exercise.id.toInt() }!!
                        exercise.setsAndReps = relation.second
                        exercise.observations = relation.third
                    }
                }
            }
            Log.d("RoutinesTest", result.map { it.id }.joinToString(","))
            result
        }
    }
}