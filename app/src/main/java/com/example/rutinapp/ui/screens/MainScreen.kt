package com.example.rutinapp.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowForward
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.rutinapp.R
import com.example.rutinapp.data.models.RoutineModel
import com.example.rutinapp.ui.premade.RutinAppCalendar
import com.example.rutinapp.ui.screenStates.FieldBeingEdited
import com.example.rutinapp.ui.screenStates.MainScreenState
import com.example.rutinapp.ui.theme.ScreenContainer
import com.example.rutinapp.ui.theme.TextFieldColor
import com.example.rutinapp.ui.theme.rutinAppButtonsColours
import com.example.rutinapp.utils.simpleDateString
import com.example.rutinapp.viewmodels.MainScreenViewModel

@Composable
fun MainScreen(navController: NavHostController, mainScreenViewModel: MainScreenViewModel) {

    val plannings by mainScreenViewModel.plannings.collectAsState()

    val uiState by mainScreenViewModel.uiState.observeAsState(MainScreenState.Observation)

    when (uiState) {
        MainScreenState.Observation -> {

        }

        is MainScreenState.PlanningOnMainFocus -> {
            PlanningEditionDialog(
                mainScreenViewModel, uiState as MainScreenState.PlanningOnMainFocus
            )
        }
    }

    ScreenContainer(title = "Menú principal", buttonText = "", floatingActionButton = {
        IconButton(onClick = { navController.navigate("Settings") }) {
            Icon(
                Icons.TwoTone.Settings,
                contentDescription = "Settings",
                modifier = Modifier.size(50.dp)
            )
        }
    }) {
        LazyVerticalGrid(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {

                Button(
                    onClick = { navController.navigate("exercises") },
                    colors = rutinAppButtonsColours(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Ejercicios", fontSize = 20.sp)
                }
            }
            item {
                Button(
                    onClick = { navController.navigate("routines") },
                    colors = rutinAppButtonsColours(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Rutinas", fontSize = 20.sp)
                }
            }
            item {
                Button(
                    onClick = { navController.navigate("workouts") },
                    colors = rutinAppButtonsColours(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Entrenamientos", fontSize = 20.sp)
                }
            }
            item {
                Button(
                    onClick = { navController.navigate("stats") },
                    colors = rutinAppButtonsColours(),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(text = "Estadisticas", fontSize = 20.sp)
                }
            }

            item(span = { GridItemSpan(2) }) {

                RutinAppCalendar(plannings) {
                    mainScreenViewModel.planningClicked(it)
                }

            }
        }
    }

}

@Composable
fun PlanningEditionDialog(
    viewModel: MainScreenViewModel, uistate: MainScreenState.PlanningOnMainFocus
) {

    Dialog(onDismissRequest = { viewModel.backToObservation() }) {
        DialogContainer {
            Text(
                text = "Objetivo el " + uistate.planningModel.date.simpleDateString(),
                fontSize = 25.sp
            )

            when (uistate.fieldBeingEdited) {
                FieldBeingEdited.NONE -> {
                    NoFieldSelectedContent(viewModel)
                }

                FieldBeingEdited.BODYPART -> {
                    BodyPartSelectedContent(onSend = { viewModel.saveBodypart(it) }) {
                        viewModel.backToSelection()
                    }
                }

                FieldBeingEdited.ROUTINE -> {
                    RoutineSelectedContent(uistate = uistate, onSelect = { viewModel.saveRoutine(it) }, onBack = {
                        viewModel.backToSelection()
                    })
                }
            }

        }
    }
}

@Composable
fun NoFieldSelectedContent(viewModel: MainScreenViewModel) {

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        IconButton(
            onClick = { viewModel.selectBodypartClicked() }, modifier = Modifier.size(80.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.body),
                contentDescription = "select body part",
                modifier = Modifier.size(200.dp)
            )
        }
        IconButton(
            onClick = { viewModel.selectRoutineClicked() }, modifier = Modifier.size(80.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.list),
                contentDescription = "select routine",
                modifier = Modifier.size(200.dp)
            )
        }
    }
    Button(
        onClick = { viewModel.backToObservation() },
        colors = rutinAppButtonsColours(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Salir")
    }
}

@Composable
fun BodyPartSelectedContent(onSend: (String) -> Unit, onExit: () -> Unit) {

    var bodyPart by rememberSaveable {
        mutableStateOf("")
    }
    TextFieldWithTitle(title = "Parte del cuerpo",
        text = bodyPart,
        onWrite = { bodyPart = it },
        sendFunction = {
            onSend(bodyPart)
        })

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {

        Button(
            onClick = { onSend(bodyPart) }, colors = rutinAppButtonsColours()
        ) {
            Text(text = "Guardar")
        }
        Button(
            onClick = { onExit() }, colors = rutinAppButtonsColours()
        ) {
            Text(text = "Volver")
        }
    }
}

@Composable
fun RoutineSelectedContent(
    uistate: MainScreenState.PlanningOnMainFocus,
    onSelect: (RoutineModel) -> Unit,
    onBack: () -> Unit
) {

    Text(text = "Rutinas disponibles", fontSize = 25.sp)

    LazyColumn(Modifier.background(TextFieldColor, RoundedCornerShape(15.dp)).heightIn(0.dp, 300.dp)) {
        if (uistate.availableRoutines.isEmpty()) item {
            Text(text = "No hay rutinas disponibles", fontSize = 20.sp, modifier = Modifier.padding(16.dp), color = Color.Red)
        }
        items(uistate.availableRoutines) {
            Column(
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                var isOpened by rememberSaveable {
                    mutableStateOf(false)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = it.name, fontSize = 20.sp, modifier = Modifier.fillMaxWidth(0.7f))
                    Row {
                        IconButton(onClick = { isOpened = !isOpened }) {
                            Icon(
                                imageVector = if (isOpened) Icons.TwoTone.KeyboardArrowDown else Icons.TwoTone.KeyboardArrowUp,
                                contentDescription = "openclose ejercises"
                            )
                        }
                        IconButton(onClick = { onSelect(it) }) {
                            Icon(
                                imageVector = Icons.TwoTone.ArrowForward,
                                contentDescription = "select routine"
                            )
                        }
                    }
                }
                if (isOpened) {
                    Column {
                        for (i in it.exercises) {
                            Text(text = i.name, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
    Button(
        onClick = { onBack() },
        colors = rutinAppButtonsColours(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Volver")
    }

}