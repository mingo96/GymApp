package com.example.rutinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.rutinapp.ui.screenStates.ExercisesState
import com.example.rutinapp.ui.theme.PrimaryColor
import com.example.rutinapp.ui.theme.rutinAppButtonsColours
import com.example.rutinapp.ui.theme.rutinAppTextFieldColors
import com.example.rutinapp.viewmodels.MainViewModel

@Composable
fun ExercisesScreen(viewModel: MainViewModel, navController: NavController) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val exercises by viewModel.exercisesState.collectAsStateWithLifecycle(
        initialValue = emptyList(), lifecycle = lifecycle
    )

    val uiState by viewModel.uiState.observeAsState()

    ExercisesContainer(navController = navController, viewModel) { it ->
        when (uiState) {
            is ExercisesState.Modifying -> {
                var name by rememberSaveable { mutableStateOf((uiState as ExercisesState.Modifying).exerciseModel.name) }
                var description by rememberSaveable { mutableStateOf((uiState as ExercisesState.Modifying).exerciseModel.description) }
                var targetedBodyPart by rememberSaveable { mutableStateOf((uiState as ExercisesState.Modifying).exerciseModel.targetedBodyPart) }
                Dialog(onDismissRequest = { viewModel.backToObserve() }) {

                    DialogContainer {

                        TextFieldWithTitle(title = "Nombre", onWrite = { name = it }, text = name)
                        TextFieldWithTitle(
                            title = "Descripción",
                            onWrite = { description = it },
                            text = description
                        )
                        TextFieldWithTitle(
                            title = "Parte del cuerpo",
                            onWrite = { targetedBodyPart = it },
                            text = targetedBodyPart
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = {
                                viewModel.updateExercise(
                                    name, description, targetedBodyPart
                                )
                            }, colors = rutinAppButtonsColours()) {
                                Text(text = "Guardar")
                            }
                            Button(
                                onClick = { viewModel.backToObserve() },
                                colors = rutinAppButtonsColours()
                            ) {
                                Text(text = "Salir")
                            }
                        }

                    }

                }
            }

            is ExercisesState.Creating -> {

                var name by rememberSaveable { mutableStateOf("") }
                var description by rememberSaveable { mutableStateOf("") }
                var targetedBodyPart by rememberSaveable { mutableStateOf("") }
                Dialog(onDismissRequest = { viewModel.backToObserve() }) {
                    DialogContainer {

                        TextFieldWithTitle(title = "Nombre", onWrite = { name = it }, text = name)
                        TextFieldWithTitle(
                            title = "Descripción",
                            onWrite = { description = it },
                            text = description
                        )
                        TextFieldWithTitle(
                            title = "Parte del cuerpo",
                            onWrite = { targetedBodyPart = it },
                            text = targetedBodyPart
                        )
                        Button(
                            onClick = {
                                viewModel.addExercise(
                                    name, description, targetedBodyPart
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(align = Alignment.CenterHorizontally),
                            colors = rutinAppButtonsColours()
                        ) {
                            Text(text = "Añadir ejercicio")
                        }

                    }
                }
            }

            else -> {

            }
        }
        LazyColumn(
            Modifier
                .fillMaxHeight()
                .padding(vertical = 16.dp),
            contentPadding = it,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(exercises) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = it.name, fontWeight = FontWeight.Bold)
                        Text(
                            text = if(it.description.length>50) it.description.substring(0,40)+"..." else it.description,
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )
                    }
                    Icon(imageVector = Icons.TwoTone.Edit,
                        contentDescription = "editar",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { viewModel.clickToEdit(it) })
                }
            }
        }
    }

}

@Composable
fun ExercisesContainer(
    navController: NavController,
    viewModel: MainViewModel,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(modifier = Modifier.padding(32.dp), containerColor = PrimaryColor, topBar = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(PrimaryColor)
        ) {
            Icon(imageVector = Icons.Outlined.Clear,
                contentDescription = "Exit",
                Modifier
                    .clickable { navController.navigateUp() }
                    .size(40.dp))
            Text(
                text = "Ejercicios",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp)
            )
        }
    }, bottomBar = {
        Row(
            Modifier
                .fillMaxWidth()
                .background(PrimaryColor),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { viewModel.clickToCreate() },
                colors = rutinAppButtonsColours(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Añadir ejercicio", fontWeight = FontWeight.Bold, fontSize = 16.sp
                )
            }
        }
    }, content = content)
}

@Composable
fun TextFieldWithTitle(title: String, onWrite: (String) -> Unit, text: String) {

    Text(text = title)
    TextField(
        value = text,
        onValueChange = onWrite,
        colors = rutinAppTextFieldColors(),
        textStyle = TextStyle(fontWeight = FontWeight.Bold),
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(align = Alignment.CenterHorizontally)
    )
}

@Composable
fun DialogContainer(content: @Composable ColumnScope.() -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryColor, RoundedCornerShape(15.dp))
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(
            16.dp, Alignment.CenterVertically
        ), horizontalAlignment = Alignment.Start, content = content
    )
}