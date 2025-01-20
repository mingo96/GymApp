package com.example.rutinapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.ArrowForward
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material.icons.twotone.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.rutinapp.R
import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.data.models.RoutineModel
import com.example.rutinapp.data.models.SetModel
import com.example.rutinapp.data.models.WorkoutModel
import com.example.rutinapp.ui.screenStates.SetState
import com.example.rutinapp.ui.screenStates.WorkoutsScreenState
import com.example.rutinapp.ui.theme.PrimaryColor
import com.example.rutinapp.ui.theme.ScreenContainer
import com.example.rutinapp.ui.theme.TextFieldColor
import com.example.rutinapp.ui.theme.rutinAppButtonsColours
import com.example.rutinapp.utils.completeHourString
import com.example.rutinapp.viewmodels.WorkoutsViewModel
import java.util.Date

@Composable
fun WorkoutsScreen(viewModel: WorkoutsViewModel, navController: NavHostController) {

    val workoutScreenState by viewModel.workoutScreenStates.observeAsState(WorkoutsScreenState.Observe)

    val bottomButtonAction = {
        if (workoutScreenState is WorkoutsScreenState.WorkoutStarted) {
            viewModel.finishTraining()
        } else viewModel.startFromEmpty()
    }

    ScreenContainer(
        onExit = {

            if (workoutScreenState is WorkoutsScreenState.WorkoutStarted) {
                viewModel.backToObserve()
            } else {
                navController.navigateUp()
            }
        },
        bottomButtonAction = if (workoutScreenState is WorkoutsScreenState.WorkoutStarted && !(workoutScreenState as WorkoutsScreenState.WorkoutStarted).workout.isFinished) bottomButtonAction else null,
        title = if (workoutScreenState is WorkoutsScreenState.WorkoutStarted) "Progreso de entrenamiento" else "Entrenamientos",
        buttonText = if (workoutScreenState is WorkoutsScreenState.WorkoutStarted) {
            "Finalizar entrenamiento"
        } else "Empezar entrenamiento sin rutina"
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
                                uiState = workoutScreenState as WorkoutsScreenState.WorkoutStarted,
                                navController = navController
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

@SuppressLint("SimpleDateFormat")
@Composable
fun WorkoutProgression(
    viewModel: WorkoutsViewModel,
    uiState: WorkoutsScreenState.WorkoutStarted,
    navController: NavHostController
) {


    if (uiState.setBeingCreated != null) {
        if (uiState.setBeingCreated is SetState.CreatingSet) {
            SetEditionDialog(viewModel = viewModel, set = uiState.setBeingCreated.set)
        } else {
            SetOptionsDialog(viewModel = viewModel, uiState = uiState)
        }
    } else if (uiState.exerciseBeingSwapped != null) {
        ExerciseSwapDialog(viewModel = viewModel, exercise = uiState.exerciseBeingSwapped)
    }

    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (uiState.workout.exercisesAndSets.isNotEmpty() && uiState.workout.exercisesAndSets.first().second.isNotEmpty()) {
            val lastSet = uiState.workout.exercisesAndSets.maxOf {
                if (it.second.isEmpty()) Date(0) else it.second.maxOf { it.date }
            }
            Text(
                text = "Último hecho " + lastSet.completeHourString(),
                fontSize = 18.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
        if (!uiState.workout.isFinished) {
            val actualDate by viewModel.currentDate.collectAsStateWithLifecycle(initialValue = System.currentTimeMillis())

            Text(
                text = Date(actualDate).completeHourString(),
                fontSize = 18.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }

    if (!uiState.workout.isFinished) BoxWithConstraints {

        val listState = rememberLazyListState()
        val flingBehavior = rememberSnapFlingBehavior(
            lazyListState = listState, snapPosition = SnapPosition.Start
        )
        LazyRow(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        ) {
            if (uiState.workout.baseRoutine != null) item {
                RoutineContent(uiState = uiState, viewModel = viewModel, maxWidth - 32.dp)
            }
            item {
                OtherExercises(
                    uiState = uiState,
                    viewModel = viewModel,
                    navController = navController,
                    maxWidth - 32.dp
                )
            }
        }
    }

    Text(
        text = "Progreso" + if (uiState.workout.isFinished) " del entrenamiento" else " actual",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )

    LazyColumn(
        Modifier
            .fillMaxWidth()
            .heightIn(max = 500.dp)
            .padding(top = 16.dp),
    ) {
        items(uiState.workout.exercisesAndSets, key = { it.first.id }) {
            Column(modifier = Modifier.animateItem(placementSpec = spring(stiffness = Spring.StiffnessHigh))) {

                var setsOpened by rememberSaveable {
                    mutableStateOf(false)
                }

                ExerciseInfo(it,
                    uiState,
                    setsOpened,
                    { setsOpened = !setsOpened }) { viewModel.startSwappingExercise(it.first) }

                ExerciseSets(it, viewModel, setsOpened, uiState)

                if (!uiState.workout.isFinished) ExerciseActions(
                    viewModel = viewModel, exerciseAndSets = it, uiState = uiState
                )
            }

        }
    }

}

@Composable
fun ExerciseSwapDialog(viewModel: WorkoutsViewModel, exercise: ExerciseModel) {
    val context = LocalContext.current
    Dialog(onDismissRequest = { viewModel.cancelExerciseSwap() }) {
        DialogContainer {
            Text(text = "Cambiar ejercicio por", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            LazyColumn(Modifier.fillMaxWidth()) {
                items(exercise.equivalentExercises) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = it.name)
                        IconButton(onClick = { viewModel.swapExerciseBeingSwapped(it, context) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.swap),
                                contentDescription = "swap exercise for ${it.name}"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SetOptionsDialog(viewModel: WorkoutsViewModel, uiState: WorkoutsScreenState.WorkoutStarted) {

    val setState = uiState.setBeingCreated!! as SetState.OptionsOfSet

    var isEditing by rememberSaveable { mutableStateOf(false) }

    if (isEditing) {
        SetEditionDialog(viewModel = viewModel, set = setState.set)
    } else

        Dialog(
            onDismissRequest = { viewModel.cancelSetEditing() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {

            DialogContainer {
                Text(
                    text = "Opciones de serie",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 40.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    TextContainer(
                        text = setState.set.observations,
                        title = "Observaciones",
                        modifier = Modifier
                            .background(TextFieldColor, RoundedCornerShape(15.dp))
                            .fillMaxWidth()
                    )
                    TextContainer(
                        text = setState.set.date.toGMTString().take(20),
                        title = "Momento de ejecución",
                        modifier = Modifier
                            .background(TextFieldColor, RoundedCornerShape(15.dp))
                            .fillMaxWidth()
                    )
                }

                ButtonsOfEditSet(exit = { viewModel.cancelSetEditing() },
                    onEditClick = { isEditing = true }) {
                    viewModel.deleteSet(setState.set)
                }

            }

        }
}

@Composable
fun ButtonsOfEditSet(exit: () -> Unit, onEditClick: () -> Unit, delete: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = { onEditClick() }, colors = rutinAppButtonsColours()) {
            Text(text = "Editar", fontSize = 20.sp)
        }
        Button(onClick = { exit() }, colors = rutinAppButtonsColours()) {
            Text(text = "Salir", fontSize = 20.sp)
        }
        Button(onClick = { delete() }, colors = rutinAppButtonsColours()) {
            Text(text = "Eliminar", fontSize = 20.sp)
        }
    }
}

@Composable
fun ExerciseSets(
    exerciseAndSets: Pair<ExerciseModel, List<SetModel>>,
    viewModel: WorkoutsViewModel,
    setsOpened: Boolean,
    uiState: WorkoutsScreenState.WorkoutStarted
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
                    if (!uiState.workout.isFinished) IconButton(onClick = {
                        viewModel.setOptionsClicked(i)
                    }) {
                        Icon(
                            imageVector = Icons.TwoTone.MoreVert, contentDescription = "set options"
                        )
                    }
                }
            }
        }
    }

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
            Icon(imageVector = Icons.TwoTone.Add, contentDescription = "add sett to exercise")
        }
        IconButton(onClick = { viewModel.removeExerciseFromRoutine(exerciseAndSets.first) }) {
            Icon(imageVector = Icons.TwoTone.Delete, contentDescription = "delete exercise")
        }
        if (exerciseAndSets != uiState.workout.exercisesAndSets.last()) {
            IconButton(onClick = { viewModel.moveExercise(exerciseAndSets.first, false) }) {
                Icon(
                    imageVector = Icons.TwoTone.ArrowBack,
                    contentDescription = "move up",
                    modifier = Modifier.rotate(-90f)
                )
            }
        }
        if (exerciseAndSets != uiState.workout.exercisesAndSets.first()) {
            IconButton(onClick = { viewModel.moveExercise(exerciseAndSets.first, true) }) {
                Icon(
                    imageVector = Icons.TwoTone.ArrowBack,
                    contentDescription = "move down",
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
    changeSetsOpened: () -> Unit,
    startSwapping: () -> Unit
) {

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = exerciseAndSets.first.name + if (exerciseAndSets.first in (uiState.workout.baseRoutine?.exercises
                    ?: emptyList())
            ) " (" + exerciseAndSets.first.setsAndReps + ")" else "",
            fontSize = 15.sp,
            maxLines = 1,
            modifier = Modifier.padding(end = 16.dp)
        )
        if (exerciseAndSets.second.isNotEmpty()) IconButton(onClick = { changeSetsOpened() }) {
            Icon(
                imageVector = if (!setsOpened) Icons.TwoTone.KeyboardArrowDown else Icons.TwoTone.KeyboardArrowUp,
                contentDescription = "toggle sets being opened"
            )
        }
        else if (!uiState.workout.isFinished && exerciseAndSets.first.equivalentExercises.isNotEmpty()) {

            IconButton(onClick = { startSwapping() }) {
                Icon(
                    painter = painterResource(id = R.drawable.swap),
                    contentDescription = "change exercise"
                )
            }

        }
    }
}

@Composable
fun SetEditionDialog(viewModel: WorkoutsViewModel, set: SetModel) {

    Dialog(onDismissRequest = { viewModel.cancelSetEditing() }) {

        var reps by rememberSaveable { mutableIntStateOf(set.reps) }
        var weight by rememberSaveable { mutableStateOf(set.weight.toString()) }
        var observations by rememberSaveable { mutableStateOf(set.observations) }

        DialogContainer {
            Text(text = "Añadir serie", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Repeticiones")
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { reps++ }) {
                    Icon(
                        imageVector = Icons.TwoTone.KeyboardArrowUp,
                        contentDescription = "more reps"
                    )
                }
                Text(text = reps.toString(), fontSize = 20.sp)
                IconButton(onClick = { reps-- }) {
                    Icon(
                        imageVector = Icons.TwoTone.KeyboardArrowDown,
                        contentDescription = "less reps"
                    )
                }
            }
            TextFieldWithTitle(
                title = "Peso", text = weight, onWrite = {
                    try {
                        it.toDouble()
                        weight = it
                    } catch (_: Exception) {

                    }
                }, typeOfKeyBoard = KeyboardType.Number
            )
            TextFieldWithTitle(title = "Observaciones",
                text = observations,
                onWrite = { observations = it })
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(colors = rutinAppButtonsColours(), onClick = {
                    viewModel.saveSet(
                        weight = weight.toDouble(), reps = reps, observations = observations
                    )
                }) {
                    Text(text = "Guardar")
                }
                Button(colors = rutinAppButtonsColours(),
                    onClick = { viewModel.cancelSetEditing() }) {
                    Text(text = "Cancelar")
                }
            }
        }
    }
}

@Composable
fun RoutineContent(
    uiState: WorkoutsScreenState.WorkoutStarted, viewModel: WorkoutsViewModel, maxWidth: Dp = 300.dp
) {

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
                    .widthIn(max = maxWidth - 36.dp),
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
fun OtherExercises(
    uiState: WorkoutsScreenState.WorkoutStarted,
    viewModel: WorkoutsViewModel,
    navController: NavHostController,
    maxWidth: Dp = 300.dp
) {

    Column(
        modifier = Modifier
            .background(TextFieldColor, RoundedCornerShape(15.dp))
            .width(maxWidth + 36.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Ejercicios disponibles", fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
                var name by rememberSaveable { mutableStateOf("") }
                SearchTextField(
                    value = name,
                    onValueChange = { name = it },
                    onSearch = { viewModel.searchExercise(name) },
                    modifier = Modifier
                )
            }

            LazyColumn(
                Modifier
                    .widthIn(150.dp, maxWidth)
                    .heightIn(max = 300.dp)
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
                            Icon(
                                imageVector = Icons.TwoTone.Add,
                                contentDescription = "add exercise to workout"
                            )
                        }
                    }
                }
                item {
                    IconButton(onClick = {
                        navController.navigate("exercises")
                        viewModel.exercisesViewModel.clickToCreate()
                    }, Modifier.fillMaxWidth()) {
                        Icon(
                            imageVector = Icons.TwoTone.Add, contentDescription = "add new exercise"
                        )
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
        .background(
            PrimaryColor, RoundedCornerShape(15.dp)
        )
        .clickable { onClick() }) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {

            Text(text = item.title)
            Text(text = "Inicio : " + item.date.completeHourString())
            if (item.isFinished)
                Text(text = "Terminado", color = Color.Green)
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