package com.example.rutinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.rutinapp.data.models.RoutineModel
import com.example.rutinapp.data.models.WorkoutModel
import com.example.rutinapp.ui.screenStates.WorkoutsScreenState
import com.example.rutinapp.ui.theme.PrimaryColor
import com.example.rutinapp.ui.theme.ScreenContainer
import com.example.rutinapp.ui.theme.TextFieldColor
import com.example.rutinapp.viewmodels.WorkoutsViewModel

@Composable
fun WorkoutsScreen(viewModel: WorkoutsViewModel, navController: NavHostController) {

    val workoutScreenState by viewModel.workoutScreenStates.observeAsState(WorkoutsScreenState.Observe)

    ScreenContainer(
        navController = navController,
        bottomButtonAction = {},
        title = "Entrenamientos",
        buttonText = "Empezar entrenamiento sin rutina"
    ) {
        Column(modifier = Modifier.padding(it), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            when (workoutScreenState) {
                WorkoutsScreenState.Observe -> {

                    ObservationContent(viewModel = viewModel)

                }

                is WorkoutsScreenState.WorkoutStarted -> {

                    WorkoutProgression(
                        viewModel = viewModel,
                        uiState = workoutScreenState as WorkoutsScreenState.WorkoutStarted
                    )

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
        horizontalArrangement = Arrangement.spacedBy(16.dp)
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
            WorkoutItem(item = it)
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

    Row() {
        if (uiState.workout.baseRoutine != null) {
            Column(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .background(TextFieldColor, RoundedCornerShape(15.dp))
                    .fillMaxWidth(0.5f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(text = "Rutina base", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Column(
                        modifier = Modifier
                            .background(PrimaryColor, RoundedCornerShape(15.dp))
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        for (i in uiState.workout.baseRoutine!!.exercises) {
                            Text(text = i.name, fontSize = 16.sp)
                        }
                    }

                }
            }
        }
        Column(
            modifier = Modifier.background(TextFieldColor, RoundedCornerShape(15.dp))
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                Text(
                    text = "Ejercicios disponibles", fontSize = 20.sp, fontWeight = FontWeight.Bold
                )

                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .background(PrimaryColor, RoundedCornerShape(15.dp))
                        .padding(16.dp)
                ) {
                    items(uiState.otherExercises) {
                        Text(text = it.name, fontSize = 16.sp)
                    }
                }
            }
        }
    }

    Text(text = "Progreso actual", fontSize = 20.sp, fontWeight = FontWeight.Bold)

    LazyColumn(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        items(uiState.workout.exercisesAndSets) {

            Text(
                text = it.first.name + if (it.first in (uiState.workout.baseRoutine?.exercises
                        ?: emptyList())
                ) " sets + reps esperadas : " + it.first.setsAndReps else "", fontSize = 16.sp
            )
            Column {
                for (i in it.second.sortedBy { it.date }) {
                    Text(text = i.reps.toString() + "x" + i.weight.toString(), fontSize = 16.sp)
                }
                IconButton(onClick = {
                    viewModel.addSet(it.first)
                }) {
                    Icon(imageVector = Icons.TwoTone.Add, contentDescription = " ")
                }
            }
        }
    }

}

@Composable
fun WorkoutItem(item: WorkoutModel) {
    Text(text = item.toString())
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