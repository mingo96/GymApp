package com.mintocode.rutinapp.ui.screens.sheets

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.viewmodels.ExercisesViewModel

/**
 * Create exercise sheet — KP design (Guide 07).
 *
 * Dialog-style form with headline, labeled inputs, chip groups for
 * reps/weight type, and footer with cancel + gradient create button.
 *
 * @param viewModel ExercisesViewModel for exercise creation
 */
@Composable
fun ExerciseCreateSheet(viewModel: ExercisesViewModel) {
    val navigator = LocalSheetNavigator.current
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var targetedBodyPart by rememberSaveable { mutableStateOf("") }
    var repsType by rememberSaveable { mutableStateOf("base") }
    var weightType by rememberSaveable { mutableStateOf("base") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 16.dp)
    ) {
        // ── Header ──
        Text(
            text = "Nuevo ejercicio",
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            letterSpacing = (-0.5).sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Configura los parámetros técnicos de tu ejercicio.",
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
            KPFormField(
                label = "NOMBRE DEL EJERCICIO *",
                isRequired = true,
                value = name,
                onValueChange = { name = it },
                placeholder = "Ej: Press de banca"
            )

            // Description (optional)
            KPFormField(
                label = "DESCRIPCIÓN",
                isRequired = false,
                value = description,
                onValueChange = { description = it },
                placeholder = "Describe el ejercicio...",
                minLines = 3
            )

            // Body part (required)
            KPFormField(
                label = "PARTE DEL CUERPO *",
                isRequired = true,
                value = targetedBodyPart,
                onValueChange = { targetedBodyPart = it },
                placeholder = "Ej: Pecho, Espalda, Piernas..."
            )

            // Reps type chips
            KPChipGroup(
                label = "TIPO DE REPS",
                options = listOf("base" to "Repeticiones", "seconds" to "Segundos"),
                selected = repsType,
                onSelect = { repsType = it }
            )

            // Weight type chips
            KPChipGroup(
                label = "TIPO DE PESO",
                options = listOf("base" to "Normal", "unilateral" to "Unilateral"),
                selected = weightType,
                onSelect = { weightType = it }
            )
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

            // Create
            val createInteraction = remember { MutableInteractionSource() }
            val createPressed by createInteraction.collectIsPressedAsState()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .scale(if (createPressed) 0.95f else 1f)
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
                        interactionSource = createInteraction,
                        indication = null
                    ) {
                        viewModel.addExercise(
                            name, description, targetedBodyPart, context, repsType, weightType
                        )
                        navigator.close()
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CREAR",
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

// ── Private helpers ──

/**
 * KP-styled form field with uppercase label.
 * Required fields use primary/70 label, optional use onSurfaceVariant.
 */
@Composable
private fun KPFormField(
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

/**
 * KP-styled chip group with single selection (radio behavior).
 */
@Composable
private fun KPChipGroup(
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
            options.forEach { (value, label) ->
                FilterChip(
                    selected = selected == value,
                    onClick = { onSelect(value) },
                    label = {
                        Text(
                            text = label.uppercase(),
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
