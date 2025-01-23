package com.mintocode.rutinapp.domain.addUseCases

import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.repositories.ExerciseRepository
import com.mintocode.rutinapp.data.repositories.toEntity
import javax.inject.Inject

class AddExerciseUseCase @Inject constructor(private val exerciseRepository: ExerciseRepository) {

    suspend operator fun invoke(exerciseModel: ExerciseModel) {
        exerciseRepository.addExercise(exerciseModel.toEntity())
    }

}