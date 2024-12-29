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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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

    val uiState by statsViewModel.uiState.observeAsState(StatsScreenState.Observation())


    ScreenContainer(title = "Tus estadisticas", onExit = { navController.navigateUp() }) {

        when (uiState) {
            is StatsScreenState.Observation -> {

                Column(Modifier.padding(it), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = "Ejercicios", fontSize = 30.sp)

                        var name by rememberSaveable { mutableStateOf("") }
                        SearchTextField(
                            value = name,
                            onValueChange = { name = it },
                            onSearch = { statsViewModel.searchExercise(name) },
                            modifier = Modifier
                        )
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                        items(if (uiState is StatsScreenState.Observation) (uiState as StatsScreenState.Observation).exercises else exercises) {
                            ExerciseItem(exercise = it) {
                                statsViewModel.selectExerciseForStats(it)
                            }
                        }

                    }
                }
            }

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

    if (!uiState.hasBeenDone) {
        Text(text = "Aún no has hecho este ejercicio", fontSize = 30.sp, color = Color.Red, modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center)
    } else {
        Text(text = "Estadisticas de " + uiState.exercise.name, fontSize = 30.sp)

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier
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
                content = uiState.mostWeightOnASet, title = "Mayor peso en una serie"
            )

            TextContainer(
                text = uiState.averageWeight.toString() + " kg", title = "Peso promedio"
            )

            TextContainer(text = uiState.timesDone.toString(), title = "Veces hecho")

            TextContainer(text = uiState.lastTimeDone, title = "Ultima vez hecho")

        }
    }
}

@Composable
fun TextContainer(text: String, title: String, modifier: Modifier = Modifier) {

    Column(modifier) {
        Column(modifier = Modifier.padding(8.dp)) {

            Text(text = title, fontSize = 25.sp)
            Text(text = text, fontSize = 25.sp)
        }
    }
}

@Composable
fun WeightContainer(content: Triple<Double, Date, String>, title: String) {

    var isOpened by rememberSaveable {
        mutableStateOf(false)
    }

    Box(Modifier.padding(8.dp)) {
        AnimatedVisibility(visible = !isOpened,
            enter = slideInHorizontally { -it },
            exit = slideOutHorizontally { -it }) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {

                Column(Modifier.fillMaxWidth(0.8f)) {
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