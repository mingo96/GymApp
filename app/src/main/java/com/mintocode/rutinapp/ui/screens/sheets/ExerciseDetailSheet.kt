package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.components.TextFieldWithTitle
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.navigation.SheetDestination
import com.mintocode.rutinapp.ui.screenStates.ExercisesState
import com.mintocode.rutinapp.ui.screens.ExerciseTypeSelectors
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.viewmodels.ExercisesViewModel

/**
 * Sheet for viewing exercise details (read-only).
 *
 * Shows exercise info, type selectors (disabled), and related exercises.
 * Provides actions to edit, upload, or obtain the exercise.
 *
 * @param viewModel ExercisesViewModel for navigation to edit
 */
@Composable
fun ExerciseDetailSheet(viewModel: ExercisesViewModel) {
    val navigator = LocalSheetNavigator.current
    val uiState by viewModel.uiState.observeAsState()

    val observeState = uiState as? ExercisesState.Observe
    val exercise = observeState?.exercise
    if (exercise == null) {
        Text(
            text = "Cargando...",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = exercise.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        TextFieldWithTitle(title = "Nombre", text = exercise.name, editing = false)
        TextFieldWithTitle(title = "Descripción", text = exercise.description, editing = false)
        TextFieldWithTitle(title = "Parte del cuerpo", text = exercise.targetedBodyPart, editing = false)

        ExerciseTypeSelectors(
            repsType = exercise.repsType,
            weightType = exercise.weightType,
            onRepsTypeChange = {},
            onWeightTypeChange = {},
            enabled = false
        )

        if (exercise.equivalentExercises.isNotEmpty()) {
            Text(
                text = "Ejercicios relacionados",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            LazyVerticalGrid(
                columns = GridCells.Adaptive(100.dp),
                modifier = Modifier
                    .heightIn(max = 200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalArrangement = Arrangement.Center
            ) {
                items(exercise.equivalentExercises) { related ->
                    Text(
                        text = related.name,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                viewModel.clickToObserve(related)
                            }
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            if (exercise.isFromThisUser) {
                Button(
                    onClick = {
                        viewModel.clickToEdit(exercise)
                        navigator.replace(SheetDestination.ExerciseEdit(exercise.id.toIntOrNull() ?: 0))
                    },
                    colors = rutinAppButtonsColours(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Editar")
                }
                Button(
                    onClick = {
                        viewModel.deleteExercise(exercise)
                        navigator.close()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Eliminar")
                }
            } else if (exercise.id == "0") {
                Button(
                    onClick = {
                        viewModel.saveExercise(exercise)
                        navigator.close()
                    },
                    colors = rutinAppButtonsColours(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Obtener")
                }
            }
        }
    }
}
