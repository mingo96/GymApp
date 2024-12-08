package com.example.rutinapp.domain.deleteUseCases

import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.models.RoutineModel
import com.example.rutinapp.data.repositories.RoutineRepository
import com.example.rutinapp.data.repositories.toEntity
import javax.inject.Inject

class DeleteRoutineExerciseRelationUseCase @Inject constructor(private val routineRepository: RoutineRepository) {

    suspend operator fun invoke(routineModel: RoutineModel, exerciseModel: ExerciseModel) {
        routineRepository.deleteRoutineExerciseRelation(routineModel.id, exerciseModel.id.toInt(), routineModel.exercises.indexOf(exerciseModel))
    }

}