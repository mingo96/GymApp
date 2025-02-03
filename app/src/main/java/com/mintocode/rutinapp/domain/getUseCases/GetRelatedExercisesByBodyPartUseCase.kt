package com.mintocode.rutinapp.domain.getUseCases

import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.repositories.ExerciseRepository
import com.mintocode.rutinapp.data.repositories.SetRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetRelatedExercisesByBodyPartUseCase @Inject constructor(
    private val exerciseRepository: ExerciseRepository, private val setRepository: SetRepository
) {

    suspend operator fun invoke(bodyPart: String): List<ExerciseModel> {
        val exercises = exerciseRepository.allExercises.first()

        val selectedExercises = exercises.filter { it.targetedBodyPart.contains(bodyPart, true) }
            .map { setRepository.numberOfSetsOfExercise(it.exerciseId) to it }.toList()

        return selectedExercises.sortedBy { it.first }.reversed().take(5).map {
            it.second.toModel().apply {
                equivalentExercises =
                    exerciseRepository.getRelatedExercises(id).map { it.toModel() }
            }
        }

    }

}