package com.example.rutinapp.domain.deleteUseCases

import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.repositories.ExerciseRepository
import com.example.rutinapp.data.repositories.toEntity
import javax.inject.Inject

class DeleteExerciseRelationUseCase @Inject constructor(private val exerciseRepository: ExerciseRepository){

    suspend operator fun invoke(exercise1:ExerciseModel, exercise2:ExerciseModel){
        exerciseRepository.unRelateExercises(exercise1.toEntity(), exercise2.toEntity())
    }

}