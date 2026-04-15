package com.mintocode.rutinapp.ui.screens.root

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ChevronRight
import androidx.compose.material.icons.twotone.FitnessCenter
import androidx.compose.material.icons.automirrored.twotone.FormatListBulleted
import androidx.compose.material.icons.twotone.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import com.mintocode.rutinapp.ui.floating.FloatingWorkoutService
import com.mintocode.rutinapp.ui.screenStates.WorkoutsScreenState
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.utils.simpleDateString
import com.mintocode.rutinapp.viewmodels.ExercisesViewModel
import com.mintocode.rutinapp.viewmodels.RoutinesViewModel
import com.mintocode.rutinapp.viewmodels.SettingsViewModel
import com.mintocode.rutinapp.viewmodels.WorkoutsViewModel

/**
 * Train root page — Kinetic Precision design.
 *
 * Gradient CTA for free workout, bento grid for Exercises/Routines,
 * recent workouts carousel, and routines carousel.
 *
 * @param workoutsViewModel ViewModel for workout data
 * @param exercisesViewModel ViewModel for exercise data
 * @param routinesViewModel ViewModel for routine data
 * @param settingsViewModel ViewModel for settings data
 */
@Composable
fun TrainPage(
    workoutsViewModel: WorkoutsViewModel,
    exercisesViewModel: ExercisesViewModel,
    routinesViewModel: RoutinesViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navigator = LocalSheetNavigator.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current

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

    val userData by settingsViewModel.data.observeAsState()
    val floatingEnabled = userData?.floatingWidgetEnabled == true

    LaunchedEffect(workoutState, floatingEnabled) {
        if (workoutState is WorkoutsScreenState.WorkoutStarted) {
            val ws = workoutState as WorkoutsScreenState.WorkoutStarted
            navigator.open(SheetDestination.ActiveWorkout(ws.workout.id))
            if (floatingEnabled) {
                FloatingWorkoutService.start(context)
            }
        } else {
            FloatingWorkoutService.stop(context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // ── Page Title ──
        Text(
            text = "Entrenar",
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 40.sp,
            letterSpacing = (-1).sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .width(48.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.primary)
        )

        Spacer(Modifier.height(28.dp))

        // ── Gradient CTA Button ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primary
                        )
                    )
                )
                .clickable { workoutsViewModel.startFromEmpty() }
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        Icons.TwoTone.PlayCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = "Entrenamiento Libre",
                            fontFamily = SpaceGroteskFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Empieza sin rutina",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                }
                Icon(
                    Icons.TwoTone.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Quick Access Bento Grid ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KPBentoCard(
                icon = Icons.TwoTone.FitnessCenter,
                label = "Ejercicios",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f),
                onClick = { navigator.open(SheetDestination.ExerciseList) }
            )
            KPBentoCard(
                icon = Icons.AutoMirrored.TwoTone.FormatListBulleted,
                label = "Rutinas",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f),
                onClick = { navigator.open(SheetDestination.RoutineList) }
            )
        }

        Spacer(Modifier.height(28.dp))

        // ── Recent Workouts ──
        KPSectionHeader(
            title = "Recientes",
            onSeeAll = { navigator.open(SheetDestination.WorkoutHistory) }
        )
        Spacer(Modifier.height(12.dp))

        if (workouts.isEmpty()) {
            KPEmptyHint("Sin entrenamientos aún")
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 16.dp)
            ) {
                items(workouts.take(10)) { workout ->
                    KPWorkoutCard(
                        workout = workout,
                        onClick = { workoutsViewModel.continueWorkout(workout) }
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // ── Routines ──
        KPSectionHeader(
            title = "Mis Rutinas",
            onSeeAll = { navigator.open(SheetDestination.RoutineList) }
        )
        Spacer(Modifier.height(12.dp))

        if (routines.isEmpty()) {
            KPEmptyHint("Sin rutinas")
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 16.dp)
            ) {
                items(routines.take(10)) { routine ->
                    KPRoutineCard(
                        routine = routine,
                        onClick = { workoutsViewModel.startFromRoutine(routine) }
                    )
                }
            }
        }

        Spacer(Modifier.height(48.dp))
    }
}

/**
 * KP bento grid card — rounded-xl, surfaceContainerLow,
 * icon container in tint/10 color.
 */
@Composable
private fun KPBentoCard(
    icon: ImageVector,
    label: String,
    tint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = label,
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * KP section header — title + "Ver todo" chevron.
 */
@Composable
private fun KPSectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSeeAll),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Icon(
            Icons.TwoTone.ChevronRight,
            contentDescription = "Ver todo",
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * KP empty state hint — surfaceContainerLow, rounded-xl.
 */
@Composable
private fun KPEmptyHint(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}

/**
 * KP workout card — rounded-xl, surfaceContainerLow, status badge.
 */
@Composable
private fun KPWorkoutCard(workout: WorkoutModel, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .size(width = 160.dp, height = 110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = workout.baseRoutine?.name ?: "Sin rutina",
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = workout.date.simpleDateString(),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        // Status badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (workout.isFinished) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = if (workout.isFinished) "Completado" else "En progreso",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (workout.isFinished) MaterialTheme.colorScheme.tertiary
                else MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * KP routine card — rounded-xl, surfaceContainerLow, body part tag.
 */
@Composable
private fun KPRoutineCard(routine: RoutineModel, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .size(width = 160.dp, height = 100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = routine.name,
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = routine.targetedBodyPart,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
