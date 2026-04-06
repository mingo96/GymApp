package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.ui.components.EmptyStateMessage
import com.mintocode.rutinapp.ui.components.LoadingIndicator
import com.mintocode.rutinapp.ui.components.OwnershipFilterRow
import com.mintocode.rutinapp.ui.components.SearchTextField
import com.mintocode.rutinapp.ui.components.rememberStaggeredRevealIndex
import com.mintocode.rutinapp.ui.premade.AnimatedItem
import com.mintocode.rutinapp.ui.screenStates.RoutinesScreenState
import com.mintocode.rutinapp.ui.screens.CreateRoutineDialog
import com.mintocode.rutinapp.ui.screens.EditRoutineDialog
import com.mintocode.rutinapp.ui.screens.ObserveRoutineDialog
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.ui.theme.rutinappCardColors
import com.mintocode.rutinapp.viewmodels.RoutinesViewModel
import java.util.Locale

/**
 * Routine list sheet content.
 *
 * Shows routines grouped by body part with search, filters, and CRUD.
 * Replaces the old RoutinesScreen — now rendered inside a ModalBottomSheet.
 *
 * @param viewModel RoutinesViewModel for data and actions
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoutineListSheet(viewModel: RoutinesViewModel) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) { viewModel.autoSync() }

    val routines by viewModel.routines.collectAsStateWithLifecycle(lifecycle = lifecycle)
    val uiState by viewModel.uiState.observeAsState()
    val showOthers by viewModel.showOthers.observeAsState(false)
    val loading by viewModel.isLoading.observeAsState(false)

    val maxIndex = rememberStaggeredRevealIndex(key = routines, totalSize = routines.size)

    // Existing dialog states
    when (uiState) {
        is RoutinesScreenState.Creating -> CreateRoutineDialog(viewModel)
        is RoutinesScreenState.Editing -> EditRoutineDialog(uiState = uiState as RoutinesScreenState.Editing, viewModel)
        is RoutinesScreenState.Observe -> ObserveRoutineDialog(uiState = uiState as RoutinesScreenState.Observe, viewModel)
        null, RoutinesScreenState.Overview -> {}
    }

    var searchText by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Rutinas",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        SearchTextField(
            value = searchText,
            onValueChange = { searchText = it },
            onSearch = { },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OwnershipFilterRow(
                showOthers = showOthers,
                onShowMine = { viewModel.showMine() },
                onShowOthers = { viewModel.showOthers() }
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = { viewModel.syncRoutines() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.TwoTone.Refresh,
                    contentDescription = "Sincronizar",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (loading) { LoadingIndicator() }

        val filtered = run {
            val byOwnership = if (showOthers) routines.filter { !it.isFromThisUser }
            else routines.filter { it.isFromThisUser }
            if (searchText.isBlank()) byOwnership else byOwnership.filter {
                it.name.contains(searchText, ignoreCase = true) ||
                        it.targetedBodyPart.contains(searchText, ignoreCase = true)
            }
        }

        if (!loading && filtered.isEmpty()) {
            EmptyStateMessage(
                text = if (showOthers) "No hay rutinas de otros" else "No tienes rutinas aún"
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                filtered.map {
                    it.targetedBodyPart.replaceFirstChar { c ->
                        if (c.isLowerCase()) c.titlecase(Locale.ROOT) else c.toString()
                    }
                }.distinct().take(maxIndex)
            ) { bodyPart ->
                AnimatedItem(enterAnimation = slideInHorizontally(), delay = 100) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = bodyPart,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        LazyRow {
                            items(filtered.filter {
                                it.targetedBodyPart.replaceFirstChar { c ->
                                    if (c.isLowerCase()) c.titlecase(Locale.ROOT) else c.toString()
                                } == bodyPart
                            }) { routine ->
                                RoutineCardItem(
                                    routine = routine,
                                    modifier = Modifier.combinedClickable(
                                        onClick = { viewModel.clickObserveRoutine(routine) },
                                        onLongClick = { viewModel.clickEditRoutine(routine) }
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.clickCreateRoutine() },
            colors = rutinAppButtonsColours(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Icon(Icons.TwoTone.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(text = "  Crear rutina")
        }
    }
}

@Composable
private fun RoutineCardItem(routine: RoutineModel, modifier: Modifier) {
    Card(
        shape = MaterialTheme.shapes.small,
        colors = rutinappCardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.padding(end = 10.dp, top = 4.dp, bottom = 4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(
                text = routine.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1
            )
            if (routine.exercises.isNotEmpty()) {
                Text(
                    text = "${routine.exercises.size} ejercicios",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
