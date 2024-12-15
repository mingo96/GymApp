package com.example.rutinapp.domain.updateUseCases

import com.example.rutinapp.data.daos.RoutineExerciseEntity
import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.models.RoutineModel
import com.example.rutinapp.data.repositories.RoutineRepository
import javax.inject.Inject

class UpdateRoutineExerciseRelationUseCase @Inject constructor(private val routineRepository: RoutineRepository) {

    suspend operator fun invoke(
        routine: RoutineModel, exercise: ExerciseModel, setsAndReps: String, observations: String
    ) {
        val relation = RoutineExerciseEntity(routine.id, exercise.id.toInt(), setsAndReps, observations)
        routineRepository.updateRoutineExerciseRelation(relation)
    }

}