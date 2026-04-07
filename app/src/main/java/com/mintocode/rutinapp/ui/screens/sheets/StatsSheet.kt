package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.ui.components.SearchTextField
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.navigation.SheetDestination
import com.mintocode.rutinapp.ui.premade.AnimatedItem
import com.mintocode.rutinapp.viewmodels.StatsViewModel

/**
 * Statistics overview sheet showing the exercise list.
 *
 * When an exercise is clicked, it opens a new stacked ExerciseStats sheet
 * instead of replacing content in the same sheet.
 *
 * @param viewModel StatsViewModel for stats data and actions
 */
@Composable
fun StatsSheet(viewModel: StatsViewModel) {
    val navigator = LocalSheetNavigator.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val exercises by viewModel.exercisesState.collectAsStateWithLifecycle(
        initialValue = emptyList(), lifecycle = lifecycle
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Estadísticas",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        var searchQuery by rememberSaveable { mutableStateOf("") }

        SearchTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            onSearch = { viewModel.searchExercise(searchQuery) },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Selecciona un ejercicio",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (exercises.isEmpty()) {
            Text(
                text = "No hay ejercicios disponibles",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(exercises) { exercise ->
                    AnimatedItem(enterAnimation = slideInVertically(), delay = 50) {
                        StatsExerciseCard(exercise = exercise) {
                            viewModel.selectExerciseForStats(exercise)
                            navigator.open(SheetDestination.ExerciseStats)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

/**
 * Clickable exercise card for the stats overview.
 */
@Composable
private fun StatsExerciseCard(exercise: ExerciseModel, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .border(1.5.dp, MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(text = exercise.name, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
        Text(
            text = exercise.description.take(30),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
