package com.mintocode.rutinapp.domain.getUseCases

import com.mintocode.rutinapp.data.repositories.WorkoutRepository
import javax.inject.Inject

class GetWorkoutIdByDateUseCase @Inject constructor(private val workoutRepository: WorkoutRepository) {

    suspend operator fun invoke(date: Long): Int {
        return workoutRepository.getWorkOutFromDate(date).workOutId

    }

}