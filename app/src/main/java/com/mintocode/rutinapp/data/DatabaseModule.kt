package com.mintocode.rutinapp.data

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mintocode.rutinapp.data.daos.AppNotificationDao
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
    fun provideAppNotificationDao(database: RutinAppDatabase): AppNotificationDao {
        return database.appNotificationDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): RutinAppDatabase {
        // Migrations:
        // v2 -> v3: add realId columns (default 0) to Exercise, Routine, WorkOut tables
        // v3 -> v4: add isFromThisUser columns (default 1) to Exercise, Routine, WorkOut tables
        // v4 -> v5: add isDirty to WorkOut/Planning, realId to Planning
        // v5 -> v6: create app_notifications table
        // Note: MIGRATION_4_5 includes safety checks for realId columns in case
        // earlier migrations (2→3) were skipped (e.g., fresh install at version 4).

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                safeAddColumn(database, "ExerciseEntity", "realId", "INTEGER NOT NULL DEFAULT 0")
                safeAddColumn(database, "RoutineEntity", "realId", "INTEGER NOT NULL DEFAULT 0")
                safeAddColumn(database, "WorkOutEntity", "realId", "INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                safeAddColumn(database, "ExerciseEntity", "isFromThisUser", "INTEGER NOT NULL DEFAULT 1")
                safeAddColumn(database, "RoutineEntity", "isFromThisUser", "INTEGER NOT NULL DEFAULT 1")
                safeAddColumn(database, "WorkOutEntity", "isFromThisUser", "INTEGER NOT NULL DEFAULT 1")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Safety: ensure realId exists on all tables (in case v2→v3 never ran)
                safeAddColumn(database, "ExerciseEntity", "realId", "INTEGER NOT NULL DEFAULT 0")
                safeAddColumn(database, "RoutineEntity", "realId", "INTEGER NOT NULL DEFAULT 0")
                safeAddColumn(database, "WorkOutEntity", "realId", "INTEGER NOT NULL DEFAULT 0")
                // New columns for v5
                safeAddColumn(database, "WorkOutEntity", "isDirty", "INTEGER NOT NULL DEFAULT 0")
                safeAddColumn(database, "PlanningEntity", "realId", "INTEGER NOT NULL DEFAULT 0")
                safeAddColumn(database, "PlanningEntity", "isDirty", "INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `app_notifications` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `serverId` INTEGER NOT NULL DEFAULT 0,
                        `title` TEXT NOT NULL,
                        `body` TEXT NOT NULL DEFAULT '',
                        `type` TEXT NOT NULL DEFAULT 'info',
                        `data` TEXT,
                        `readAt` TEXT,
                        `createdAt` TEXT NOT NULL DEFAULT '',
                        `updatedAt` TEXT NOT NULL DEFAULT ''
                    )
                """.trimIndent())
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_app_notifications_readAt` ON `app_notifications` (`readAt`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_app_notifications_createdAt` ON `app_notifications` (`createdAt`)")
            }
        }

        return Room.databaseBuilder(appContext, RutinAppDatabase::class.java, "RutinAppDatabase.db")
            .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
            .build()
    }

    /**
     * Safely adds a column to a table, ignoring the error if the column already exists.
     *
     * SQLite does not support ALTER TABLE ... ADD COLUMN IF NOT EXISTS,
     * so we catch the exception for duplicate column names.
     *
     * @param db The SQLite database
     * @param table Table name
     * @param column Column name to add
     * @param definition Column type and constraints (e.g. "INTEGER NOT NULL DEFAULT 0")
     */
    private fun safeAddColumn(
        db: SupportSQLiteDatabase,
        table: String,
        column: String,
        definition: String
    ) {
        try {
            db.execSQL("ALTER TABLE $table ADD COLUMN $column $definition")
        } catch (e: Exception) {
            // Column already exists — safe to ignore
            android.util.Log.d("DatabaseModule", "Column $column already exists in $table, skipping")
        }
    }
}