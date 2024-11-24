package com.example.rutinapp.domain.addUseCases

import com.example.rutinapp.newData.models.ExerciseModel
import com.example.rutinapp.newData.repositories.ExerciseRepository
import com.example.rutinapp.newData.repositories.toEntity
import javax.inject.Inject

class AddExerciseUseCase @Inject constructor(private val exerciseRepository: ExerciseRepository) {

    suspend operator fun invoke(exerciseModel: ExerciseModel){
        exerciseRepository.addExercise(exerciseModel.toEntity())
    }

}