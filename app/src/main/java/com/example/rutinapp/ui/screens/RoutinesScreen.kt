package com.example.rutinapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.models.RoutineModel
import com.example.rutinapp.ui.screenStates.RoutinesScreenState
import com.example.rutinapp.ui.theme.PrimaryColor
import com.example.rutinapp.ui.theme.ScreenContainer
import com.example.rutinapp.ui.theme.TextFieldColor
import com.example.rutinapp.ui.theme.rutinAppButtonsColours
import com.example.rutinapp.ui.theme.rutinappCardColors
import com.example.rutinapp.viewmodels.RoutinesViewModel
import java.util.Locale
import kotlin.math.max

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoutinesScreen(viewModel: RoutinesViewModel, navController: NavHostController) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val routines by viewModel.routines.collectAsStateWithLifecycle(
        lifecycle = lifecycle
    )

    //val flo = flow {
    //    while (true){
    //        emit(routines.size)
    //        Log.d("RoutinesScreen", "RoutinesScreen: ${routines.size}")
    //    }
    //}
//
    //flo.collectAsStateWithLifecycle(initialValue = 0, lifecycle = lifecycle)

    val uiState by viewModel.uiState.observeAsState()

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

    ScreenContainer(
        onExit = {
            viewModel.backToObserve()
            navController.navigateUp()
        },
        bottomButtonAction = { viewModel.clickCreateRoutine() },
        title = "Rutinas",
        buttonText = "Crear nueva rutina"
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            items(routines.map {
                it.targetedBodyPart.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.ROOT
                    ) else it.toString()
                }
            }
                .distinct()) { thisBodyPart ->

                Column {
                    Text(
                        text = thisBodyPart,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textDecoration = TextDecoration.Underline
                    )
                    LazyRow {
                        items(routines.filter {
                            it.targetedBodyPart.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.ROOT
                                ) else it.toString()
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

}

@Composable
fun EditRoutineDialog(uiState: RoutinesScreenState.Editing, viewModel: RoutinesViewModel) {

    Dialog(onDismissRequest = { viewModel.backToObserve() }) {
        AnimatedVisibility(visible = uiState.positionOfScreen && uiState.selectedExercise == null,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it }) {
            DialogContainer {
                EditRoutineContent(uiState, viewModel)
            }
        }
        AnimatedVisibility(visible = !uiState.positionOfScreen,
            enter = slideInHorizontally { -it },
            exit = slideOutHorizontally { -it }) {
            DialogContainer {
                EditRoutineExercises(uiState, viewModel)
            }
        }
        AnimatedVisibility(visible = uiState.selectedExercise != null && uiState.positionOfScreen,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it }) {
            DialogContainer {
                EditRoutineExerciseRelation(uiState = uiState, viewModel = viewModel)
            }
        }

    }

}

@Composable
fun EditRoutineExerciseRelation(
    uiState: RoutinesScreenState.Editing, viewModel: RoutinesViewModel
) {

    var setsAndReps by rememberSaveable { mutableStateOf(uiState.selectedExercise!!.setsAndReps) }

    var manualEdition by rememberSaveable { mutableStateOf(!setsAndReps.isSetsAndReps()) }

    var observations by rememberSaveable { mutableStateOf(uiState.selectedExercise!!.observations) }

    Text(
        text = uiState.routine.name + " -> " + uiState.selectedExercise!!.name,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )

    if (manualEdition) {
        TextFieldWithTitle(
            title = "Series y repeticiones",
            text = setsAndReps,
            onWrite = { setsAndReps = it })
    } else {
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(
                text = "Sets",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
            )
            Text(
                text = "Repeticiones",
                Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .background(TextFieldColor, RoundedCornerShape(15.dp)),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { setsAndReps = setsAndReps.changeValue(true, true) }) {
                Icon(
                    imageVector = Icons.TwoTone.KeyboardArrowUp, contentDescription = "Add to sets"
                )
            }
            Text(text = setsAndReps.split("x")[0])
            IconButton(onClick = { setsAndReps = setsAndReps.changeValue(true, false) }) {
                Icon(
                    imageVector = Icons.TwoTone.KeyboardArrowDown,
                    contentDescription = "Delete from sets"
                )
            }

            IconButton(onClick = { setsAndReps = setsAndReps.changeValue(false, true) }) {
                Icon(
                    imageVector = Icons.TwoTone.KeyboardArrowUp, contentDescription = "Add to sets"
                )
            }
            Text(text = setsAndReps.split("x")[1])
            IconButton(onClick = { setsAndReps = setsAndReps.changeValue(false, false) }) {
                Icon(
                    imageVector = Icons.TwoTone.KeyboardArrowDown,
                    contentDescription = "Delete from sets"
                )
            }
        }
    }

    TextFieldWithTitle(title = "Observaciones",
        text = observations,
        onWrite = { observations = it })

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = { viewModel.updateRoutineExerciseRelation(setsAndReps, observations) },
            colors = rutinAppButtonsColours(),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = if (uiState.selectedExercise.setsAndReps == setsAndReps && uiState.selectedExercise.observations == observations) Icons.TwoTone.ArrowBack else Icons.TwoTone.Check,
                contentDescription = "Delete exercise"
            )
        }
        Button(onClick = {
            manualEdition = !manualEdition
            if (!setsAndReps.isSetsAndReps()) {
                setsAndReps = "0x0"
            }
        }, colors = rutinAppButtonsColours()) {
            Text(text = "Cambiar modo de escritura")
        }
    }

}

