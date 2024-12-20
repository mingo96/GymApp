package com.example.rutinapp.domain.getUseCases

import com.example.rutinapp.data.repositories.WorkoutRepository
import javax.inject.Inject

class GetWorkoutIdByDateUseCase @Inject constructor(private val workoutRepository: WorkoutRepository) {

    suspend operator fun invoke(date: String): Int {
        return workoutRepository.getWorkOutFromDate(date).workOutId

    }

}