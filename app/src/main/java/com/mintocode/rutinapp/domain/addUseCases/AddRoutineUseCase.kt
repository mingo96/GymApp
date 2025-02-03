package com.mintocode.rutinapp.domain.addUseCases

import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.data.repositories.RoutineRepository
import javax.inject.Inject

class AddRoutineUseCase @Inject constructor(private val routineRepository: RoutineRepository) {

    suspend operator fun invoke(routine: RoutineModel): Int {
        return routineRepository.addRoutine(routine.toEntity())
    }

}