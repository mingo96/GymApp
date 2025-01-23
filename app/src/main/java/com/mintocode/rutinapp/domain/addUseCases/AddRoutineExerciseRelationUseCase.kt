package com.mintocode.rutinapp.domain.addUseCases

import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.data.repositories.RoutineRepository
import javax.inject.Inject

class AddRoutineExerciseRelationUseCase @Inject constructor(private val routineRepository: RoutineRepository) {

    suspend operator fun invoke(routine: RoutineModel, exercise: ExerciseModel) {
        routineRepository.relateExerciseToRoutine(routine.id, exercise.id.toInt())
    }
}