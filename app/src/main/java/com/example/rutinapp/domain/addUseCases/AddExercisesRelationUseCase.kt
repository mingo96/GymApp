package com.example.rutinapp.domain.addUseCases

import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.repositories.ExerciseRepository
import com.example.rutinapp.data.repositories.toEntity
import javax.inject.Inject

class AddExercisesRelationUseCase @Inject constructor(private val exerciseRepository: ExerciseRepository) {

    suspend operator fun invoke(exercise1: ExerciseModel, exercise2: ExerciseModel) {
        exerciseRepository.relateExercises(exercise1.toEntity(), exercise2.toEntity())
    }

}