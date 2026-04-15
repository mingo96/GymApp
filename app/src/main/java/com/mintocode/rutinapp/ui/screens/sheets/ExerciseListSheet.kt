package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowRight
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mintocode.rutinapp.R
import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.ui.components.EmptyStateMessage
import com.mintocode.rutinapp.ui.components.LoadingIndicator
import com.mintocode.rutinapp.ui.components.OwnershipFilterRow
import com.mintocode.rutinapp.ui.components.SearchTextField
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.navigation.SheetDestination
import com.mintocode.rutinapp.ui.premade.AnimatedItem
import com.mintocode.rutinapp.ui.screenStates.ExercisesState
import com.mintocode.rutinapp.ui.screens.AddRelationsDialog
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.viewmodels.ExercisesViewModel
import kotlinx.coroutines.delay
import java.util.Locale

/**
 * Exercise list sheet — Kinetic Precision design (Guide 05).
 *
 * Header with back/title, search input, filter chips,
 * exercise cards with colored muscle tags, and gradient FAB.
 *
 * @param viewModel ExercisesViewModel for data and actions
 */
@Composable
fun ExerciseListSheet(viewModel: ExercisesViewModel) {
    val navigator = LocalSheetNavigator.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.autoSync() }

    val exercises by viewModel.exercisesState.collectAsStateWithLifecycle(
        initialValue = emptyList(), lifecycle = lifecycle
    )
    val uiState by viewModel.uiState.observeAsState()
    val showOthers by viewModel.showOthers.observeAsState(false)
    val loading by viewModel.isLoading.observeAsState(false)

    var name by rememberSaveable { mutableStateOf("") }
    var maxIndex by rememberSaveable { mutableIntStateOf(0) }
    var stateOfSearch: Boolean? by rememberSaveable { mutableStateOf(null) }

    LaunchedEffect(uiState) {
        when (uiState) {
            is ExercisesState.Modifying -> navigator.open(SheetDestination.ExerciseEdit(0))
            is ExercisesState.Creating -> navigator.open(SheetDestination.ExerciseCreate)
            is ExercisesState.Observe -> {
                stateOfSearch = null
                if ((uiState as ExercisesState.Observe).exercise != null) {
                    navigator.open(SheetDestination.ExerciseDetail(0))
                }
            }
            is ExercisesState.AddingRelations -> {}
            is ExercisesState.SearchingForExercise -> { stateOfSearch = false }
            is ExercisesState.ExploringExercises -> { stateOfSearch = true }
            null -> {}
        }
    }

    if (uiState is ExercisesState.AddingRelations) {
        AddRelationsDialog(viewModel, uiState as ExercisesState.AddingRelations)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            // ── Header ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ejercicios",
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    letterSpacing = (-0.5).sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { viewModel.changeToUploadedExercises() },
                        modifier = Modifier.size(40.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (stateOfSearch == true)
                                MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.hente),
                            contentDescription = "Explorar",
                            modifier = Modifier.size(18.dp),
                            tint = if (stateOfSearch == true)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { viewModel.syncExercises(context = context) },
                        modifier = Modifier.size(40.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                    ) {
                        Icon(
                            imageVector = Icons.TwoTone.Refresh,
                            contentDescription = "Sincronizar",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ── Search ──
            SearchTextField(
                value = name,
                onValueChange = { name = it },
                onSearch = { viewModel.writeOnExerciseName(name) },
                placeholder = "Buscar ejercicio..."
            )

            Spacer(Modifier.height(16.dp))

            // ── Filter Chips ──
            OwnershipFilterRow(
                showOthers = showOthers,
                onShowMine = { viewModel.showMine(context) },
                onShowOthers = { viewModel.showOthers(context) }
            )

            Spacer(Modifier.height(16.dp))

            // ── Exercise items ──
            val items = when (uiState) {
                is ExercisesState.SearchingForExercise ->
                    (uiState as ExercisesState.SearchingForExercise).possibleValues.take(maxIndex)
                is ExercisesState.ExploringExercises ->
                    (uiState as ExercisesState.ExploringExercises).possibleValues.take(maxIndex)
                else -> {
                    val filtered = if (showOthers) exercises.filter { !it.isFromThisUser }
                    else exercises.filter { it.isFromThisUser }
                    val searched = if (name.isBlank()) filtered else filtered.filter {
                        it.name.contains(name, ignoreCase = true) ||
                                it.targetedBodyPart.contains(name, ignoreCase = true)
                    }
                    searched.take(maxIndex)
                }
            }

            LaunchedEffect(maxIndex) {
                while (true) {
                    delay(100)
                    if (maxIndex < when (stateOfSearch) {
                            false -> (uiState as ExercisesState.SearchingForExercise).possibleValues.size
                            true -> (uiState as ExercisesState.ExploringExercises).possibleValues.size
                            else -> exercises.size
                        }
                    ) maxIndex++
                }
            }

            if (loading) { LoadingIndicator() }
            if (items.isEmpty() && !loading) {
                EmptyStateMessage(
                    text = if (showOthers) "No hay ejercicios de otros" else "No tienes ejercicios aún"
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items, key = { it.id }) { exercise ->
                    val isLast = items.indexOf(exercise) == items.lastIndex && maxIndex < exercises.size
                    AnimatedItem(enterAnimation = slideInHorizontally { +it }, delay = 40) {
                        KPExerciseCard(
                            exercise = exercise,
                            faded = isLast,
                            onClick = {
                                viewModel.clickToObserve(exercise)
                                navigator.open(SheetDestination.ExerciseDetail(0))
                            }
                        )
                    }
                }
            }
        }

        // ── FAB ──
        val fabInteraction = remember { MutableInteractionSource() }
        val fabPressed by fabInteraction.collectIsPressedAsState()

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 40.dp)
                .size(64.dp)
                .scale(if (fabPressed) 0.9f else 1f)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                )
                .clickable(
                    interactionSource = fabInteraction,
                    indication = null
                ) { navigator.open(SheetDestination.ExerciseCreate) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.TwoTone.Add,
                contentDescription = "Crear ejercicio",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

/**
 * Exercise card with colored muscle tag, description, and chevron.
 *
 * @param exercise Exercise model to display
 * @param faded Whether to show at reduced opacity (last item scroll hint)
 * @param onClick Callback for click action
 */
@Composable
private fun KPExerciseCard(
    exercise: ExerciseModel,
    faded: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (faded) 0.6f else 1f)
            .scale(if (pressed) 0.98f else 1f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Muscle tag + name row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (exercise.targetedBodyPart.isNotBlank()) {
                    MuscleTag(bodyPart = exercise.targetedBodyPart)
                }
                Text(
                    text = exercise.name,
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Description
            if (exercise.description.isNotBlank()) {
                Text(
                    text = exercise.description,
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Chevron
        Icon(
            imageVector = Icons.AutoMirrored.TwoTone.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Colored muscle group tag — uppercase with tracking-widest.
 * Colors vary by body part as per Stitch Guide 05.
 */
@Composable
private fun MuscleTag(bodyPart: String) {
    val lower = bodyPart.lowercase()
    val (bg, textColor) = when {
        lower.contains("pecho") || lower.contains("chest") ->
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f) to MaterialTheme.colorScheme.secondary
        lower.contains("core") || lower.contains("abdom") ->
            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f) to MaterialTheme.colorScheme.tertiary
        lower.contains("pierna") || lower.contains("leg") || lower.contains("cuádríceps") || lower.contains("cuadriceps") ->
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) to MaterialTheme.colorScheme.primary
        lower.contains("espalda") || lower.contains("back") ->
            MaterialTheme.colorScheme.surfaceContainerHighest to MaterialTheme.colorScheme.onSurfaceVariant
        lower.contains("hombro") || lower.contains("shoulder") ->
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f) to MaterialTheme.colorScheme.secondary
        lower.contains("brazo") || lower.contains("bíceps") || lower.contains("tríceps") || lower.contains("arm") ->
            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f) to MaterialTheme.colorScheme.tertiary
        else ->
            MaterialTheme.colorScheme.surfaceContainerHighest to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 2.dp)
    ) {
        Text(
            text = bodyPart.uppercase(),
            fontFamily = ManropeFont,
            fontWeight = FontWeight.Black,
            fontSize = 10.sp,
            letterSpacing = 2.sp,
            color = textColor
        )
    }
}
