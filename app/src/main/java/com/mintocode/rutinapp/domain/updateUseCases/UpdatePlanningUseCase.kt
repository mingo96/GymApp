package com.mintocode.rutinapp.domain.updateUseCases

import com.mintocode.rutinapp.data.models.PlanningModel
import com.mintocode.rutinapp.data.repositories.PlanningRepository
import javax.inject.Inject

class UpdatePlanningUseCase @Inject constructor(private val planningRepository: PlanningRepository) {

    suspend operator fun invoke(planning: PlanningModel) {
        planningRepository.update(planning.toEntity())
    }

}