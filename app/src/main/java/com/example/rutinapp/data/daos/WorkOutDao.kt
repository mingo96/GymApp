package com.example.rutinapp.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Entity
data class WorkOutEntity(
    @PrimaryKey(autoGenerate = true) val workOutId: Int,
    val date: Long,
    var title: String,
)

@Dao
interface WorkOutDao {

    @Query("SELECT * FROM WorkOutEntity ORDER BY date LIMIT 10")
    fun get10MoreRecent(): Flow<List<WorkOutEntity>>

    @Insert
    suspend fun addWorkOut(training: WorkOutEntity)

    @Query("SELECT * FROM WorkOutEntity WHERE date = :date")
    suspend fun getByDate(date: Long): WorkOutEntity

    @Delete
    suspend fun delete(workout: WorkOutEntity)

}