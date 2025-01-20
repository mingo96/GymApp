package com.example.rutinapp.domain.addUseCases

import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.repositories.ExerciseRepository
import com.example.rutinapp.data.repositories.toEntity
import javax.inject.Inject

class AddExerciseUseCase @Inject constructor(private val exerciseRepository: ExerciseRepository) {

    suspend operator fun invoke(exerciseModel: ExerciseModel) {
        exerciseRepository.addExercise(exerciseModel.toEntity())
    }

}