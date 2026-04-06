package com.mintocode.rutinapp.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mintocode.rutinapp.data.models.ExerciseModel

/**
 * Scrollable list of exercises with selection highlighting.
 *
 * Used in routine editing to show included and available exercises.
 * Note: the list is copied to avoid animation crashes when the source list
 * is modified while staggered animations are running.
 *
 * @param exerciseList Exercises to display
 * @param selected Currently selected exercise (highlighted)
 * @param selectExercise Callback when an exercise is tapped
 */
@Composable
fun ListOfExercises(
    exerciseList: List<ExerciseModel>,
    selected: ExerciseModel?,
    selectExercise: (ExerciseModel) -> Unit
) {

    val exercises = exerciseList.toList()

    LazyColumn(
        Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
            .padding(16.dp)
            .fillMaxWidth()
            .heightIn(max = 200.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (exercises.isEmpty()) item {
            Text(text = "No hay ejercicios disponibles", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else items(exercises, contentType = { ExerciseModel::class.java }) {

            ExerciseItemForRoutineEditing(
                item = it, modifier = Modifier
                    .animateItem()
                    .background(
                        if (it != selected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
                        MaterialTheme.shapes.medium
                    )
                    .padding(8.dp), onEditClick = { selectExercise(it) }, opened = it == selected
            )
        }
    }
}

/**
 * Expandable exercise item for routine editing, showing details when selected.
 *
 * @param opened Whether the item is expanded to show full details
 * @param item The exercise model to display
 * @param onEditClick Callback when the item is tapped
 * @param modifier Modifier for the item container
 */
@Composable
private fun ExerciseItemForRoutineEditing(
    opened: Boolean, item: ExerciseModel, onEditClick: () -> Unit, modifier: Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onEditClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = item.name, fontWeight = FontWeight.Bold, maxLines = 1
            )
            Text(
                text = item.description.take(40)
            )
            if (opened) {
                if (item.setsAndReps != "" && item.setsAndReps != "0x0") Text(text = "Series y repeticiones : ${item.setsAndReps}")
                if (item.observations != "") Text(text = "Observaciones : ${item.observations}")
                Text(text = "Parte del cuerpo : ${item.targetedBodyPart}")
                if (item.equivalentExercises.isEmpty()) Text(text = "No hay ejercicios equivalentes")
                else {
                    Text(text = "Ejercicios relacionados : ")
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium)
                            .padding(8.dp)
                            .heightIn(max = 200.dp)
                            .fillMaxWidth(),

                        ) {
                        items(item.equivalentExercises) {
                            Box(
                                modifier = Modifier.border(
                                    1.dp, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium
                                ), contentAlignment = Alignment.Center
                            ) {

                                Text(text = it.name, modifier = Modifier.padding(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }

}

/**
 * Exercise item for routine creation phase with a checkbox for selection.
 *
 * @param opened Whether the item is expanded to show full details
 * @param item The exercise model to display
 * @param onEditClick Callback when toggled
 * @param selected Whether the exercise is currently selected
 * @param modifier Modifier for the item container
 */
@Composable
private fun ExerciseItemForRoutineCreationPhase(
    opened: Boolean,
    item: ExerciseModel,
    onEditClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(Modifier.fillMaxWidth(0.8f)) {
            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Text(
                text = item.description.take(40), modifier = Modifier.fillMaxWidth(0.8f)
            )
            if (opened) {
                Text(text = "Parte del cuerpo : ${item.targetedBodyPart}")
                if (item.equivalentExercises.isEmpty()) Text(text = "No hay ejercicios equivalentes")
                else {
                    Text(text = "Ejercicios relacionados : ")
                    LazyColumn(
                        Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium)
                            .padding(8.dp)
                            .heightIn(max = 200.dp)
                    ) {
                        items(item.equivalentExercises) {
                            Text(text = it.name)
                        }
                    }
                }
            }
        }
        Checkbox(checked = selected, onCheckedChange = { onEditClick() })
    }

}

/**
 * Simple read-only exercise item showing name and description.
 *
 * @param item The exercise model to display
 * @param modifier Modifier for the item container
 */
@Composable
fun SimpleExerciseItem(
    item: ExerciseModel, modifier: Modifier
) {

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = item.description.take(40), modifier = Modifier.fillMaxWidth()
            )
        }
    }

}
