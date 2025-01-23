package com.mintocode.rutinapp.domain.getUseCases

import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.repositories.ExerciseRepository
import com.mintocode.rutinapp.data.repositories.SetRepository
import com.mintocode.rutinapp.data.repositories.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetExercisesUseCase @Inject constructor(private val exerciseRepository: ExerciseRepository, private val setsRepository: SetRepository) {

    operator fun invoke(): Flow<List<ExerciseModel>> =
        exerciseRepository.allExercises.map { items ->
            val result = items.map {
                it.toModel().apply {
                    equivalentExercises =
                        exerciseRepository.getRelatedExercises(id).map { it.toModel() }
                } to setsRepository.numberOfSetsOfExercise(it.exerciseId)
            }

            result.sortedByDescending { it.second }.map { it.first }
        }

}