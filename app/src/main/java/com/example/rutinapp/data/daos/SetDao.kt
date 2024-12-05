package com.example.rutinapp.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Entity(
    foreignKeys = [ForeignKey(
        entity = ExerciseEntity::class,
        parentColumns = ["exerciseId"],
        childColumns = ["exerciseDoneId"],
        onDelete = ForeignKey.CASCADE
    )]
)

data class SetEntity(
    @PrimaryKey(autoGenerate = true) val setId: Int,
    val exerciseDoneId: Int,
    val weight: Double,
    val reps: Int,
    val date: String,
    var observations: String
)

@Dao
interface SetDao {

    @Query("SELECT * FROM SetEntity")
    fun getAll(): Flow<List<SetEntity>>

    @Insert
    suspend fun addSet(set: SetEntity)

    @Delete
    suspend fun deleteSet(set: SetEntity)

    @Update
    suspend fun updateSet(set: SetEntity)

}