package com.mintocode.rutinapp.domain.addUseCases

import com.mintocode.rutinapp.data.models.WorkoutModel
import com.mintocode.rutinapp.data.repositories.WorkoutRepository
import com.mintocode.rutinapp.data.repositories.toEntity
import javax.inject.Inject

class AddWorkoutUseCase @Inject constructor(private val workoutRepository: WorkoutRepository) {

    suspend operator fun invoke(workout: WorkoutModel): Int {
        val id = workoutRepository.addWorkout(workout.toEntity())
        if (workout.baseRoutine != null) {
            workoutRepository.addWorkoutRoutineRelation(
                workout.toEntity(), workout.baseRoutine!!.toEntity()
            )
        }
        return id
    }

}