package com.mintocode.rutinapp.domain.deleteUseCases

import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.repositories.ExerciseRepository
import javax.inject.Inject

/**
 * Deletes an exercise from the local database.
 *
 * @property exerciseRepository Repository handling exercise persistence
 */
class DeleteExerciseUseCase @Inject constructor(private val exerciseRepository: ExerciseRepository) {

    /**
     * Deletes the given exercise.
     *
     * @param exerciseModel The exercise to delete
     */
    suspend operator fun invoke(exerciseModel: ExerciseModel) {
        exerciseRepository.deleteExercise(exerciseModel.toEntity())
    }
}
