package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowRight
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.viewmodels.ExercisesViewModel
import kotlinx.coroutines.delay
import java.util.Locale

/**
 * Exercise list sheet content (Kinetic Precision design).
 *
 * Shows the full exercise list with search, filter chips, and CRUD entry points.
 * Cards display body part badge, description, and chevron navigation indicator.
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
            is ExercisesState.Modifying -> {
                navigator.open(SheetDestination.ExerciseEdit(0))
            }
            is ExercisesState.Creating -> {
                navigator.open(SheetDestination.ExerciseCreate)
            }
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
        com.mintocode.rutinapp.ui.screens.AddRelationsDialog(viewModel, uiState as ExercisesState.AddingRelations)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // ── Header ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ejercicios",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = { viewModel.changeToUploadedExercises() },
                    modifier = Modifier.size(36.dp),
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
                    modifier = Modifier.size(36.dp),
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
            placeholder = "Buscar ejercicios..."
        )

        Spacer(Modifier.height(8.dp))

        // ── Filter row ──
        OwnershipFilterRow(
            showOthers = showOthers,
            onShowMine = { viewModel.showMine(context) },
            onShowOthers = { viewModel.showOthers(context) }
        )

        Spacer(Modifier.height(8.dp))

        // ── Item list ──
        val items = when (uiState) {
            is ExercisesState.SearchingForExercise -> {
                (uiState as ExercisesState.SearchingForExercise).possibleValues.take(maxIndex)
            }
            is ExercisesState.ExploringExercises -> {
                (uiState as ExercisesState.ExploringExercises).possibleValues.take(maxIndex)
            }
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
            contentPadding = PaddingValues(top = 4.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(items, key = { it.id }) { exercise ->
                AnimatedItem(enterAnimation = slideInHorizontally { +it }, delay = 40) {
                    ExerciseListItem(
                        item = exercise,
                        onEditClick = {
                            viewModel.clickToEdit(exercise)
                            navigator.open(SheetDestination.ExerciseEdit(0))
                        },
                        onClick = {
                            viewModel.clickToObserve(exercise)
                            navigator.open(SheetDestination.ExerciseDetail(0))
                        }
                    )
                }
            }
        }

        // ── FAB-style create button ──
        Card(
            onClick = { navigator.open(SheetDestination.ExerciseCreate) },
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.TwoTone.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Crear ejercicio",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

/**
 * Card de ejercicio con badge de grupo muscular, descripción y chevron.
 *
 * @param item Modelo del ejercicio
 * @param onEditClick Callback para editar
 * @param onClick Callback para ver detalle
 */
@Composable
private fun ExerciseListItem(item: ExerciseModel, onEditClick: () -> Unit, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Body part badge icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.targetedBodyPart
                        .take(2)
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (item.description.isNotBlank()) {
                    Text(
                        text = item.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (item.targetedBodyPart.isNotBlank()) {
                    Text(
                        text = item.targetedBodyPart
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                }
            }

            // Edit / chevron
            if (item.isFromThisUser) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.Edit,
                        contentDescription = "editar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.TwoTone.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
