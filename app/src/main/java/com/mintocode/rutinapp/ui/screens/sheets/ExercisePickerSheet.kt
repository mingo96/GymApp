package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.slideInHorizontally
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.ui.components.EmptyStateMessage
import com.mintocode.rutinapp.ui.components.SearchTextField
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.premade.AnimatedItem
import com.mintocode.rutinapp.ui.screenStates.WorkoutsScreenState
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.viewmodels.WorkoutsViewModel

/**
 * Exercise picker sheet for the active workout.
 *
 * Shows the list of available exercises (not already in the workout)
 * so the user can tap one to add it immediately.
 *
 * @param viewModel WorkoutsViewModel to access otherExercises and add to workout
 */
@Composable
fun ExercisePickerSheet(viewModel: WorkoutsViewModel) {
    val navigator = LocalSheetNavigator.current
    val workoutState by viewModel.workoutScreenStates.observeAsState(WorkoutsScreenState.Observe())

    val otherExercises = when (val state = workoutState) {
        is WorkoutsScreenState.WorkoutStarted -> state.otherExercises
        else -> emptyList()
    }

    var search by rememberSaveable { mutableStateOf("") }

    val filtered = if (search.isBlank()) otherExercises
    else otherExercises.filter {
        it.name.contains(search, ignoreCase = true) ||
                it.targetedBodyPart.contains(search, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        // ── Header ──
        Text(
            text = "Añadir ejercicio",
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            letterSpacing = (-0.5).sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Selecciona un ejercicio para añadirlo al entrenamiento",
            fontFamily = ManropeFont,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // ── Search ──
        SearchTextField(
            value = search,
            onValueChange = { search = it },
            onSearch = {},
            placeholder = "Buscar ejercicio..."
        )

        Spacer(Modifier.height(16.dp))

        // ── List ──
        if (filtered.isEmpty()) {
            EmptyStateMessage(
                text = if (search.isBlank()) "No hay más ejercicios disponibles"
                else "Sin resultados para \"$search\""
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered, key = { it.id }) { exercise ->
                AnimatedItem(enterAnimation = slideInHorizontally { +it }, delay = 30) {
                    PickerExerciseRow(
                        exercise = exercise,
                        onClick = {
                            viewModel.addExerciseToWorkout(exercise)
                            navigator.close()
                        }
                    )
                }
            }
        }
    }
}

/**
 * Single exercise row in the picker — tap to add.
 */
@Composable
private fun PickerExerciseRow(
    exercise: ExerciseModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.TwoTone.FitnessCenter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name,
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (exercise.targetedBodyPart.isNotBlank()) {
                Text(
                    text = exercise.targetedBodyPart.uppercase(),
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Add icon
        Icon(
            Icons.TwoTone.Add,
            contentDescription = "Añadir",
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(22.dp)
        )
    }
}
