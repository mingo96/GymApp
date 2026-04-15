package com.mintocode.rutinapp.ui.screens.root

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.twotone.ChevronRight
import androidx.compose.material.icons.twotone.FitnessCenter
import androidx.compose.material.icons.automirrored.twotone.FormatListBulleted
import androidx.compose.material.icons.twotone.PlayCircle
import androidx.compose.material.icons.twotone.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import com.mintocode.rutinapp.ui.theme.ManropeFont
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
            fontSize = 48.sp,
            letterSpacing = (-2.4).sp,
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

        Spacer(Modifier.height(32.dp))

        // ── Gradient CTA Button ──
        val isWorkoutActive = workoutState is WorkoutsScreenState.WorkoutStarted
        KPGradientCTA(
            isActive = isWorkoutActive,
            onClick = {
                if (isWorkoutActive) {
                    val ws = workoutState as WorkoutsScreenState.WorkoutStarted
                    navigator.open(SheetDestination.ActiveWorkout(ws.workout.id))
                } else {
                    workoutsViewModel.startFromEmpty()
                }
            }
        )

        // ── End Workout Button (only when active) ──
        if (isWorkoutActive) {
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                    .clickable { workoutsViewModel.finishTraining() }
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.TwoTone.Stop,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Finalizar Entrenamiento",
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(Modifier.height(40.dp))

        // ── Quick Access Bento Grid ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
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

        Spacer(Modifier.height(48.dp))

        // ── Recent Workouts ──
        KPSectionHeader(
            title = "Recientes",
            actionLabel = "Ver todos",
            onAction = { navigator.open(SheetDestination.WorkoutHistory) }
        )
        Spacer(Modifier.height(16.dp))

        if (workouts.isEmpty()) {
            KPEmptyHint("Sin entrenamientos aún")
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(end = 24.dp)
            ) {
                items(workouts.take(10)) { workout ->
                    KPWorkoutCard(
                        workout = workout,
                        isActive = !workout.isFinished,
                        onClick = { workoutsViewModel.continueWorkout(workout) }
                    )
                }
            }
        }

        Spacer(Modifier.height(48.dp))

        // ── My Routines ──
        KPSectionHeader(
            title = "Mis Rutinas",
            actionIcon = Icons.Filled.Add,
            onAction = { navigator.open(SheetDestination.RoutineCreate) }
        )
        Spacer(Modifier.height(16.dp))

        if (routines.isEmpty()) {
            KPEmptyHint("Sin rutinas")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                routines.take(5).forEach { routine ->
                    KPRoutineRow(
                        routine = routine,
                        onClick = { workoutsViewModel.startFromRoutine(routine) }
                    )
                }
            }
        }

        Spacer(Modifier.height(48.dp))
    }
}

// ── CTA Button ──

/**
 * Gradient CTA — full-width button with primaryContainer→primary gradient.
 * When workout is active, switches to tertiaryContainer→tertiary.
 */
@Composable
private fun KPGradientCTA(isActive: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(150),
        label = "cta_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    if (isActive) listOf(
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.tertiary
                    ) else listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primary
                    )
                )
            )
            .clickable(interactionSource, indication = null, onClick = onClick)
            .padding(24.dp)
    ) {
        // Decorative blob
        Box(
            modifier = Modifier
                .size(192.dp)
                .align(Alignment.TopEnd)
                .graphicsLayer(translationX = 48f, translationY = -48f)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (isActive) "EN CURSO" else "QUICK START",
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.TwoTone.PlayCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = if (isActive) "Continuar Entrenamiento" else "Entrenamiento libre",
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Icon(
                Icons.TwoTone.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f),
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

// ── Quick Access Bento ──

/**
 * KP bento grid card — rounded-xl, surfaceContainerLow, icon in tinted container.
 */
@Composable
private fun KPBentoCard(
    icon: ImageVector,
    label: String,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(tint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(30.dp)
                )
            }
            Text(
                text = label,
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ── Section Header ──

/**
 * KP section header — title + text link or icon action.
 */
@Composable
private fun KPSectionHeader(
    title: String,
    actionLabel: String? = null,
    actionIcon: ImageVector? = null,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            letterSpacing = (-0.4).sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (actionLabel != null) {
            Text(
                text = actionLabel.uppercase(),
                fontFamily = ManropeFont,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onAction)
            )
        } else if (actionIcon != null) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .clickable(onClick = onAction),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    actionIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

// ── Empty Hint ──

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

// ── Workout Card ──

/**
 * KP workout card — vertical card for carousel with status badge.
 * Active workouts get a tertiary border. Finished ones use standard surface.
 */
@Composable
private fun KPWorkoutCard(workout: WorkoutModel, isActive: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .size(width = 200.dp, height = 140.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Status badge
        if (isActive) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "EN PROGRESO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }
        } else {
            Spacer(Modifier.height(1.dp))
        }

        Column {
            Text(
                text = workout.baseRoutine?.name ?: "Sin rutina",
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = workout.date.simpleDateString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Routine Row ──

/**
 * KP routine row card — horizontal layout with icon, name, and body part tag.
 */
@Composable
private fun KPRoutineRow(routine: RoutineModel, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Icon placeholder (instead of image)
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.TwoTone.FitnessCenter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier.size(36.dp)
            )
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = routine.name,
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${routine.exercises.size} ejercicios",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            // Body part tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = routine.targetedBodyPart.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
