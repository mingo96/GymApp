package com.mintocode.rutinapp.domain.deleteUseCases

import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.data.repositories.RoutineRepository
import javax.inject.Inject

class DeleteRoutineExerciseRelationUseCase @Inject constructor(private val routineRepository: RoutineRepository) {

    suspend operator fun invoke(routineModel: RoutineModel, exerciseModel: ExerciseModel) {
        routineRepository.deleteRoutineExerciseRelation(routineModel.id, exerciseModel.id.toInt())
    }

}