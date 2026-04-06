package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mintocode.rutinapp.ui.screenStates.MainScreenState
import com.mintocode.rutinapp.ui.screens.PlanningEditionDialog
import com.mintocode.rutinapp.viewmodels.MainScreenViewModel

/**
 * Planning edit sheet content.
 *
 * Delegates to the existing PlanningEditionDialog composable which handles
 * body part / routine selection for a given date's planning.
 *
 * @param viewModel MainScreenViewModel for planning actions
 */
@Composable
fun PlanningEditSheet(viewModel: MainScreenViewModel) {
    val uiState by viewModel.uiState.observeAsState(MainScreenState.Observation)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Planificación",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        when (val state = uiState) {
            is MainScreenState.PlanningOnMainFocus -> {
                PlanningEditionDialog(viewModel = viewModel, uistate = state)
            }
            else -> {
                Text(
                    text = "Selecciona un día en el calendario para planificarlo",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
