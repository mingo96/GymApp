package com.example.rutinapp.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Entity(
    indices = [Index("routineId")]
)
data class RoutineEntity(
    @PrimaryKey val routineId: Int,
    var name: String,
    var targetedBodyPart: String,
)

@Dao
interface RoutineDao {

    @Query("SELECT * FROM RoutineEntity")
    fun getAllAsFlow(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM RoutineEntity WHERE routineId = :routineId")
    suspend fun getFromId(routineId: Int) : RoutineEntity

    @Insert
    suspend fun addRoutine(routine: RoutineEntity):Long

    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)

}