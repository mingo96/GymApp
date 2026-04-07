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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
                                WorkoutItem(item = workout, onClick = { viewModel.continueWorkout(workout) })
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
}
