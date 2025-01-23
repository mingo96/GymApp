package com.mintocode.rutinapp.domain.updateUseCases

import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.repositories.ExerciseRepository
import com.mintocode.rutinapp.data.repositories.toEntity
import javax.inject.Inject

class UpdateExerciseUseCase @Inject constructor(private val exerciseRepository: ExerciseRepository) {

    suspend operator fun invoke(exercise: ExerciseModel) {
        exerciseRepository.updateExercise(exercise.toEntity())
    }


}