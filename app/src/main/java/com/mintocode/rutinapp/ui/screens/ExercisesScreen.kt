package com.mintocode.rutinapp.ui.screens

import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.mintocode.rutinapp.ui.premade.AnimatedItem
import com.mintocode.rutinapp.ui.screenStates.ExercisesState
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.viewmodels.ExercisesViewModel
import kotlinx.coroutines.delay


/**
 * Exercise list screen with search, filters and CRUD dialogs.
 *
 * Displays the user's exercises in a scrollable list with animated item entry.
 * Supports filtering by ownership (mine/others), server exploration, and sync.
 *
 * @param viewModel ExercisesViewModel for exercise data and actions
 */
@Composable
fun ExercisesScreen(viewModel: ExercisesViewModel) {

    LaunchedEffect(Unit) {
        viewModel.autoSync()
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val exercises by viewModel.exercisesState.collectAsStateWithLifecycle(
        initialValue = emptyList(), lifecycle = lifecycle
    )

    val uiState by viewModel.uiState.observeAsState()

    var maxIndex by rememberSaveable { mutableIntStateOf(0) }

    var stateOfSearch: Boolean? by rememberSaveable { mutableStateOf(null) }

    when (uiState) {
        is ExercisesState.Modifying -> {
            ModifyExerciseDialog(viewModel, uiState as ExercisesState.Modifying)
        }
        is ExercisesState.Creating -> {
            CreateExerciseDialog(viewModel = viewModel)
        }
        is ExercisesState.Observe -> {
            stateOfSearch = null
            if ((uiState as ExercisesState.Observe).exercise != null) {
                ObserveExerciseDialog(viewModel, uiState as ExercisesState.Observe)
            }
        }
        is ExercisesState.AddingRelations -> {
            AddRelationsDialog(viewModel, uiState as ExercisesState.AddingRelations)
        }
        is ExercisesState.SearchingForExercise -> {
            stateOfSearch = false
        }
        null -> {}
        is ExercisesState.ExploringExercises -> {
            stateOfSearch = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        var name by rememberSaveable { mutableStateOf("") }
        val showOthers by viewModel.showOthers.observeAsState(false)
        val loading by viewModel.isLoading.observeAsState(false)

        SearchTextField(
            value = name,
            onValueChange = { name = it },
            onSearch = { viewModel.writeOnExerciseName(name) }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val context = LocalContext.current

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
                    contentDescription = "Explorar ejercicios",
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
                    contentDescription = "Sincronizar ejercicios",
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
                val othersFlag = viewModel.showOthers.observeAsState(false).value
                val filtered = if (othersFlag) exercises.filter { !it.isFromThisUser } else exercises.filter { it.isFromThisUser }
                val searched = if (name.isBlank()) filtered else filtered.filter {
                    it.name.contains(name, ignoreCase = true) || it.targetedBodyPart.contains(name, ignoreCase = true)
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

        if (loading) {
            LoadingIndicator()
        }

        if (items.isEmpty() && !loading) {
            EmptyStateMessage(
                text = if (showOthers) "No hay ejercicios de otros usuarios" else "No tienes ejercicios aÃºn"
            )
        }

        LazyColumn(
            Modifier.fillMaxHeight(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items) { exercise ->
                AnimatedItem(enterAnimation = slideInHorizontally { +it }, delay = 50) {
                    ExerciseItem(
                        item = exercise,
                        onEditClick = { viewModel.clickToEdit(exercise) },
                        onClick = { viewModel.clickToObserve(exercise) }
                    )
                }
            }
        }

        // Create FAB
        Button(
            onClick = { viewModel.clickToCreate() },
            colors = rutinAppButtonsColours(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(text = "Crear un ejercicio")
        }
    }
}


@Composable
private fun ExerciseItem(item: ExerciseModel, onEditClick: () -> Unit, onClick: () -> Unit = {}) {

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
        if (item.isFromThisUser) Icon(
            imageVector = Icons.TwoTone.Edit,
            contentDescription = "editar",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(22.dp)
                .clickable { onEditClick() }
        )
    }
}

/**
 * Dialog for editing an existing exercise.
 */

@Composable
fun ExerciseTypeSelectors(
    repsType: String,
    weightType: String,
    onRepsTypeChange: (String) -> Unit,
    onWeightTypeChange: (String) -> Unit,
    enabled: Boolean = true
) {
    val repsOptions = listOf("base" to "Repeticiones", "seconds" to "Segundos")
    val weightOptions = listOf("base" to "Normal", "unilateral" to "Unilateral")

    Text(text = "Tipo de repeticiones", color = MaterialTheme.colorScheme.onSurface)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repsOptions.forEach { (value, label) ->
            FilterChip(
                selected = repsType == value,
                onClick = { if (enabled) onRepsTypeChange(value) },
                label = { Text(label, fontSize = 13.sp) },
                enabled = enabled,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                    disabledSelectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            )
        }
    }

    Text(text = "Tipo de peso", color = MaterialTheme.colorScheme.onSurface)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        weightOptions.forEach { (value, label) ->
            FilterChip(
                selected = weightType == value,
                onClick = { if (enabled) onWeightTypeChange(value) },
                label = { Text(label, fontSize = 13.sp) },
                enabled = enabled,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                    disabledSelectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            )
        }
    }
}
