package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.Refresh
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

/**
 * Exercise list sheet content.
 *
 * Shows the full exercise list with search, filter, and CRUD entry points.
 * Replaces the old ExercisesScreen — now rendered inside a ModalBottomSheet.
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

    // Dialog states still handled via ViewModel for create/edit/observe
    when (uiState) {
        is ExercisesState.Modifying -> {
            com.mintocode.rutinapp.ui.screens.ModifyExerciseDialog(viewModel, uiState as ExercisesState.Modifying)
        }
        is ExercisesState.Creating -> {
            com.mintocode.rutinapp.ui.screens.CreateExerciseDialog(viewModel = viewModel)
        }
        is ExercisesState.Observe -> {
            stateOfSearch = null
            if ((uiState as ExercisesState.Observe).exercise != null) {
                com.mintocode.rutinapp.ui.screens.ObserveExerciseDialog(viewModel, uiState as ExercisesState.Observe)
            }
        }
        is ExercisesState.AddingRelations -> {
            com.mintocode.rutinapp.ui.screens.AddRelationsDialog(viewModel, uiState as ExercisesState.AddingRelations)
        }
        is ExercisesState.SearchingForExercise -> { stateOfSearch = false }
        is ExercisesState.ExploringExercises -> { stateOfSearch = true }
        null -> {}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // ── Header ──
        Text(
            text = "Ejercicios",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        SearchTextField(
            value = name,
            onValueChange = { name = it },
            onSearch = { viewModel.writeOnExerciseName(name) }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OwnershipFilterRow(
                showOthers = showOthers,
                onShowMine = { viewModel.showMine(context) },
                onShowOthers = { viewModel.showOthers(context) }
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = { viewModel.changeToUploadedExercises() },
                modifier = Modifier.size(36.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (stateOfSearch == true) MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.hente),
                    contentDescription = "Explorar",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(
                onClick = { viewModel.syncExercises(context = context) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.TwoTone.Refresh,
                    contentDescription = "Sincronizar",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

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
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { exercise ->
                AnimatedItem(enterAnimation = slideInHorizontally { +it }, delay = 50) {
                    ExerciseListItem(
                        item = exercise,
                        onEditClick = { viewModel.clickToEdit(exercise) },
                        onClick = { viewModel.clickToObserve(exercise) }
                    )
                }
            }
        }

        // ── Create button ──
        Button(
            onClick = { viewModel.clickToCreate() },
            colors = rutinAppButtonsColours(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Icon(Icons.TwoTone.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(text = "  Crear ejercicio")
        }
    }
}

@Composable
private fun ExerciseListItem(item: ExerciseModel, onEditClick: () -> Unit, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (item.description.isNotBlank()) {
                Text(
                    text = item.description.take(50),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
        if (item.isFromThisUser) {
            Icon(
                imageVector = Icons.TwoTone.Edit,
                contentDescription = "editar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(22.dp)
                    .clickable { onEditClick() }
            )
        }
    }
}
