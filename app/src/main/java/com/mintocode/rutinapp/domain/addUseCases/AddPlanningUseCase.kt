package com.mintocode.rutinapp.domain.addUseCases

import com.mintocode.rutinapp.data.models.PlanningModel
import com.mintocode.rutinapp.data.repositories.PlanningRepository
import javax.inject.Inject

class AddPlanningUseCase @Inject constructor(private val planningRepository: PlanningRepository) {

    suspend operator fun invoke(planning: PlanningModel) {
        planningRepository.insert(planning.toEntity())
    }

}