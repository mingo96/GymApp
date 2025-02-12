package com.mintocode.rutinapp.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mintocode.rutinapp.data.UserDetails
import com.mintocode.rutinapp.data.api.Rutinappi
import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.domain.addUseCases.AddExerciseUseCase
import com.mintocode.rutinapp.domain.addUseCases.AddExercisesRelationUseCase
import com.mintocode.rutinapp.domain.deleteUseCases.DeleteExerciseRelationUseCase
import com.mintocode.rutinapp.domain.getUseCases.GetExercisesUseCase
import com.mintocode.rutinapp.domain.updateUseCases.UpdateExerciseUseCase
import com.mintocode.rutinapp.ui.screenStates.ExercisesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExercisesViewModel @Inject constructor(
    private val addExerciseUseCase: AddExerciseUseCase,
    private val getExercisesUseCase: GetExercisesUseCase,
    private val addExerciseRelationUseCase: AddExercisesRelationUseCase,
    private val deleteExerciseRelationUseCase: DeleteExerciseRelationUseCase,
    private val updateExerciseUseCase: UpdateExerciseUseCase
) : ViewModel() {

    val exercisesState: StateFlow<List<ExerciseModel>> = getExercisesUseCase().catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState: MutableLiveData<ExercisesState> =
        MutableLiveData(ExercisesState.Observe())

    val uiState: LiveData<ExercisesState> = _uiState

    fun writeOnExerciseName(name: String) {

        if (uiState.value is ExercisesState.SearchingForExercise){
            localSearch(name)
        }else{
            searchOnDB(name)
        }

    }

    private fun localSearch(name: String){
        _uiState.postValue(
            ExercisesState.SearchingForExercise(exercisesState.value.filter {
                it.name.lowercase(Locale.getDefault())
                    .contains(name, true) || it.targetedBodyPart.contains(name, true)
            })
        )
    }

    fun addExercise(name: String, description: String, targetedBodyPart: String, context: Context) {
        if (name.isNotEmpty() && description.isNotEmpty() && targetedBodyPart.isNotEmpty()) viewModelScope.launch(
            context = Dispatchers.IO
        ) {
            val exercise = ExerciseModel(
                name = name, description = description, targetedBodyPart = targetedBodyPart
            )
            addExerciseUseCase(
                exercise
            )
            _uiState.postValue(ExercisesState.Observe())

            if (UserDetails.actualValue?.authToken?.isEmpty() != false) return@launch

            try {

                val response = Rutinappi.retrofitService.createExercise(
                    exercise.toAPIModel(), UserDetails.actualValue!!.authToken
                )

                if (response.isSuccessful) {
                    updateExerciseUseCase(exercise.copy(realId = response.body()!!.realId))
                }
            } catch (e: Exception) {
                println("help")
            }

        }
        else Toast.makeText(context, "Faltan campos por rellenar", Toast.LENGTH_SHORT).show()
    }

    fun fetchExercises(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            val response =
                Rutinappi.retrofitService.getExercises(UserDetails.actualValue!!.authToken)
            if (response.isSuccessful) {
                if (response.body() != null) {

                    response.body()!!.forEach {
                        addExerciseUseCase(it.toModel(), it.equivalentExercises.map { it.toLong() })
                    }
                    val fetchedExercises = response.body()!!.size
                    Toast.makeText(
                        context,
                        if (fetchedExercises == 0) "No se han podido obtener ejercicios" else "Se han obtenido $fetchedExercises ejercicio" + if (fetchedExercises > 1) "s" else "",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context, "No se han podido obtener los ejercicios", Toast.LENGTH_SHORT
                    ).show()
                }
            }
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
        _uiState.postValue(ExercisesState.Modifying(selected, selected.equivalentExercises))
    }

    /**this will only be reached during edit state*/
    fun clickToAddRelatedExercises(context: Context) {
        try {
            assert(_uiState.value is ExercisesState.Modifying)
            val selected = (_uiState.value as ExercisesState.Modifying).exerciseModel
            _uiState.postValue(
                ExercisesState.AddingRelations(selected,
                    exercisesState.value.filter { it != selected && it.id !in selected.equivalentExercises.map { it.id } })
            )
        } catch (error: AssertionError) {
            Toast.makeText(context, "You are not in edit mode", Toast.LENGTH_SHORT).show()
        }
    }

    fun backToObserve() {
        _uiState.postValue(ExercisesState.Observe())
    }

    fun updateExercise(
        name: String, description: String, targetedBodyPart: String, context: Context
    ) {
        if (name.isNotEmpty() && description.isNotEmpty() && targetedBodyPart.isNotEmpty())

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    assert(_uiState.value is ExercisesState.Modifying)
                    val selected = (_uiState.value as ExercisesState.Modifying).exerciseModel
                    selected.name = name
                    selected.description = description
                    selected.targetedBodyPart = targetedBodyPart
                    updateExerciseUseCase(selected)
                    _uiState.postValue(ExercisesState.Observe(selected))
                    if (UserDetails.actualValue?.authToken?.isEmpty() != false && selected.realId != 0L) return@launch

                    val response = Rutinappi.retrofitService.updateExercise(
                        selected.toAPIModel(), UserDetails.actualValue!!.authToken
                    )

                } catch (error: AssertionError) {
                    //i dont know how would this happen
                }
            }
        else Toast.makeText(context, "Faltan campos por rellenar", Toast.LENGTH_SHORT).show()
    }

    fun clickToCreate() {
        _uiState.postValue(ExercisesState.Creating)
    }

    fun clickToObserve(exercise: ExerciseModel) {
        _uiState.postValue(ExercisesState.Observe(exercise))
    }

    fun uploadExercise(exercise: ExerciseModel) {
        viewModelScope.launch {
            try {
                UserDetails.actualValue?.authToken ?: return@launch

                val response = Rutinappi.retrofitService.createExercise(
                    exercise.toAPIModel(), UserDetails.actualValue!!.authToken
                )
                if (response.isSuccessful) {
                    updateExerciseUseCase(exercise.copy(realId = response.body()!!.realId))
                    backToObserve()
                }
            } catch (e: Exception) {

            }
        }
    }

    fun changeToUploadedExercises() {
        viewModelScope.launch {
            if (_uiState.value !is ExercisesState.ExploringExercises) {
            _uiState.postValue(ExercisesState.ExploringExercises(emptyList()))
            }else{
                _uiState.postValue(ExercisesState.Observe())
            }
        }
    }

    private fun searchOnDB(text: String) {
        viewModelScope.launch {

            val token = UserDetails.actualValue?.authToken ?: return@launch
            try {
                val response = Rutinappi.retrofitService.findExercise(token, text)

                if (response.isSuccessful){
                    val values = response.body()?.map { it.toModel() }?.filter { it.realId !in exercisesState.value.map { it.realId } } ?: emptyList()
                    _uiState.postValue(ExercisesState.ExploringExercises(values))
                }

            }catch (e:Exception){

            }
        }
    }

    fun saveExercise(exercise: ExerciseModel) {

        viewModelScope.launch(Dispatchers.IO) {

            if (exercise.realId == 0L) return@launch

            val token = UserDetails.actualValue?.authToken ?: return@launch

            try {

                val response = Rutinappi.retrofitService.fetchRelatedExercises(token, exercise.realId.toString())

                if (!response.isSuccessful) return@launch

                exercise.id = addExerciseUseCase(exercise).toString()

                for (relatedExercise in response.body()!!){
                    relatedExercise.id = addExerciseUseCase(relatedExercise.toModel()).toString()

                    addExerciseRelationUseCase(exercise, relatedExercise.toModel())
                }

            }catch (e:Exception){

            }
            _uiState.postValue(ExercisesState.Observe())
        }

    }

}