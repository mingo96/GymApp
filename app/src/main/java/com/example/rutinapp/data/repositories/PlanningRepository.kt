package com.example.rutinapp.data.repositories

import com.example.rutinapp.data.daos.PlanningDao
import com.example.rutinapp.data.daos.PlanningEntity
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


}