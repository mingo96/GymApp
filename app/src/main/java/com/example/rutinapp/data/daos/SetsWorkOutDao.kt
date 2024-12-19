package com.example.rutinapp.data.daos

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Entity(
    primaryKeys = ["workOutId", "setId"], foreignKeys = [ForeignKey(
        entity = WorkOutEntity::class,
        parentColumns = ["workOutId"],
        childColumns = ["workOutId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = SetEntity::class,
        parentColumns = ["setId"],
        childColumns = ["setId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SetsWorkoutEntity(
    val workOutId: Int,
    val setId: Int,
)


@Dao
interface SetsWorkOutDao {

    @Query("SELECT * FROM SetsWorkoutEntity")
    fun getAll(): Flow<List<SetsWorkoutEntity>>

    @Query("SELECT * FROM SetsWorkoutEntity WHERE workOutId = :id")
    fun getByWorOutId(id: Int): List<SetsWorkoutEntity>

    @Query("SELECT * FROM SetsWorkoutEntity WHERE `setId` = :id")
    fun getBySetId(id: Int): SetsWorkoutEntity

    @Insert
    suspend fun addSetWorkOut(set: SetsWorkoutEntity)

}