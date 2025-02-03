package com.mintocode.rutinapp.domain.getUseCases

import com.mintocode.rutinapp.data.models.PlanningModel
import com.mintocode.rutinapp.data.repositories.PlanningRepository
import com.mintocode.rutinapp.data.repositories.RoutineRepository
import com.mintocode.rutinapp.utils.toSimpleDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

const val DAY_IN_MILLIS = 86400000L

class GetPlanningsUseCase @Inject constructor(
    private val planningRepository: PlanningRepository,
    private val routineRepository: RoutineRepository
) {

    operator fun invoke(firstToSecondDate: Pair<Date, Date>): Flow<List<PlanningModel>> {
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
            }.filter { it.date.time in firstToSecondDate.first.time..firstToSecondDate.second.time }

            val addedValues = mutableListOf<PlanningModel>()

            val rangeOfDates = firstToSecondDate.first.time..firstToSecondDate.second.time step DAY_IN_MILLIS

            for (i in rangeOfDates) {
                if (plannings.none { planning -> planning.date.toSimpleDate().time == Date(i).toSimpleDate().time }) {
                    addedValues += PlanningModel(
                        id = 0,
                        date = Date(i)
                    )
                    println(addedValues.last().date.time)
                }
            }

            (plannings + addedValues).sortedBy { it.date }

        }
    }


}