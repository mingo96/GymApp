package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.FitnessCenter
import androidx.compose.material.icons.twotone.PlayArrow
import androidx.compose.material.icons.twotone.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.data.models.WorkoutModel
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.premade.AnimatedItem
import com.mintocode.rutinapp.ui.screenStates.WorkoutsScreenState
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.viewmodels.WorkoutsViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Workout history sheet — KP design.
 *
 * Hero header, stats summary row, routine suggestions,
 * chronological workout list grouped by month.
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // ── Hero Header ──
        Text(
            text = "Historial de\nentrenamientos",
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            letterSpacing = (-0.9).sp,
            lineHeight = 40.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Revisa tu progreso y continúa donde lo dejaste",
            fontFamily = ManropeFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(Modifier.height(24.dp))

        // ── Stats Summary Row ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val finishedCount = workouts.count { it.isFinished }
            val thisWeekCount = workouts.count {
                val diff = System.currentTimeMillis() - it.date.time
                diff < 7 * 24 * 60 * 60 * 1000 && it.isFinished
            }

            KPGlassStatCard(
                label = "TOTAL",
                value = finishedCount.toString(),
                modifier = Modifier.weight(1f)
            )
            KPGlassStatCard(
                label = "ESTA SEMANA",
                value = thisWeekCount.toString(),
                modifier = Modifier.weight(1f)
            )
            KPGlassStatCard(
                label = "RACHA",
                value = "–",
                unit = "DÍAS",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(32.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Routine Suggestions ──
            if (routines.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RUTINAS DISPONIBLES",
                            fontFamily = SpaceGroteskFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 2.sp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                        )
                    }
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(end = 16.dp)
                    ) {
                        items(routines.take(maxRoutines)) { routine ->
                            AnimatedItem(delay = 100, enterAnimation = slideInVertically()) {
                                KPRoutineSuggestionCard(
                                    routine = routine,
                                    onClick = { viewModel.startFromRoutine(routine) }
                                )
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(8.dp)) }
            }

            // ── Planning Quick Start ──
            val state = workoutState
            if (state is WorkoutsScreenState.Observe && state.planning != null) {
                item {
                    Text(
                        text = "LO PLANIFICADO",
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (state.planning.statedRoutine != null) {
                        KPRoutineSuggestionCard(
                            routine = state.planning.statedRoutine!!,
                            isPrimary = true,
                            onClick = { viewModel.startFromRoutine(state.planning.statedRoutine!!) }
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }

            // ── Chronological Workout List ──
            item {
                Text(
                    text = "HISTORIAL",
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (workouts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron entrenamientos",
                            fontFamily = ManropeFont,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Group by month
            val groupedByMonth = workouts.take(maxWorkouts).groupBy {
                SimpleDateFormat("MMMM yyyy", Locale("es")).format(it.date).uppercase()
            }

            groupedByMonth.forEach { (month, monthWorkouts) ->
                item {
                    Text(
                        text = month,
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 3.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                    )
                }

                items(monthWorkouts, key = { it.id }) { workout ->
                    AnimatedItem(delay = 50, enterAnimation = slideInHorizontally()) {
                        KPWorkoutHistoryCard(
                            workout = workout,
                            onClick = { viewModel.continueWorkout(workout) },
                            onLongPress = { selectedWorkout = workout.id }
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
        }

        // ── Start Empty Workout Button ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primary
                        )
                    )
                )
                .clickable { viewModel.startFromEmpty() }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.TwoTone.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "ENTRENAR SIN RUTINA",
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
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

// ── Private helper composables ──

/**
 * Glass-style stat card for the summary row.
 */
@Composable
private fun KPGlassStatCard(
    label: String,
    value: String,
    unit: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(112.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.6f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = label,
                fontFamily = ManropeFont,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column {
                Text(
                    text = value,
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
                if (unit != null) {
                    Text(
                        text = unit,
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

/**
 * Routine suggestion card with decorative icon and CTA.
 */
@Composable
private fun KPRoutineSuggestionCard(
    routine: RoutineModel,
    isPrimary: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(240.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        // Decorative icon
        Icon(
            Icons.TwoTone.FitnessCenter,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.TopEnd)
                .offset(x = 16.dp, y = (-16).dp)
        )

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = routine.name,
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.TwoTone.Timer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${routine.exercises.size} ejercicios",
                    fontFamily = ManropeFont,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(4.dp))

            // CTA
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isPrimary) Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                        else Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.surfaceContainerHighest,
                                MaterialTheme.colorScheme.surfaceContainerHighest
                            )
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "INICIAR",
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    color = if (isPrimary) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Workout history card — 3 visual variants: Completed, InProgress, Planned.
 */
@Composable
private fun KPWorkoutHistoryCard(
    workout: WorkoutModel,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) {
    val isFinished = workout.isFinished
    val isInProgress = !isFinished && workout.exercisesAndSets.any { it.second.isNotEmpty() }
    val dateFormat = SimpleDateFormat("dd MMM yyyy · HH:mm", Locale("es"))

    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .then(
                if (isInProgress) Modifier.border(
                    width = 4.dp,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) else Modifier
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onClickLabel = "open workout"
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Title
            Text(
                text = workout.title,
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = if (isFinished) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Date
            Text(
                text = dateFormat.format(workout.date),
                fontFamily = ManropeFont,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Badge
            val badgeText = when {
                isFinished -> "COMPLETADO"
                isInProgress -> "EN CURSO"
                else -> "PLANIFICADO"
            }
            val badgeBg = when {
                isFinished -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                isInProgress -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                else -> Color.Transparent
            }
            val badgeTextColor = when {
                isFinished -> MaterialTheme.colorScheme.tertiary
                isInProgress -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            val badgeBorderColor = when {
                isFinished -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                isInProgress -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.outlineVariant
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(badgeBg)
                    .border(1.dp, badgeBorderColor, RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                    color = badgeTextColor
                )
            }

            // Metrics
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                val totalSets = workout.exercisesAndSets.sumOf { it.second.size }
                val totalExercises = workout.exercisesAndSets.size

                KPMetric(label = "SERIES", value = totalSets.toString())
                KPMetric(label = "EJERCICIOS", value = totalExercises.toString())
            }
        }
    }
}

/**
 * Small metric display with label and value.
 */
@Composable
private fun KPMetric(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontFamily = ManropeFont,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = (-0.5).sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
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
        containerColor = Color(0xFF1A1A24)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = workout.title,
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                letterSpacing = (-0.5).sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Status badge
            val statusText = if (workout.isFinished) "Terminado" else "En progreso"
            val statusColor = if (workout.isFinished) MaterialTheme.colorScheme.tertiary
            else MaterialTheme.colorScheme.primary

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(statusColor.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = statusText.uppercase(),
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    color = statusColor
                )
            }

            // Exercise list
            if (workout.exercisesAndSets.isNotEmpty()) {
                Text(
                    text = "EJERCICIOS",
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                )

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    workout.exercisesAndSets.forEach { (exercise, sets) ->
                        Text(
                            text = "${exercise.name} — ${sets.size} series",
                            fontFamily = ManropeFont,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Continue/View button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.primary
                            )
                        )
                    )
                    .clickable(onClick = onContinue)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.TwoTone.PlayArrow,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (workout.isFinished) "VER ENTRENAMIENTO" else "CONTINUAR",
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // Delete button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.error.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                    .clickable(onClick = onDelete)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.TwoTone.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "ELIMINAR ENTRENAMIENTO",
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
