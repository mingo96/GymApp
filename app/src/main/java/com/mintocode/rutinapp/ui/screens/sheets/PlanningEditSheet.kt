package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Alarm
import androidx.compose.material.icons.twotone.CheckCircle
import androidx.compose.material.icons.twotone.ExpandMore
import androidx.compose.material.icons.twotone.FitnessCenter
import androidx.compose.material.icons.automirrored.twotone.FormatListBulleted
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.ui.components.GlassCard
import com.mintocode.rutinapp.ui.components.SectionLabel
import com.mintocode.rutinapp.ui.components.TextFieldWithTitle
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.screenStates.FieldBeingEdited
import com.mintocode.rutinapp.ui.screenStates.MainScreenState
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.utils.simpleDateString
import com.mintocode.rutinapp.viewmodels.MainScreenViewModel

// ── KP Color constants ──
private val KP_PRIMARY = Color(0xFFBAC3FF)
private val KP_PRIMARY_CONTAINER = Color(0xFF4361EE)
private val KP_ON_PRIMARY_CONTAINER = Color(0xFFF4F2FF)
private val KP_TERTIARY = Color(0xFF27E0A9)
private val KP_SECONDARY = Color(0xFFD2BBFF)
private val KP_SURFACE_BG = Color(0xFF1A1A24)
private val KP_SURFACE_CONTAINER_LOW = Color(0xFF1B1B20)
private val KP_SURFACE_CONTAINER_HIGH = Color(0xFF2A292F)
private val KP_SURFACE_CONTAINER_HIGHEST = Color(0xFF35343A)
private val KP_ON_SURFACE = Color(0xFFE4E1E9)
private val KP_ON_SURFACE_VARIANT = Color(0xFFC4C5D7)
private val KP_OUTLINE = Color(0xFF8E8FA1)
private val KP_OUTLINE_VARIANT = Color(0xFF444655)

/**
 * Planning edit sheet with KP design system.
 *
 * Shows body part / routine selection for a given date's planning.
 * Features a 2-column selection grid with glow effect, dropdown with
 * tertiary dot indicator, reminder section with time and day chips,
 * and a sticky bottom gradient save button.
 *
 * @param viewModel MainScreenViewModel for planning actions
 */
@Composable
fun PlanningEditSheet(viewModel: MainScreenViewModel) {
    val navigator = LocalSheetNavigator.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.observeAsState(MainScreenState.Observation)

    var wasInPlanningState by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is MainScreenState.PlanningOnMainFocus) {
            wasInPlanningState = true
        }
        if (wasInPlanningState && uiState is MainScreenState.Observation) {
            navigator.close()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KP_SURFACE_BG)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            // ── Drag Handle ──
            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
                    .width(48.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(KP_OUTLINE_VARIANT.copy(alpha = 0.3f))
            )

            when (val state = uiState) {
                is MainScreenState.PlanningOnMainFocus -> {
                    // ── Header ──
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "Planificación",
                                fontFamily = SpaceGroteskFont,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = KP_ON_SURFACE
                            )
                            Text(
                                text = "Objetivo el ${state.planningModel.date.simpleDateString()}",
                                fontFamily = ManropeFont,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = KP_PRIMARY
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // ── Body ──
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        when (state.fieldBeingEdited) {
                            FieldBeingEdited.NONE -> PlanningFieldSelection(viewModel)
                            FieldBeingEdited.BODYPART -> PlanningBodyPartEditor(
                                onSave = { viewModel.saveBodypart(it, context) },
                                onBack = { viewModel.backToSelection() }
                            )
                            FieldBeingEdited.ROUTINE -> PlanningRoutineSelector(
                                availableRoutines = state.availableRoutines,
                                onSelect = { viewModel.saveRoutine(it) },
                                onBack = { viewModel.backToSelection() }
                            )
                        }
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Selecciona un día en el calendario para planificarlo",
                            fontFamily = ManropeFont,
                            fontSize = 14.sp,
                            color = KP_ON_SURFACE_VARIANT
                        )
                    }
                }
            }
        }
    }
}

