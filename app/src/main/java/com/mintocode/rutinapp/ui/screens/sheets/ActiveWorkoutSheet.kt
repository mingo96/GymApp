package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material.icons.twotone.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.premade.AnimatedItem
import com.mintocode.rutinapp.ui.screenStates.WorkoutsScreenState
import com.mintocode.rutinapp.ui.screens.DigitalWatch
import com.mintocode.rutinapp.ui.screens.WorkoutProgression
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.viewmodels.WorkoutsViewModel

/**
 * Active workout sheet — KP fullscreen session design.
 *
 * TopAppBar with timer icon and FINISH button.
 * DigitalWatch with large timer and workout title.
 * WorkoutProgression with exercise carousel and inline set table.
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
            .background(MaterialTheme.colorScheme.surface)
    ) {
        when (val state = workoutState) {
            is WorkoutsScreenState.WorkoutStarted -> {
                // ── KP Session TopAppBar ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: timer icon + session label
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.TwoTone.Timer,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "TRAINING SESSION",
                            fontFamily = SpaceGroteskFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 2.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Right: FINISH button
                    if (!state.workout.isFinished) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f))
                                .clickable { viewModel.finishTraining() }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "FINISH",
                                fontFamily = SpaceGroteskFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = (-0.5).sp,
                                color = MaterialTheme.colorScheme.error
                            )
                            Icon(
                                Icons.TwoTone.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // ── Content ──
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        AnimatedItem(enterAnimation = slideInHorizontally { +it }, delay = 10) {
                            DigitalWatch(uiState = state, viewModel = viewModel)
                        }
                    }

                    item {
                        WorkoutProgression(
                            viewModel = viewModel,
                            uiState = state,
                            onNavigateToExercises = onNavigateToExercises
                        )
                    }
                }

                // ── Bottom status ──
                if (state.workout.isFinished) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.TwoTone.Done,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Entrenamiento finalizado",
                            fontFamily = ManropeFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            is WorkoutsScreenState.Observe -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay entrenamiento activo",
                        fontFamily = ManropeFont,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
