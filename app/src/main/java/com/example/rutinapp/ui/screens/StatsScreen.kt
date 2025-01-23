package com.example.rutinapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.rutinapp.data.models.ExerciseModel
import com.example.rutinapp.ui.premade.AnimatedItem
import com.example.rutinapp.ui.premade.RutinAppLineChart
import com.example.rutinapp.ui.premade.RutinAppPieChart
import com.example.rutinapp.ui.screenStates.StatsScreenState
import com.example.rutinapp.ui.theme.ContentColor
import com.example.rutinapp.ui.theme.ScreenContainer
import com.example.rutinapp.ui.theme.SecondaryColor
import com.example.rutinapp.ui.theme.TextFieldColor
import com.example.rutinapp.utils.dateString
import com.example.rutinapp.utils.timeString
import com.example.rutinapp.utils.truncatedToNDecimals
import com.example.rutinapp.viewmodels.StatsViewModel
import kotlinx.coroutines.delay
import java.util.Date

@Composable
fun StatsScreen(navController: NavHostController, statsViewModel: StatsViewModel) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val exercises by statsViewModel.exercisesState.collectAsStateWithLifecycle(
        initialValue = emptyList(), lifecycle = lifecycle
    )

    val uiState by statsViewModel.uiState.observeAsState(StatsScreenState.Observation())

    ScreenContainer(title = "Tus estadisticas", onExit = {
        if(uiState is StatsScreenState.StatsOfExercise) statsViewModel.backToObservation() else
        navController.navigateUp()
    }) {

        Column(Modifier.padding(it), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            when (uiState) {
                is StatsScreenState.Observation -> {

                    ObservationContent(statsViewModel,exercises)

                }

                is StatsScreenState.StatsOfExercise -> {
                    ExerciseStats(uiState = uiState as StatsScreenState.StatsOfExercise,
                        onExit = { statsViewModel.backToObservation() })
                }

            }
        }

    }

}

@Composable
fun ObservationContent(statsViewModel: StatsViewModel, exercises: List<ExerciseModel>){

    var maxIndex by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(exercises) {
        while (true) {
            delay(100)
            if (maxIndex < exercises.size) maxIndex++
        }
    }

    AnimatedItem(enterAnimation = slideInHorizontally { it }, delay = 100) {

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
    }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

        items(exercises.take(maxIndex)) {
            AnimatedItem(enterAnimation = slideInVertically(), delay = 50) {

                ExerciseItem(exercise = it) {
                    statsViewModel.selectExerciseForStats(it)
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
fun ExerciseStats(uiState: StatsScreenState.StatsOfExercise, onExit: () -> Unit) {

    BackHandler(onBack = onExit)

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = if (!uiState.hasBeenDone) "Aún no has hecho este ejercicio" else "Estadisticas de " + uiState.exercise.name,
            fontSize = 30.sp,
            modifier = Modifier.fillMaxWidth(0.8f),
            color = if (!uiState.hasBeenDone) Color.Red else ContentColor,
        )
    }
    if (uiState.hasBeenDone) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {

            item(span = { GridItemSpan(2) }) {
                Column {

                    TextContainer(title = "Gráfica de rendimiento")

                    RutinAppLineChart(value = uiState.weigths)
                }
            }
            item(span = { GridItemSpan(2) }) {
                Column {

                    TextContainer(title = "Días que lo entrenas")
                    RutinAppPieChart(values = uiState.daysDone)
                }
            }
            item {

                WeightContainer(
                    content = uiState.highestWeight, title = "Mayor peso"
                )

            }
            item {

                TextContainer(
                    text = uiState.averageWeight.truncatedToNDecimals(2) + " kg",
                    title = "Peso promedio"
                )
            }
            item {

                TextContainer(text = uiState.timesDone.toString(), title = "Veces hecho")

            }
            item {
                TextContainer(text = uiState.lastTimeDone, title = "Ultima vez hecho")

            }


        }
    }
}

@Composable
fun TextContainer(modifier: Modifier = Modifier, text: String? = null, title: String) {

    Column(modifier) {
        Column(modifier = Modifier.padding(8.dp)) {

            Text(text = title, fontSize = 20.sp)
            if (text != null) Text(text = text, fontSize = 20.sp)
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
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(Modifier.fillMaxWidth(0.8f)) {
                    Text(text = title, fontSize = 20.sp)
                    Text(text = "${content.first.truncatedToNDecimals(2)} kg", fontSize = 20.sp)
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
                Text(
                    text = content.second.dateString() + " " + content.second.timeString(),
                    fontSize = 25.sp
                )
                Text(text = content.third, fontSize = 15.sp)
            }


        }
    }

}