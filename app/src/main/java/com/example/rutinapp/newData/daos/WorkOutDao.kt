package com.example.rutinapp.newData.daos

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Entity
data class WorkOutEntity(
    @PrimaryKey(autoGenerate = true) val workOutId: Int,
    val date: String,
    var title: String,
)

@Dao
interface WorkOutDao {

    @Query("SELECT * FROM WorkOutEntity")
    fun getAll(): Flow<List<WorkOutEntity>>

    @Insert
    suspend fun addWorkOut(training: WorkOutEntity)

}