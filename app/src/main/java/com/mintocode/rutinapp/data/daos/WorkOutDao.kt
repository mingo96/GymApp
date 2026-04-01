package com.mintocode.rutinapp.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Update
import com.mintocode.rutinapp.data.models.WorkoutModel
import kotlinx.coroutines.flow.Flow
import java.util.Date


@Entity
data class WorkOutEntity(
    @PrimaryKey(autoGenerate = true) val workOutId: Int,
    val date: Long,
    var title: String,
    var isFinished: Boolean,
    var realId: Int = 0,
    var isDirty: Boolean = false
){
    fun toModel(): WorkoutModel {
        return WorkoutModel(
            id = this.workOutId,
            realId = this.realId.toLong(),
            date = Date(this.date),
            title = this.title,
            isFinished = this.isFinished,
            isDirty = this.isDirty
        )
    }
}

data class WorkOutWithSets(
    @Embedded val workOut: WorkOutEntity,
    @Relation(
        parentColumn = "workOutId",
        entityColumn = "workoutDoneId"
    )
    val sets: List<SetEntity>
)

@Dao
interface WorkOutDao {

    @Query("SELECT * FROM WorkOutEntity ORDER BY date DESC LIMIT 10")
    fun get10MoreRecent(): Flow<List<WorkOutWithSets>>

    @Insert
    suspend fun addWorkOut(training: WorkOutEntity): Long

    @Query("SELECT * FROM WorkOutEntity WHERE date = :date")
    suspend fun getByDate(date: Long): WorkOutEntity

    @Delete
    suspend fun delete(workout: WorkOutEntity)

    @Update
    suspend fun update(workOut: WorkOutEntity)

}