/**
 * 2-column selection grid with glow effect on selected card.
 *
 * Cards show icons for body part and routine selection types.
 */
@Composable
private fun PlanningFieldSelection(viewModel: MainScreenViewModel) {
    Text(
        text = "¿Qué quieres planificar?",
        fontFamily = ManropeFont,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = KP_ON_SURFACE
    )

    Spacer(Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SelectionCard(
            icon = Icons.TwoTone.FitnessCenter,
            label = "Parte del cuerpo",
            isSelected = false,
            onClick = { viewModel.selectBodypartClicked() },
            modifier = Modifier.weight(1f)
        )
        SelectionCard(
            icon = Icons.AutoMirrored.TwoTone.FormatListBulleted,
            label = "Rutina",
            isSelected = false,
            onClick = { viewModel.selectRoutineClicked() },
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(Modifier.height(32.dp))

    // ── Reminder Section ──
    ReminderSection()
}

/**
 * Selection card with glow effect when selected.
 *
 * Selected: surfaceContainerHigh bg, primary border-2, primary icon, glow.
 * Inactive: surfaceContainerLow bg, no border, onSurfaceVariant icon.
 *
 * @param icon Card icon
 * @param label Card label text
 * @param isSelected Whether card is currently selected
 * @param onClick Callback when tapped
 * @param modifier Optional modifier
 */
@Composable
private fun SelectionCard(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100), label = "card_scale"
    )

    Box(modifier = modifier.graphicsLayer(scaleX = scale, scaleY = scale)) {
        // Glow layer for selected state
        if (isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(KP_PRIMARY.copy(alpha = 0.2f))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isSelected)
                        Modifier.border(2.dp, KP_PRIMARY, RoundedCornerShape(16.dp))
                    else Modifier
                )
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isSelected) KP_SURFACE_CONTAINER_HIGH
                    else KP_SURFACE_CONTAINER_LOW
                )
                .clickable(interactionSource, indication = null, onClick = onClick)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) KP_PRIMARY else KP_ON_SURFACE_VARIANT,
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = label,
                fontFamily = ManropeFont,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) KP_ON_PRIMARY_CONTAINER else KP_ON_SURFACE_VARIANT
            )
        }
    }
}

/**
 * Body part text field editor with dropdown and KP styling.
 *
 * @param onSave Callback with body part text on save
 * @param onBack Callback to go back to selection
 */
