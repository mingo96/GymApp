package com.mintocode.rutinapp.domain.deleteUseCases

import com.mintocode.rutinapp.data.models.WorkoutModel
import com.mintocode.rutinapp.data.repositories.WorkoutRepository
import com.mintocode.rutinapp.data.repositories.toEntity
import javax.inject.Inject

class DeleteWorkoutUseCase @Inject constructor(val workoutRepository: WorkoutRepository) {

    suspend operator fun invoke(workoutModel: WorkoutModel) {
        workoutRepository.deleteWorkout(workoutModel.toEntity())
    }


}