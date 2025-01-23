package com.mintocode.rutinapp.domain.getUseCases

import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.models.SetModel
import com.mintocode.rutinapp.data.repositories.SetRepository
import com.mintocode.rutinapp.data.repositories.toEntity
import javax.inject.Inject

class GetSetsOfExerciseUseCase @Inject constructor(private val setRepository: SetRepository) {

    suspend operator fun invoke(exercise: ExerciseModel): List<SetModel> {

        return setRepository.getSetsOfExercise(exercise.toEntity()).map {
            it.toModel()
        }

    }
}