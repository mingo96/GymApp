package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.FitnessCenter
import androidx.compose.material.icons.twotone.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.navigation.SheetDestination
import com.mintocode.rutinapp.ui.screenStates.ExercisesState
import com.mintocode.rutinapp.ui.screens.ExerciseTypeSelectors
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.viewmodels.ExercisesViewModel

/**
 * Exercise detail sheet — KP design (Guide 06).
 *
 * Accent gradient bar, header with name + tag + icon,
 * description card with left border, metrics grid, equivalent exercises chips,
 * and action row (close + edit gradient).
 *
 * @param viewModel ExercisesViewModel for navigation and actions
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseDetailSheet(viewModel: ExercisesViewModel) {
    val navigator = LocalSheetNavigator.current
    val uiState by viewModel.uiState.observeAsState()

    val observeState = uiState as? ExercisesState.Observe
    val exercise = observeState?.exercise
    if (exercise == null) {
        Text(
            text = "Cargando...",
            fontFamily = ManropeFont,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(32.dp)
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Accent gradient bar ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ── Header ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.name,
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        letterSpacing = (-0.5).sp,
                        lineHeight = 34.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (exercise.targetedBodyPart.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f))
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                                    RoundedCornerShape(50)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = exercise.targetedBodyPart.uppercase(),
                                fontFamily = ManropeFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 2.sp,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }

                // Icon box
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.TwoTone.FitnessCenter,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // ── Description ──
            if (exercise.description.isNotBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    KPSectionLabel("DESCRIPCIÓN")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(20.dp)
                    ) {
                        Text(
                            text = exercise.description,
                            fontFamily = ManropeFont,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ── Metrics Grid ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                KPMetricCard(
                    label = "TIPO REPS",
                    value = when (exercise.repsType) {
                        "base" -> "Repeticiones"
                        "seconds" -> "Segundos"
                        else -> exercise.repsType
                    },
                    modifier = Modifier.weight(1f)
                )
                KPMetricCard(
                    label = "TIPO PESO",
                    value = when (exercise.weightType) {
                        "base" -> "Normal"
                        "unilateral" -> "Unilateral"
                        else -> exercise.weightType
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Equivalent Exercises ──
            if (exercise.equivalentExercises.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        KPSectionLabel("EJERCICIOS EQUIVALENTES")
                        if (exercise.isFromThisUser) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.outlineVariant,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { /* add relation handled from edit */ }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.TwoTone.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "AÑADIR",
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

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        exercise.equivalentExercises.forEach { related ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                    .border(
                                        1.dp,
                                        Color.White.copy(alpha = 0.05f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.clickToObserve(related) }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.TwoTone.Link,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = related.name,
                                        fontFamily = ManropeFont,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Action Row ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.05f))
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Close button
                val closeInteraction = remember { MutableInteractionSource() }
                val closePressed by closeInteraction.collectIsPressedAsState()
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .scale(if (closePressed) 0.95f else 1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(
                            interactionSource = closeInteraction,
                            indication = null
                        ) { navigator.close() }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CERRAR",
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Edit/Obtain/Upload button
                if (exercise.isFromThisUser) {
                    KPGradientActionButton(
                        text = "EDITAR",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.clickToEdit(exercise)
                            navigator.replace(
                                SheetDestination.ExerciseEdit(exercise.id.toIntOrNull() ?: 0)
                            )
                        }
                    )
                } else if (exercise.id == "0") {
                    KPGradientActionButton(
                        text = "OBTENER",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.saveExercise(exercise)
                            navigator.close()
                        }
                    )
                }

                if (exercise.realId == 0L && exercise.isFromThisUser) {
                    KPGradientActionButton(
                        text = "SUBIR",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.uploadExercise(exercise) }
                    )
                }
            }
        }
    }
}

// ── Private helpers ──

/**
 * Section label — uppercase, tracking-widest.
 */
@Composable
private fun KPSectionLabel(text: String) {
    Text(
        text = text,
        fontFamily = SpaceGroteskFont,
        fontWeight = FontWeight.Black,
        fontSize = 12.sp,
        letterSpacing = 2.sp,
        color = MaterialTheme.colorScheme.outline
    )
}

/**
 * Metric card with label and value.
 */
@Composable
private fun KPMetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.4f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.05f),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = label,
                fontFamily = ManropeFont,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = value,
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Gradient action button (primary→primaryContainer).
 */
@Composable
private fun KPGradientActionButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    Box(
        modifier = modifier
            .scale(if (pressed) 0.95f else 1f)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = ManropeFont,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 2.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
