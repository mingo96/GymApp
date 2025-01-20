package com.example.rutinapp.domain.updateUseCases

import com.example.rutinapp.data.models.WorkoutModel
import com.example.rutinapp.data.repositories.WorkoutRepository
import com.example.rutinapp.data.repositories.toEntity
import javax.inject.Inject

class UpdateWorkoutUseCase @Inject constructor(private val workoutRepository: WorkoutRepository) {

    suspend operator fun invoke(workoutModel: WorkoutModel) {
        workoutRepository.updateWorkout(workoutModel.toEntity())
    }


}