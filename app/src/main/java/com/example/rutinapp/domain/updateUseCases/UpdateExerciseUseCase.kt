package com.example.rutinapp.domain.updateUseCases

import com.example.rutinapp.newData.models.ExerciseModel
import com.example.rutinapp.newData.repositories.ExerciseRepository
import com.example.rutinapp.newData.repositories.toEntity
import javax.inject.Inject

class UpdateExerciseUseCase @Inject constructor(private val exerciseRepository: ExerciseRepository) {

    suspend operator fun invoke(exercise: ExerciseModel) {
        exerciseRepository.updateExercise(exercise.toEntity())
    }


}