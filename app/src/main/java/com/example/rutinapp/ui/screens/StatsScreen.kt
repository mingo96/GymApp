package com.example.rutinapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.ui.screenStates.StatsScreenState
import com.example.rutinapp.ui.theme.ScreenContainer
import com.example.rutinapp.ui.theme.SecondaryColor
import com.example.rutinapp.ui.theme.TextFieldColor
import com.example.rutinapp.viewmodels.StatsViewModel
import kotlinx.coroutines.delay
import java.util.Date

@Composable
fun StatsScreen(navController: NavHostController, statsViewModel: StatsViewModel) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val exercises by statsViewModel.exercisesState.collectAsStateWithLifecycle(
        initialValue = emptyList(), lifecycle = lifecycle
    )

    val routines by statsViewModel.routinesState.collectAsState()

    val uiState by statsViewModel.uiState.observeAsState(StatsScreenState.Observation)

    when (uiState) {
        StatsScreenState.Observation -> {}
        is StatsScreenState.StatsOfExercise -> {
            Dialog(
                onDismissRequest = { statsViewModel.backToObservation() },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                DialogContainer {
                    ExerciseStats(uiState = uiState as StatsScreenState.StatsOfExercise)
                }
            }
        }
    }

    ScreenContainer(title = "Tus estadisticas", onExit = { navController.navigateUp() }) {

        Column(Modifier.padding(it), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Text(text = "Ejercicios", fontSize = 30.sp)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                items(exercises) {
                    ExerciseItem(exercise = it) {
                        statsViewModel.selectExerciseForStats(it)
                    }
                }

            }
        }

    }

}

@Composable
fun ExerciseItem(exercise: ExerciseModel, onClick: () -> Unit) {

    Column(modifier = Modifier
        .border(4.dp, SecondaryColor, RoundedCornerShape(15.dp))
        .background(
            TextFieldColor, RoundedCornerShape(15.dp)
        )
        .padding(16.dp)
        .clickable { onClick() }) {

        Text(text = exercise.name, fontSize = 20.sp)
        Text(text = exercise.description.take(30), fontSize = 15.sp)

    }

}

@Composable
fun ExerciseStats(uiState: StatsScreenState.StatsOfExercise) {

    Text(text = "Estadisticas de " + uiState.exercise.name, fontSize = 30.sp)

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier
            .background(
                TextFieldColor, RoundedCornerShape(15.dp)
            )
            .padding(8.dp)
            .fillMaxWidth()
    ) {

        WeightContainer(
            content = uiState.highestWeight, title = "Mayor peso usado"
        )

        WeightContainer(
            content = uiState.mostWeigthOnASet, title = "Mayor peso en una serie"
        )

        TextContainer(
            text = uiState.averageWeight.toString() + " kg", title = "Peso promedio"
        )

        TextContainer(text = uiState.timesDone.toString(), title = "Veces hecho")

        TextContainer(text = uiState.lastTimeDone, title = "Ultima vez hecho")

    }
}

@Composable
fun TextContainer(text: String, title: String) {

    Column {
        Text(text = title, fontSize = 25.sp)
        Text(text = text, fontSize = 25.sp)
    }
}

@Composable
fun WeightContainer(content: Triple<Double, Date, String>, title: String) {

    var isOpened by rememberSaveable {
        mutableStateOf(false)
    }

    Box() {
        AnimatedVisibility(visible = !isOpened,
            enter = slideInHorizontally { -it },
            exit = slideOutHorizontally { -it }) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {

                Column {
                    Text(text = title, fontSize = 25.sp)
                    Text(text = "${content.first} kg", fontSize = 25.sp)
                }
                IconButton(onClick = { isOpened = true }) {
                    Icon(
                        imageVector = Icons.TwoTone.Info,
                        contentDescription = "info about this lift"
                    )
                }
            }
        }
        AnimatedVisibility(visible = isOpened,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it }) {
            LaunchedEffect(key1 = isOpened) {
                delay(5000)
                isOpened = false
            }

            Column(Modifier.fillMaxWidth()) {
                Text(text = content.second.toGMTString(), fontSize = 25.sp)
                Text(text = content.third, fontSize = 25.sp)
            }


        }
    }

}