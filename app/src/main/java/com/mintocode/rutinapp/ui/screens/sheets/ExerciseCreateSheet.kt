package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mintocode.rutinapp.ui.components.TextFieldWithTitle
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.screens.ExerciseTypeSelectors
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.viewmodels.ExercisesViewModel

/**
 * Sheet for creating a new exercise.
 *
 * Renders the exercise creation form as full sheet content instead of a dialog.
 * On successful creation, closes the sheet automatically.
 *
 * @param viewModel ExercisesViewModel for exercise creation
 */
@Composable
fun ExerciseCreateSheet(viewModel: ExercisesViewModel) {
    val navigator = LocalSheetNavigator.current
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var targetedBodyPart by rememberSaveable { mutableStateOf("") }
    var repsType by rememberSaveable { mutableStateOf("base") }
    var weightType by rememberSaveable { mutableStateOf("base") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Crear ejercicio",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        TextFieldWithTitle(title = "Nombre", onWrite = { name = it }, text = name)
        TextFieldWithTitle(title = "Descripción", onWrite = { description = it }, text = description)
        TextFieldWithTitle(
            title = "Parte del cuerpo",
            onWrite = { targetedBodyPart = it },
            text = targetedBodyPart,
            sendFunction = {
                viewModel.addExercise(name, description, targetedBodyPart, context, repsType, weightType)
                navigator.close()
            }
        )

        ExerciseTypeSelectors(
            repsType = repsType,
            weightType = weightType,
            onRepsTypeChange = { repsType = it },
            onWeightTypeChange = { weightType = it }
        )

        Button(
            onClick = {
                viewModel.addExercise(name, description, targetedBodyPart, context, repsType, weightType)
                navigator.close()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = rutinAppButtonsColours()
        ) {
            Text(text = "Añadir ejercicio")
        }
    }
}
