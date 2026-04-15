package com.mintocode.rutinapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mintocode.rutinapp.R
import com.mintocode.rutinapp.ui.components.DialogContainer
import com.mintocode.rutinapp.ui.components.TextFieldWithTitle
import com.mintocode.rutinapp.ui.screenStates.RoutinesScreenState
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.utils.changeValue
import com.mintocode.rutinapp.utils.isSetsAndReps
import com.mintocode.rutinapp.utils.orSetsAndReps
import com.mintocode.rutinapp.viewmodels.RoutinesViewModel

/**
 * Dialog for editing a routine, with animated transitions between content/exercises/relation views.
 *
 * @param uiState Current editing state with routine, exercises, and position
 * @param viewModel ViewModel managing routine actions
 */
@Composable
fun EditRoutineDialog(uiState: RoutinesScreenState.Editing, viewModel: RoutinesViewModel) {

    Dialog(onDismissRequest = { viewModel.backToObserve() }) {
        AnimatedVisibility(visible = uiState.positionOfScreen && uiState.selectedExercise == null,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it }) {
            DialogContainer {
                EditRoutineContent(uiState, viewModel)
            }
        }
        AnimatedVisibility(visible = !uiState.positionOfScreen,
            enter = slideInHorizontally { -it },
            exit = slideOutHorizontally { -it }) {
            DialogContainer {
                EditRoutineExercises(uiState, viewModel)
            }
        }
        AnimatedVisibility(visible = uiState.selectedExercise != null && uiState.positionOfScreen,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it }) {
            DialogContainer {
                EditRoutineExerciseRelation(uiState = uiState, viewModel = viewModel)
            }
        }

    }

}

/**
 * Edit view for the sets/reps relation between a routine and a selected exercise.
 *
 * Supports both manual text input and increment/decrement counter modes.
 *
 * @param uiState Current editing state with the selected exercise
 * @param viewModel ViewModel managing routine actions
 */
@Composable
private fun EditRoutineExerciseRelation(
    uiState: RoutinesScreenState.Editing, viewModel: RoutinesViewModel
) {

    var setsAndReps by rememberSaveable { mutableStateOf(uiState.selectedExercise!!.setsAndReps.orSetsAndReps()) }

    var manualEdition by rememberSaveable { mutableStateOf(!uiState.selectedExercise!!.setsAndReps.isSetsAndReps()) }

    var observations by rememberSaveable { mutableStateOf(uiState.selectedExercise!!.observations) }

    Text(
        text = uiState.routine.name + " -> " + uiState.selectedExercise!!.name,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )

    if (manualEdition) {
        TextFieldWithTitle(title = "Series y repeticiones",
            text = setsAndReps,
            onWrite = { setsAndReps = it })
    } else {
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(
                text = "Sets",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
            )
            Text(
                text = "Repeticiones",
                Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { setsAndReps = setsAndReps.changeValue(true, true) }) {
                Icon(
                    imageVector = Icons.TwoTone.KeyboardArrowUp, contentDescription = "Add to sets"
                )
            }
            Text(text = setsAndReps.split("x").first())
            IconButton(onClick = { setsAndReps = setsAndReps.changeValue(true, false) }) {
                Icon(
                    imageVector = Icons.TwoTone.KeyboardArrowDown,
                    contentDescription = "Delete from sets"
                )
            }

            IconButton(onClick = { setsAndReps = setsAndReps.changeValue(false, true) }) {
                Icon(
                    imageVector = Icons.TwoTone.KeyboardArrowUp, contentDescription = "Add to sets"
                )
            }
            Text(text = setsAndReps.split("x").last())
            IconButton(onClick = { setsAndReps = setsAndReps.changeValue(false, false) }) {
                Icon(
                    imageVector = Icons.TwoTone.KeyboardArrowDown,
                    contentDescription = "Delete from sets"
                )
            }
        }
    }

    TextFieldWithTitle(
        title = "Observaciones",
        text = observations,
        onWrite = { observations = it })

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = { viewModel.updateRoutineExerciseRelation(setsAndReps, observations) },
            colors = rutinAppButtonsColours(),
        ) {
            Icon(
                imageVector = if (uiState.selectedExercise.setsAndReps == setsAndReps && uiState.selectedExercise.observations == observations) Icons.AutoMirrored.TwoTone.ArrowBack else Icons.TwoTone.Check,
                contentDescription = "Delete exercise"
            )
        }
        Button(onClick = {
            manualEdition = !manualEdition
            setsAndReps = "0x0"

        }, colors = rutinAppButtonsColours()) {
            Text(text = "Cambiar contador", modifier = Modifier)
        }
    }

}

/**
 * Edit view for adding/removing exercises from a routine.
 *
 * Shows exercises in the routine and available exercises not yet included.
 *
 * @param uiState Current editing state with routine and available exercises
 * @param viewModel ViewModel managing routine actions
 */
