package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.premade.AnimatedItem
import com.mintocode.rutinapp.ui.screenStates.WorkoutsScreenState
import com.mintocode.rutinapp.ui.screens.DigitalWatch
import com.mintocode.rutinapp.ui.screens.WorkoutProgression
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.viewmodels.WorkoutsViewModel

/**
 * Active workout sheet content.
 *
 * Displays the DigitalWatch timer and WorkoutProgression while a workout is in progress.
 * Reuses existing composables from WorkoutProgressionSection.kt.
 *
 * @param viewModel WorkoutsViewModel for workout actions
 * @param onNavigateToExercises Callback to open exercise list sheet
 */
@Composable
fun ActiveWorkoutSheet(
    viewModel: WorkoutsViewModel,
    onNavigateToExercises: () -> Unit
) {
    val navigator = LocalSheetNavigator.current
    val workoutState by viewModel.workoutScreenStates.observeAsState(WorkoutsScreenState.Observe())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        when (val state = workoutState) {
            is WorkoutsScreenState.WorkoutStarted -> {
                AnimatedItem(enterAnimation = slideInHorizontally { +it }, delay = 10) {
                    DigitalWatch(uiState = state, viewModel = viewModel)
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        WorkoutProgression(
                            viewModel = viewModel,
                            uiState = state,
                            onNavigateToExercises = onNavigateToExercises
                        )
                    }
                }

                if (!state.workout.isFinished) {
                    Button(
                        onClick = { viewModel.finishTraining() },
                        colors = rutinAppButtonsColours(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text(text = "Finalizar entrenamiento")
                    }
                } else {
                    // Workout finished — auto-close sheet
                    Text(
                        text = "Entrenamiento finalizado",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }

            is WorkoutsScreenState.Observe -> {
                // Workout ended or doesn't exist — close the sheet
                Text(
                    text = "No hay entrenamiento activo",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
