package com.example.rutinapp.data.repositories

import com.example.rutinapp.data.daos.ExerciseEntity
import com.example.rutinapp.data.daos.SetDao
import com.example.rutinapp.data.daos.SetEntity
import javax.inject.Inject

class SetRepository @Inject constructor(private val setDao: SetDao) {

    suspend fun getSetsOfExercise(exerciseEntity: ExerciseEntity): List<SetEntity> {
        return setDao.getByExerciseId(exerciseEntity.exerciseId)
    }

    suspend fun insertSet(set: SetEntity): Int {

        return setDao.addSet(set).toInt()

    }

    suspend fun deleteSet(set: SetEntity) {
        setDao.deleteSet(set.date)
    }

    suspend fun updateSet(set: SetEntity) {
        setDao.updateSet(set)
    }

    suspend fun numberOfSetsOfExercise(id:Int): Int {
        return setDao.numberOfSetsOfExercise(id)
    }

}