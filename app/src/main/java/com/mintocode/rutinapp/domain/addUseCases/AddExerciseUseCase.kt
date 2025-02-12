package com.mintocode.rutinapp.domain.addUseCases

import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.repositories.ExerciseRepository
import javax.inject.Inject

class AddExerciseUseCase @Inject constructor(private val exerciseRepository: ExerciseRepository) {

    suspend operator fun invoke(exerciseModel: ExerciseModel) : Int{
        return exerciseRepository.addExercise(exerciseModel.toEntity()).toInt()
    }

    suspend operator fun invoke(exerciseModel: ExerciseModel, idsOfRelatedExercises : List<Long>) {
        exerciseRepository.addExercise(exerciseModel.toEntity(), idsOfRelatedExercises)
    }

}