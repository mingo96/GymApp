package com.mintocode.rutinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Delete
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mintocode.rutinapp.ui.components.DialogContainer
import com.mintocode.rutinapp.ui.components.TextFieldWithTitle
import com.mintocode.rutinapp.ui.screenStates.ExercisesState
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.viewmodels.ExercisesViewModel

/**
 * Dialog for adding related exercises to the current exercise.
 *
 * Shows a grid of available exercises with add buttons.
 *
 * @param viewModel ViewModel managing exercise actions
 * @param addingRelations State with the exercise and available relations
 */
@Composable
fun AddRelationsDialog(
    viewModel: ExercisesViewModel, addingRelations: ExercisesState.AddingRelations
) {

    Dialog(onDismissRequest = { viewModel.backToObserve() }) {

        DialogContainer {
            Text(
                text = "Añadir ejercicios relacionados",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            LazyVerticalGrid(
                columns = GridCells.Adaptive(100.dp),
                modifier = Modifier
                    .heightIn(max = 200.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                if (addingRelations.possibleValues.isNotEmpty()) {
                    items(addingRelations.possibleValues) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = it.name,
                                maxLines = 3,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(0.6f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            IconButton(onClick = {
                                viewModel.toggleExercisesRelation(it)
                            }) {
                                Icon(
                                    imageVector = Icons.TwoTone.Add,
                                    contentDescription = "Add exercise relation"
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Text(text = "No hay ejercicios disponibles")
                    }
                }
            }

            Button(
                onClick = { viewModel.clickToEdit(addingRelations.exerciseModel) },
                colors = rutinAppButtonsColours(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Volver")
            }
        }

    }

}

/**
 * Dialog for editing an existing exercise with name, description, body part, and related exercises.
 *
 * @param viewModel ViewModel managing exercise actions
 * @param uiState Current modifying state with exercise data and related exercises
 */
@Composable
fun ModifyExerciseDialog(viewModel: ExercisesViewModel, uiState: ExercisesState.Modifying) {

    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf(uiState.exerciseModel.name) }
    var description by rememberSaveable { mutableStateOf(uiState.exerciseModel.description) }
    var targetedBodyPart by rememberSaveable { mutableStateOf(uiState.exerciseModel.targetedBodyPart) }
    var repsType by rememberSaveable { mutableStateOf(uiState.exerciseModel.repsType) }
    var weightType by rememberSaveable { mutableStateOf(uiState.exerciseModel.weightType) }
    Dialog(onDismissRequest = { viewModel.backToObserve() }) {

        DialogContainer {

            TextFieldWithTitle(title = "Nombre",
                onWrite = { name = it },
                text = name,
                sendFunction = {
                    viewModel.updateExercise(
                        name, description, targetedBodyPart, context, repsType, weightType
                    )
                })
            TextFieldWithTitle(title = "Descripción",
                onWrite = { description = it },
                text = description,
                sendFunction = {
                    viewModel.updateExercise(
                        name, description, targetedBodyPart, context, repsType, weightType
                    )
                })
            TextFieldWithTitle(title = "Parte del cuerpo",
                onWrite = { targetedBodyPart = it },
                text = targetedBodyPart,
                sendFunction = {
                    viewModel.updateExercise(
                        name, description, targetedBodyPart, context, repsType, weightType
                    )
                })
            ExerciseTypeSelectors(
                repsType = repsType,
                weightType = weightType,
                onRepsTypeChange = { repsType = it },
                onWeightTypeChange = { weightType = it }
            )

            Text(
                text = "Ejercicios relacionados",
                color = MaterialTheme.colorScheme.onSurface
            )
            LazyVerticalGrid(
                columns = GridCells.Adaptive(100.dp),
                modifier = Modifier
                    .heightIn(max = 200.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                item {
                    IconButton(onClick = { viewModel.clickToAddRelatedExercises(context) }) {
                        Icon(
                            imageVector = Icons.TwoTone.Add,
                            contentDescription = "Add related exercises"
                        )
                    }
                }
                items(uiState.relatedExercises) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .animateItem()
                    ) {
                        Text(
                            text = it.name,
                            Modifier
                                .fillMaxWidth(0.8f)
                                .clickable { viewModel.clickToObserve(it) },
                            maxLines = 2,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = { viewModel.toggleExercisesRelation(it) }) {
                            Icon(
                                imageVector = Icons.TwoTone.Delete, contentDescription = "Unrelate"
                            )
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    viewModel.updateExercise(
                        name, description, targetedBodyPart, context, repsType, weightType
                    )
                }, colors = rutinAppButtonsColours()) {
                    Text(text = "Guardar")
                }
                Button(
                    onClick = { viewModel.backToObserve() }, colors = rutinAppButtonsColours()
                ) {
                    Text(text = "Salir")
                }
            }

        }

    }
}

/**
 * Dialog for creating a new exercise with name, description, body part, and type selectors.
 *
 * @param viewModel ViewModel managing exercise actions
 * @param onExit Optional callback invoked after successful creation
 */
@Composable
fun CreateExerciseDialog(viewModel: ExercisesViewModel, onExit: (() -> Unit)? = null) {

    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var targetedBodyPart by rememberSaveable { mutableStateOf("") }
    var repsType by rememberSaveable { mutableStateOf("base") }
    var weightType by rememberSaveable { mutableStateOf("base") }
    Dialog(onDismissRequest = { viewModel.backToObserve() }) {
        DialogContainer {

            TextFieldWithTitle(title = "Nombre", onWrite = { name = it }, text = name)
            TextFieldWithTitle(
                title = "Descripción", onWrite = { description = it }, text = description
            )
            TextFieldWithTitle(title = "Parte del cuerpo",
                onWrite = { targetedBodyPart = it },
                text = targetedBodyPart,
                sendFunction = {
                    viewModel.addExercise(
                        name, description, targetedBodyPart, context, repsType, weightType
                    )
                })
            ExerciseTypeSelectors(
                repsType = repsType,
                weightType = weightType,
                onRepsTypeChange = { repsType = it },
                onWeightTypeChange = { weightType = it }
            )
            Button(
                onClick = {
                    viewModel.addExercise(
                        name, description, targetedBodyPart, context, repsType, weightType
                    )
                    if (onExit != null) onExit()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(align = Alignment.CenterHorizontally),
                colors = rutinAppButtonsColours()
            ) {
                Text(text = "Añadir ejercicio")
            }

        }
    }
}

/**
 * Dialog for viewing an exercise's details in read-only mode, with options to edit, upload, or obtain.
 *
 * @param viewModel ViewModel managing exercise actions
 * @param uiState Current observe state with the exercise to display
 */
@Composable
fun ObserveExerciseDialog(viewModel: ExercisesViewModel, uiState: ExercisesState.Observe) {
    Dialog(onDismissRequest = { viewModel.backToObserve() }) {

        DialogContainer {

            TextFieldWithTitle(title = "Nombre", text = uiState.exercise!!.name, editing = false)
            TextFieldWithTitle(
                title = "Descripción", text = uiState.exercise.description, editing = false
            )
            TextFieldWithTitle(
                title = "Parte del cuerpo",
                text = uiState.exercise.targetedBodyPart,
                editing = false
            )
            ExerciseTypeSelectors(
                repsType = uiState.exercise.repsType,
                weightType = uiState.exercise.weightType,
                onRepsTypeChange = {},
                onWeightTypeChange = {},
                enabled = false
            )
            if (uiState.exercise.equivalentExercises.isNotEmpty()) {
                Text(
                    text = "Ejercicios relacionados",
                    color = MaterialTheme.colorScheme.onSurface
                )
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(100.dp),
                    modifier = Modifier
                        .heightIn(max = 200.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalArrangement = Arrangement.Center
                ) {
                    items(uiState.exercise.equivalentExercises) {
                        Text(text = it.name,
                            maxLines = 2,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.clickable { viewModel.clickToObserve(it) })
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { viewModel.backToObserve() }, colors = rutinAppButtonsColours()
                ) {
                    Text(text = "Salir")
                }
                if (uiState.exercise.realId == 0L) {
                    Button(
                        onClick = { viewModel.uploadExercise(uiState.exercise) },
                        colors = rutinAppButtonsColours()
                    ) {
                        Text(text = "Subir")
                    }
                }
                if (uiState.exercise.isFromThisUser) {
                    Button(
                        onClick = { viewModel.clickToEdit(uiState.exercise) },
                        colors = rutinAppButtonsColours()
                    ) {
                        Text(text = "Editar")
                    }
                } else {
                    if (uiState.exercise.id == "0") {
                        Button(
                            onClick = { viewModel.saveExercise(uiState.exercise) },
                            colors = rutinAppButtonsColours()
                        ) {
                            Text(text = "Obtener")
                        }
                    }
                }
            }

        }

    }
}
