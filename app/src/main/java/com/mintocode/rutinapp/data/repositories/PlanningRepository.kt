package com.mintocode.rutinapp.data.repositories

import com.mintocode.rutinapp.data.daos.PlanningDao
import com.mintocode.rutinapp.data.daos.PlanningEntity
import com.mintocode.rutinapp.utils.toSimpleDate
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class PlanningRepository @Inject constructor(
    private val planningDao: PlanningDao
) {

    val allPlannings = planningDao.getPlannings()

    suspend fun insert(planning: PlanningEntity) {
        planningDao.insertPlanning(planning)
    }

    suspend fun delete(planning: PlanningEntity) {
        planningDao.deletePlanning(planning)
    }

    suspend fun update(planning: PlanningEntity) {
        planningDao.updatePlanning(planning)
    }

    fun getTodaysPlanning(): Flow<PlanningEntity?> {
        return planningDao.getPlanningOf(Date().toSimpleDate().time)
    }
}
