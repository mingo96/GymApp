package com.example.rutinapp.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rutinapp.domain.addUseCases.AddExerciseUseCase
import com.example.rutinapp.domain.addUseCases.AddExercisesRelationUseCase
import com.example.rutinapp.domain.deleteUseCases.DeleteExerciseRelationUseCase
import com.example.rutinapp.domain.getUseCases.GetExerciseUseCase
import com.example.rutinapp.domain.updateUseCases.UpdateExerciseUseCase
import com.example.rutinapp.newData.models.ExerciseModel
import com.example.rutinapp.ui.screenStates.ExercisesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val addExerciseUseCase: AddExerciseUseCase,
    private val getExercisesUseCase: GetExerciseUseCase,
    private val addExerciseRelationUseCase: AddExercisesRelationUseCase,
    private val deleteExerciseRelationUseCase: DeleteExerciseRelationUseCase,
    private val updateExerciseUseCase: UpdateExerciseUseCase
) : ViewModel() {

    val exercisesState: StateFlow<List<ExerciseModel>> = getExercisesUseCase().catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState: MutableLiveData<ExercisesState> =
        MutableLiveData(ExercisesState.Observe())

    val uiState: LiveData<ExercisesState> = _uiState

    fun addExercise(name: String, description: String, targetedBodyPart: String) {
        viewModelScope.launch(context = Dispatchers.IO) {
            addExerciseUseCase(
                ExerciseModel(
                    name = name, description = description, targetedBodyPart = targetedBodyPart
                )
            )
            _uiState.postValue(ExercisesState.Observe())
        }
    }

    fun toggleExercisesRelation(exercise: ExerciseModel) {
        viewModelScope.launch(context = Dispatchers.IO) {
            if (_uiState.value is ExercisesState.AddingRelations) {
                val actualExercise =
                    (_uiState.value as ExercisesState.AddingRelations).exerciseModel
                if (!actualExercise.equivalentExercises.map { it.id }.contains(exercise.id)) {
                    addExerciseRelationUseCase(actualExercise, exercise)
                    actualExercise.equivalentExercises += exercise
                    _uiState.postValue(
                        ExercisesState.Modifying(
                            actualExercise, actualExercise.equivalentExercises
                        )
                    )
                } else {
                    //honestly i dont know how would this happen
                }
            } else {
                if (_uiState.value is ExercisesState.Modifying) {

                    val actualExercise = (_uiState.value as ExercisesState.Modifying).exerciseModel

                    deleteExerciseRelationUseCase(actualExercise, exercise)
                    actualExercise.equivalentExercises -= exercise

                    _uiState.postValue(
                        ExercisesState.Modifying(
                            actualExercise, actualExercise.equivalentExercises
                        )
                    )

                } else {
                    //i dont even know how would this happen
                }
            }
        }
    }

    fun clickToEdit(selected: ExerciseModel) {
        _uiState.value = ExercisesState.Modifying(selected, selected.equivalentExercises)
    }

    /**this will only be reached during edit state*/
    fun clickToAddRelatedExercises(context: Context) {
        try {
            assert(_uiState.value is ExercisesState.Modifying)
            val selected = (_uiState.value as ExercisesState.Modifying).exerciseModel
            _uiState.value = ExercisesState.AddingRelations(selected,
                exercisesState.value.filter { it != selected && it.id !in selected.equivalentExercises.map { it.id } })

        } catch (error: AssertionError) {
            Toast.makeText(context, "You are not in edit mode", Toast.LENGTH_SHORT).show()
        }
    }

    fun backToObserve() {
        _uiState.value = ExercisesState.Observe()
    }

    fun updateExercise(name: String, description: String, targetedBodyPart: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                assert(_uiState.value is ExercisesState.Modifying)
                val selected = (_uiState.value as ExercisesState.Modifying).exerciseModel
                selected.name = name
                selected.description = description
                selected.targetedBodyPart = targetedBodyPart
                updateExerciseUseCase(selected)
                _uiState.postValue(ExercisesState.Observe(selected))
            } catch (error: AssertionError) {
                //i dont know how would this happen
            }
        }
    }

    fun clickToCreate() {
        _uiState.value = ExercisesState.Creating
    }

    fun clickToObserve(exercise: ExerciseModel) {
        _uiState.value = ExercisesState.Observe(exercise)
    }

}