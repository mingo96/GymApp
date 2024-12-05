package com.example.rutinapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.example.rutinapp.ui.screenStates.ExercisesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
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

    val exercisesState : StateFlow<List<ExerciseModel>> =getExercisesUseCase().catch { Error(it) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState : MutableLiveData<ExercisesState> = MutableLiveData(ExercisesState.Observe())

    val uiState : LiveData<ExercisesState> = _uiState

    fun addExercise(name: String, description : String, targetedBodyPart : String) {
        viewModelScope.launch(context = Dispatchers.IO){
            addExerciseUseCase(ExerciseModel(name = name, description = description, targetedBodyPart = targetedBodyPart))
            _uiState.postValue(ExercisesState.Observe())
        }
    }

    fun relateExercises(){
        viewModelScope.launch(context = Dispatchers.IO){
            val value1 = exercisesState.value[Random().nextInt(exercisesState.value.size)]
            val value2 = exercisesState.value[Random().nextInt(exercisesState.value.size)]
            if (value1.id != value2.id && value1.equivalentExercises.none { it.id == value2.id }) {
                addExerciseRelationUseCase(value1, value2)
            }
        }
    }

    fun clickToEdit(selected : ExerciseModel){
        _uiState.value = ExercisesState.Modifying(selected)
    }

    fun backToObserve() {
        _uiState.value = ExercisesState.Observe()
    }

    fun updateExercise(name: String, description: String, targetedBodyPart: String) {
        viewModelScope.launch(Dispatchers.IO) {
            //persistir cambios
        }
    }

    fun clickToCreate() {
        _uiState.value = ExercisesState.Creating
    }

    fun clickToObserve( exercise: ExerciseModel) {
        _uiState.value = ExercisesState.Observe(exercise)
    }

}