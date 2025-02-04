package com.mintocode.rutinapp.domain.updateUseCases

import com.mintocode.rutinapp.data.daos.RoutineExerciseEntity
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.data.repositories.ExerciseRepository
import com.mintocode.rutinapp.data.repositories.RoutineRepository
import javax.inject.Inject

class UpdateRoutineUseCase @Inject constructor(private val routineRepository: RoutineRepository, private val exerciseRepository: ExerciseRepository) {
    suspend operator fun invoke(routine : RoutineModel){
        routineRepository.updateRoutine(routine.toEntity())
    }
}