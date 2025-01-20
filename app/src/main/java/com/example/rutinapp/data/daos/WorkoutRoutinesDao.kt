package com.example.rutinapp.data.daos

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "WorkoutRoutineEntity",
    primaryKeys = ["workoutId", "routineId"],
    foreignKeys = [ForeignKey(
        entity = WorkOutEntity::class,
        parentColumns = ["workOutId"],
        childColumns = ["workoutId"],
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["routineId"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkoutRoutineEntity(
    val workoutId: Int,
    val routineId: Int
)

@Dao
interface WorkoutRoutinesDao {

    @Query("SELECT * FROM WorkoutRoutineEntity")
    fun getAll(): Flow<List<WorkoutRoutineEntity>>

    @Query("SELECT * FROM WorkoutRoutineEntity WHERE workoutId = :workoutId")
    suspend fun getByWorkoutId(workoutId: Int): WorkoutRoutineEntity?

    @Insert
    suspend fun insert(workoutRoutineEntity: WorkoutRoutineEntity)

}