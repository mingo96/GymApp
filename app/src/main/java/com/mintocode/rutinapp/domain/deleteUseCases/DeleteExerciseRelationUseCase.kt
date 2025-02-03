package com.mintocode.rutinapp.domain.deleteUseCases

import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.repositories.ExerciseRepository
import javax.inject.Inject

class DeleteExerciseRelationUseCase @Inject constructor(private val exerciseRepository: ExerciseRepository) {

    suspend operator fun invoke(exercise1: ExerciseModel, exercise2: ExerciseModel) {
        exerciseRepository.unRelateExercises(exercise1.toEntity(), exercise2.toEntity())
    }

}