package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.components.TextFieldWithTitle
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.navigation.SheetDestination
import com.mintocode.rutinapp.ui.screenStates.RoutinesScreenState
import com.mintocode.rutinapp.ui.screens.SimpleExerciseItem
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.viewmodels.RoutinesViewModel

/**
 * Sheet for viewing routine details (read-only).
 *
 * Shows routine name, body part, and exercise list.
 * Provides action to navigate to the edit sheet.
 *
 * @param viewModel RoutinesViewModel for navigation to edit
 */
@Composable
fun RoutineDetailSheet(viewModel: RoutinesViewModel) {
    val navigator = LocalSheetNavigator.current
    val uiState by viewModel.uiState.observeAsState()

    val observeState = uiState as? RoutinesScreenState.Observe
    if (observeState == null) {
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
            text = observeState.routine.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        TextFieldWithTitle(title = "Nombre", text = observeState.routine.name, editing = false)
        TextFieldWithTitle(
            title = "Parte del cuerpo",
            text = observeState.routine.targetedBodyPart,
            editing = false
        )

        Text(
            text = "Ejercicios",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                .padding(16.dp)
                .heightIn(max = 300.dp)
                .fillMaxWidth()
        ) {
            if (observeState.routine.exercises.isEmpty()) {
                item {
                    Text(
                        text = "No hay ejercicios en esta rutina",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(observeState.routine.exercises) { exercise ->
                    SimpleExerciseItem(
                        item = exercise,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    viewModel.clickEditRoutine(observeState.routine)
                    navigator.replace(SheetDestination.RoutineEdit(observeState.routine.id))
                },
                colors = rutinAppButtonsColours(),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Editar")
            }
            Button(
                onClick = {
                    viewModel.deleteRoutine(observeState.routine)
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
        }
    }
}
