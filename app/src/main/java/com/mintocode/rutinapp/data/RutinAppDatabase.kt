package com.mintocode.rutinapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mintocode.rutinapp.data.daos.AppNotificationDao
import com.mintocode.rutinapp.data.daos.AppNotificationEntity
import com.mintocode.rutinapp.data.daos.CalendarPhaseDao
import com.mintocode.rutinapp.data.daos.CalendarPhaseEntity
import com.mintocode.rutinapp.data.daos.ExerciseDao
import com.mintocode.rutinapp.data.daos.ExerciseEntity
import com.mintocode.rutinapp.data.daos.ExerciseToExerciseDao
import com.mintocode.rutinapp.data.daos.ExerciseToExerciseEntity
import com.mintocode.rutinapp.data.daos.PlanningDao
import com.mintocode.rutinapp.data.daos.PlanningEntity
import com.mintocode.rutinapp.data.daos.RoutineDao
import com.mintocode.rutinapp.data.daos.RoutineEntity
import com.mintocode.rutinapp.data.daos.RoutineExerciseDao
import com.mintocode.rutinapp.data.daos.RoutineExerciseEntity
import com.mintocode.rutinapp.data.daos.SetDao
import com.mintocode.rutinapp.data.daos.SetEntity
import com.mintocode.rutinapp.data.daos.WorkOutDao
import com.mintocode.rutinapp.data.daos.WorkOutEntity
import com.mintocode.rutinapp.data.daos.WorkoutRoutineEntity
import com.mintocode.rutinapp.data.daos.WorkoutRoutinesDao

@Database(
    entities = [
        ExerciseEntity::class,
        WorkoutRoutineEntity::class,
        ExerciseToExerciseEntity::class,
        RoutineEntity::class,
        RoutineExerciseEntity::class,
        SetEntity::class,
        WorkOutEntity::class,
        PlanningEntity::class,
        AppNotificationEntity::class,
        CalendarPhaseEntity::class
    ],
    version = 8,
    exportSchema = true,
)
abstract class RutinAppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao

    abstract fun exerciseToExerciseDao(): ExerciseToExerciseDao

    abstract fun routineDao(): RoutineDao

    abstract fun routineExerciseDao(): RoutineExerciseDao

    abstract fun setDao(): SetDao

    abstract fun workoutDao(): WorkOutDao

    abstract fun workoutRoutineDao(): WorkoutRoutinesDao

    abstract fun planningDao(): PlanningDao

    abstract fun appNotificationDao(): AppNotificationDao

    abstract fun calendarPhaseDao(): CalendarPhaseDao

}