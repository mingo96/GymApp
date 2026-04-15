package com.mintocode.rutinapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material.icons.twotone.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mintocode.rutinapp.R
import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.models.SetModel
import com.mintocode.rutinapp.ui.premade.AnimatedItem
import com.mintocode.rutinapp.ui.screenStates.SetState
import com.mintocode.rutinapp.ui.screenStates.WorkoutsScreenState
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.utils.completeHourString
import com.mintocode.rutinapp.viewmodels.WorkoutsViewModel
import kotlinx.coroutines.delay
import java.util.Date

/**
 * Shows active workout progression: exercise carousel, sets, and dialogs.
 *
 * KP design: horizontal snap carousel for exercises, inline set table,
 * exercise swap/edit dialogs.
 *
 * @param viewModel ViewModel managing workout actions
 * @param uiState Current workout state with exercises, sets, and dialogs
 * @param onNavigateToExercises Callback to navigate to the exercises screen
 */
@SuppressLint("SimpleDateFormat")
@Composable
fun WorkoutProgression(
    viewModel: WorkoutsViewModel,
    uiState: WorkoutsScreenState.WorkoutStarted,
    onNavigateToExercises: () -> Unit
) {
    // Dialogs
    if (uiState.setBeingCreated != null) {
        if (uiState.setBeingCreated is SetState.CreatingSet) {
            SetEditionSheet(viewModel = viewModel, set = uiState.setBeingCreated.set)
        } else {
            SetOptionsSheet(viewModel = viewModel, uiState = uiState)
        }
    } else if (uiState.exerciseBeingSwapped != null) {
        ExerciseSwapSheet(viewModel = viewModel, exercise = uiState.exerciseBeingSwapped)
    }

    // Exercise Carousel + Progression
    if (!uiState.workout.isFinished) BoxWithConstraints {
        val listState = rememberLazyListState()
        val flingBehavior = rememberSnapFlingBehavior(
            lazyListState = listState, snapPosition = SnapPosition.Start
        )

        // ── Exercise Carousel ──
        LazyRow(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Active & pending exercise cards
            items(uiState.workout.exercisesAndSets) { exerciseAndSets ->
                val isActive = exerciseAndSets == uiState.workout.exercisesAndSets.firstOrNull {
                    it.second.isEmpty() || it.second.any { s -> !s.observations.contains("completed", true) }
                }
                ExerciseCarouselCard(
                    exercise = exerciseAndSets.first,
                    setCount = exerciseAndSets.second.size,
                    totalSets = exerciseAndSets.first.setsAndReps?.split("x")?.firstOrNull()?.trim()?.toIntOrNull() ?: 0,
                    isActive = isActive,
                    onClick = { /* scroll to exercise in progression */ },
                    width = maxWidth - 80.dp
                )
            }

            // Add exercise card
            item {
                AddExerciseCard(
                    onClick = onNavigateToExercises,
                    width = 192.dp
                )
            }
        }
    }

    // ── Section Header ──
    Text(
        text = if (uiState.workout.isFinished) "PROGRESO DEL ENTRENAMIENTO" else "PROGRESO ACTUAL",
        fontFamily = SpaceGroteskFont,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 2.sp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
        modifier = Modifier.padding(bottom = 16.dp)
    )

    // ── Exercise Progression List ──
    var maxIndex by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(key1 = maxIndex) {
        while (true) {
            delay(100)
            if (maxIndex < uiState.workout.exercisesAndSets.size) maxIndex++
        }
    }

    LazyColumn(
        Modifier
            .fillMaxWidth()
            .heightIn(max = 600.dp)
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(uiState.workout.exercisesAndSets.take(maxIndex + 1), key = { it.first.id }) {
            AnimatedItem(delay = 50, enterAnimation = slideInHorizontally()) {
                Column(
                    modifier = Modifier
                        .animateItem(placementSpec = spring(stiffness = Spring.StiffnessHigh))
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(20.dp)
                ) {
                    var setsOpened by rememberSaveable { mutableStateOf(false) }

                    ExerciseInfo(
                        it, uiState, setsOpened,
                        { setsOpened = !setsOpened }
                    ) { viewModel.startSwappingExercise(it.first) }

                    ExerciseSets(it, viewModel, setsOpened, uiState)

                    if (!uiState.workout.isFinished) {
                        ExerciseActions(
                            viewModel = viewModel,
                            exerciseAndSets = it,
                            uiState = uiState
                        )
                    }
                }
            }
        }
    }
}

