package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.viewmodels.RoutinesViewModel

/**
 * Sheet for creating a new routine.
 *
 * Renders the routine creation form as full sheet content.
 * On successful creation, closes the sheet and opens the edit sheet to add exercises.
 *
 * @param viewModel RoutinesViewModel for routine creation
 */
@Composable
fun RoutineCreateSheet(viewModel: RoutinesViewModel) {
    val navigator = LocalSheetNavigator.current
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var targetedBodyPart by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Crear nueva rutina",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        TextFieldWithTitle(title = "Nombre de la rutina", text = name, onWrite = { name = it })
        TextFieldWithTitle(
            title = "Parte del cuerpo",
            text = targetedBodyPart,
            onWrite = { targetedBodyPart = it },
            sendFunction = {
                viewModel.createRoutine(name, targetedBodyPart, context)
                navigator.close()
            }
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    viewModel.createRoutine(name, targetedBodyPart, context)
                    navigator.close()
                },
                colors = rutinAppButtonsColours(),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Crear")
            }
        }
    }
}
