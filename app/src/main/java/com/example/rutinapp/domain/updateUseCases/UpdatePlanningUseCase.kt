package com.example.rutinapp.domain.updateUseCases

import com.example.rutinapp.data.models.PlanningModel
import com.example.rutinapp.data.repositories.PlanningRepository
import javax.inject.Inject

class UpdatePlanningUseCase @Inject constructor(private val planningRepository: PlanningRepository) {

    suspend operator fun invoke(planning: PlanningModel) {
        planningRepository.update(planning.toEntity())
    }

}