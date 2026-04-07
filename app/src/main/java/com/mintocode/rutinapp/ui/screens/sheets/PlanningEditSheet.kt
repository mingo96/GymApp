package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowForward
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.R
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.ui.components.TextFieldWithTitle
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.screenStates.FieldBeingEdited
import com.mintocode.rutinapp.ui.screenStates.MainScreenState
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.utils.simpleDateString
import com.mintocode.rutinapp.viewmodels.MainScreenViewModel

/**
 * Planning edit sheet content rendered inline (no Dialog wrapper).
 *
 * Shows body part / routine selection for a given date's planning.
 * Automatically closes the sheet when the planning state returns to Observation.
 *
 * @param viewModel MainScreenViewModel for planning actions
 */
@Composable
fun PlanningEditSheet(viewModel: MainScreenViewModel) {
    val navigator = LocalSheetNavigator.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.observeAsState(MainScreenState.Observation)

    // Track if we ever saw the planning state so we only auto-close on transition back
    var wasInPlanningState by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is MainScreenState.PlanningOnMainFocus) {
            wasInPlanningState = true
        }
        if (wasInPlanningState && uiState is MainScreenState.Observation) {
            navigator.close()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (val state = uiState) {
            is MainScreenState.PlanningOnMainFocus -> {
                Text(
                    text = "Planificación",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Objetivo el " + state.planningModel.date.simpleDateString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                when (state.fieldBeingEdited) {
                    FieldBeingEdited.NONE -> PlanningFieldSelection(viewModel)
                    FieldBeingEdited.BODYPART -> PlanningBodyPartEditor(
                        onSave = { viewModel.saveBodypart(it, context) },
                        onBack = { viewModel.backToSelection() }
                    )
                    FieldBeingEdited.ROUTINE -> PlanningRoutineSelector(
                        availableRoutines = state.availableRoutines,
                        onSelect = { viewModel.saveRoutine(it) },
                        onBack = { viewModel.backToSelection() }
                    )
                }
            }
            else -> {
                // Should auto-close via LaunchedEffect, but show fallback
                Text(
                    text = "Selecciona un día en el calendario para planificarlo",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

/**
 * Initial selection: choose between body part or routine.
 */
@Composable
private fun PlanningFieldSelection(viewModel: MainScreenViewModel) {
    Text(
        text = "¿Qué quieres planificar?",
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurface
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(
                onClick = { viewModel.selectBodypartClicked() },
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.body),
                    contentDescription = "Parte del cuerpo",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text("Parte del cuerpo", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(
                onClick = { viewModel.selectRoutineClicked() },
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.list),
                    contentDescription = "Rutina",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text("Rutina", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    Spacer(Modifier.height(8.dp))
    Button(
        onClick = { viewModel.backToObservation() },
        colors = rutinAppButtonsColours(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Cerrar")
    }
}

/**
 * Body part text field with save/back actions.
 */
@Composable
private fun PlanningBodyPartEditor(onSave: (String) -> Unit, onBack: () -> Unit) {
    var bodyPart by rememberSaveable { mutableStateOf("") }

    TextFieldWithTitle(
        title = "Parte del cuerpo",
        text = bodyPart,
        onWrite = { bodyPart = it },
        sendFunction = { onSave(bodyPart) }
    )

    Spacer(Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = { onSave(bodyPart) },
            colors = rutinAppButtonsColours(),
            modifier = Modifier.weight(1f)
        ) {
            Text("Guardar")
        }
        Button(
            onClick = onBack,
            colors = rutinAppButtonsColours(),
            modifier = Modifier.weight(1f)
        ) {
            Text("Volver")
        }
    }
}

/**
 * Routine list selector with expand/collapse per routine.
 */
@Composable
private fun PlanningRoutineSelector(
    availableRoutines: List<RoutineModel>,
    onSelect: (RoutineModel) -> Unit,
    onBack: () -> Unit
) {
    Text(
        text = "Rutinas disponibles",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
    )

    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
            .heightIn(0.dp, 350.dp)
    ) {
        if (availableRoutines.isEmpty()) {
            item {
                Text(
                    text = "No hay rutinas disponibles",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        items(availableRoutines) { routine ->
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                var isOpened by rememberSaveable { mutableStateOf(false) }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = routine.name,
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth(0.7f),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row {
                        IconButton(onClick = { isOpened = !isOpened }) {
                            Icon(
                                imageVector = if (isOpened) Icons.TwoTone.KeyboardArrowUp
                                else Icons.TwoTone.KeyboardArrowDown,
                                contentDescription = "Expandir ejercicios",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onSelect(routine) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.TwoTone.ArrowForward,
                                contentDescription = "Seleccionar rutina",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                if (isOpened) {
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        routine.exercises.forEach { exercise ->
                            Text(
                                text = exercise.name,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    Spacer(Modifier.height(8.dp))

    Button(
        onClick = onBack,
        colors = rutinAppButtonsColours(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Volver")
    }
}