fun String.isSetsAndReps(): Boolean {
    if (this.isEmpty()) return false
    if (this.split("x").size != 2) return false
    if (this.split("x")[0].toIntOrNull() == null) return false
    if (this.split("x")[1].toIntOrNull() == null) return false
    return true
}

fun String.changeValue(firstValue: Boolean, addOrDelete: Boolean): String {
    if (this.isSetsAndReps()) {
        var firstNumber = this.split("x")[0].toInt()
        var secondNumber = this.split("x")[1].toInt()
        if (firstValue) if (addOrDelete) firstNumber++
        else firstNumber--
        else if (addOrDelete) secondNumber++
        else secondNumber--
        return "${max(firstNumber, 0)}x${max(secondNumber, 0)}"
    } else return ""
}

@Composable
fun EditRoutineExercises(uiState: RoutinesScreenState.Editing, viewModel: RoutinesViewModel) {

    Text(text = uiState.routine.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)

    Row(
        Modifier
            .fillMaxWidth()
            .animateContentSize(alignment = Alignment.Center),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Ejercicios")
        if (uiState.selectedExercise != null) Row {

            IconButton(onClick = {
                viewModel.changeExercisePresenceOnRoutine()
            }) {
                Icon(
                    imageVector = if (uiState.selectedExercise in uiState.routine.exercises) Icons.TwoTone.Delete else Icons.TwoTone.Add,
                    contentDescription = "delete/add"
                )
            }

            if (uiState.selectedExercise in uiState.routine.exercises) IconButton(onClick = {
                viewModel.toggleEditingState(
                    true
                )
            }) {
                Icon(
                    imageVector = Icons.TwoTone.Edit, contentDescription = "Edit selected exercise"
                )
            }
        }
    }
    ListOfExercises(exerciseList = uiState.routine.exercises,
        selected = uiState.selectedExercise,
        selectExercise = { viewModel.selectExercise(it) })
    Text(text = "Ejercicios no incluidos")
    ListOfExercises(
        exerciseList = uiState.availableExercises, selected = uiState.selectedExercise
    ) {
        viewModel.selectExercise(it)
    }

    Button(onClick = { viewModel.toggleEditingState() }, colors = rutinAppButtonsColours()) {
        Text(
            text = "Ir a rutina",
            Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
    }

}

@Composable
fun ListOfExercises(
    exerciseList: List<ExerciseModel>,
    selected: ExerciseModel?,
    selectExercise: (ExerciseModel) -> Unit
) {

    //doing this so UI doesnt explode because of animations, not the best solution, but if
    //not done, there's a case where animation tries to access to an index of the list that
    //doesnt exist but since ui hasnt noticed it, it crashes, would only happen if opening
    //the exercise and while animation is running, delete the exercise from routine

    val exercises = exerciseList.toList()

    LazyColumn(
        Modifier
            .background(TextFieldColor, RoundedCornerShape(15.dp))
            .padding(16.dp)
            .fillMaxWidth()
            .heightIn(max = 200.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (exercises.isEmpty()) item {
            Text(text = "No hay ejercicios disponibles", color = Color.Red)
        } else items(exercises, contentType = { ExerciseModel::class.java }) {

            ExerciseItemForRoutineEditing(
                item = it, modifier = Modifier
                    .animateItem()
                    .background(
                        if (it != selected) TextFieldColor else TextFieldColor.copy(0.7f),
                        RoundedCornerShape(15.dp)
                    )
                    .padding(8.dp), onEditClick = { selectExercise(it) }, opened = it == selected
            )
        }
    }
}

@Composable
fun EditRoutineContent(uiState: RoutinesScreenState.Editing, viewModel: RoutinesViewModel) {

    var name by rememberSaveable { mutableStateOf(uiState.routine.name) }
    var targetedBodyPart by rememberSaveable { mutableStateOf(uiState.routine.targetedBodyPart) }

    Text(text = uiState.routine.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)

    TextFieldWithTitle(title = "Nombre", text = name, onWrite = { name = it })
    TextFieldWithTitle(title = "Parte del cuerpo que entrena",
        text = targetedBodyPart,
        onWrite = { targetedBodyPart = it })

    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {

        Button(
            onClick = { viewModel.toggleEditingState() }, colors = rutinAppButtonsColours()
        ) {
            Text(text = "Ir a ejercicios")
        }
        Button(onClick = { viewModel.backToObserve() }, colors = rutinAppButtonsColours()) {
            Text(text = "Salir", Modifier.wrapContentWidth(Alignment.CenterHorizontally))
        }
    }


}

@Composable
fun ObserveRoutineDialog(uiState: RoutinesScreenState.Observe, viewModel: RoutinesViewModel) {

    Dialog(onDismissRequest = { viewModel.backToObserve() }) {
        DialogContainer {

            Text(text = uiState.routine.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)

            TextFieldWithTitle(
                title = "Nombre", text = uiState.routine.name, editing = false
            )
            TextFieldWithTitle(
                title = "Parte del cuerpo", text = uiState.routine.targetedBodyPart, editing = false
            )
            Text(text = "Ejercicios", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .background(TextFieldColor, RoundedCornerShape(15.dp))
                    .padding(16.dp)
                    .heightIn(max = 300.dp)
                    .fillMaxWidth()
            ) {

                if (uiState.routine.exercises.isEmpty()) item {
                    Text(text = "No hay ejercicios en esta rutina", color = Color.Red)
                } else items(uiState.routine.exercises) {
                    SimpleExerciseItem(
                        item = it, modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { viewModel.clickEditRoutine(uiState.routine) },
                    colors = rutinAppButtonsColours()
                ) {
                    Text(
                        text = "Editar", fontWeight = FontWeight.Bold, fontSize = 16.sp
                    )
                }

                Button(onClick = { viewModel.backToObserve() }, colors = rutinAppButtonsColours()) {
                    Text(
                        text = "Salir", fontWeight = FontWeight.Bold, fontSize = 16.sp
                    )
                }

            }

        }
    }

}

@Composable
fun CreateRoutineDialog(viewModel: RoutinesViewModel) {

    Dialog(onDismissRequest = { viewModel.backToObserve() }) {
        DialogContainer {
            RoutineCreationPhase(onDismissRequest = { viewModel.backToObserve() },
                onNextPhase = { name, targetedBodyPart ->
                    viewModel.createRoutine(name, targetedBodyPart)
                })

        }
    }
}

@Composable
fun RoutineCreationPhase(
    onDismissRequest: () -> Unit, onNextPhase: (String, String) -> Unit
) {

    var name by rememberSaveable { mutableStateOf("") }
    var targetedBodyPart by rememberSaveable { mutableStateOf("") }
    Text(
        text = "Crear nueva rutina",
        Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally),
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
    TextFieldWithTitle(title = "Nombre de la rutina", text = name, onWrite = { name = it })
    TextFieldWithTitle(title = "Parte del cuerpo a la que se aplica",
        text = targetedBodyPart,
        onWrite = { targetedBodyPart = it },
        sendFunction = { onNextPhase(name, targetedBodyPart) })
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { onNextPhase(name, targetedBodyPart) }, colors = rutinAppButtonsColours()
        ) {
            Text(text = "Aceptar")
        }
        Button(onClick = onDismissRequest, colors = rutinAppButtonsColours()) {
            Text(text = "Cancelar")
        }
    }
}


@Composable
fun RoutineCard(routine: RoutineModel, modifier: Modifier) {
    Card(
        shape = RoundedCornerShape(15.dp),
        colors = rutinappCardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = Modifier.padding(16.dp),
        border = BorderStroke(2.dp, Color.Black)
    ) {
        Text(text = routine.name, modifier = modifier.padding(16.dp))
    }
}


@Composable
fun ExerciseItemForRoutineEditing(
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
                            .background(PrimaryColor, RoundedCornerShape(15.dp))
                            .padding(8.dp)
                            .heightIn(max = 200.dp)
                            .fillMaxWidth(),

                        ) {
                        items(item.equivalentExercises) {
                            Box(
                                modifier = Modifier.border(
                                    1.dp, TextFieldColor, RoundedCornerShape(15.dp)
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

@Composable
fun ExerciseItemForRoutineCreationPhase(
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
                            .background(PrimaryColor, RoundedCornerShape(15.dp))
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