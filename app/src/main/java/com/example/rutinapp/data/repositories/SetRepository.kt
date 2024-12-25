package com.example.rutinapp.data.repositories

import com.example.rutinapp.data.daos.SetDao
import com.example.rutinapp.data.daos.SetEntity
import com.example.rutinapp.data.daos.WorkOutDao
import javax.inject.Inject

class SetRepository @Inject constructor(private val setDao: SetDao, private val workoutDao: WorkOutDao) {

    suspend fun insertSet(set: SetEntity):Int {

        return setDao.addSet(set).toInt()

    }

    suspend fun deleteSet(set: SetEntity) {
        setDao.deleteSet(set.date)
    }

    suspend fun updateSet(set: SetEntity) {
        setDao.updateSet(set)
    }

}