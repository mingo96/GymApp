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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.components.TextFieldWithTitle
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.screenStates.ExercisesState
import com.mintocode.rutinapp.ui.screens.ExerciseTypeSelectors
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.viewmodels.ExercisesViewModel

/**
 * Sheet for editing an existing exercise.
 *
 * Renders the exercise edit form as full sheet content with related exercises management.
 * On save, updates the exercise and closes the sheet.
 *
 * @param viewModel ExercisesViewModel for exercise editing
 */
@Composable
fun ExerciseEditSheet(viewModel: ExercisesViewModel) {
    val navigator = LocalSheetNavigator.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.observeAsState()

    val modifyingState = uiState as? ExercisesState.Modifying
    if (modifyingState == null) {
        Text(
            text = "Cargando...",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    var name by rememberSaveable { mutableStateOf(modifyingState.exerciseModel.name) }
    var description by rememberSaveable { mutableStateOf(modifyingState.exerciseModel.description) }
    var targetedBodyPart by rememberSaveable { mutableStateOf(modifyingState.exerciseModel.targetedBodyPart) }
    var repsType by rememberSaveable { mutableStateOf(modifyingState.exerciseModel.repsType) }
    var weightType by rememberSaveable { mutableStateOf(modifyingState.exerciseModel.weightType) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Editar ejercicio",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        TextFieldWithTitle(
            title = "Nombre",
            onWrite = { name = it },
            text = name,
            sendFunction = {
                viewModel.updateExercise(name, description, targetedBodyPart, context, repsType, weightType)
            }
        )
        TextFieldWithTitle(
            title = "Descripción",
            onWrite = { description = it },
            text = description,
            sendFunction = {
                viewModel.updateExercise(name, description, targetedBodyPart, context, repsType, weightType)
            }
        )
        TextFieldWithTitle(
            title = "Parte del cuerpo",
            onWrite = { targetedBodyPart = it },
            text = targetedBodyPart,
            sendFunction = {
                viewModel.updateExercise(name, description, targetedBodyPart, context, repsType, weightType)
            }
        )

        ExerciseTypeSelectors(
            repsType = repsType,
            weightType = weightType,
            onRepsTypeChange = { repsType = it },
            onWeightTypeChange = { weightType = it }
        )

        // Related exercises section
        Text(
            text = "Ejercicios relacionados",
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp),
            modifier = Modifier
                .heightIn(max = 200.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                .padding(16.dp)
        ) {
            item {
                IconButton(onClick = { viewModel.clickToAddRelatedExercises(context) }) {
                    Icon(
                        imageVector = Icons.TwoTone.Add,
                        contentDescription = "Añadir relación",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            items(modifyingState.relatedExercises) { exercise ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = exercise.name,
                        Modifier
                            .fillMaxWidth(0.8f)
                            .clickable { viewModel.clickToObserve(exercise) },
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )
                    IconButton(onClick = { viewModel.toggleExercisesRelation(exercise) }) {
                        Icon(
                            imageVector = Icons.TwoTone.Delete,
                            contentDescription = "Eliminar relación",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Button(
                onClick = {
                    viewModel.updateExercise(name, description, targetedBodyPart, context, repsType, weightType)
                    navigator.close()
                },
                colors = rutinAppButtonsColours(),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Guardar")
            }
        }
    }
}
