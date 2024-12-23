package com.example.rutinapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.ArrowForward
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.models.RoutineModel
import com.example.rutinapp.data.models.SetModel
import com.example.rutinapp.data.models.WorkoutModel
import com.example.rutinapp.ui.screenStates.WorkoutsScreenState
import com.example.rutinapp.ui.theme.PrimaryColor
import com.example.rutinapp.ui.theme.ScreenContainer
import com.example.rutinapp.ui.theme.TextFieldColor
import com.example.rutinapp.ui.theme.rutinAppButtonsColours
import com.example.rutinapp.viewmodels.WorkoutsViewModel

@Composable
fun WorkoutsScreen(viewModel: WorkoutsViewModel, navController: NavHostController) {

    val workoutScreenState by viewModel.workoutScreenStates.observeAsState(WorkoutsScreenState.Observe)

    ScreenContainer(onExit = {

        if (workoutScreenState is WorkoutsScreenState.WorkoutStarted) {
            viewModel.backToObserve()
        } else {
            navController.navigateUp()
        }
    },
        bottomButtonAction = {
            if (workoutScreenState is WorkoutsScreenState.WorkoutStarted) {
                viewModel.backToObserve()
            } else viewModel.startFromEmpty()
        },
        title = if (workoutScreenState is WorkoutsScreenState.WorkoutStarted) "Progreso de entrenamiento" else "Entrenamientos",
        buttonText = if (workoutScreenState is WorkoutsScreenState.WorkoutStarted) "Finalizar entrenamiento" else "Empezar entrenamiento sin rutina"
    ) {
        Column(modifier = Modifier.padding(it)) {
            when (workoutScreenState) {
                WorkoutsScreenState.Observe -> {

                    ObservationContent(viewModel = viewModel)

                }

                is WorkoutsScreenState.WorkoutStarted -> {

                    LazyColumn {

                        item {
                            WorkoutProgression(
                                viewModel = viewModel,
                                uiState = workoutScreenState as WorkoutsScreenState.WorkoutStarted
                            )
                        }
                    }

                }
            }
        }

    }

}

@Composable
fun ObservationContent(viewModel: WorkoutsViewModel) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val workouts by viewModel.workouts.collectAsStateWithLifecycle(
        initialValue = emptyList(), lifecycle = lifecycle
    )

    val routines by viewModel.routines.collectAsStateWithLifecycle(
        lifecycle = lifecycle, initialValue = emptyList()
    )

    Text(text = "Entrenamientos recientes", fontSize = 20.sp, fontWeight = FontWeight.Bold)

    LazyRow(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .background(TextFieldColor, RoundedCornerShape(15.dp)),
    ) {
        if (workouts.isEmpty()) {
            item {
                Text(
                    text = "No hay entrenamientos recientes",
                    fontSize = 16.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        items(workouts) {
            WorkoutItem(item = it, onClick = { viewModel.continueWorkout(it) })
        }
    }
    Text(text = "Rutinas", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    LazyRow(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .background(TextFieldColor, RoundedCornerShape(15.dp))
    ) {
        items(routines) {
            RoutineItem(routine = it, modifier = Modifier.padding(16.dp)) {
                viewModel.startFromRoutine(it)
            }
        }
    }

}

@Composable
fun WorkoutProgression(viewModel: WorkoutsViewModel, uiState: WorkoutsScreenState.WorkoutStarted) {

    if (uiState.setBeingCreated != null) {
        SetCreationDialog(viewModel = viewModel, uiState = uiState)
    }

    LazyRow(modifier = Modifier.padding(bottom = 16.dp)) {
        if (uiState.workout.baseRoutine != null) item {
            RoutineContent(uiState = uiState, viewModel = viewModel)
        }
        item {
            OtherExercises(uiState = uiState, viewModel = viewModel)

        }
    }

    Text(text = "Progreso actual", fontSize = 20.sp, fontWeight = FontWeight.Bold)

    LazyColumn(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp, max = 500.dp)
            .padding(top = 16.dp),
    ) {
        items(uiState.workout.exercisesAndSets, key = { it.first.id }) {
            Column(modifier = Modifier.animateItem(placementSpec = spring(stiffness = Spring.StiffnessHigh))) {

                var setsOpened by rememberSaveable {
                    mutableStateOf(false)
                }

                ExerciseInfo(it, uiState, setsOpened) { setsOpened = !setsOpened }

                ExerciseSets(it, viewModel, uiState, setsOpened)
            }

        }
    }

}

@Composable
fun ExerciseSets(
    exerciseAndSets: Pair<ExerciseModel, List<SetModel>>,
    viewModel: WorkoutsViewModel,
    uiState: WorkoutsScreenState.WorkoutStarted,
    setsOpened: Boolean
) {
    AnimatedVisibility(
        visible = setsOpened, enter = expandVertically(), exit = shrinkVertically()
    ) {

        Column {

            for (i in exerciseAndSets.second) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(TextFieldColor, RoundedCornerShape(15.dp))
                ) {
                    Text(
                        text = i.reps.toString() + " reps x " + i.weight.toString() + " kgs",
                        fontSize = 15.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.TwoTone.Edit, contentDescription = " ")
                    }
                }
            }
        }
    }
    ExerciseActions(
        viewModel = viewModel, exerciseAndSets = exerciseAndSets, uiState = uiState
    )

}

