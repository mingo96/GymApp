package com.example.rutinapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.rutinapp.data.models.RoutineModel
import com.example.rutinapp.ui.screenStates.RoutinesScreenState
import com.example.rutinapp.ui.theme.PrimaryColor
import com.example.rutinapp.ui.theme.SecondaryColor
import com.example.rutinapp.ui.theme.rutinAppButtonsColours
import com.example.rutinapp.ui.theme.rutinappCardColors
import com.example.rutinapp.viewmodels.RoutinesViewModel

@Composable
fun RoutinesScreen(viewModel: RoutinesViewModel, navController: NavHostController) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val routines by viewModel.routinesState.collectAsStateWithLifecycle(
        initialValue = emptyList(), lifecycle = lifecycle
    )

    val uiState by viewModel.uiState.observeAsState()

    when (uiState) {
        is RoutinesScreenState.Creating -> {
            CreateRoutineDialog(uiState = uiState as RoutinesScreenState.Creating, viewModel)
        }

        is RoutinesScreenState.Editing -> {

        }

        is RoutinesScreenState.Observe -> {

        }

        null -> {}
    }

    RoutinesContainer(navController = navController, viewModel = viewModel) {
        LazyColumn(modifier = Modifier.padding(it)) {
            items(routines.map { it.targetedBodyPart.capitalize() }.distinct()) { thisBodyPart ->

                Column() {
                    Text(text = thisBodyPart, fontWeight = FontWeight.Bold, fontSize = 20.sp, textDecoration = TextDecoration.Underline)
                    LazyRow {
                        items(routines.filter { it.targetedBodyPart.capitalize() == thisBodyPart }) {
                            RoutineCard(routine = it)
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun RoutinesContainer(
    navController: NavHostController,
    viewModel: RoutinesViewModel,
    content: @Composable (PaddingValues) -> Unit
) {

    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        containerColor = PrimaryColor,
        topBar = { TopBar(navController = navController, "Rutinas") },
        bottomBar = {
            Button(
                onClick = { viewModel.clickCreateRoutine() }, colors = rutinAppButtonsColours()
            ) {
                Text(
                    text = "Crear nueva rutina",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        content = content
    )

}

@Composable
fun CreateRoutineDialog(uiState: RoutinesScreenState.Creating, viewModel: RoutinesViewModel) {

    Dialog(onDismissRequest = { viewModel.backToObserve() }) {
        DialogContainer {
            if (uiState.routine == null) {
                CreateRoutineContentPhase1(onDismissRequest = { viewModel.backToObserve() },
                    onNextPhase = { name, targetedBodyPart ->
                        viewModel.createRoutine(name, targetedBodyPart)
                    })
            }else{

            }
        }
    }
}

@Composable
fun CreateRoutineContentPhase1(
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
        onWrite = { targetedBodyPart = it })
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { onNextPhase(name, targetedBodyPart) }) {
            Text(text = "Aceptar")
        }
        Button(onClick = onDismissRequest) {
            Text(text = "Cancelar")
        }
    }
}

@Composable
fun RoutineCard(routine: RoutineModel) {
    Card(
        shape = RoundedCornerShape(15.dp),
        colors = rutinappCardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = Modifier.padding(16.dp),
        border = BorderStroke(2.dp, Color.Black)
    ) {
        Text(text = routine.name, modifier = Modifier.padding(16.dp))
    }
}