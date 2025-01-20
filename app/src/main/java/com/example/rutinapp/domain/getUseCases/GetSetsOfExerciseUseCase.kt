package com.example.rutinapp.domain.getUseCases

import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.models.SetModel
import com.example.rutinapp.data.repositories.SetRepository
import com.example.rutinapp.data.repositories.toEntity
import javax.inject.Inject

class GetSetsOfExerciseUseCase @Inject constructor(private val setRepository: SetRepository) {

    suspend operator fun invoke(exercise: ExerciseModel): List<SetModel> {

        return setRepository.getSetsOfExercise(exercise.toEntity()).map {
            it.toModel()
        }

    }
}