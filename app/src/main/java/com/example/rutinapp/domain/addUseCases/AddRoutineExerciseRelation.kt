package com.example.rutinapp.domain.addUseCases

import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.models.RoutineModel
import com.example.rutinapp.data.repositories.RoutineRepository
import javax.inject.Inject

class AddRoutineExerciseRelation @Inject constructor(private val routineRepository: RoutineRepository) {

    suspend operator fun invoke(routine : RoutineModel, exercise : ExerciseModel){
        routineRepository.relateExerciseToRoutine(routine.id.toLong(), exercise.id.toLong())
    }
}