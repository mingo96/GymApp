package com.example.rutinapp.domain.addUseCases

import com.example.rutinapp.data.models.WorkoutModel
import com.example.rutinapp.data.repositories.WorkoutRepository
import com.example.rutinapp.data.repositories.toEntity
import kotlinx.coroutines.delay
import javax.inject.Inject

class AddWorkoutUseCase @Inject constructor(private val workoutRepository: WorkoutRepository) {

    suspend operator fun invoke(workout: WorkoutModel):Int {
        val id = workoutRepository.addWorkout(workout.toEntity())
        if (workout.baseRoutine != null) {
            workoutRepository.addWorkoutRoutineRelation(
                workout.toEntity(), workout.baseRoutine!!.toEntity()
            )
        }
        return id
    }

}