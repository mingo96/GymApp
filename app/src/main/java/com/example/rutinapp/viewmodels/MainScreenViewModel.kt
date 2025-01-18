package com.example.rutinapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rutinapp.data.models.PlanningModel
import com.example.rutinapp.domain.addUseCases.AddPlanningUseCase
import com.example.rutinapp.domain.getUseCases.GetPlanningsUseCase
import com.example.rutinapp.domain.updateUseCases.UpdatePlanningUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    getPlanningsUseCase: GetPlanningsUseCase,
    private val addPlanningUseCase: AddPlanningUseCase,
    private val updatePlanningUseCase: UpdatePlanningUseCase
) : ViewModel() {

    val plannings: StateFlow<List<PlanningModel>> =
        getPlanningsUseCase().catch { Error(it) }.stateIn(
            viewModelScope, SharingStarted.Eagerly, emptyList()
        )

}