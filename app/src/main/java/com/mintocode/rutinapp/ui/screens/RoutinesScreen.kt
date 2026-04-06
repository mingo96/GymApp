package com.mintocode.rutinapp.ui.screens

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
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.ui.theme.rutinappCardColors
import com.mintocode.rutinapp.viewmodels.RoutinesViewModel
import java.util.Locale

/**
 * Routines screen with search, filter, create, edit, and observe functionality.
 *
 * @param viewModel ViewModel managing routine state
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoutinesScreen(viewModel: RoutinesViewModel) {

    LaunchedEffect(Unit) {
        viewModel.autoSync()
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val routines by viewModel.routines.collectAsStateWithLifecycle(
        lifecycle = lifecycle
    )

    val uiState by viewModel.uiState.observeAsState()

    val maxIndex = rememberStaggeredRevealIndex(
        key = routines,
        totalSize = routines.size
    )

    when (uiState) {
        is RoutinesScreenState.Creating -> {
            CreateRoutineDialog(viewModel)
        }

        is RoutinesScreenState.Editing -> {
            EditRoutineDialog(uiState = uiState as RoutinesScreenState.Editing, viewModel)
        }

        is RoutinesScreenState.Observe -> {
            ObserveRoutineDialog(uiState = uiState as RoutinesScreenState.Observe, viewModel)
        }

        null -> {}
        RoutinesScreenState.Overview -> {}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        val showOthers by viewModel.showOthers.observeAsState(false)
        val loading by viewModel.isLoading.observeAsState(false)
        var searchText by rememberSaveable { mutableStateOf("") }

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

            Spacer(modifier = Modifier.weight(1f))

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

        if (loading) {
            LoadingIndicator()
        }

        val filtered = run {
            val byOwnership = if (showOthers) routines.filter { !it.isFromThisUser } else routines.filter { it.isFromThisUser }
            if (searchText.isBlank()) byOwnership else byOwnership.filter {
                it.name.contains(searchText, ignoreCase = true) || it.targetedBodyPart.contains(searchText, ignoreCase = true)
            }
        }
        if (!loading && filtered.isEmpty()) {
            EmptyStateMessage(
                text = if (showOthers) "No hay rutinas de otros usuarios" else "No tienes rutinas aÃºn"
            )
        }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(filtered.map {
                it.targetedBodyPart.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                }
            }.distinct().take(maxIndex)) { thisBodyPart ->
                AnimatedItem(enterAnimation = slideInHorizontally(), delay = 100) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = thisBodyPart,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        LazyRow {
                            items(filtered.filter {
                                it.targetedBodyPart.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                                } == thisBodyPart
                            }) {
                                RoutineCard(
                                    routine = it, modifier = Modifier.combinedClickable(onClick = {
                                        viewModel.clickObserveRoutine(it)
                                    }, onLongClick = { viewModel.clickEditRoutine(it) })
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
                .padding(top = 16.dp)
        ) {
            Text(text = "Crear nueva rutina")
        }
    }
}



@Composable
private fun RoutineCard(routine: RoutineModel, modifier: Modifier) {
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
