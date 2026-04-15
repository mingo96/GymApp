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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.screenStates.ExercisesState
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.viewmodels.ExercisesViewModel

/**
 * Edit exercise sheet — KP design (Guide 07, edit mode).
 *
 * Same form structure as create, but pre-filled. Includes related exercises
 * section with chips and add/remove actions.
 *
 * @param viewModel ExercisesViewModel for exercise editing
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseEditSheet(viewModel: ExercisesViewModel) {
    val navigator = LocalSheetNavigator.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.observeAsState()

    val modifyingState = uiState as? ExercisesState.Modifying
    if (modifyingState == null) {
        Text(
            text = "Cargando...",
            fontFamily = ManropeFont,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(32.dp)
        )
        return
    }

    var name by rememberSaveable { mutableStateOf(modifyingState.exerciseModel.name) }
    var description by rememberSaveable { mutableStateOf(modifyingState.exerciseModel.description) }
    var targetedBodyPart by rememberSaveable { mutableStateOf(modifyingState.exerciseModel.targetedBodyPart) }
    var repsType by rememberSaveable { mutableStateOf(modifyingState.exerciseModel.repsType) }
    var weightType by rememberSaveable { mutableStateOf(modifyingState.exerciseModel.weightType) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 16.dp)
    ) {
        // ── Header ──
        Text(
            text = "Editar ejercicio",
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            letterSpacing = (-0.5).sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Modifica los parámetros de tu ejercicio.",
            fontFamily = ManropeFont,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(Modifier.height(24.dp))

        // ── Form body (scrollable) ──
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Name (required)
            KPEditField(
                label = "NOMBRE DEL EJERCICIO *",
                isRequired = true,
                value = name,
                onValueChange = { name = it },
                placeholder = "Ej: Press de banca"
            )

            // Description (optional)
            KPEditField(
                label = "DESCRIPCIÓN",
                isRequired = false,
                value = description,
                onValueChange = { description = it },
                placeholder = "Describe el ejercicio...",
                minLines = 3
            )

            // Body part (required)
            KPEditField(
                label = "PARTE DEL CUERPO *",
                isRequired = true,
                value = targetedBodyPart,
                onValueChange = { targetedBodyPart = it },
                placeholder = "Ej: Pecho, Espalda, Piernas..."
            )

            // Reps type chips
            KPEditChipGroup(
                label = "TIPO DE REPS",
                options = listOf("base" to "Repeticiones", "seconds" to "Segundos"),
                selected = repsType,
                onSelect = { repsType = it }
            )

            // Weight type chips
            KPEditChipGroup(
                label = "TIPO DE PESO",
                options = listOf("base" to "Normal", "unilateral" to "Unilateral"),
                selected = weightType,
                onSelect = { weightType = it }
            )

            // ── Related exercises ──
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EJERCICIOS RELACIONADOS",
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    IconButton(
                        onClick = { viewModel.clickToAddRelatedExercises(context) },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.TwoTone.Add,
                            contentDescription = "Añadir relación",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    modifyingState.relatedExercises.forEach { exercise ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                .border(
                                    1.dp,
                                    Color.White.copy(alpha = 0.05f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(start = 16.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = exercise.name,
                                    fontFamily = ManropeFont,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(1f, fill = false)
                                        .clickable { viewModel.clickToObserve(exercise) }
                                )
                                IconButton(
                                    onClick = { viewModel.toggleExercisesRelation(exercise) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.TwoTone.Close,
                                        contentDescription = "Eliminar relación",
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Footer ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.05f))
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cancel
            val cancelInteraction = remember { MutableInteractionSource() }
            val cancelPressed by cancelInteraction.collectIsPressedAsState()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .scale(if (cancelPressed) 0.95f else 1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable(
                        interactionSource = cancelInteraction,
                        indication = null
                    ) { navigator.close() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CANCELAR",
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Save
            val saveInteraction = remember { MutableInteractionSource() }
            val savePressed by saveInteraction.collectIsPressedAsState()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .scale(if (savePressed) 0.95f else 1f)
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
                        interactionSource = saveInteraction,
                        indication = null
                    ) {
                        viewModel.updateExercise(
                            name, description, targetedBodyPart, context, repsType, weightType
                        )
                        navigator.close()
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "GUARDAR",
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

// ── Private helpers (scoped to edit sheet) ──

@Composable
private fun KPEditField(
    label: String,
    isRequired: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 2.sp,
            color = if (isRequired) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    fontFamily = ManropeFont,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            minLines = minLines
        )
    }
}

@Composable
private fun KPEditChipGroup(
    label: String,
    options: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 2.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { (value, displayLabel) ->
                FilterChip(
                    selected = selected == value,
                    onClick = { onSelect(value) },
                    label = {
                        Text(
                            text = displayLabel.uppercase(),
                            fontFamily = ManropeFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = (-0.5).sp
                        )
                    },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
