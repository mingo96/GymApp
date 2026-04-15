package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.automirrored.twotone.ArrowForward
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.mintocode.rutinapp.utils.changeValue
import com.mintocode.rutinapp.utils.isSetsAndReps
import com.mintocode.rutinapp.utils.orSetsAndReps
import com.mintocode.rutinapp.viewmodels.RoutinesViewModel

/**
 * Sheet for editing an existing routine (KP design) with animated transitions
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
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Editar rutina",
            style = MaterialTheme.typography.headlineMedium,
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

/**
 * First screen: routine name and body part fields with save action.
 */
@Composable
private fun EditRoutineContentSection(
    uiState: RoutinesScreenState.Editing,
    viewModel: RoutinesViewModel
) {
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf(uiState.routine.name) }
    var targetedBodyPart by rememberSaveable { mutableStateOf(uiState.routine.targetedBodyPart) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = uiState.routine.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(
                onClick = { viewModel.editRoutine(name, targetedBodyPart, context) },
                modifier = Modifier.size(36.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.save),
                    contentDescription = "Guardar",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Card(
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextFieldWithTitle(title = "Nombre", text = name, onWrite = { name = it })
                TextFieldWithTitle(
                    title = "Parte del cuerpo",
                    text = targetedBodyPart,
                    onWrite = { targetedBodyPart = it }
                )
            }
        }

        Card(
            onClick = { viewModel.toggleEditingState() },
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.TwoTone.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Ir a ejercicios",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

/**
 * Second screen: exercise lists (included + available) with add/remove/edit actions.
 */
@Composable
private fun EditRoutineExercisesSection(
    uiState: RoutinesScreenState.Editing,
    viewModel: RoutinesViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = uiState.routine.name,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            Modifier
                .fillMaxWidth()
                .animateContentSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "EJERCICIOS",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (uiState.selectedExercise != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { viewModel.changeExercisePresenceOnRoutine() },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (uiState.selectedExercise in uiState.routine.exercises)
                                MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = if (uiState.selectedExercise in uiState.routine.exercises)
                                Icons.TwoTone.Delete else Icons.TwoTone.Add,
                            contentDescription = "Añadir/Eliminar",
                            modifier = Modifier.size(16.dp),
                            tint = if (uiState.selectedExercise in uiState.routine.exercises)
                                MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    if (uiState.selectedExercise in uiState.routine.exercises) {
                        IconButton(
                            onClick = { viewModel.toggleEditingState(true) },
                            modifier = Modifier.size(32.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        ) {
                            Icon(
                                imageVector = Icons.TwoTone.Edit,
                                contentDescription = "Editar relación",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
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
            text = "NO INCLUIDOS",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ListOfExercises(
            exerciseList = uiState.availableExercises,
            selected = uiState.selectedExercise
        ) {
            viewModel.selectExercise(it)
        }

        Card(
            onClick = { viewModel.toggleEditingState() },
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.TwoTone.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Ir a rutina",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

/**
 * Third screen: sets/reps counter and observations editor for a selected exercise.
 */
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

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "${uiState.routine.name} → ${uiState.selectedExercise.name}",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Card(
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (manualEdition) {
                    TextFieldWithTitle(
                        title = "Series y repeticiones",
                        text = setsAndReps,
                        onWrite = { setsAndReps = it }
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "SETS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally)
                        )
                        Text(
                            "REPETICIONES",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceContainerHighest,
                                MaterialTheme.shapes.medium
                            ),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { setsAndReps = setsAndReps.changeValue(true, true) }) {
                            Icon(
                                Icons.TwoTone.KeyboardArrowUp, contentDescription = "Más sets",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = setsAndReps.split("x").first(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = { setsAndReps = setsAndReps.changeValue(true, false) }) {
                            Icon(
                                Icons.TwoTone.KeyboardArrowDown, contentDescription = "Menos sets",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { setsAndReps = setsAndReps.changeValue(false, true) }) {
                            Icon(
                                Icons.TwoTone.KeyboardArrowUp, contentDescription = "Más reps",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = setsAndReps.split("x").last(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = { setsAndReps = setsAndReps.changeValue(false, false) }) {
                            Icon(
                                Icons.TwoTone.KeyboardArrowDown, contentDescription = "Menos reps",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                TextFieldWithTitle(
                    title = "Observaciones",
                    text = observations,
                    onWrite = { observations = it }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                onClick = { viewModel.updateRoutineExerciseRelation(setsAndReps, observations) },
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (uiState.selectedExercise.setsAndReps == setsAndReps &&
                            uiState.selectedExercise.observations == observations
                        ) Icons.AutoMirrored.TwoTone.ArrowBack else Icons.TwoTone.Check,
                        contentDescription = "Guardar",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Card(
                onClick = {
                    manualEdition = !manualEdition
                    setsAndReps = "0x0"
                },
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Cambiar contador",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
