package com.example.rutinapp.newData.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Entity
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true) val routineId: Int,
    var name: String,
    var targetedBodyPart: String,
)

@Dao
interface RoutineDao {

    @Query("SELECT * FROM RoutineEntity")
    fun getAll(): Flow<List<RoutineEntity>>

    @Insert
    suspend fun addRoutine(routine: RoutineEntity)

    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)

}