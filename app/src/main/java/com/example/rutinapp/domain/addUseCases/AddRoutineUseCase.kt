package com.example.rutinapp.domain.addUseCases

import com.example.rutinapp.newData.models.RoutineModel
import com.example.rutinapp.newData.repositories.RoutineRepository
import com.example.rutinapp.newData.repositories.toEntity
import javax.inject.Inject

class AddRoutineUseCase @Inject constructor(private val routineRepository: RoutineRepository){

    suspend operator fun invoke(routine : RoutineModel){
        routineRepository.addRoutine(routine.toEntity())
    }

}