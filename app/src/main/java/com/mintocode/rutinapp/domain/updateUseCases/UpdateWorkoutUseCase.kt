package com.mintocode.rutinapp.domain.updateUseCases

import com.mintocode.rutinapp.data.models.WorkoutModel
import com.mintocode.rutinapp.data.repositories.WorkoutRepository
import javax.inject.Inject

class UpdateWorkoutUseCase @Inject constructor(private val workoutRepository: WorkoutRepository) {

    suspend operator fun invoke(workoutModel: WorkoutModel) {
        workoutRepository.updateWorkout(workoutModel.toEntity())
    }


}