package com.example.rutinapp.ui.RutinApp

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.rutinapp.newData.models.ExerciseModel
import com.example.rutinapp.newData.models.RoutineModel
import com.example.rutinapp.viewmodels.MainViewModel

@Composable
fun RoutinesDialog(viewModel: MainViewModel, innerPadding: PaddingValues, back : ()->Unit) {

    val exercises by viewModel.exercises.collectAsState()

    val routines by viewModel.routines.collectAsState()

    var routineName by rememberSaveable { mutableStateOf("") }

    var routineTarget by rememberSaveable { mutableStateOf("") }

    var selectedRoutine: RoutineModel? by rememberSaveable { mutableStateOf(null) }

    var selectedExercise: ExerciseModel? by rememberSaveable {
        mutableStateOf(
            null
        )
    }

    Dialog(onDismissRequest = back) {

        LazyColumn(Modifier.height(500.dp)) {
            item {
                Row(Modifier.fillMaxWidth()) {

                    LazyColumn(
                        Modifier
                            .fillMaxWidth(0.5f)
                            .height(200.dp)) {
                        items(exercises) {
                            Text(text = it.name,)
                        }
                    }
                    LazyColumn(
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp)) {
                        items(routines) {
                            Text(text = it.name,)
                        }
                    }
                }
            }
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(value = routineName,
                        onValueChange = { routineName = it }, label = { Text(text = "nombre de la rutina")})
                    TextField(value = routineTarget,
                        onValueChange = { routineTarget = it }, label = { Text(text = "objetivo de la rutina")})

                    Row {

                        Button(onClick = {
                            viewModel.addRoutine(
                                routineName, routineTarget
                            )
                        }) {
                            Text(text = "añadir")
                        }

                        Button(onClick = {
                            selectedRoutine?.let {
                                selectedExercise?.let { it1 ->
                                    viewModel.relateRoutine(
                                        it, it1
                                    )
                                }
                            }
                        }) {
                            Text(text = "relacionar a un ejercicio")
                        }
                    }
                }
            }
        }
    }

}