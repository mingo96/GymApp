package com.mintocode.rutinapp.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mintocode.rutinapp.ui.premade.AnimatedItem
import com.mintocode.rutinapp.ui.screenStates.WorkoutsScreenState
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.viewmodels.WorkoutsViewModel
import kotlinx.coroutines.delay

/**
 * Workout screen showing recent workouts, routines, and active workout progression.
 *
 * @param viewModel ViewModel managing workout state and actions
 * @param onNavigateToExercises Callback to navigate to exercises screen
 */
@Composable
fun WorkoutsScreen(viewModel: WorkoutsViewModel, onNavigateToExercises: () -> Unit = {}) {

    LaunchedEffect(Unit) {
        viewModel.autoSync()
    }

    val workoutScreenState by viewModel.workoutScreenStates.observeAsState(WorkoutsScreenState.Observe())

    val bottomButtonAction =
        if (workoutScreenState is WorkoutsScreenState.WorkoutStarted && (workoutScreenState as WorkoutsScreenState.WorkoutStarted).workout.isFinished) {
            null
        } else {
            {
                if (workoutScreenState is WorkoutsScreenState.WorkoutStarted) {
                    viewModel.finishTraining()
                } else viewModel.startFromEmpty()
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        when (workoutScreenState) {
            is WorkoutsScreenState.Observe -> {
                ObservationContent(
                    viewModel = viewModel,
                    state = workoutScreenState as WorkoutsScreenState.Observe
                )
            }

            is WorkoutsScreenState.WorkoutStarted -> {
                AnimatedItem(enterAnimation = slideInHorizontally { +it }, delay = 10) {
                    DigitalWatch(
                        uiState = workoutScreenState as WorkoutsScreenState.WorkoutStarted,
                        viewModel = viewModel
                    )
                }
                LazyColumn {
                    item {
                        WorkoutProgression(
                            viewModel = viewModel,
                            uiState = workoutScreenState as WorkoutsScreenState.WorkoutStarted,
                            onNavigateToExercises = onNavigateToExercises
                        )
                    }
                }
            }
        }

        if (bottomButtonAction != null) {
            Button(
                onClick = bottomButtonAction,
                colors = rutinAppButtonsColours(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = if (workoutScreenState is WorkoutsScreenState.WorkoutStarted) {
                        "Finalizar entrenamiento"
                    } else "Entrenar sin rutina"
                )
            }
        }
    }
}

/**
 * Content shown when no workout is active â€” recent workouts, routines, and planned session.
 */
@Composable
private fun ObservationContent(viewModel: WorkoutsViewModel, state: WorkoutsScreenState.Observe) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val workouts by viewModel.workouts.collectAsStateWithLifecycle(
        initialValue = emptyList(), lifecycle = lifecycle
    )

    val routines by viewModel.routines.collectAsStateWithLifecycle(
        lifecycle = lifecycle, initialValue = emptyList()
    )

    var maxIndexOfWorkouts by rememberSaveable { mutableIntStateOf(0) }

    var maxIndexOfRoutines by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(key1 = workouts) {
        delay(200)
        while (true) {
            delay(100)
            if (maxIndexOfWorkouts < workouts.size) maxIndexOfWorkouts++
        }
    }

    LaunchedEffect(key1 = routines) {
        delay(200)
        while (true) {
            delay(100)
            if (maxIndexOfRoutines < routines.size) maxIndexOfRoutines++

        }
    }

    LaunchedEffect(key1 = viewModel) {
        viewModel.refreshPlanning()
    }
    LazyColumn(Modifier.fillMaxWidth()) {

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
                        .padding(vertical = 16.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                        .animateContentSize(),
                ) {
                    if (workouts.isEmpty()) {
                        item {
                            Text(
                                text = "No hay entrenamientos recientes",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    items(workouts.take(maxIndexOfWorkouts)) {
                        AnimatedItem(delay = 100, enterAnimation = slideInVertically()) {
                            WorkoutItem(item = it, onClick = { viewModel.continueWorkout(it) })
                        }
                    }
                }
            }
        }
        item {
            AnimatedItem(enterAnimation = slideInHorizontally { +it }, delay = 100) {

                Text(text = "Rutinas", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                LazyRow(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                ) {
                    if (routines.isEmpty()) item {
                        Text(
                            text = "No hay rutinas",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    items(routines.take(maxIndexOfRoutines)) {
                        AnimatedItem(delay = 100, enterAnimation = slideInVertically()) {
                            RoutineItem(routine = it, modifier = Modifier.padding(16.dp)) {
                                viewModel.startFromRoutine(it)
                            }
                        }
                    }
                }
            }
        }
        item {
            AnimatedItem(enterAnimation = slideInHorizontally(), delay = 100) {
                if (state.planning != null) {
                    Text(text = "Lo planificado", fontSize = 17.sp, fontWeight = FontWeight.Bold)

                    if (state.planning.statedRoutine != null) {
                        RoutineItem(
                            routine = state.planning.statedRoutine!!,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            viewModel.startFromRoutine(state.planning.statedRoutine!!)
                        }
                    } else if (state.planning.statedBodyPart != null) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Hacer " + state.planning.statedBodyPart,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { viewModel.startFromStatedBodyPart() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.TwoTone.ArrowForward,
                                    contentDescription = "start from scheduled bodypart"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
