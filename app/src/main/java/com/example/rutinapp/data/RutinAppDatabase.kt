package com.example.rutinapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rutinapp.data.daos.ExerciseDao
import com.example.rutinapp.data.daos.ExerciseEntity
import com.example.rutinapp.data.daos.ExerciseToExerciseDao
import com.example.rutinapp.data.daos.ExerciseToExerciseEntity
import com.example.rutinapp.data.daos.RoutineDao
import com.example.rutinapp.data.daos.RoutineEntity
import com.example.rutinapp.data.daos.RoutineExerciseDao
import com.example.rutinapp.data.daos.RoutineExerciseEntity
import com.example.rutinapp.data.daos.SetDao
import com.example.rutinapp.data.daos.SetEntity
import com.example.rutinapp.data.daos.SetsWorkOutDao
import com.example.rutinapp.data.daos.SetsWorkoutEntity
import com.example.rutinapp.data.daos.WorkOutDao
import com.example.rutinapp.data.daos.WorkOutEntity
import com.example.rutinapp.data.daos.WorkoutRoutineEntity
import com.example.rutinapp.data.daos.WorkoutRoutinesDao

@Database(
    entities = [ExerciseEntity::class, WorkoutRoutineEntity::class, ExerciseToExerciseEntity::class, RoutineEntity::class, RoutineExerciseEntity::class, SetEntity::class, SetsWorkoutEntity::class, WorkOutEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class RutinAppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao

    abstract fun exerciseToExerciseDao(): ExerciseToExerciseDao

    abstract fun routineDao(): RoutineDao

    abstract fun routineExerciseDao(): RoutineExerciseDao

    abstract fun setDao(): SetDao

    abstract fun setsWorkOutDao(): SetsWorkOutDao

    abstract fun workoutDao(): WorkOutDao

    abstract fun workoutRoutineDao(): WorkoutRoutinesDao

}