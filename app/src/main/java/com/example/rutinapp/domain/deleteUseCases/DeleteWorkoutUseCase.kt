package com.example.rutinapp.domain.deleteUseCases

import com.example.rutinapp.data.models.WorkoutModel
import com.example.rutinapp.data.repositories.WorkoutRepository
import com.example.rutinapp.data.repositories.toEntity
import javax.inject.Inject

class DeleteWorkoutUseCase @Inject constructor(val workoutRepository: WorkoutRepository) {

    suspend operator fun invoke(workoutModel: WorkoutModel) {
        workoutRepository.deleteWorkout(workoutModel.toEntity())
    }


}