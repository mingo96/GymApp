package com.example.rutinapp.data.repositories

import com.example.rutinapp.data.daos.PlanningDao
import com.example.rutinapp.data.daos.PlanningEntity
import com.example.rutinapp.utils.toSimpleDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
