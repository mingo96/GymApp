package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.livedata.observeAsState
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
import com.mintocode.rutinapp.R
import com.mintocode.rutinapp.ui.components.TextFieldWithTitle
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.screenStates.RoutinesScreenState
import com.mintocode.rutinapp.ui.screens.ListOfExercises
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.utils.changeValue
import com.mintocode.rutinapp.utils.isSetsAndReps
import com.mintocode.rutinapp.utils.orSetsAndReps
import com.mintocode.rutinapp.viewmodels.RoutinesViewModel

/**
 * Sheet for editing an existing routine with animated transitions
 * between content editing, exercise management, and exercise relation editing.
 *
 * @param viewModel RoutinesViewModel for routine editing actions
 */
@Composable
fun RoutineEditSheet(viewModel: RoutinesViewModel) {
    val navigator = LocalSheetNavigator.current
    val uiState by viewModel.uiState.observeAsState()

    val editingState = uiState as? RoutinesScreenState.Editing
    if (editingState == null) {
        Text(
            text = "Cargando...",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Editar rutina",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Animated content switching
        AnimatedVisibility(
            visible = editingState.positionOfScreen && editingState.selectedExercise == null,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it }
        ) {
            EditRoutineContentSection(editingState, viewModel)
        }

        AnimatedVisibility(
            visible = !editingState.positionOfScreen,
            enter = slideInHorizontally { -it },
            exit = slideOutHorizontally { -it }
        ) {
            EditRoutineExercisesSection(editingState, viewModel)
        }

        AnimatedVisibility(
            visible = editingState.selectedExercise != null && editingState.positionOfScreen,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it }
        ) {
            EditRoutineExerciseRelationSection(editingState, viewModel)
        }
    }
}

@Composable
private fun EditRoutineContentSection(
    uiState: RoutinesScreenState.Editing,
    viewModel: RoutinesViewModel
) {
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf(uiState.routine.name) }
    var targetedBodyPart by rememberSaveable { mutableStateOf(uiState.routine.targetedBodyPart) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = uiState.routine.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(
                onClick = { viewModel.editRoutine(name, targetedBodyPart, context) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.save),
                    contentDescription = "Guardar",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        TextFieldWithTitle(title = "Nombre", text = name, onWrite = { name = it })
        TextFieldWithTitle(
            title = "Parte del cuerpo",
            text = targetedBodyPart,
            onWrite = { targetedBodyPart = it }
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { viewModel.toggleEditingState() },
                colors = rutinAppButtonsColours(),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Ir a ejercicios")
            }
        }
    }
}

@Composable
private fun EditRoutineExercisesSection(
    uiState: RoutinesScreenState.Editing,
    viewModel: RoutinesViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = uiState.routine.name,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            Modifier
                .fillMaxWidth()
                .animateContentSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Ejercicios", color = MaterialTheme.colorScheme.onSurface)
            if (uiState.selectedExercise != null) {
                Row {
                    IconButton(onClick = { viewModel.changeExercisePresenceOnRoutine() }) {
                        Icon(
                            imageVector = if (uiState.selectedExercise in uiState.routine.exercises)
                                Icons.TwoTone.Delete else Icons.TwoTone.Add,
                            contentDescription = "Añadir/Eliminar"
                        )
                    }
                    if (uiState.selectedExercise in uiState.routine.exercises) {
                        IconButton(onClick = { viewModel.toggleEditingState(true) }) {
                            Icon(
                                imageVector = Icons.TwoTone.Edit,
                                contentDescription = "Editar relación"
                            )
                        }
                    }
                }
            }
        }

        ListOfExercises(
            exerciseList = uiState.routine.exercises,
            selected = uiState.selectedExercise,
            selectExercise = { viewModel.selectExercise(it) }
        )

        Text(
            text = "Ejercicios no incluidos",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ListOfExercises(
            exerciseList = uiState.availableExercises,
            selected = uiState.selectedExercise
        ) {
            viewModel.selectExercise(it)
        }

        Button(
            onClick = { viewModel.toggleEditingState() },
            colors = rutinAppButtonsColours(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Ir a rutina")
        }
    }
}

@Composable
private fun EditRoutineExerciseRelationSection(
    uiState: RoutinesScreenState.Editing,
    viewModel: RoutinesViewModel
) {
    if (uiState.selectedExercise == null) return

    var setsAndReps by rememberSaveable {
        mutableStateOf(uiState.selectedExercise.setsAndReps.orSetsAndReps())
    }
    var manualEdition by rememberSaveable {
        mutableStateOf(!uiState.selectedExercise.setsAndReps.isSetsAndReps())
    }
    var observations by rememberSaveable { mutableStateOf(uiState.selectedExercise.observations) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "${uiState.routine.name} → ${uiState.selectedExercise.name}",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (manualEdition) {
            TextFieldWithTitle(
                title = "Series y repeticiones",
                text = setsAndReps,
                onWrite = { setsAndReps = it }
            )
        } else {
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Text("Sets", modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally))
                Text("Repeticiones", modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally))
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { setsAndReps = setsAndReps.changeValue(true, true) }) {
                    Icon(Icons.TwoTone.KeyboardArrowUp, contentDescription = "Más sets")
                }
                Text(text = setsAndReps.split("x").first())
                IconButton(onClick = { setsAndReps = setsAndReps.changeValue(true, false) }) {
                    Icon(Icons.TwoTone.KeyboardArrowDown, contentDescription = "Menos sets")
                }
                IconButton(onClick = { setsAndReps = setsAndReps.changeValue(false, true) }) {
                    Icon(Icons.TwoTone.KeyboardArrowUp, contentDescription = "Más reps")
                }
                Text(text = setsAndReps.split("x").last())
                IconButton(onClick = { setsAndReps = setsAndReps.changeValue(false, false) }) {
                    Icon(Icons.TwoTone.KeyboardArrowDown, contentDescription = "Menos reps")
                }
            }
        }

        TextFieldWithTitle(
            title = "Observaciones",
            text = observations,
            onWrite = { observations = it }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.updateRoutineExerciseRelation(setsAndReps, observations) },
                colors = rutinAppButtonsColours()
            ) {
                Icon(
                    imageVector = if (uiState.selectedExercise.setsAndReps == setsAndReps &&
                        uiState.selectedExercise.observations == observations
                    ) Icons.AutoMirrored.TwoTone.ArrowBack else Icons.TwoTone.Check,
                    contentDescription = "Guardar"
                )
            }
            Button(
                onClick = {
                    manualEdition = !manualEdition
                    setsAndReps = "0x0"
                },
                colors = rutinAppButtonsColours()
            ) {
                Text(text = "Cambiar contador")
            }
        }
    }
}
