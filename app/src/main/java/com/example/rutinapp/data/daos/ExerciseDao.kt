package com.example.rutinapp.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Entity(
    indices = [Index("exerciseId")]
)
class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val exerciseId: Int = 0,
    var exerciseName: String = "",
    var exerciseDescription: String = "",
    var targetedBodyPart: String = "",
){

}

@Entity(
    primaryKeys = ["exercise1Id", "exercise2Id"],
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["exerciseId"],
            childColumns = ["exercise1Id"]
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["exerciseId"],
            childColumns = ["exercise2Id"]
        ),
    ]
)
data class ExerciseToExerciseEntity(
    val exercise1Id: Int, val exercise2Id: Int
)

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM ExerciseEntity")
    fun getAllAsFlow(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM ExerciseEntity")
    fun getAll(): List<ExerciseEntity>

    @Query("SELECT * FROM ExerciseEntity WHERE exerciseId = :id")
    suspend fun getById(id: Int): ExerciseEntity

    @Insert
    suspend fun insert(item : ExerciseEntity)

    @Update
    suspend fun update(item : ExerciseEntity)

}

@Dao
interface ExerciseToExerciseDao{

    @Query("SELECT * FROM ExerciseToExerciseEntity")
    fun getAll(): Flow<List<ExerciseToExerciseEntity>>

    @Query("SELECT * FROM ExerciseToExerciseEntity WHERE exercise1Id = :exerciseId OR exercise2Id = :exerciseId")
    fun getRelatedExercises(exerciseId: Int): Flow<List<ExerciseToExerciseEntity>>

    @Insert
    suspend fun insert(item : ExerciseToExerciseEntity)

    @Delete
    suspend fun delete(item : ExerciseToExerciseEntity)

}