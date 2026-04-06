package com.mintocode.rutinapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import com.mintocode.rutinapp.R
import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.models.SetModel
import com.mintocode.rutinapp.ui.components.DialogContainer
import com.mintocode.rutinapp.ui.components.TextFieldWithTitle
import com.mintocode.rutinapp.ui.screenStates.SetState
import com.mintocode.rutinapp.ui.screenStates.WorkoutsScreenState
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.utils.isValidAsNumber
import com.mintocode.rutinapp.viewmodels.WorkoutsViewModel

/**
 * Dialog for swapping an exercise with one of its equivalents during a workout.
 *
 * @param viewModel ViewModel managing workout actions
 * @param exercise The exercise being swapped, with its list of equivalents
 */
@Composable
fun ExerciseSwapDialog(viewModel: WorkoutsViewModel, exercise: ExerciseModel) {
    val context = LocalContext.current
    Dialog(onDismissRequest = { viewModel.cancelExerciseSwap() }) {
        DialogContainer {
            Text(text = "Cambiar ejercicio por", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            LazyColumn(Modifier.fillMaxWidth()) {
                items(exercise.equivalentExercises) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = it.name)
                        IconButton(onClick = { viewModel.swapExerciseBeingSwapped(it, context) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.swap),
                                contentDescription = "swap exercise for ${it.name}"
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dialog showing options for an existing set (view details, edit, or delete).
 *
 * @param viewModel ViewModel managing workout actions
 * @param uiState Current workout state containing the set being inspected
 */
@Composable
fun SetOptionsDialog(viewModel: WorkoutsViewModel, uiState: WorkoutsScreenState.WorkoutStarted) {

    val setState = uiState.setBeingCreated!! as SetState.OptionsOfSet

    var isEditing by rememberSaveable { mutableStateOf(false) }

    if (isEditing) {
        SetEditionDialog(viewModel = viewModel, set = setState.set)
    } else

        Dialog(
            onDismissRequest = { viewModel.cancelSetEditing() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {

            DialogContainer {
                Text(
                    text = "Opciones de serie",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 26.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    TextContainer(
                        text = setState.set.observations,
                        title = "Observaciones",
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                            .fillMaxWidth()
                    )
                    TextContainer(
                        text = setState.set.date.toGMTString().take(20),
                        title = "Momento de ejecución",
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                            .fillMaxWidth()
                    )
                }

                ButtonsOfEditSet(exit = { viewModel.cancelSetEditing() },
                    onEditClick = { isEditing = true }) {
                    viewModel.deleteSet(setState.set)
                }

            }

        }
}

/**
 * Dialog for creating or editing a set (reps, weight, observations).
 *
 * @param viewModel ViewModel managing workout actions
 * @param set The set being created or edited
 */
@Composable
fun SetEditionDialog(viewModel: WorkoutsViewModel, set: SetModel) {

    Dialog(onDismissRequest = { viewModel.cancelSetEditing() }) {

        var reps by rememberSaveable { mutableIntStateOf(set.reps) }
        var weight by rememberSaveable { mutableStateOf("") }
        var observations by rememberSaveable { mutableStateOf(set.observations) }

        DialogContainer {
            Text(text = "Añadir serie", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Repeticiones")
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { reps++ }) {
                    Icon(
                        imageVector = Icons.TwoTone.KeyboardArrowUp,
                        contentDescription = "more reps"
                    )
                }
                Text(text = reps.toString(), fontSize = 20.sp)
                IconButton(onClick = { reps-- }) {
                    Icon(
                        imageVector = Icons.TwoTone.KeyboardArrowDown,
                        contentDescription = "less reps"
                    )
                }
            }
            TextFieldWithTitle(
                title = "Peso", text = weight, onWrite = {
                    if (it.isValidAsNumber()) {
                        weight = it
                    }
                }, typeOfKeyBoard = KeyboardType.Number
            )
            TextFieldWithTitle(
                title = "Observaciones",
                text = observations,
                onWrite = { observations = it })
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(colors = rutinAppButtonsColours(), onClick = {
                    viewModel.saveSet(
                        weight = if (weight.isValidAsNumber() && weight.isNotEmpty()) weight.toDouble() else 0.0,
                        reps = reps,
                        observations = observations
                    )
                }) {
                    Text(text = "Guardar")
                }
                Button(
                    colors = rutinAppButtonsColours(),
                    onClick = { viewModel.cancelSetEditing() }) {
                    Text(text = "Cancelar")
                }
            }
        }
    }
}

/**
 * Row of action buttons for a set options dialog (Edit, Exit, Delete).
 *
 * @param exit Callback to close the dialog
 * @param onEditClick Callback to switch to edit mode
 * @param delete Callback to delete the set
 */
@Composable
private fun ButtonsOfEditSet(exit: () -> Unit, onEditClick: () -> Unit, delete: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = { onEditClick() }, colors = rutinAppButtonsColours()) {
            Text(text = "Editar", fontSize = 15.sp)
        }
        Button(onClick = { exit() }, colors = rutinAppButtonsColours()) {
            Text(text = "Salir", fontSize = 15.sp)
        }
        Button(onClick = { delete() }, colors = rutinAppButtonsColours()) {
            Text(text = "Eliminar", fontSize = 15.sp)
        }
    }
}
