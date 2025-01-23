package com.mintocode.rutinapp.domain.addUseCases

import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.repositories.ExerciseRepository
import com.mintocode.rutinapp.data.repositories.toEntity
import javax.inject.Inject

class AddExercisesRelationUseCase @Inject constructor(private val exerciseRepository: ExerciseRepository) {

    suspend operator fun invoke(exercise1: ExerciseModel, exercise2: ExerciseModel) {
        exerciseRepository.relateExercises(exercise1.toEntity(), exercise2.toEntity())
    }

}