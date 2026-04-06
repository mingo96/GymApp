package com.mintocode.rutinapp.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowForward
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.CheckCircle
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.premade.AdjustableText
import com.mintocode.rutinapp.ui.premade.RutinAppCalendar
import com.mintocode.rutinapp.ui.screenStates.MainScreenState
import com.mintocode.rutinapp.ui.theme.rutinAppDatePickerColors
import com.mintocode.rutinapp.utils.simpleDateString
import com.mintocode.rutinapp.viewmodels.MainScreenViewModel
import java.util.Date

/**
 * Calendar-centered dashboard screen.
 *
 * Displays today's planning card at the top, a collapsible date range picker,
 * and the full calendar view below. Uses the new v2 design system with
 * MaterialTheme.colorScheme tokens throughout.
 *
 * @param onNavigateToTrain Callback to navigate to the training screen
 * @param mainScreenViewModel ViewModel providing plannings, today's plan, and calendar phases
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToTrain: () -> Unit, mainScreenViewModel: MainScreenViewModel
) {

    LaunchedEffect(Unit) {
        mainScreenViewModel.autoSync()
    }

    val plannings by mainScreenViewModel.plannings.observeAsState(emptyList())
    val uiState by mainScreenViewModel.uiState.observeAsState(MainScreenState.Observation)
    val todaysPlanning by mainScreenViewModel.todaysPlanning.observeAsState()
    val calendarPhases by mainScreenViewModel.calendarPhases.collectAsState()

    when (uiState) {
        MainScreenState.Observation -> { }
        is MainScreenState.PlanningOnMainFocus -> {
            PlanningEditionDialog(
                mainScreenViewModel, uiState as MainScreenState.PlanningOnMainFocus
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // — Today's planning card —
        AdjustableText(
            "Plan de hoy · " + Date().simpleDateString(),
            TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        if (todaysPlanning != null) {
            Row(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        ),
                        shape = MaterialTheme.shapes.medium
                    )
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val content =
                    if (todaysPlanning!!.statedBodyPart != null) todaysPlanning!!.statedBodyPart
                    else if (todaysPlanning!!.statedRoutine != null) todaysPlanning!!.statedRoutine!!.name
                    else "Nada planeado"

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (todaysPlanning!!.isFromTrainer) {
                        Icon(
                            imageVector = Icons.TwoTone.Person,
                            contentDescription = "Creado por entrenador",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    AdjustableText(
                        "Objetivo: $content",
                        TextStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Row {
                    if (content != "Nada planeado") {
                        IconButton(
                            onClick = { onNavigateToTrain() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.TwoTone.ArrowForward,
                                contentDescription = "Iniciar entrenamiento",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(
                        onClick = { mainScreenViewModel.planningClicked(todaysPlanning!!) }
                    ) {
                        Icon(
                            imageVector = if (content == null) Icons.TwoTone.Add else Icons.TwoTone.Edit,
                            contentDescription = if (content == null) "add planning" else "edit planning",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Planned exercises sub-card
            if (todaysPlanning!!.planningExercises.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            MaterialTheme.shapes.small
                        )
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Ejercicios planificados:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    todaysPlanning!!.planningExercises.sortedBy { it.position }.forEach { pe ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "•",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = pe.exerciseName.ifBlank { "Ejercicio #${pe.exerciseId}" },
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (!pe.expectationText.isNullOrBlank()) {
                                Text(
                                    text = "· ${pe.expectationText}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // -- Date range picker (collapsible) --
        val dateRangePickerState = rememberDateRangePickerState()
        var isExtended by rememberSaveable { mutableStateOf(false) }

        DateRangePicker(
            dateRangePickerState,
            title = null,
            headline = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        AdjustableText(
                            "Rango de fechas",
                            TextStyle(
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.padding(12.dp)
                        )
                        if (isExtended) IconButton(onClick = {
                            mainScreenViewModel.changeDates(
                                dateRangePickerState.selectedStartDateMillis,
                                dateRangePickerState.selectedEndDateMillis
                            )
                            isExtended = false
                        }) {
                            Icon(
                                Icons.TwoTone.CheckCircle,
                                contentDescription = "edit dates",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(onClick = { isExtended = !isExtended }) {
                        Icon(
                            if (!isExtended) Icons.TwoTone.KeyboardArrowDown else Icons.TwoTone.KeyboardArrowUp,
                            contentDescription = "edit dates",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            colors = rutinAppDatePickerColors(),
            modifier = Modifier
                .heightIn(0.dp, if (isExtended) 280.dp else 52.dp)
                .animateContentSize(),
            showModeToggle = false,
        )

        // â”€â”€ Calendar â”€â”€
        RutinAppCalendar(plannings, calendarPhases) {
            mainScreenViewModel.planningClicked(it)
        }
    }
}

