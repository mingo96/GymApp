package com.mintocode.rutinapp.ui.screens

import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowForward
import androidx.compose.material.icons.twotone.Add
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.data.models.WorkoutModel
import com.mintocode.rutinapp.ui.components.SearchTextField
import com.mintocode.rutinapp.ui.premade.AnimatedItem
import com.mintocode.rutinapp.ui.screenStates.WorkoutsScreenState
import com.mintocode.rutinapp.utils.completeHourString
import com.mintocode.rutinapp.viewmodels.WorkoutsViewModel
import kotlinx.coroutines.delay

/**
 * Card for a single workout in the recent workouts list.
 *
 * @param item The workout model to display
 * @param onClick Callback when the card is tapped (e.g. continue workout)
 * @param onLongPress Callback when the card is long-pressed (e.g. open CRUD sheet)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WorkoutItem(item: WorkoutModel, onClick: () -> Unit, onLongPress: () -> Unit = {}) {
    Box(modifier = Modifier
        .padding(end = 10.dp, top = 4.dp, bottom = 4.dp)
        .background(
            MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.small
        )
        .combinedClickable(onClick = onClick, onLongClick = onLongPress)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {

            Text(text = item.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(text = item.date.completeHourString(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (item.isFinished) Text(text = "Terminado", color = MaterialTheme.colorScheme.tertiary, fontSize = 12.sp)
        }
    }
}

/**
 * Card for a routine in the routines list.
 *
 * @param routine The routine model to display
 * @param modifier Modifier for the container
 * @param onPress Callback when the start button is tapped
 */
@Composable
fun RoutineItem(routine: RoutineModel, modifier: Modifier, onPress: () -> Unit) {
    Row(
        modifier = modifier.background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.8f)
        ) {
            Text(text = routine.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = routine.targetedBodyPart)
            for (i in routine.exercises) {
                Text(text = i.name)
            }
        }
        IconButton(onClick = { onPress() }, modifier = Modifier.padding(end = 16.dp, top = 16.dp)) {
            Icon(imageVector = Icons.AutoMirrored.TwoTone.ArrowForward, contentDescription = null)
        }
    }
}

/**
 * Panel showing the base routine's exercises during an active workout.
 *
 * @param uiState Current workout state
 * @param viewModel ViewModel managing workout actions
 * @param maxWidth Maximum width constraint for the panel
 */
@Composable
fun RoutineContent(
    uiState: WorkoutsScreenState.WorkoutStarted, viewModel: WorkoutsViewModel, maxWidth: Dp = 300.dp
) {

    Column(
        modifier = Modifier
            .padding(end = 16.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(text = "Rutina base", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            LazyColumn(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium)
                    .padding(16.dp)
                    .heightIn(max = 120.dp)
                    .widthIn(max = maxWidth - 36.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.workout.baseRoutine!!.exercises) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = it.name, fontSize = 15.sp)
                        if (it.id !in uiState.workout.exercisesAndSets.map { it.first.id }) {
                            IconButton(onClick = { viewModel.addExerciseToWorkout(it) }) {
                                Icon(imageVector = Icons.TwoTone.Add, contentDescription = " ")
                            }
                        }
                    }
                }
            }

        }
    }
}

/**
 * Panel showing available exercises to add to the current workout.
 *
 * @param uiState Current workout state with the list of available exercises
 * @param viewModel ViewModel managing workout actions
 * @param onNavigateToExercises Callback to navigate to the exercises screen
 * @param maxWidth Maximum width constraint for the panel
 */
@Composable
fun OtherExercises(
    uiState: WorkoutsScreenState.WorkoutStarted,
    viewModel: WorkoutsViewModel,
    onNavigateToExercises: () -> Unit,
    maxWidth: Dp = 300.dp
) {

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
            .width(maxWidth + 36.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Ejercicios disponibles", fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
                var name by rememberSaveable { mutableStateOf("") }
                SearchTextField(value = name,
                    onValueChange = { name = it },
                    onSearch = { viewModel.searchExercise(name) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            var maxIndex by rememberSaveable { mutableIntStateOf(0) }

            LaunchedEffect(key1 = uiState) {
                while (true) {
                    delay(100)
                    if (maxIndex < uiState.otherExercises.size) maxIndex++
                }
            }

            LazyColumn(
                Modifier
                    .widthIn(150.dp, maxWidth)
                    .heightIn(50.dp, 300.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                if (uiState.otherExercises.isEmpty()) {
                    item {
                        Text(
                            text = "No hay ejercicios disponibles",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )
                    }
                }
                items(uiState.otherExercises.take(maxIndex), key = { it.id }) {

                    AnimatedItem(delay = 50, enterAnimation = slideInHorizontally()) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                        ) {

                            Text(
                                text = it.name,
                                fontSize = 15.sp,
                                modifier = Modifier.fillMaxWidth(0.8f)
                            )

                            IconButton(onClick = {
                                viewModel.addExerciseToWorkout(it)
                            }) {
                                Icon(
                                    imageVector = Icons.TwoTone.Add,
                                    contentDescription = "add exercise to workout"
                                )
                            }
                        }
                    }
                }
                item {
                    IconButton(onClick = {
                        onNavigateToExercises()
                        viewModel.exercisesViewModel.clickToCreate()
                    }, Modifier.fillMaxWidth()) {
                        Icon(
                            imageVector = Icons.TwoTone.Add, contentDescription = "add new exercise"
                        )
                    }
                }
            }
        }
    }
}
