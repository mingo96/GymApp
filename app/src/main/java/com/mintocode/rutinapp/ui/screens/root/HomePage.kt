package com.mintocode.rutinapp.ui.screens.root

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.ChevronRight
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material.icons.twotone.CheckCircle
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.navigation.SheetDestination
import com.mintocode.rutinapp.ui.premade.AdjustableText
import com.mintocode.rutinapp.ui.premade.RutinAppCalendar
import com.mintocode.rutinapp.ui.screenStates.MainScreenState
import com.mintocode.rutinapp.ui.theme.rutinAppDatePickerColors
import com.mintocode.rutinapp.utils.simpleDateString
import com.mintocode.rutinapp.viewmodels.MainScreenViewModel
import java.util.Date

/**
 * Home root page: Today's plan summary, date range picker, and calendar.
 *
 * Tapping on a planning item opens a PlanningEdit sheet.
 * Tapping "start training" navigates to the Train root page (handled by parent).
 *
 * @param viewModel MainScreenViewModel for planning data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(viewModel: MainScreenViewModel) {
    val navigator = LocalSheetNavigator.current

    LaunchedEffect(Unit) {
        viewModel.autoSync()
    }

    val plannings by viewModel.plannings.observeAsState(emptyList())
    val uiState by viewModel.uiState.observeAsState(MainScreenState.Observation)
    val todaysPlanning by viewModel.todaysPlanning.observeAsState()
    val calendarPhases by viewModel.calendarPhases.collectAsState()

    var lastHandledPlanningDate by rememberSaveable { mutableStateOf(-1L) }

    // Handle planning edition as a sheet instead of dialog (guarded against re-triggers)
    LaunchedEffect(uiState) {
        if (uiState is MainScreenState.PlanningOnMainFocus) {
            val state = uiState as MainScreenState.PlanningOnMainFocus
            val dateMillis = state.planningModel.date.time
            if (dateMillis != lastHandledPlanningDate) {
                lastHandledPlanningDate = dateMillis
                navigator.open(SheetDestination.PlanningEdit(dateMillis))
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Date header ──
        item {
        Text(
            text = "Plan de hoy",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        }
        item {
        Text(
            text = Date().simpleDateString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        }

        item { Spacer(Modifier.height(4.dp)) }

        // ── Today's planning card ──
        if (todaysPlanning != null) {
            val planning = todaysPlanning!!
            val content = when {
                planning.statedBodyPart != null -> planning.statedBodyPart
                planning.statedRoutine != null -> planning.statedRoutine!!.name
                else -> "Nada planeado"
            }

            item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.shapes.medium
                    )
                    .clickable { viewModel.planningClicked(planning) }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (planning.isFromTrainer) {
                            Icon(
                                imageVector = Icons.TwoTone.Person,
                                contentDescription = "Creado por entrenador",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Objetivo",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = content ?: "Sin definir",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.TwoTone.ChevronRight,
                        contentDescription = "Ver detalle",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            }

            // Planned exercises
            if (planning.planningExercises.isNotEmpty()) {
                item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            MaterialTheme.shapes.small
                        )
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Ejercicios planificados",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    planning.planningExercises.sortedBy { it.position }.forEach { pe ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("•", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
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
        } else {
            // No planning today
            item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.shapes.medium
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sin plan para hoy",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp
                )
            }
            }
        }

        // ── Date range picker (collapsible) ──
        item {
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
                    ) {
                        AdjustableText(
                            "Rango de fechas",
                            TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface),
                            modifier = Modifier.padding(12.dp)
                        )
                        if (isExtended) {
                            IconButton(onClick = {
                                viewModel.changeDates(
                                    dateRangePickerState.selectedStartDateMillis,
                                    dateRangePickerState.selectedEndDateMillis
                                )
                                isExtended = false
                            }) {
                                Icon(
                                    Icons.TwoTone.CheckCircle,
                                    contentDescription = "Confirmar fechas",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    IconButton(onClick = { isExtended = !isExtended }) {
                        Icon(
                            if (!isExtended) Icons.TwoTone.KeyboardArrowDown
                            else Icons.TwoTone.KeyboardArrowUp,
                            contentDescription = "Expandir fechas",
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
        }

        // ── Calendar ──
        item {
        Column(modifier = Modifier.heightIn(max = 500.dp)) {
        RutinAppCalendar(plannings, calendarPhases) {
            viewModel.planningClicked(it)
        }
        }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}
