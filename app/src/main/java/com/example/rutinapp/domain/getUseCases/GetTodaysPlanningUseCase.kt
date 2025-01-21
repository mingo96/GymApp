package com.example.rutinapp.domain.getUseCases

import com.example.rutinapp.data.models.PlanningModel
import com.example.rutinapp.data.repositories.PlanningRepository
import com.example.rutinapp.data.repositories.RoutineRepository
import com.example.rutinapp.data.repositories.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTodaysPlanningUseCase @Inject constructor(private val planningRepository: PlanningRepository, private val routineRepository: RoutineRepository) {

    operator fun invoke(): Flow<PlanningModel?> {
        return planningRepository.getTodaysPlanning().map {

            val result =it?.toModel()
            if (result!= null){
                if (it.routineId!= null){
                    result.statedRoutine = routineRepository.getRoutineById(it.routineId!!).toModel()
                }else if(it.bodyPart != null){
                    result.statedBodyPart = it.bodyPart
                }
            }

            result
        }
    }

}