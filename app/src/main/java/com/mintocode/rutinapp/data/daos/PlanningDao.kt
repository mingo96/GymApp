package com.mintocode.rutinapp.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.mintocode.rutinapp.data.models.PlanningModel
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Entity(
    foreignKeys = [ForeignKey(
        entity = RoutineEntity::class, parentColumns = ["routineId"], childColumns = ["routineId"]
    )]
)
data class PlanningEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long,
    var routineId: Int?,
    var bodyPart: String?,
    var realId: Int = 0,
    var isDirty: Boolean = false
) {
    fun toModel(): PlanningModel {
        return PlanningModel(
            id = id,
            realId = realId.toLong(),
            date = Date(date),
            isDirty = isDirty
        )
    }
}

@Dao
interface PlanningDao {

    @Query("SELECT * FROM PlanningEntity")
    fun getPlannings(): Flow<List<PlanningEntity>>

    @Delete
    suspend fun deletePlanning(planning: PlanningEntity)

    @Update
    suspend fun updatePlanning(planning: PlanningEntity)

    @Insert
    suspend fun insertPlanning(planning: PlanningEntity)

    @Query("SELECT * FROM PlanningEntity WHERE date = :date")
    fun getPlanningOf(date: Long): Flow<PlanningEntity?>

}