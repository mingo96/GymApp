package com.mintocode.rutinapp.ui.screens.root

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.twotone.FitnessCenter
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.data.models.CalendarPhaseModel
import com.mintocode.rutinapp.data.models.PlanningModel
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.navigation.SheetDestination
import com.mintocode.rutinapp.ui.screenStates.MainScreenState
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.utils.simpleDateString
import com.mintocode.rutinapp.viewmodels.MainScreenViewModel
import java.util.Calendar
import java.util.Date

/**
 * Home root page — Kinetic Precision design.
 *
 * Today's plan with objective card, weekly calendar,
 * and quick stats overview.
 *
 * @param viewModel MainScreenViewModel for planning data
 */
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

    val weekDays = remember(plannings) { buildWeekDays(plannings) }

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
                fontFamily = ManropeFont,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // ── Page Title ──
        item {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Plan de hoy",
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                letterSpacing = (-0.9).sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item { Spacer(Modifier.height(24.dp)) }

        // ── Today's Objective Card (KP double-layer) ──
        if (todaysPlanning != null) {
            val planning = todaysPlanning!!
            val routineName = when {
                planning.statedRoutine != null -> planning.statedRoutine!!.name
                planning.statedBodyPart != null -> planning.statedBodyPart!!
                else -> "Nada planeado"
            }
            val description = when {
                planning.statedRoutine != null -> {
                    val bodyPart = planning.statedBodyPart ?: ""
                    val exerciseCount = planning.planningExercises.size
                    if (bodyPart.isNotBlank() && exerciseCount > 0) "$bodyPart • $exerciseCount ejercicios"
                    else if (bodyPart.isNotBlank()) bodyPart
                    else "$exerciseCount ejercicios"
                }
                else -> ""
            }

            item {
                ObjectiveCard(
                    routineName = routineName,
                    description = description,
                    isFromTrainer = planning.isFromTrainer,
                    onClick = { viewModel.planningClicked(planning) }
                )
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
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

        item { Spacer(Modifier.height(24.dp)) }

        // ── Weekly Calendar ──
        item {
            WeeklyCalendarHeader(calendarPhases)
        }

        item { Spacer(Modifier.height(12.dp)) }

        // Generate week days (Monday through Sunday)
        items(weekDays) { dayInfo ->
            DayRow(
                dayInfo = dayInfo,
                onClick = {
                    dayInfo.planning?.let { viewModel.planningClicked(it) }
                }
            )
        }

        item { Spacer(Modifier.height(32.dp)) }

        // ── Quick Stats ──
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickStatCard(
                    label = "Racha",
                    value = "–",
                    unit = "Días",
                    accentColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    label = "Carga Semanal",
                    value = "–",
                    unit = "%",
                    accentColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

// ── Objective Card (double-layer KP design) ──

/**
 * KP objective card — outer surfaceContainerLow shell with inner surfaceContainer card.
 * Gradient overlay from transparent → primaryContainer/10.
 */
@Composable
private fun ObjectiveCard(
    routineName: String,
    description: String,
    isFromTrainer: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(150),
        label = "obj_card_scale"
    )

    // Outer container
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(4.dp)
    ) {
        // Gradient overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                        )
                    )
                )
        )

        // Inner card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(11.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Row(
                modifier = Modifier
                    .clickable(interactionSource, indication = null, onClick = onClick)
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left content
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "OBJETIVO",
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 2.5.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = routineName,
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (description.isNotBlank()) {
                        Text(
                            text = description,
                            fontFamily = ManropeFont,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Right content — icon + label
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFromTrainer) Icons.TwoTone.Person
                            else Icons.TwoTone.FitnessCenter,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Text(
                        text = "COMENZAR",
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ── Weekly Calendar ──

private enum class DayState { COMPLETED, IN_PROGRESS, FUTURE, REST, UNPLANNED }

private data class DayInfo(
    val dayOfWeek: String,
    val dayNumber: Int,
    val routineName: String?,
    val state: DayState,
    val planning: PlanningModel?,
    val isToday: Boolean
)

/**
 * Builds a list of 7 DayInfo objects for Monday–Sunday of the current week.
 */
private fun buildWeekDays(plannings: List<PlanningModel>): List<DayInfo> {
    val cal = Calendar.getInstance()
    val today = Calendar.getInstance()

    // Go to Monday of this week
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    val dayAbbreviations = arrayOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM")
    val result = mutableListOf<DayInfo>()

    for (i in 0 until 7) {
        val dayCal = cal.clone() as Calendar
        dayCal.add(Calendar.DAY_OF_YEAR, i)

        val isToday = dayCal.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && dayCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)

        val isFuture = dayCal.after(today) && !isToday

        val matchingPlanning = plannings.find { p ->
            val pCal = Calendar.getInstance().apply { time = p.date }
            pCal.get(Calendar.YEAR) == dayCal.get(Calendar.YEAR)
                    && pCal.get(Calendar.DAY_OF_YEAR) == dayCal.get(Calendar.DAY_OF_YEAR)
        }

        val routineName = matchingPlanning?.let {
            it.statedRoutine?.name ?: it.statedBodyPart ?: "Entrenamiento"
        }

        val dayState = when {
            matchingPlanning != null && !isToday && !isFuture -> DayState.COMPLETED
            matchingPlanning != null && isToday -> DayState.IN_PROGRESS
            matchingPlanning != null && isFuture -> DayState.FUTURE
            isToday || !isFuture -> DayState.REST
            else -> DayState.UNPLANNED
        }

        result.add(
            DayInfo(
                dayOfWeek = dayAbbreviations[i],
                dayNumber = dayCal.get(Calendar.DAY_OF_MONTH),
                routineName = routineName,
                state = dayState,
                planning = matchingPlanning,
                isToday = isToday
            )
        )
    }
    return result
}

/**
 * Weekly calendar section header — title + optional phase badge.
 */
@Composable
private fun WeeklyCalendarHeader(phases: List<CalendarPhaseModel>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "CALENDARIO SEMANAL",
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            letterSpacing = (-0.4).sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (phases.isNotEmpty()) {
            val currentPhase = phases.firstOrNull()?.name ?: ""
            if (currentPhase.isNotBlank()) {
                Text(
                    text = "FASE: ${currentPhase.uppercase()}",
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

/**
 * Individual day row — visual style varies by state.
 */
@Composable
private fun DayRow(dayInfo: DayInfo, onClick: () -> Unit) {
    val bgColor = when (dayInfo.state) {
        DayState.IN_PROGRESS -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surfaceContainerLow
    }
    val borderColor = when (dayInfo.state) {
        DayState.IN_PROGRESS -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        else -> Color.Transparent
    }

    val alpha = when (dayInfo.state) {
        DayState.COMPLETED, DayState.IN_PROGRESS -> 1f
        DayState.FUTURE -> 0.8f
        DayState.REST -> 0.6f
        DayState.UNPLANNED -> 0.4f
    }

    val indicatorColor = when (dayInfo.state) {
        DayState.COMPLETED -> MaterialTheme.colorScheme.tertiary
        DayState.IN_PROGRESS -> MaterialTheme.colorScheme.primary
        DayState.FUTURE -> MaterialTheme.colorScheme.secondary
        DayState.REST, DayState.UNPLANNED -> MaterialTheme.colorScheme.outlineVariant
    }

    val badgeBg = when (dayInfo.state) {
        DayState.IN_PROGRESS -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val badgeTextColor = when (dayInfo.state) {
        DayState.IN_PROGRESS -> Color.White
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(alpha = alpha),
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        border = if (borderColor != Color.Transparent) BorderStroke(1.dp, borderColor) else null
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Day badge
            Column(
                modifier = Modifier
                    .size(width = 48.dp, height = 56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeBg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = dayInfo.dayOfWeek,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = if (dayInfo.state == DayState.IN_PROGRESS) Color.White.copy(alpha = 0.8f)
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = dayInfo.dayNumber.toString(),
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = badgeTextColor
                )
            }

            // Indicator bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(50))
                    .background(indicatorColor)
            )

            // Content
            Column(modifier = Modifier.weight(1f)) {
                val displayName = when (dayInfo.state) {
                    DayState.REST -> "Descanso"
                    DayState.UNPLANNED -> "Sin planear"
                    else -> dayInfo.routineName ?: "Entrenamiento"
                }
                Text(
                    text = displayName,
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (dayInfo.state == DayState.REST || dayInfo.state == DayState.UNPLANNED)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
                    fontStyle = if (dayInfo.state == DayState.REST || dayInfo.state == DayState.UNPLANNED)
                        FontStyle.Italic else FontStyle.Normal
                )
                val statusText = when (dayInfo.state) {
                    DayState.COMPLETED -> "Completado"
                    DayState.IN_PROGRESS -> "En progreso"
                    DayState.FUTURE -> "Mañana"
                    else -> null
                }
                if (statusText != null) {
                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (dayInfo.state) {
                            DayState.COMPLETED -> MaterialTheme.colorScheme.tertiary
                            DayState.IN_PROGRESS -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            // Trailing icon
            when (dayInfo.state) {
                DayState.COMPLETED -> Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
                DayState.IN_PROGRESS -> Icon(
                    Icons.Filled.RadioButtonChecked,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                DayState.FUTURE, DayState.REST, DayState.UNPLANNED -> {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null,
                            tint = if (dayInfo.state == DayState.FUTURE) MaterialTheme.colorScheme.onSurfaceVariant
                            else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── Quick Stats ──

/**
 * Stat card — label, large value, and accent unit text.
 */
@Composable
private fun QuickStatCard(
    label: String,
    value: String,
    unit: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = label.uppercase(),
                fontFamily = ManropeFont,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 2.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = unit.uppercase(),
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = accentColor,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}
