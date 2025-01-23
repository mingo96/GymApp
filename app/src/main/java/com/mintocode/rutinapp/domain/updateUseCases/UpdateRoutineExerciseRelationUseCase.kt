package com.mintocode.rutinapp.domain.updateUseCases

import com.mintocode.rutinapp.data.daos.RoutineExerciseEntity
import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.data.repositories.RoutineRepository
import javax.inject.Inject

class UpdateRoutineExerciseRelationUseCase @Inject constructor(private val routineRepository: RoutineRepository) {

    suspend operator fun invoke(
        routine: RoutineModel, exercise: ExerciseModel
    ) {
        val relation = RoutineExerciseEntity(
            routine.id,
            exercise.id.toInt(),
            exercise.setsAndReps,
            exercise.observations
        )
        routineRepository.updateRoutineExerciseRelation(relation)
    }

}