@Composable
fun ExerciseActions(
    viewModel: WorkoutsViewModel,
    exerciseAndSets: Pair<ExerciseModel, List<SetModel>>,
    uiState: WorkoutsScreenState.WorkoutStarted
) {

    Row {
        IconButton(onClick = {
            viewModel.addSetClicked(exerciseAndSets.first)
        }) {
            Icon(imageVector = Icons.TwoTone.Add, contentDescription = " ")
        }
        IconButton(onClick = { viewModel.removeExerciseFromRoutine(exerciseAndSets.first) }) {
            Icon(imageVector = Icons.TwoTone.Delete, contentDescription = " ")
        }
        if (exerciseAndSets != uiState.workout.exercisesAndSets.last()) {
            IconButton(onClick = { viewModel.moveExercise(exerciseAndSets.first, false) }) {
                Icon(
                    imageVector = Icons.TwoTone.ArrowBack,
                    contentDescription = " ",
                    modifier = Modifier.rotate(-90f)
                )
            }
        }
        if (exerciseAndSets != uiState.workout.exercisesAndSets.first()) {
            IconButton(onClick = { viewModel.moveExercise(exerciseAndSets.first, true) }) {
                Icon(
                    imageVector = Icons.TwoTone.ArrowBack,
                    contentDescription = " ",
                    modifier = Modifier.rotate(90f)
                )
            }
        }
    }
}

@Composable
fun ExerciseInfo(
    exerciseAndSets: Pair<ExerciseModel, List<SetModel>>,
    uiState: WorkoutsScreenState.WorkoutStarted,
    setsOpened: Boolean,
    changeSetsOpened: () -> Unit
) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = exerciseAndSets.first.name + if (exerciseAndSets.first in (uiState.workout.baseRoutine?.exercises
                    ?: emptyList())
            ) " (" + exerciseAndSets.first.setsAndReps + ")" else "", fontSize = 15.sp, maxLines = 1
        )
        if (exerciseAndSets.second.isNotEmpty()) IconButton(onClick = { changeSetsOpened() }) {
            Icon(
                imageVector = if (!setsOpened) Icons.TwoTone.KeyboardArrowDown else Icons.TwoTone.KeyboardArrowUp,
                contentDescription = " "
            )
        }
    }
}

