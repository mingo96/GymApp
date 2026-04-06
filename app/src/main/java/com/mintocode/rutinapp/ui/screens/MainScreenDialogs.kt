package com.mintocode.rutinapp.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mintocode.rutinapp.R
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.ui.components.DialogContainer
import com.mintocode.rutinapp.ui.components.TextFieldWithTitle
import com.mintocode.rutinapp.ui.screenStates.FieldBeingEdited
import com.mintocode.rutinapp.ui.screenStates.MainScreenState
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.utils.simpleDateString
import com.mintocode.rutinapp.viewmodels.MainScreenViewModel

/**
 * Dialog for editing a specific day's planning.
 *
 * Allows the user to select a body part or routine for the planning date.
 *
 * @param viewModel MainScreenViewModel for state management
 * @param uistate Current planning focus state with available routines
 */
@Composable
fun PlanningEditionDialog(
    viewModel: MainScreenViewModel, uistate: MainScreenState.PlanningOnMainFocus
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = { viewModel.backToObservation() }) {
        DialogContainer {
            Text(
                text = "Objetivo el " + uistate.planningModel.date.simpleDateString(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            when (uistate.fieldBeingEdited) {
                FieldBeingEdited.NONE -> {
                    NoFieldSelectedContent(viewModel)
                }
                FieldBeingEdited.BODYPART -> {
                    BodyPartSelectedContent(onSend = { viewModel.saveBodypart(it, context) }) {
                        viewModel.backToSelection()
                    }
                }
                FieldBeingEdited.ROUTINE -> {
                    RoutineSelectedContent(
                        uistate = uistate,
                        onSelect = { viewModel.saveRoutine(it) },
                        onBack = { viewModel.backToSelection() }
                    )
                }
            }
        }
    }
}

/**
 * Content shown when no field (body part / routine) is selected yet.
 * Displays two large icon buttons for choosing the planning type.
 */
@Composable
private fun NoFieldSelectedContent(viewModel: MainScreenViewModel) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        IconButton(
            onClick = { viewModel.selectBodypartClicked() }, modifier = Modifier.size(80.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.body),
                contentDescription = "select body part",
                modifier = Modifier.size(200.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(
            onClick = { viewModel.selectRoutineClicked() }, modifier = Modifier.size(80.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.list),
                contentDescription = "select routine",
                modifier = Modifier.size(200.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
    Button(
        onClick = { viewModel.backToObservation() },
        colors = rutinAppButtonsColours(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Salir")
    }
}

/**
 * Content for entering a body part name for the planning.
 *
 * @param onSend Callback with the entered body part string
 * @param onExit Callback to go back to field selection
 */
@Composable
private fun BodyPartSelectedContent(onSend: (String) -> Unit, onExit: () -> Unit) {
    var bodyPart by rememberSaveable { mutableStateOf("") }

    TextFieldWithTitle(
        title = "Parte del cuerpo",
        text = bodyPart,
        onWrite = { bodyPart = it },
        sendFunction = { onSend(bodyPart) }
    )

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Button(onClick = { onSend(bodyPart) }, colors = rutinAppButtonsColours()) {
            Text(text = "Guardar")
        }
        Button(onClick = { onExit() }, colors = rutinAppButtonsColours()) {
            Text(text = "Volver")
        }
    }
}

/**
 * Content for selecting a routine from the available list.
 *
 * @param uistate Current UI state with available routines
 * @param onSelect Callback when a routine is selected
 * @param onBack Callback to go back to field selection
 */
@Composable
private fun RoutineSelectedContent(
    uistate: MainScreenState.PlanningOnMainFocus,
    onSelect: (RoutineModel) -> Unit,
    onBack: () -> Unit
) {
    Text(
        text = "Rutinas disponibles",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
    )

    LazyColumn(
        Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
            .heightIn(0.dp, 300.dp)
    ) {
        if (uistate.availableRoutines.isEmpty()) item {
            Text(
                text = "No hay rutinas disponibles",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        items(uistate.availableRoutines) {
            Column(
                Modifier
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
                        text = it.name,
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth(0.7f),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row {
                        IconButton(onClick = { isOpened = !isOpened }) {
                            Icon(
                                imageVector = if (isOpened) Icons.TwoTone.KeyboardArrowDown else Icons.TwoTone.KeyboardArrowUp,
                                contentDescription = "openclose ejercises",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onSelect(it) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.TwoTone.ArrowForward,
                                contentDescription = "select routine",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                if (isOpened) {
                    Column {
                        for (i in it.exercises) {
                            Text(
                                text = i.name,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
    Button(
        onClick = { onBack() },
        colors = rutinAppButtonsColours(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Volver")
    }
}
