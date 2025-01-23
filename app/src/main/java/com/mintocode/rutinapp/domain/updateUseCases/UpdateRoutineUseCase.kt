package com.mintocode.rutinapp.domain.updateUseCases

import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.data.repositories.RoutineRepository
import com.mintocode.rutinapp.data.repositories.toEntity
import javax.inject.Inject

class UpdateRoutineUseCase @Inject constructor(private val routineRepository: RoutineRepository) {
    suspend operator fun invoke(routine : RoutineModel){

        routineRepository.updateRoutine(routine.toEntity())
    }
}