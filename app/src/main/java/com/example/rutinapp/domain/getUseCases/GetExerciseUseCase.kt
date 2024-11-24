package com.example.rutinapp.domain.getUseCases

import android.util.Log
import com.example.rutinapp.newData.models.ExerciseModel
import com.example.rutinapp.newData.repositories.ExerciseRepository
import com.example.rutinapp.newData.repositories.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetExerciseUseCase @Inject constructor(private val exerciseRepository: ExerciseRepository) {

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