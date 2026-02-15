package com.mintocode.rutinapp.data.daos

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Update
import com.mintocode.rutinapp.data.models.RoutineModel
import kotlinx.coroutines.flow.Flow


@Entity(
    indices = [Index("routineId")]
)
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true)
    val routineId: Int,
    var name: String,
    var targetedBodyPart: String,
    @ColumnInfo(defaultValue = "0") var realId: Int,
    @ColumnInfo(defaultValue = "1") var isFromThisUser: Boolean = true
){
    fun toModel() = RoutineModel(
        id = routineId,
        name = name,
        targetedBodyPart = targetedBodyPart,
        exercises = mutableListOf(),
        isFromThisUser = isFromThisUser,
        realId = realId
    )
}


data class RoutineWithExercises(
    @Embedded val routine: RoutineEntity,
    @Relation(
        parentColumn = "routineId",
        entityColumn = "routineId"
    )
    val exerciseRelations: List<RoutineExerciseEntity>
)

@Dao
interface RoutineDao {

    @Query("SELECT * FROM RoutineEntity")
    fun getAllAsFlow(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM RoutineEntity")
    fun allWithRelations(): Flow<List<RoutineWithExercises>>

    @Query("SELECT * FROM RoutineEntity WHERE routineId = :routineId")
    suspend fun getFromId(routineId: Int): RoutineEntity

    @Insert
    suspend fun addRoutine(routine: RoutineEntity): Long

    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)

}