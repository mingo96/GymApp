package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.navigation.SheetDestination
import com.mintocode.rutinapp.viewmodels.RoutinesViewModel
import java.util.Locale

/**
 * Routine list sheet content (Kinetic Precision design).
 *
 * Shows routines grouped by body part with search, filters, and CRUD.
 * Cards use surfaceContainerHigh with primary-tinted badges.
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

    val navigator = LocalSheetNavigator.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is RoutinesScreenState.Creating -> {
                navigator.open(SheetDestination.RoutineCreate)
            }
            is RoutinesScreenState.Editing -> {
                navigator.open(SheetDestination.RoutineEdit(0))
            }
            is RoutinesScreenState.Observe -> {
                if ((uiState as RoutinesScreenState.Observe).routine != null) {
                    navigator.open(SheetDestination.RoutineDetail(0))
                }
            }
            null, RoutinesScreenState.Overview -> {}
        }
    }

    var searchText by rememberSaveable { mutableStateOf("") }

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
                text = "Rutinas",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(
                onClick = { viewModel.syncRoutines() },
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

        // ── Search ──
        SearchTextField(
            value = searchText,
            onValueChange = { searchText = it },
            onSearch = { },
            placeholder = "Buscar rutinas...",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // ── Filter row ──
        OwnershipFilterRow(
            showOthers = showOthers,
            onShowMine = { viewModel.showMine() },
            onShowOthers = { viewModel.showOthers() }
        )

        Spacer(Modifier.height(8.dp))

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
            contentPadding = PaddingValues(top = 4.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                filtered.map {
                    it.targetedBodyPart.replaceFirstChar { c ->
                        if (c.isLowerCase()) c.titlecase(Locale.ROOT) else c.toString()
                    }
                }.distinct().take(maxIndex)
            ) { bodyPart ->
                AnimatedItem(enterAnimation = slideInHorizontally(), delay = 80) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Section header
                        Text(
                            text = bodyPart.uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(end = 4.dp)
                        ) {
                            items(filtered.filter {
                                it.targetedBodyPart.replaceFirstChar { c ->
                                    if (c.isLowerCase()) c.titlecase(Locale.ROOT) else c.toString()
                                } == bodyPart
                            }) { routine ->
                                RoutineCardItem(
                                    routine = routine,
                                    modifier = Modifier.combinedClickable(
                                        onClick = {
                                            viewModel.clickObserveRoutine(routine)
                                            navigator.open(SheetDestination.RoutineDetail(routine.id))
                                        },
                                        onLongClick = {
                                            viewModel.clickEditRoutine(routine)
                                            navigator.open(SheetDestination.RoutineEdit(routine.id))
                                        }
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Create button ──
        Card(
            onClick = { navigator.open(SheetDestination.RoutineCreate) },
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
                    text = "Crear rutina",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

/**
 * Card de rutina con badge de grupo muscular y conteo de ejercicios.
 *
 * @param routine Modelo de la rutina
 * @param modifier Modifier con click handlers
 */
@Composable
private fun RoutineCardItem(routine: RoutineModel, modifier: Modifier) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.width(160.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            // Body part badge
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = routine.targetedBodyPart
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = routine.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
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
