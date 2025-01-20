package com.example.rutinapp.domain.updateUseCases

import com.example.rutinapp.data.models.RoutineModel
import com.example.rutinapp.data.repositories.RoutineRepository
import com.example.rutinapp.data.repositories.toEntity
import javax.inject.Inject

class UpdateRoutineUseCase @Inject constructor(private val routineRepository: RoutineRepository) {
    suspend operator fun invoke(routine : RoutineModel){

        routineRepository.updateRoutine(routine.toEntity())
    }
}