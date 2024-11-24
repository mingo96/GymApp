package com.example.rutinapp.domain.addUseCases

import com.example.rutinapp.newData.repositories.ExerciseRepository
import javax.inject.Inject

class AddExercisesRelationUseCase @Inject constructor(private val exerciseRepository: ExerciseRepository) {

    suspend operator fun invoke(exerciseId: Long, relatedExerciseId: Long) {
        exerciseRepository.relateExercises(exerciseId.toString(), relatedExerciseId = relatedExerciseId.toString())
    }

}