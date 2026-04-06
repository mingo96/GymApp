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
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mintocode.rutinapp.R
import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.models.SetModel
import com.mintocode.rutinapp.ui.premade.AnimatedItem
import com.mintocode.rutinapp.ui.screenStates.SetState
import com.mintocode.rutinapp.ui.screenStates.WorkoutsScreenState
import com.mintocode.rutinapp.viewmodels.WorkoutsViewModel
import com.mintocode.rutinapp.utils.completeHourString
import kotlinx.coroutines.delay
import java.util.Date

/**
 * Shows active workout progression: exercise list, sets, and swap/edit dialogs.
 *
 * Displays the base routine panel and other exercises in a snap-scrollable row,
 * followed by the chronological exercise progression with expandable sets.
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

    if (uiState.setBeingCreated != null) {
        if (uiState.setBeingCreated is SetState.CreatingSet) {
            SetEditionDialog(viewModel = viewModel, set = uiState.setBeingCreated.set)
        } else {
            SetOptionsDialog(viewModel = viewModel, uiState = uiState)
        }
    } else if (uiState.exerciseBeingSwapped != null) {
        ExerciseSwapDialog(viewModel = viewModel, exercise = uiState.exerciseBeingSwapped)
    }

    if (!uiState.workout.isFinished) BoxWithConstraints {

        val listState = rememberLazyListState()
        val flingBehavior = rememberSnapFlingBehavior(
            lazyListState = listState, snapPosition = SnapPosition.Start
        )
        LazyRow(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        ) {
            if (uiState.workout.baseRoutine != null) item {
                RoutineContent(uiState = uiState, viewModel = viewModel, maxWidth - 32.dp)
            }
            item {
                OtherExercises(
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigateToExercises = onNavigateToExercises,
                    maxWidth - 32.dp
                )
            }
        }
    }

    Text(
        text = "Progreso" + if (uiState.workout.isFinished) " del entrenamiento" else " actual",
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold
    )

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
            .heightIn(max = 500.dp)
            .padding(top = 16.dp),
    ) {

        items(uiState.workout.exercisesAndSets.take(maxIndex + 1), key = { it.first.id }) {

            AnimatedItem(delay = 50, enterAnimation = slideInHorizontally()) {

                Column(modifier = Modifier.animateItem(placementSpec = spring(stiffness = Spring.StiffnessHigh))) {

                    var setsOpened by rememberSaveable {
                        mutableStateOf(false)
                    }

                    ExerciseInfo(
                        it,
                        uiState,
                        setsOpened,
                        { setsOpened = !setsOpened }) { viewModel.startSwappingExercise(it.first) }

                    ExerciseSets(it, viewModel, setsOpened, uiState)

                    if (!uiState.workout.isFinished) ExerciseActions(
                        viewModel = viewModel, exerciseAndSets = it, uiState = uiState
                    )
                }
            }

        }
    }

}

/**
 * Displays exercise name, set count toggle, and swap button in a workout.
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

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = exerciseAndSets.first.name + if (exerciseAndSets.first in (uiState.workout.baseRoutine?.exercises
                    ?: emptyList())
            ) " (" + exerciseAndSets.first.setsAndReps + ")" else "",
            fontSize = 15.sp,
            maxLines = 1,
            modifier = Modifier.padding(end = 16.dp)
        )
        if (exerciseAndSets.second.isNotEmpty()) IconButton(onClick = { changeSetsOpened() }) {
            Icon(
                imageVector = if (!setsOpened) Icons.TwoTone.KeyboardArrowDown else Icons.TwoTone.KeyboardArrowUp,
                contentDescription = "toggle sets being opened"
            )
        }
        else if (!uiState.workout.isFinished && exerciseAndSets.first.equivalentExercises.isNotEmpty()) {

            IconButton(onClick = { startSwapping() }) {
                Icon(
                    painter = painterResource(id = R.drawable.swap),
                    contentDescription = "change exercise"
                )
            }

        }
    }
}

/**
 * Expandable list of sets for an exercise, showing reps and weight.
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
        visible = setsOpened, enter = expandVertically(), exit = shrinkVertically()
    ) {

        Column {

            for (i in exerciseAndSets.second) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                ) {
                    Text(
                        text = i.reps.toString() + " reps x " + i.weight.toString() + " kgs",
                        fontSize = 15.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                    if (!uiState.workout.isFinished) IconButton(onClick = {
                        viewModel.setOptionsClicked(i)
                    }) {
                        Icon(
                            imageVector = Icons.TwoTone.MoreVert, contentDescription = "set options"
                        )
                    }
                }
            }
        }
    }

}

/**
 * Action buttons for an exercise row: add set, delete, reorder.
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

    Row {
        IconButton(onClick = {
            viewModel.addSetClicked(exerciseAndSets.first)
        }) {
            Icon(imageVector = Icons.TwoTone.Add, contentDescription = "add sett to exercise")
        }
        IconButton(onClick = { viewModel.removeExerciseFromRoutine(exerciseAndSets.first) }) {
            Icon(imageVector = Icons.TwoTone.Delete, contentDescription = "delete exercise")
        }
        if (exerciseAndSets != uiState.workout.exercisesAndSets.last()) {
            IconButton(onClick = { viewModel.moveExercise(exerciseAndSets.first, false) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.TwoTone.ArrowBack,
                    contentDescription = "move up",
                    modifier = Modifier.rotate(-90f)
                )
            }
        }
        if (exerciseAndSets != uiState.workout.exercisesAndSets.first()) {
            IconButton(onClick = { viewModel.moveExercise(exerciseAndSets.first, true) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.TwoTone.ArrowBack,
                    contentDescription = "move down",
                    modifier = Modifier.rotate(90f)
                )
            }
        }
    }
}

/**
 * Shows the current time and last set time during an active workout.
 *
 * @param uiState Current workout state with exercise/set history
 * @param viewModel ViewModel providing the live clock
 */
@Composable
fun DigitalWatch(uiState: WorkoutsScreenState.WorkoutStarted, viewModel: WorkoutsViewModel) {

    Row(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (uiState.workout.exercisesAndSets.isNotEmpty() && uiState.workout.exercisesAndSets.first().second.isNotEmpty()) {
            val lastSet = uiState.workout.exercisesAndSets.maxOf {
                if (it.second.isEmpty()) Date(0) else it.second.maxOf { it.date }
            }
            Text(
                text = "Último: " + lastSet.completeHourString(),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )
        }
        if (!uiState.workout.isFinished) {
            val actualDate by viewModel.currentDate.collectAsStateWithLifecycle(initialValue = System.currentTimeMillis())

            Text(
                text = Date(actualDate).completeHourString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
