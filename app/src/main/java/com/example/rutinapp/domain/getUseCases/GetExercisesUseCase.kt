package com.example.rutinapp.domain.getUseCases

import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.repositories.ExerciseRepository
import com.example.rutinapp.data.repositories.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetExercisesUseCase @Inject constructor(private val exerciseRepository: ExerciseRepository) {

    operator fun invoke(): Flow<List<ExerciseModel>> =
        exerciseRepository.allExercises.map { items ->
            val result = items.map {
                it.toModel().apply {
                    equivalentExercises =
                        exerciseRepository.getRelatedExercises(id).map { it.toModel() }
                }
            }

            result
        }

}