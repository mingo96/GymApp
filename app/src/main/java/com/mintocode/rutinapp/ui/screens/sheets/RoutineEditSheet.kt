package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.automirrored.twotone.ArrowForward
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.R
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.screenStates.RoutinesScreenState
import com.mintocode.rutinapp.ui.screens.ListOfExercises
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
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
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ── Title ──
        Text(
            text = "Editar rutina",
            fontFamily = SpaceGroteskFont,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
            color = MaterialTheme.colorScheme.onSurface
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

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Routine name subtitle + save
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = uiState.routine.name,
                fontFamily = ManropeFont,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
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

        // Name field
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "NOMBRE *",
                fontFamily = ManropeFont,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
            TextField(
                value = name,
                onValueChange = { name = it },
                textStyle = TextStyle(
                    fontFamily = ManropeFont,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Body part field
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "PARTE DEL CUERPO",
                fontFamily = ManropeFont,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
            TextField(
                value = targetedBodyPart,
                onValueChange = { targetedBodyPart = it },
                textStyle = TextStyle(
                    fontFamily = ManropeFont,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Navigate to exercises button (gradient)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { viewModel.toggleEditingState() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "IR A EJERCICIOS",
                    fontFamily = SpaceGroteskFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.AutoMirrored.TwoTone.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
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
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Routine name
        Text(
            text = uiState.routine.name,
            fontFamily = ManropeFont,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Section label + action buttons
        Row(
            Modifier
                .fillMaxWidth()
                .animateContentSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "EJERCICIOS",
                fontFamily = ManropeFont,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
            if (uiState.selectedExercise != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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

        // Included exercises list
        ListOfExercises(
            exerciseList = uiState.routine.exercises,
            selected = uiState.selectedExercise,
            selectExercise = { viewModel.selectExercise(it) }
        )

        // Available exercises
        Text(
            text = "NO INCLUIDOS",
            fontFamily = ManropeFont,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ListOfExercises(
            exerciseList = uiState.availableExercises,
            selected = uiState.selectedExercise
        ) {
            viewModel.selectExercise(it)
        }

        // Back to routine button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(12.dp)
                )
                .clickable { viewModel.toggleEditingState() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.TwoTone.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "IR A RUTINA",
                    fontFamily = SpaceGroteskFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(16.dp))
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

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Breadcrumb
        Text(
            text = "${uiState.routine.name} → ${uiState.selectedExercise.name}",
            fontFamily = ManropeFont,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Sets & Reps card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceContainerHigh,
                    RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (manualEdition) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "SERIES Y REPETICIONES",
                        fontFamily = ManropeFont,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                    TextField(
                        value = setsAndReps,
                        onValueChange = { setsAndReps = it },
                        textStyle = TextStyle(
                            fontFamily = ManropeFont,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            } else {
                // Counter mode
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "SETS",
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally)
                    )
                    Text(
                        "REPETICIONES",
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceContainerHighest,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(vertical = 8.dp),
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
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
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
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
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

            // Observations
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "OBSERVACIONES",
                    fontFamily = ManropeFont,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
                TextField(
                    value = observations,
                    onValueChange = { observations = it },
                    textStyle = TextStyle(
                        fontFamily = ManropeFont,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        }

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Save / back button (gradient)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { viewModel.updateRoutineExerciseRelation(setsAndReps, observations) }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (uiState.selectedExercise.setsAndReps == setsAndReps &&
                        uiState.selectedExercise.observations == observations
                    ) Icons.AutoMirrored.TwoTone.ArrowBack else Icons.TwoTone.Check,
                    contentDescription = "Guardar",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Toggle counter mode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        manualEdition = !manualEdition
                        setsAndReps = "0x0"
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CAMBIAR CONTADOR",
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
