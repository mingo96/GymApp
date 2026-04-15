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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.CheckCircle
import androidx.compose.material.icons.twotone.ChevronRight
import androidx.compose.material.icons.twotone.FitnessCenter
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.navigation.SheetDestination
import com.mintocode.rutinapp.ui.premade.AdjustableText
import com.mintocode.rutinapp.ui.premade.RutinAppCalendar
import com.mintocode.rutinapp.ui.screenStates.MainScreenState
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.ui.theme.rutinAppDatePickerColors
import com.mintocode.rutinapp.utils.simpleDateString
import com.mintocode.rutinapp.viewmodels.MainScreenViewModel
import java.util.Date

/**
 * Home root page — Kinetic Precision design.
 *
 * Today's plan with objective card, planned exercises,
 * date range picker, and full calendar view.
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

    LaunchedEffect(uiState) {
        if (uiState is MainScreenState.PlanningOnMainFocus) {
            val state = uiState as MainScreenState.PlanningOnMainFocus
            navigator.open(SheetDestination.PlanningEdit(state.planningModel.date.time))
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ── Date label ──
        item {
            Text(
                text = Date().simpleDateString().uppercase(),
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // ── Page Title ──
        item {
            Text(
                text = "Plan de hoy",
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                letterSpacing = (-1).sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        item {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .width(48.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }

        item { Spacer(Modifier.height(20.dp)) }

        // ── Today's Objective Card ──
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
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .clickable { viewModel.planningClicked(planning) }
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            // Icon container
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (planning.isFromTrainer) Icons.TwoTone.Person
                                    else Icons.TwoTone.FitnessCenter,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Objetivo",
                                    fontFamily = SpaceGroteskFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    letterSpacing = 2.sp,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = content ?: "Sin definir",
                                    fontFamily = SpaceGroteskFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.TwoTone.ChevronRight,
                            contentDescription = "Ver detalle",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // ── Planned Exercises ──
            if (planning.planningExercises.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "EJERCICIOS PLANIFICADOS",
                            fontFamily = SpaceGroteskFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 2.sp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                        )
                        planning.planningExercises.sortedBy { it.position }.forEach { pe ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(MaterialTheme.colorScheme.secondary)
                                )
                                Text(
                                    text = pe.exerciseName.ifBlank { "Ejercicio #${pe.exerciseId}" },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
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
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(24.dp),
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

        item { Spacer(Modifier.height(12.dp)) }

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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AdjustableText(
                                "Rango de fechas",
                                TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = SpaceGroteskFont,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
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
