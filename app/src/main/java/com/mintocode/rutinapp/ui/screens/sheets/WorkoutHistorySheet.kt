package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mintocode.rutinapp.data.models.WorkoutModel
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.navigation.SheetDestination
import com.mintocode.rutinapp.ui.premade.AnimatedItem
import com.mintocode.rutinapp.ui.screenStates.WorkoutsScreenState
import com.mintocode.rutinapp.ui.screens.RoutineItem
import com.mintocode.rutinapp.ui.screens.WorkoutItem
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.viewmodels.WorkoutsViewModel
import kotlinx.coroutines.delay

/**
 * Workout history sheet content.
 *
 * Shows recent workouts and available routines for starting new workouts.
 * Reuses existing WorkoutItem and RoutineItem composables.
 *
 * @param viewModel WorkoutsViewModel for data and actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutHistorySheet(viewModel: WorkoutsViewModel) {
    val navigator = LocalSheetNavigator.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        viewModel.autoSync()
        viewModel.refreshPlanning()
    }

    val workouts by viewModel.workouts.collectAsStateWithLifecycle(
        initialValue = emptyList(), lifecycle = lifecycle
    )
    val routines by viewModel.routines.collectAsStateWithLifecycle(
        initialValue = emptyList(), lifecycle = lifecycle
    )
    val workoutState by viewModel.workoutScreenStates.observeAsState(WorkoutsScreenState.Observe())

    var maxWorkouts by rememberSaveable { mutableIntStateOf(0) }
    var maxRoutines by rememberSaveable { mutableIntStateOf(0) }

    var selectedWorkout by rememberSaveable { mutableStateOf<Int?>(null) }

    LaunchedEffect(workouts) {
        delay(200)
        while (maxWorkouts < workouts.size) { delay(100); maxWorkouts++ }
    }
    LaunchedEffect(routines) {
        delay(200)
        while (maxRoutines < routines.size) { delay(100); maxRoutines++ }
    }

    // WorkoutStarted navigation is handled by TrainPage (always alive in pager)
    // to avoid double sheet opens from both TrainPage and this sheet.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Historial",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                AnimatedItem(enterAnimation = slideInHorizontally(), delay = 100) {
                    Text(
                        text = "Entrenamientos recientes",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    LazyRow(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.shapes.medium
                            )
                    ) {
                        if (workouts.isEmpty()) {
                            item {
                                Text(
                                    text = "No hay entrenamientos recientes",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        items(workouts.take(maxWorkouts)) { workout ->
                            AnimatedItem(delay = 100, enterAnimation = slideInVertically()) {
                                WorkoutItem(
                                    item = workout,
                                    onClick = { viewModel.continueWorkout(workout) },
                                    onLongPress = { selectedWorkout = workout.id }
                                )
                            }
                        }
                    }
                }
            }

            item {
                AnimatedItem(enterAnimation = slideInHorizontally { +it }, delay = 100) {
                    Text(
                        text = "Rutinas",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    LazyRow(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.shapes.medium
                            )
                    ) {
                        if (routines.isEmpty()) {
                            item {
                                Text(
                                    text = "No hay rutinas",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        items(routines.take(maxRoutines)) { routine ->
                            AnimatedItem(delay = 100, enterAnimation = slideInVertically()) {
                                RoutineItem(
                                    routine = routine,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    viewModel.startFromRoutine(routine)
                                }
                            }
                        }
                    }
                }
            }

            // Planning section
            item {
                val state = workoutState
                if (state is WorkoutsScreenState.Observe && state.planning != null) {
                    AnimatedItem(enterAnimation = slideInHorizontally(), delay = 100) {
                        Text(
                            text = "Lo planificado",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (state.planning.statedRoutine != null) {
                            RoutineItem(
                                routine = state.planning.statedRoutine!!,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                viewModel.startFromRoutine(state.planning.statedRoutine!!)
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.startFromEmpty() },
            colors = rutinAppButtonsColours(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(text = "Entrenar sin rutina")
        }
    }

    // CRUD sheet for long-pressed workout
    if (selectedWorkout != null) {
        val workout = workouts.find { it.id == selectedWorkout }
        if (workout != null) {
            WorkoutActionsSheet(
                workout = workout,
                onDismiss = { selectedWorkout = null },
                onDelete = {
                    viewModel.deleteWorkout(workout)
                    selectedWorkout = null
                },
                onContinue = {
                    selectedWorkout = null
                    viewModel.continueWorkout(workout)
                }
            )
        }
    }
}

/**
 * Bottom sheet with CRUD actions for a workout (continue, delete).
 *
 * @param workout The workout to act on
 * @param onDismiss Callback to close the sheet
 * @param onDelete Callback to delete the workout
 * @param onContinue Callback to continue the workout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutActionsSheet(
    workout: WorkoutModel,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onContinue: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = workout.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (workout.isFinished) "Terminado" else "En progreso",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (workout.exercisesAndSets.isNotEmpty()) {
                Text(
                    text = "Ejercicios",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                workout.exercisesAndSets.forEach { (exercise, sets) ->
                    Text(
                        text = "${exercise.name} — ${sets.size} series",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Button(
                onClick = onContinue,
                colors = rutinAppButtonsColours(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (workout.isFinished) "Ver entrenamiento" else "Continuar")
            }

            Button(
                onClick = onDelete,
                colors = rutinAppButtonsColours(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Eliminar entrenamiento")
            }
        }
    }
}
