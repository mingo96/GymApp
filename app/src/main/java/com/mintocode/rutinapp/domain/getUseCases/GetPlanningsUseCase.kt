package com.mintocode.rutinapp.domain.getUseCases

import com.mintocode.rutinapp.data.models.PlanningModel
import com.mintocode.rutinapp.data.repositories.PlanningRepository
import com.mintocode.rutinapp.data.repositories.RoutineRepository
import com.mintocode.rutinapp.data.repositories.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class GetPlanningsUseCase @Inject constructor(
    private val planningRepository: PlanningRepository,
    private val routineRepository: RoutineRepository
) {

    operator fun invoke(): Flow<List<PlanningModel>> {
        return planningRepository.allPlannings.map {
            val plannings = it.map { planning ->
                val newItem = planning.toModel()

                if (planning.bodyPart != null) {
                    newItem.statedBodyPart = planning.bodyPart
                } else if (planning.routineId != null) {
                    newItem.statedRoutine =
                        routineRepository.getRoutineById(planning.routineId!!).toModel()
                }

                newItem
            }

            val actualDate = Date()
            val dayOfWeek = actualDate.day - 1
            val today = actualDate.date
            val rangeOfDates = -dayOfWeek + today..13 - dayOfWeek + today
            val addedValues = mutableListOf<PlanningModel>()

            for (i in rangeOfDates) {
                if (plannings.none { planning -> planning.date.date == i }) {
                    addedValues += PlanningModel(
                        id = 0,
                        date = Date(
                            actualDate.year,
                            if (i > 0) actualDate.month else actualDate.month - 1,
                            if (i > 0) i else i + 30
                        )
                    )
                }
            }

            (plannings + addedValues).sortedBy { it.date }

        }
    }


}