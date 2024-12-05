package com.example.rutinapp.domain.addUseCases

import com.example.rutinapp.data.models.RoutineModel
import com.example.rutinapp.data.repositories.RoutineRepository
import com.example.rutinapp.data.repositories.toEntity
import javax.inject.Inject

class AddRoutineUseCase @Inject constructor(private val routineRepository: RoutineRepository){

    suspend operator fun invoke(routine : RoutineModel){
        routineRepository.addRoutine(routine.toEntity())
    }

}