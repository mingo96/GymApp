package com.example.rutinapp.newData

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rutinapp.newData.daos.ExerciseDao
import com.example.rutinapp.newData.daos.ExerciseEntity
import com.example.rutinapp.newData.daos.ExerciseToExerciseDao
import com.example.rutinapp.newData.daos.ExerciseToExerciseEntity
import com.example.rutinapp.newData.daos.RoutineDao
import com.example.rutinapp.newData.daos.RoutineEntity
import com.example.rutinapp.newData.daos.RoutineExerciseDao
import com.example.rutinapp.newData.daos.RoutineExerciseEntity
import com.example.rutinapp.newData.daos.SetDao
import com.example.rutinapp.newData.daos.SetEntity
import com.example.rutinapp.newData.daos.SetsWorkOutDao
import com.example.rutinapp.newData.daos.SetsWorkoutEntity
import com.example.rutinapp.newData.daos.WorkOutDao
import com.example.rutinapp.newData.daos.WorkOutEntity

@Database(
    entities = [ExerciseEntity::class, ExerciseToExerciseEntity::class, RoutineEntity::class, RoutineExerciseEntity::class, SetEntity::class, SetsWorkoutEntity::class, WorkOutEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RutinAppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao

    abstract fun exerciseToExerciseDao(): ExerciseToExerciseDao

    abstract fun routineDao(): RoutineDao

    abstract fun routineExerciseDao(): RoutineExerciseDao

    abstract fun setDao(): SetDao

    abstract fun setsWorkOutDao(): SetsWorkOutDao

    abstract fun workoutDao(): WorkOutDao

}