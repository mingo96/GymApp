package com.example.rutinapp.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Entity(
    primaryKeys = ["routineId", "exerciseId"], foreignKeys = [ForeignKey(
        entity = ExerciseEntity::class,
        parentColumns = ["exerciseId"],
        childColumns = ["exerciseId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = RoutineEntity::class,
        parentColumns = ["routineId"],
        childColumns = ["routineId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("routineId"), Index("exerciseId")]
)
data class RoutineExerciseEntity(
    val routineId: Int, val exerciseId: Int, var statedSetsAndReps: String="0x0", var observations: String=""
)

@Dao
interface RoutineExerciseDao {

    @Query("SELECT * FROM RoutineExerciseEntity")
    fun getAllRoutineExercises(): Flow<List<RoutineExerciseEntity>>

    @Query("SELECT * FROM RoutineExerciseEntity WHERE routineId = :routineId")
    fun getRoutineExercisesByRoutineId(routineId: Long): Flow<List<RoutineExerciseEntity>>

    @Query("SELECT * FROM RoutineExerciseEntity WHERE routineId = :exerciseId")
    fun getRoutineExercisesByExerciseId(exerciseId: Long): Flow<List<RoutineExerciseEntity>>

    @Insert(entity = RoutineExerciseEntity::class)
    suspend fun addRoutineExercise(routineExercise: RoutineExerciseEntity)

    @Query("DELETE FROM RoutineExerciseEntity WHERE routineId = :routineId AND exerciseId = :exerciseId")
    suspend fun deleteRoutineExercise(routineId: Int, exerciseId: Int)

    @Update(entity = RoutineExerciseEntity::class)
    suspend fun updateRoutineExercise(relation: RoutineExerciseEntity)

}