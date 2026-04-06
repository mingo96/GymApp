package com.mintocode.rutinapp.ui.screens.root

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.ChevronRight
import androidx.compose.material.icons.twotone.FitnessCenter
import androidx.compose.material.icons.twotone.FormatListBulleted
import androidx.compose.material.icons.twotone.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.data.models.WorkoutModel
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.navigation.SheetDestination
import com.mintocode.rutinapp.ui.premade.AnimatedItem
import com.mintocode.rutinapp.ui.screenStates.WorkoutsScreenState
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.utils.simpleDateString
import com.mintocode.rutinapp.viewmodels.ExercisesViewModel
import com.mintocode.rutinapp.viewmodels.RoutinesViewModel
import com.mintocode.rutinapp.viewmodels.WorkoutsViewModel
import kotlinx.coroutines.delay

/**
 * Train root page: Entry point for exercises, routines, and workouts.
 *
 * Displays quick-start options, recent workouts carousel, routines carousel,
 * and action buttons to browse full lists (which open as sheets).
 *
 * @param workoutsViewModel ViewModel for workout data
 * @param exercisesViewModel ViewModel for exercise data
 * @param routinesViewModel ViewModel for routine data
 */
@Composable
fun TrainPage(
    workoutsViewModel: WorkoutsViewModel,
    exercisesViewModel: ExercisesViewModel,
    routinesViewModel: RoutinesViewModel
) {
    val navigator = LocalSheetNavigator.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        workoutsViewModel.autoSync()
        exercisesViewModel.autoSync()
        routinesViewModel.autoSync()
    }

    val workouts by workoutsViewModel.workouts.collectAsStateWithLifecycle(
        initialValue = emptyList(), lifecycle = lifecycle
    )
    val routines by workoutsViewModel.routines.collectAsStateWithLifecycle(
        initialValue = emptyList(), lifecycle = lifecycle
    )
    val workoutState by workoutsViewModel.workoutScreenStates.observeAsState(WorkoutsScreenState.Observe())
    var lastHandledWorkoutId by rememberSaveable { mutableIntStateOf(-1) }

    // If a workout was started, open the ActiveWorkout sheet (guarded against re-triggers)
    LaunchedEffect(workoutState) {
        if (workoutState is WorkoutsScreenState.WorkoutStarted) {
            val ws = workoutState as WorkoutsScreenState.WorkoutStarted
            if (ws.workout.id != lastHandledWorkoutId) {
                lastHandledWorkoutId = ws.workout.id
                navigator.open(SheetDestination.ActiveWorkout(ws.workout.id))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Quick Start ──
        Text(
            text = "Entrenar",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Button(
            onClick = { workoutsViewModel.startFromEmpty() },
            colors = rutinAppButtonsColours(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.TwoTone.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "  Entrenamiento libre",
                fontWeight = FontWeight.SemiBold
            )
        }

        // ── Browse sections ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionCard(
                icon = Icons.TwoTone.FitnessCenter,
                label = "Ejercicios",
                modifier = Modifier.weight(1f),
                onClick = { navigator.open(SheetDestination.ExerciseList) }
            )
            SectionCard(
                icon = Icons.TwoTone.FormatListBulleted,
                label = "Rutinas",
                modifier = Modifier.weight(1f),
                onClick = { navigator.open(SheetDestination.RoutineList) }
            )
        }

        // ── Recent Workouts ──
        SectionHeader(
            title = "Recientes",
            onSeeAll = { navigator.open(SheetDestination.WorkoutHistory) }
        )

        if (workouts.isEmpty()) {
            EmptyHint("Sin entrenamientos aún")
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 16.dp)
            ) {
                items(workouts.take(10)) { workout ->
                    WorkoutCard(
                        workout = workout,
                        onClick = { workoutsViewModel.continueWorkout(workout) }
                    )
                }
            }
        }

        // ── Routines carousel ──
        SectionHeader(
            title = "Rutinas",
            onSeeAll = { navigator.open(SheetDestination.RoutineList) }
        )

        if (routines.isEmpty()) {
            EmptyHint("Sin rutinas")
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 16.dp)
            ) {
                items(routines.take(10)) { routine ->
                    RoutineCard(
                        routine = routine,
                        onClick = { workoutsViewModel.startFromRoutine(routine) }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSeeAll),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Icon(
            Icons.TwoTone.ChevronRight,
            contentDescription = "Ver todo",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun EmptyHint(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun WorkoutCard(workout: WorkoutModel, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .size(width = 140.dp, height = 90.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = workout.baseRoutine?.name ?: "Sin rutina",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = workout.date.simpleDateString(),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (workout.isFinished) {
            Text(
                text = "✓ Completado",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
        } else {
            Text(
                text = "En progreso",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun RoutineCard(routine: RoutineModel, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .size(width = 140.dp, height = 80.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = routine.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = routine.targetedBodyPart,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}