@Composable
fun SetCreationDialog(viewModel: WorkoutsViewModel, uiState: WorkoutsScreenState.WorkoutStarted) {

    Dialog(onDismissRequest = { viewModel.cancelSetCreation() }) {

        var reps by rememberSaveable { mutableIntStateOf(uiState.setBeingCreated!!.reps) }
        var weight by rememberSaveable { mutableDoubleStateOf(uiState.setBeingCreated!!.weight) }
        var observations by rememberSaveable { mutableStateOf(uiState.setBeingCreated!!.observations) }

        DialogContainer {
            Text(text = "Añadir serie", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Repeticiones")
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { reps++ }) {
                    Icon(imageVector = Icons.TwoTone.KeyboardArrowUp, contentDescription = null)
                }
                Text(text = reps.toString(), fontSize = 20.sp)
                IconButton(onClick = { reps-- }) {
                    Icon(imageVector = Icons.TwoTone.KeyboardArrowDown, contentDescription = null)
                }
            }
            TextFieldWithTitle(
                title = "Peso",
                text = weight.toString(),
                onWrite = { weight = it.toDouble() },
                typeOfKeyBoard = KeyboardType.Number
            )
            TextFieldWithTitle(
                title = "Observaciones",
                text = observations,
                onWrite = { observations = it })
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(colors = rutinAppButtonsColours(), onClick = {
                    viewModel.createSet(
                        weight = weight, reps = reps, observations = observations
                    )
                }) {
                    Text(text = "Guardar")
                }
                Button(
                    colors = rutinAppButtonsColours(),
                    onClick = { viewModel.cancelSetCreation() }) {
                    Text(text = "Cancelar")
                }
            }
        }
    }
}

@Composable
fun RoutineContent(uiState: WorkoutsScreenState.WorkoutStarted, viewModel: WorkoutsViewModel) {

    Column(
        modifier = Modifier
            .padding(end = 16.dp)
            .background(TextFieldColor, RoundedCornerShape(15.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(text = "Rutina base", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            LazyColumn(
                modifier = Modifier
                    .background(PrimaryColor, RoundedCornerShape(15.dp))
                    .padding(16.dp)
                    .heightIn(max = 120.dp)
                    .widthIn(max = 200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.workout.baseRoutine!!.exercises) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = it.name, fontSize = 15.sp)
                        if (it.id !in uiState.workout.exercisesAndSets.map { it.first.id }) {
                            IconButton(onClick = { viewModel.addExerciseToWorkout(it) }) {
                                Icon(imageVector = Icons.TwoTone.Add, contentDescription = " ")
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun OtherExercises(uiState: WorkoutsScreenState.WorkoutStarted, viewModel: WorkoutsViewModel) {

    Column(
        modifier = Modifier.background(TextFieldColor, RoundedCornerShape(15.dp))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Text(
                text = "Ejercicios disponibles", fontSize = 20.sp, fontWeight = FontWeight.Bold
            )

            LazyColumn(
                Modifier
                    .widthIn(150.dp, 300.dp)
                    .heightIn(max = 150.dp)
                    .background(PrimaryColor, RoundedCornerShape(15.dp))
                    .padding(16.dp),
            ) {
                if (uiState.otherExercises.isEmpty()) {
                    item {
                        Text(
                            text = "No hay ejercicios disponibles",
                            fontSize = 15.sp,
                            color = Color.Red,
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )
                    }
                }
                items(uiState.otherExercises) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = it.name, fontSize = 15.sp, modifier = Modifier.fillMaxWidth(0.8f)
                        )

                        IconButton(onClick = {
                            viewModel.addExerciseToWorkout(it)
                        }) {
                            Icon(imageVector = Icons.TwoTone.Add, contentDescription = " ")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutItem(item: WorkoutModel, onClick: () -> Unit) {
    Box(modifier = Modifier
        .padding(16.dp)
        .background(PrimaryColor, RoundedCornerShape(15.dp))
        .clickable { onClick() }) {
        Column(Modifier.padding(16.dp)) {

            Text(text = item.title)
            Text(text = item.date.toLocaleString())
        }
    }
}

@Composable
fun RoutineItem(routine: RoutineModel, modifier: Modifier, onPress: () -> Unit) {
    Row(
        modifier = modifier.background(PrimaryColor, RoundedCornerShape(15.dp)),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.8f)
        ) {
            Text(text = routine.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = routine.targetedBodyPart)
            for (i in routine.exercises) {
                Text(text = i.name)
            }
        }
        IconButton(onClick = { onPress() }, modifier = Modifier.padding(end = 16.dp, top = 16.dp)) {
            Icon(imageVector = Icons.TwoTone.ArrowForward, contentDescription = null)
        }
    }
}