@Composable
private fun EditRoutineExercises(uiState: RoutinesScreenState.Editing, viewModel: RoutinesViewModel) {

    Text(text = uiState.routine.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)

    Row(
        Modifier
            .fillMaxWidth()
            .animateContentSize(alignment = Alignment.Center),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Ejercicios")
        if (uiState.selectedExercise != null) Row {

            IconButton(onClick = {
                viewModel.changeExercisePresenceOnRoutine()
            }) {
                Icon(
                    imageVector = if (uiState.selectedExercise in uiState.routine.exercises) Icons.TwoTone.Delete else Icons.TwoTone.Add,
                    contentDescription = "delete/add"
                )
            }

            if (uiState.selectedExercise in uiState.routine.exercises) IconButton(onClick = {
                viewModel.toggleEditingState(
                    true
                )
            }) {
                Icon(
                    imageVector = Icons.TwoTone.Edit, contentDescription = "Editar ejercicio seleccionado"
                )
            }
        }
    }
    ListOfExercises(exerciseList = uiState.routine.exercises,
        selected = uiState.selectedExercise,
        selectExercise = { viewModel.selectExercise(it) })
    Text(text = "Ejercicios no incluidos")
    ListOfExercises(
        exerciseList = uiState.availableExercises, selected = uiState.selectedExercise
    ) {
        viewModel.selectExercise(it)
    }

    Button(onClick = { viewModel.toggleEditingState() }, colors = rutinAppButtonsColours()) {
        Text(
            text = "Ir a rutina",
            Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
    }

}

/**
 * Edit view for routine name and targeted body part fields.
 *
 * @param uiState Current editing state with routine data
 * @param viewModel ViewModel managing routine actions
 */
@Composable
private fun EditRoutineContent(uiState: RoutinesScreenState.Editing, viewModel: RoutinesViewModel) {

    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf(uiState.routine.name) }
    var targetedBodyPart by rememberSaveable { mutableStateOf(uiState.routine.targetedBodyPart) }

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = uiState.routine.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)

        IconButton(
            onClick = { viewModel.editRoutine(name, targetedBodyPart, context) },
            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.save), contentDescription = "Guardar cambios"
            )
        }
    }

    TextFieldWithTitle(title = "Nombre", text = name, onWrite = { name = it })
    TextFieldWithTitle(title = "Parte del cuerpo que entrena",
        text = targetedBodyPart,
        onWrite = { targetedBodyPart = it })

    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {

        Button(
            onClick = { viewModel.toggleEditingState() }, colors = rutinAppButtonsColours()
        ) {
            Text(text = "Ir a ejercicios")
        }
        Button(onClick = { viewModel.backToObserve() }, colors = rutinAppButtonsColours()) {
            Text(text = "Salir", Modifier.wrapContentWidth(Alignment.CenterHorizontally))
        }
    }


}

/**
 * Dialog for observing routine details (read-only) with options to edit or exit.
 *
 * @param uiState Current observe state with the routine to display
 * @param viewModel ViewModel managing routine actions
 */
@Composable
fun ObserveRoutineDialog(uiState: RoutinesScreenState.Observe, viewModel: RoutinesViewModel) {

    Dialog(onDismissRequest = { viewModel.backToObserve() }) {
        DialogContainer {

            Text(text = uiState.routine.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)

            TextFieldWithTitle(
                title = "Nombre", text = uiState.routine.name, editing = false
            )
            TextFieldWithTitle(
                title = "Parte del cuerpo", text = uiState.routine.targetedBodyPart, editing = false
            )
            Text(text = "Ejercicios", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                    .padding(16.dp)
                    .heightIn(max = 300.dp)
                    .fillMaxWidth()
            ) {

                if (uiState.routine.exercises.isEmpty()) item {
                    Text(text = "No hay ejercicios en esta rutina", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else items(uiState.routine.exercises) {
                    SimpleExerciseItem(
                        item = it, modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { viewModel.clickEditRoutine(uiState.routine) },
                    colors = rutinAppButtonsColours()
                ) {
                    Text(
                        text = "Editar", fontWeight = FontWeight.Bold, fontSize = 16.sp
                    )
                }

                Button(onClick = { viewModel.backToObserve() }, colors = rutinAppButtonsColours()) {
                    Text(
                        text = "Salir", fontWeight = FontWeight.Bold, fontSize = 16.sp
                    )
                }

            }

        }
    }

}

/**
 * Dialog for creating a new routine with name and body part fields.
 *
 * @param viewModel ViewModel managing routine actions
 */
@Composable
fun CreateRoutineDialog(viewModel: RoutinesViewModel) {

    val context = LocalContext.current

    Dialog(onDismissRequest = { viewModel.backToObserve() }) {
        DialogContainer {
            RoutineCreationPhase(onDismissRequest = { viewModel.backToObserve() },
                onNextPhase = { name, targetedBodyPart ->
                    viewModel.createRoutine(name, targetedBodyPart, context)
                })

        }
    }
}

/**
 * Form content for the routine creation dialog.
 *
 * @param onDismissRequest Callback to cancel creation
 * @param onNextPhase Callback with name and body part when accepted
 */
@Composable
private fun RoutineCreationPhase(
    onDismissRequest: () -> Unit, onNextPhase: (String, String) -> Unit
) {

    var name by rememberSaveable { mutableStateOf("") }
    var targetedBodyPart by rememberSaveable { mutableStateOf("") }
    Text(
        text = "Crear nueva rutina",
        Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally),
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
    TextFieldWithTitle(title = "Nombre de la rutina", text = name, onWrite = { name = it })
    TextFieldWithTitle(title = "Parte del cuerpo a la que se aplica",
        text = targetedBodyPart,
        onWrite = { targetedBodyPart = it },
        sendFunction = { onNextPhase(name, targetedBodyPart) })
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { onNextPhase(name, targetedBodyPart) }, colors = rutinAppButtonsColours()
        ) {
            Text(text = "Aceptar")
        }
        Button(onClick = onDismissRequest, colors = rutinAppButtonsColours()) {
            Text(text = "Cancelar")
        }
    }
}
