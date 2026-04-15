package com.mintocode.rutinapp.ui.screens

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.R
import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.models.SetModel
import com.mintocode.rutinapp.ui.screenStates.SetState
import com.mintocode.rutinapp.ui.screenStates.WorkoutsScreenState
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.utils.isValidAsNumber
import com.mintocode.rutinapp.viewmodels.WorkoutsViewModel

/**
 * Bottom sheet for swapping an exercise with one of its equivalents during a workout.
 *
 * KP design: glass container, section label header, swap icon per equivalent.
 *
 * @param viewModel ViewModel managing workout actions
 * @param exercise The exercise being swapped, with its list of equivalents
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSwapSheet(viewModel: WorkoutsViewModel, exercise: ExerciseModel) {
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = { viewModel.cancelExerciseSwap() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Cambiar ejercicio",
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                letterSpacing = (-0.5).sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = exercise.name,
                fontFamily = ManropeFont,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.height(8.dp))

            LazyColumn(Modifier.fillMaxWidth()) {
                items(exercise.equivalentExercises) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .clickable { viewModel.swapExerciseBeingSwapped(it, context) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = it.name,
                            fontFamily = ManropeFont,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.swap),
                            contentDescription = "swap exercise for ${it.name}",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

/**
 * Bottom sheet showing options for an existing set (view details, edit, or delete).
 *
 * KP design: surfaceContainerLow cards for data, action buttons row.
 *
 * @param viewModel ViewModel managing workout actions
 * @param uiState Current workout state containing the set being inspected
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetOptionsSheet(viewModel: WorkoutsViewModel, uiState: WorkoutsScreenState.WorkoutStarted) {
    val setState = uiState.setBeingCreated!! as SetState.OptionsOfSet
    var isEditing by rememberSaveable { mutableStateOf(false) }

    if (isEditing) {
        SetEditionSheet(viewModel = viewModel, set = setState.set)
    } else {
        ModalBottomSheet(
            onDismissRequest = { viewModel.cancelSetEditing() },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color(0xFF1A1A24)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Tonal glow
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 24.dp, y = (-24).dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .blur(40.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Opciones de serie",
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        letterSpacing = (-0.5).sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(8.dp))

                    // Observations
                    if (setState.set.observations.isNotBlank()) {
                        KPInfoCard(
                            label = "OBSERVACIONES",
                            value = setState.set.observations
                        )
                    }

                    // Execution time
                    KPInfoCard(
                        label = "MOMENTO DE EJECUCIÓN",
                        value = setState.set.date.toGMTString().take(20)
                    )

                    // Set data
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        KPInfoCard(
                            label = "PESO",
                            value = "${setState.set.weight} kg",
                            modifier = Modifier.weight(1f)
                        )
                        KPInfoCard(
                            label = "REPS",
                            value = "${setState.set.reps}",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Edit
                        KPOutlinedButton(
                            text = "EDITAR",
                            modifier = Modifier.weight(1f),
                            onClick = { isEditing = true }
                        )
                        // Delete
                        KPOutlinedButton(
                            text = "ELIMINAR",
                            modifier = Modifier.weight(1f),
                            borderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.3f),
                            textColor = MaterialTheme.colorScheme.error,
                            onClick = { viewModel.deleteSet(setState.set) }
                        )
                    }

                    // Close
                    KPGradientActionButton(
                        text = "CERRAR",
                        onClick = { viewModel.cancelSetEditing() }
                    )
                }
            }
        }
    }
}

/**
 * Bottom sheet for creating or editing a set — KP design with prominent weight stepper.
 *
 * Weight input uses large 56dp buttons and 72sp font-black value.
 * Reps input is inside a surfaceContainerLow card with 40dp buttons.
 *
 * @param viewModel ViewModel managing workout actions
 * @param set The set being created or edited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetEditionSheet(viewModel: WorkoutsViewModel, set: SetModel) {
    ModalBottomSheet(
        onDismissRequest = { viewModel.cancelSetEditing() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color(0xFF1A1A24)
    ) {
        var reps by rememberSaveable { mutableIntStateOf(set.reps) }
        var weight by rememberSaveable { mutableStateOf(if (set.weight > 0) set.weight.toString() else "") }
        var observations by rememberSaveable { mutableStateOf(set.observations) }

        Box(modifier = Modifier.fillMaxWidth()) {
            // Tonal glow — decorative
            Box(
                modifier = Modifier
                    .size(192.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 24.dp, y = (-24).dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .blur(80.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                // Header
                Text(
                    text = "Registrar serie",
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    letterSpacing = (-0.8).sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (set.exercise != null) {
                    Text(
                        text = set.exercise!!.name,
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(Modifier.height(32.dp))

                // ── Weight Input (prominent) ──
                val weightLabel = if (set.exercise?.weightType == "unilateral") "PESO (KG/LADO)" else "PESO (KG)"
                KPSectionLabel(weightLabel)
                Spacer(Modifier.height(16.dp))

                val weightValue = weight.toDoubleOrNull() ?: 0.0

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Minus button (56dp)
                    StepperButton(
                        icon = Icons.TwoTone.Remove,
                        size = 56,
                        tint = MaterialTheme.colorScheme.primary,
                        onClick = {
                            val newVal = (weightValue - 1.0).coerceAtLeast(0.0)
                            weight = if (newVal == newVal.toLong().toDouble()) newVal.toLong().toString() else newVal.toString()
                        }
                    )

                    // Weight value
                    Text(
                        text = if (weight.isBlank()) "0" else weight,
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Black,
                        fontSize = 72.sp,
                        letterSpacing = (-3).sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { /* TODO: show keyboard for direct input */ }
                    )

                    // Plus button (56dp)
                    StepperButton(
                        icon = Icons.TwoTone.Add,
                        size = 56,
                        tint = MaterialTheme.colorScheme.primary,
                        onClick = {
                            val newVal = weightValue + 1.0
                            weight = if (newVal == newVal.toLong().toDouble()) newVal.toLong().toString() else newVal.toString()
                        }
                    )
                }

                Spacer(Modifier.height(40.dp))

                // ── Reps Input (secondary, inside card) ──
                val repsLabel = if (set.exercise?.repsType == "seconds") "SEGUNDOS" else "REPETICIONES"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f))
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        .padding(24.dp)
                ) {
                    Column {
                        KPSectionLabel(repsLabel)
                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Minus (40dp)
                            StepperButton(
                                icon = Icons.TwoTone.Remove,
                                size = 40,
                                tint = MaterialTheme.colorScheme.secondary,
                                onClick = { if (reps > 0) reps-- }
                            )

                            Text(
                                text = reps.toString(),
                                fontFamily = SpaceGroteskFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 36.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )

                            // Plus (40dp)
                            StepperButton(
                                icon = Icons.TwoTone.Add,
                                size = 40,
                                tint = MaterialTheme.colorScheme.secondary,
                                onClick = { reps++ }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                // ── Observations ──
                KPSectionLabel("OBSERVACIONES")
                Spacer(Modifier.height(8.dp))

                TextField(
                    value = observations,
                    onValueChange = { observations = it },
                    placeholder = {
                        Text(
                            "RPE 8, Máx esfuerzo, ...",
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            fontFamily = ManropeFont,
                            fontSize = 14.sp
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = ManropeFont
                    )
                )

                Spacer(Modifier.height(40.dp))

                // ── Actions ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Cancel
                    KPOutlinedButton(
                        text = "CANCELAR",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.cancelSetEditing() }
                    )

                    // Save (gradient)
                    KPGradientActionButton(
                        text = "GUARDAR",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.saveSet(
                                weight = if (weight.isValidAsNumber() && weight.isNotEmpty()) weight.toDouble() else 0.0,
                                reps = reps,
                                observations = observations
                            )
                        }
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// ── Private helper composables ──

/**
 * Section label — uppercase, bold, tracking.
 */
@Composable
private fun KPSectionLabel(text: String) {
    Text(
        text = text,
        fontFamily = ManropeFont,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 2.sp,
        color = MaterialTheme.colorScheme.outline
    )
}

/**
 * Info card for displaying a labeled value.
 */
@Composable
private fun KPInfoCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(16.dp)
    ) {
        Text(
            text = label,
            fontFamily = ManropeFont,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 2.sp,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            fontFamily = ManropeFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Circular stepper button (+ / -).
 *
 * @param icon Icon to display
 * @param size Button diameter in dp
 * @param tint Icon tint color
 * @param onClick Click handler
 */
@Composable
private fun StepperButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    size: Int,
    tint: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(100),
        label = "stepper_scale"
    )

    Box(
        modifier = Modifier
            .size(size.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(1.dp, Color.White.copy(alpha = 0.05f), CircleShape)
            .clickable(interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size((size * 0.5).dp)
        )
    }
}

/**
 * Outlined action button with KP styling.
 */
@Composable
private fun KPOutlinedButton(
    text: String,
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = ManropeFont,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 1.sp,
            color = textColor
        )
    }
}

/**
 * Gradient action button (primaryContainer → primary).
 */
@Composable
private fun KPGradientActionButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "gradient_btn_scale"
    )

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        Color(0xFF2E4EDC)
                    )
                )
            )
            .clickable(interactionSource, indication = null, onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = ManropeFont,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 1.sp,
            color = Color.White
        )
    }
}

/**
 * Container for a text value with a title label (used in SetOptionsSheet).
 */
@Composable
private fun TextContainer(text: String, title: String, modifier: Modifier) {
    Column(modifier = modifier.padding(12.dp)) {
        Text(
            text = title,
            fontFamily = ManropeFont,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 2.sp,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = text.ifBlank { "—" },
            fontFamily = ManropeFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
