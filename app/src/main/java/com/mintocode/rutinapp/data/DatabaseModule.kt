package com.mintocode.rutinapp.data

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mintocode.rutinapp.data.daos.ExerciseDao
import com.mintocode.rutinapp.data.daos.ExerciseToExerciseDao
import com.mintocode.rutinapp.data.daos.PlanningDao
import com.mintocode.rutinapp.data.daos.RoutineDao
import com.mintocode.rutinapp.data.daos.RoutineExerciseDao
import com.mintocode.rutinapp.data.daos.SetDao
import com.mintocode.rutinapp.data.daos.WorkOutDao
import com.mintocode.rutinapp.data.daos.WorkoutRoutinesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun provideExerciseDao(database: RutinAppDatabase): ExerciseDao {
        return database.exerciseDao()
    }

    @Provides
    fun provideExerciseToExerciseDao(database: RutinAppDatabase): ExerciseToExerciseDao {
        return database.exerciseToExerciseDao()
    }

    @Provides
    fun provideRoutineDao(database: RutinAppDatabase): RoutineDao {
        return database.routineDao()
    }

    @Provides
    fun provideRoutineWithExercisesDao(database: RutinAppDatabase): RoutineExerciseDao {
        return database.routineExerciseDao()
    }

    @Provides
    fun providePlanningDao(database: RutinAppDatabase): PlanningDao {
        return database.planningDao()
    }

    @Provides
    fun provideSetDao(database: RutinAppDatabase): SetDao {
        return database.setDao()
    }

    @Provides
    fun provideWorkOutDao(database: RutinAppDatabase): WorkOutDao {
        return database.workoutDao()
    }

    @Provides
    fun provideWorkoutRoutineDao(database: RutinAppDatabase): WorkoutRoutinesDao {
        return database.workoutRoutineDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): RutinAppDatabase {
        // Migrations:
        // v2 -> v3: add realId columns (default 0) to Exercise, Routine, WorkOut tables
        // v3 -> v4: add isFromThisUser columns (default 1) to Exercise, Routine, WorkOut tables
        // Direct v2 -> v4 supported by sequential application.

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add realId columns if not exist (Room doesn't support IF NOT EXISTS inside ALTER properly, assume clean add)
                database.execSQL("ALTER TABLE ExerciseEntity ADD COLUMN realId INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE RoutineEntity ADD COLUMN realId INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE WorkOutEntity ADD COLUMN realId INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ExerciseEntity ADD COLUMN isFromThisUser INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE RoutineEntity ADD COLUMN isFromThisUser INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE WorkOutEntity ADD COLUMN isFromThisUser INTEGER NOT NULL DEFAULT 1")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE WorkOutEntity ADD COLUMN isDirty INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE PlanningEntity ADD COLUMN realId INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE PlanningEntity ADD COLUMN isDirty INTEGER NOT NULL DEFAULT 0")
            }
        }

        return Room.databaseBuilder(appContext, RutinAppDatabase::class.java, "RutinAppDatabase.db")
            .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
            .build()
    }


}