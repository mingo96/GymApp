package com.example.rutinapp.newData

import android.content.Context
import androidx.room.Room
import com.example.rutinapp.newData.daos.ExerciseDao
import com.example.rutinapp.newData.daos.ExerciseToExerciseDao
import com.example.rutinapp.newData.daos.RoutineDao
import com.example.rutinapp.newData.daos.RoutineExerciseDao
import com.example.rutinapp.newData.daos.SetDao
import com.example.rutinapp.newData.daos.SetsWorkOutDao
import com.example.rutinapp.newData.daos.WorkOutDao
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
    fun provideSetDao(database: RutinAppDatabase): SetDao {
        return database.setDao()
    }

    @Provides
    fun provideSetsWorkOutDao(database: RutinAppDatabase): SetsWorkOutDao {
        return database.setsWorkOutDao()
    }

    @Provides
    fun provideWorkOutDao(database: RutinAppDatabase): WorkOutDao {
        return database.workoutDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): RutinAppDatabase {
        return Room.databaseBuilder(appContext, RutinAppDatabase::class.java, "RutinAppDatabase")
            .build()
    }


}