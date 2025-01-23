package com.mintocode.rutinapp.data

import android.content.Context
import androidx.room.Room
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
        return Room.databaseBuilder(appContext, RutinAppDatabase::class.java, "RutinAppDatabase.db")
            .fallbackToDestructiveMigration()
            .build()
    }


}