@Composable
private fun PlanningBodyPartEditor(onSave: (String) -> Unit, onBack: () -> Unit) {
    var bodyPart by rememberSaveable { mutableStateOf("") }

    SectionLabel("Seleccionar Parte del Cuerpo")
    Spacer(Modifier.height(8.dp))

    // ── Dropdown row ──
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(KP_SURFACE_CONTAINER_HIGHEST)
            .border(1.dp, KP_OUTLINE_VARIANT.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tertiary dot indicator
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(50))
                .background(KP_TERTIARY)
        )
        Column(modifier = Modifier.weight(1f)) {
            TextFieldWithTitle(
                title = "",
                text = bodyPart,
                onWrite = { bodyPart = it },
                sendFunction = { onSave(bodyPart) }
            )
        }
        Icon(
            Icons.TwoTone.ExpandMore,
            contentDescription = null,
            tint = KP_OUTLINE,
            modifier = Modifier.size(24.dp)
        )
    }

    Spacer(Modifier.height(24.dp))

    // ── Action row ──
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Back button
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(KP_SURFACE_CONTAINER_HIGH)
                .clickable(onClick = onBack)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Volver",
                fontFamily = ManropeFont,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = KP_ON_SURFACE
            )
        }
        // Save button (gradient)
        StickyGradientButton(
            text = "Guardar",
            onClick = { onSave(bodyPart) },
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Routine list selector with expandable items and KP styling.
 *
 * @param availableRoutines List of available routines
 * @param onSelect Callback when a routine is selected
 * @param onBack Callback to go back to selection
 */
@Composable
private fun PlanningRoutineSelector(
    availableRoutines: List<RoutineModel>,
    onSelect: (RoutineModel) -> Unit,
    onBack: () -> Unit
) {
    SectionLabel("Rutinas Disponibles")
    Spacer(Modifier.height(8.dp))

    LazyColumn(
        modifier = Modifier.heightIn(0.dp, 400.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (availableRoutines.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No hay rutinas disponibles",
                        fontFamily = ManropeFont,
                        fontSize = 14.sp,
                        color = KP_ON_SURFACE_VARIANT
                    )
                }
            }
        }
        items(availableRoutines) { routine ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                var isOpened by rememberSaveable { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(routine) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = routine.name,
                            fontFamily = SpaceGroteskFont,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = KP_ON_SURFACE
                        )
                        Text(
                            text = "${routine.exercises.size} ejercicios",
                            fontFamily = ManropeFont,
                            fontSize = 12.sp,
                            color = KP_ON_SURFACE_VARIANT
                        )
                    }
                    IconButton(onClick = { isOpened = !isOpened }) {
                        Icon(
                            Icons.TwoTone.ExpandMore,
                            contentDescription = "Expandir",
                            tint = KP_ON_SURFACE_VARIANT,
                            modifier = Modifier.graphicsLayer(
                                rotationZ = if (isOpened) 180f else 0f
                            )
                        )
                    }
                }
                if (isOpened) {
                    Spacer(Modifier.height(8.dp))
                    routine.exercises.forEach { exercise ->
                        Row(
                            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(KP_PRIMARY_CONTAINER)
                            )
                            Text(
                                text = exercise.name,
                                fontFamily = ManropeFont,
                                fontSize = 13.sp,
                                color = KP_ON_SURFACE_VARIANT
                            )
                        }
                    }
                }
            }
        }
    }

    Spacer(Modifier.height(16.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(KP_SURFACE_CONTAINER_HIGH)
            .clickable(onClick = onBack)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Volver",
            fontFamily = ManropeFont,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = KP_ON_SURFACE
        )
    }
}

/**
 * Reminder section with time display and day chips.
 *
 * Displays "09:00" styled time and L-D day selection chips
 * with active/inactive KP color states.
 */
@Composable
private fun ReminderSection() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            Icons.TwoTone.Alarm,
            contentDescription = null,
            tint = KP_SECONDARY,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = "Recordatorio",
            fontFamily = ManropeFont,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = KP_ON_SURFACE
        )
    }

    Spacer(Modifier.height(8.dp))

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        // ── Time display ──
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "09:00",
                fontFamily = SpaceGroteskFont,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = KP_ON_SURFACE,
                letterSpacing = (-1).sp
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "AM",
                fontFamily = ManropeFont,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = KP_OUTLINE,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        // ── Day chips ──
        val days = listOf("L", "M", "X", "J", "V", "S", "D")
        val activeDays = setOf(1, 3) // Tuesday and Thursday active (0-indexed)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            days.forEachIndexed { index, day ->
                val isActive = index in activeDays
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .then(
                            if (isActive) Modifier.shadow(
                                8.dp, RoundedCornerShape(8.dp),
                                spotColor = KP_PRIMARY.copy(alpha = 0.2f)
                            ) else Modifier
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isActive) KP_PRIMARY_CONTAINER
                            else KP_SURFACE_CONTAINER_HIGH
                        )
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        fontFamily = ManropeFont,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) KP_ON_PRIMARY_CONTAINER else KP_ON_SURFACE
                    )
                }
            }
        }
    }
}

/**
 * Gradient button with check_circle icon, used for save actions.
 *
 * @param text Button label
 * @param onClick Callback when tapped
 * @param modifier Optional modifier
 */
@Composable
private fun StickyGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(150), label = "save_btn_scale"
    )

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = KP_PRIMARY_CONTAINER.copy(alpha = 0.3f))
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(listOf(KP_PRIMARY, KP_PRIMARY_CONTAINER))
            )
            .clickable(interactionSource, indication = null, onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = text,
                fontFamily = ManropeFont,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00218D)
            )
            Icon(
                Icons.TwoTone.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF00218D),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