/**
 * Exercise card in the horizontal carousel.
 *
 * Active: primaryContainer bg, white text.
 * Pending: surfaceContainerLow bg, 60% alpha.
 */
@Composable
private fun ExerciseCarouselCard(
    exercise: ExerciseModel,
    setCount: Int,
    totalSets: Int,
    isActive: Boolean,
    onClick: () -> Unit,
    width: androidx.compose.ui.unit.Dp
) {
    val bgColor = if (isActive) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surfaceContainerLow
    val contentAlpha = if (isActive) 1f else 0.6f

    Box(
        modifier = Modifier
            .width(width)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor.copy(alpha = contentAlpha))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        // Decorative blob for active card
        if (isActive) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 16.dp, y = (-16).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Body part tag
                val bodyPart = exercise.targetedBodyPart.uppercase()
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isActive) Color.White.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = bodyPart,
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Set count
                Text(
                    text = "$setCount/$totalSets sets",
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = if (isActive) Color.White.copy(alpha = 0.8f)
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = exercise.name,
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Dashed-border card to add a new exercise to the workout.
 */
@Composable
private fun AddExerciseCard(
    onClick: () -> Unit,
    width: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .width(width)
            .clip(RoundedCornerShape(16.dp))
            .border(
                2.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.TwoTone.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "AÑADIR EJERCICIO",
                fontFamily = ManropeFont,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

/**
 * Displays exercise name, type hints, set count toggle, and swap button.
 *
 * @param exerciseAndSets Pair of exercise model and its sets
 * @param uiState Current workout state
 * @param setsOpened Whether the sets list is currently expanded
 * @param changeSetsOpened Callback to toggle set list visibility
 * @param startSwapping Callback to initiate exercise swap
 */
@Composable
private fun ExerciseInfo(
    exerciseAndSets: Pair<ExerciseModel, List<SetModel>>,
    uiState: WorkoutsScreenState.WorkoutStarted,
    setsOpened: Boolean,
    changeSetsOpened: () -> Unit,
    startSwapping: () -> Unit
) {
    val typeHints = buildList {
        if (exerciseAndSets.first.repsType == "seconds") add("Tiempo")
        if (exerciseAndSets.first.weightType == "unilateral") add("Unilateral")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exerciseAndSets.first.name + if (exerciseAndSets.first in (uiState.workout.baseRoutine?.exercises
                        ?: emptyList())
                ) " (${exerciseAndSets.first.setsAndReps})" else "",
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = (-0.3).sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (typeHints.isNotEmpty()) {
                Text(
                    text = typeHints.joinToString(" · "),
                    fontFamily = ManropeFont,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Body part tag
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = exerciseAndSets.first.targetedBodyPart.uppercase(),
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        if (exerciseAndSets.second.isNotEmpty()) {
            IconButton(onClick = changeSetsOpened) {
                Icon(
                    imageVector = if (!setsOpened) Icons.TwoTone.KeyboardArrowDown
                    else Icons.TwoTone.KeyboardArrowUp,
                    contentDescription = "mostrar series",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else if (!uiState.workout.isFinished && exerciseAndSets.first.equivalentExercises.isNotEmpty()) {
            IconButton(onClick = startSwapping) {
                Icon(
                    painter = painterResource(id = R.drawable.swap),
                    contentDescription = "cambiar ejercicio",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Expandable set rows for an exercise — KP table style.
 *
 * Completed sets show tertiary check, active set is highlighted.
 *
 * @param exerciseAndSets Pair of exercise model and its sets
 * @param viewModel ViewModel managing workout actions
 * @param setsOpened Whether the sets list is visible
 * @param uiState Current workout state
 */
@Composable
private fun ExerciseSets(
    exerciseAndSets: Pair<ExerciseModel, List<SetModel>>,
    viewModel: WorkoutsViewModel,
    setsOpened: Boolean,
    uiState: WorkoutsScreenState.WorkoutStarted
) {
    AnimatedVisibility(
        visible = setsOpened,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        val repsLabel = if (exerciseAndSets.first.repsType == "seconds") "segs" else "reps"
        val weightLabel = if (exerciseAndSets.first.weightType == "unilateral") "kgs/lado" else "kgs"

        Column(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "SET",
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.width(32.dp)
                )
                Text(
                    text = weightLabel.uppercase(),
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = repsLabel.uppercase(),
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Box(modifier = Modifier.size(32.dp)) // Status column placeholder
            }

            // Set rows
            exerciseAndSets.second.forEachIndexed { index, set ->
                val isLastSet = index == exerciseAndSets.second.lastIndex

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isLastSet && !uiState.workout.isFinished)
                                MaterialTheme.colorScheme.surfaceContainerHigh
                            else MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.5f)
                        )
                        .then(
                            if (isLastSet && !uiState.workout.isFinished)
                                Modifier.border(
                                    2.dp,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    RoundedCornerShape(12.dp)
                                )
                            else Modifier
                        )
                        .clickable { viewModel.setOptionsClicked(set) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // SET number
                    Text(
                        text = "${index + 1}",
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isLastSet && !uiState.workout.isFinished)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(32.dp)
                    )

                    // Weight
                    Text(
                        text = "${set.weight}",
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    // Reps
                    Text(
                        text = "${set.reps}",
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    // Status icon
                    Icon(
                        imageVector = if (isLastSet && !uiState.workout.isFinished)
                            Icons.Filled.RadioButtonUnchecked
                        else Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = if (isLastSet && !uiState.workout.isFinished)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * Action buttons for an exercise row: add set, delete, reorder.
 *
 * KP design: icon buttons with tinted colors.
 *
 * @param viewModel ViewModel managing workout actions
 * @param exerciseAndSets Pair of exercise model and its sets
 * @param uiState Current workout state
 */
@Composable
private fun ExerciseActions(
    viewModel: WorkoutsViewModel,
    exerciseAndSets: Pair<ExerciseModel, List<SetModel>>,
    uiState: WorkoutsScreenState.WorkoutStarted
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(onClick = { viewModel.addSetClicked(exerciseAndSets.first) }) {
            Icon(
                imageVector = Icons.TwoTone.Add,
                contentDescription = "Añadir serie",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = { viewModel.removeExerciseFromRoutine(exerciseAndSets.first) }) {
            Icon(
                imageVector = Icons.TwoTone.Delete,
                contentDescription = "eliminar ejercicio",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
            )
        }
        if (exerciseAndSets != uiState.workout.exercisesAndSets.last()) {
            IconButton(onClick = { viewModel.moveExercise(exerciseAndSets.first, false) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.TwoTone.ArrowBack,
                    contentDescription = "subir",
                    modifier = Modifier.rotate(-90f),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (exerciseAndSets != uiState.workout.exercisesAndSets.first()) {
            IconButton(onClick = { viewModel.moveExercise(exerciseAndSets.first, true) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.TwoTone.ArrowBack,
                    contentDescription = "bajar",
                    modifier = Modifier.rotate(90f),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Shows the current time and last set time during an active workout.
 *
 * KP design: large timer with digital glow effect.
 *
 * @param uiState Current workout state with exercise/set history
 * @param viewModel ViewModel providing the live clock
 */
@Composable
fun DigitalWatch(uiState: WorkoutsScreenState.WorkoutStarted, viewModel: WorkoutsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Workout name
        Text(
            text = uiState.workout.baseRoutine?.name ?: "Entrenamiento libre",
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            letterSpacing = (-0.5).sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        // Live timer
        if (!uiState.workout.isFinished) {
            val actualDate by viewModel.currentDate.collectAsStateWithLifecycle(initialValue = System.currentTimeMillis())
            Text(
                text = Date(actualDate).completeHourString(),
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Black,
                fontSize = 48.sp,
                letterSpacing = (-2).sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }

        // Last set time
        if (uiState.workout.exercisesAndSets.isNotEmpty() &&
            uiState.workout.exercisesAndSets.any { it.second.isNotEmpty() }
        ) {
            val lastSet = uiState.workout.exercisesAndSets.maxOf {
                if (it.second.isEmpty()) Date(0) else it.second.maxOf { s -> s.date }
            }
            Text(
                text = "Última serie: ${lastSet.completeHourString()}",
                fontFamily = ManropeFont,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
