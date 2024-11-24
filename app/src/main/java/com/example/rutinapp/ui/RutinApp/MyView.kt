package com.example.rutinapp.ui.RutinApp

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.rutinapp.viewmodels.MainViewModel

@Composable
fun ExercisesDialog(mainViewModel: MainViewModel, back : () -> Unit){

    val exercises by mainViewModel.exercises.collectAsState()

    Dialog(onDismissRequest = back) {
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            items(exercises) {
                Row {
                    Text(text = it.name + " : ${it.id} ",
                        color = Color.White,)
                    if (it.equivalentExercises.isNotEmpty()) {
                        for (exercise in it.equivalentExercises) {
                            Text(
                                text = exercise.name + " : ${exercise.id} ",
                                color = Color.White
                            )
                        }
                    }
                }
            }
            item {

                var exerciseName by rememberSaveable { mutableStateOf("nombre del ejercicio") }
                TextField(value = exerciseName, onValueChange = { exerciseName = it })
                Button(onClick = { mainViewModel.addExercise(exerciseName) }) {
                    Text(text = "test extra")
                }
                Button(onClick = { mainViewModel.relateExercises() }) {
                    Text(text = "relación aleatoria")
                }
            }
        }
    }


}