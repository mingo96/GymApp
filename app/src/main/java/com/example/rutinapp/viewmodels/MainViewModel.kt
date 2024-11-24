package com.example.rutinapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rutinapp.domain.addUseCases.AddExerciseUseCase
import com.example.rutinapp.domain.addUseCases.AddExercisesRelationUseCase
import com.example.rutinapp.domain.addUseCases.AddRoutineExerciseRelation
import com.example.rutinapp.domain.addUseCases.AddRoutineUseCase
import com.example.rutinapp.domain.getUseCases.GetExerciseUseCase
import com.example.rutinapp.domain.getUseCases.GetRoutinesUseCase
import com.example.rutinapp.newData.models.ExerciseModel
import com.example.rutinapp.newData.models.RoutineModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val addExerciseUseCase: AddExerciseUseCase,
    private val getExercisesUseCase: GetExerciseUseCase,
    private val addExerciseRelationUseCase: AddExercisesRelationUseCase,
    private val addRoutineUseCase: AddRoutineUseCase,
    private val addRoutineExerciseRelation: AddRoutineExerciseRelation,
    private val getRoutineUseCase: GetRoutinesUseCase
) : ViewModel() {

    val exercises : StateFlow<List<ExerciseModel>> =getExercisesUseCase().catch { Error(it) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val routines : StateFlow<List<RoutineModel>> = getRoutineUseCase().catch { Error(it) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addExercise(name: String) {
        viewModelScope.launch(context = Dispatchers.IO){
            addExerciseUseCase(ExerciseModel(id = "3",name = name, description = "test", targetedBodyPart = "test"))
        }
    }

    fun relateExercises(){
        viewModelScope.launch(context = Dispatchers.IO){
            val value1 = exercises.value[Random().nextInt(exercises.value.size)]
            val value2 = exercises.value[Random().nextInt(exercises.value.size)]
            if (value1.id != value2.id && value1.equivalentExercises.none { it.id == value2.id }) {
                addExerciseRelationUseCase(value1.id.toLong(), value2.id.toLong())
            }
        }
    }

    fun addRoutine(name : String, targetedBodyPart : String) {
        viewModelScope.launch(context = Dispatchers.IO){
            addRoutineUseCase(RoutineModel(name = name, targetedBodyPart = targetedBodyPart))

        }
    }

    fun relateRoutine(routineModel: RoutineModel, exerciseModel: ExerciseModel) {
        viewModelScope.launch(context = Dispatchers.IO) {
            addRoutineExerciseRelation(routineModel, exerciseModel)
        }
    }
}