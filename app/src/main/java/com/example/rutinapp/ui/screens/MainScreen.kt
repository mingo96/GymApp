package com.example.rutinapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.rutinapp.ui.theme.ScreenContainer
import com.example.rutinapp.ui.theme.rutinAppButtonsColours

@Composable
fun MainScreen(navController: NavHostController){

    ScreenContainer(
        title = "Menú principal",
        buttonText = ""
    ) {
        Column(Modifier.padding(it)) {

            Button(onClick = { navController.navigate("exercises") }, colors = rutinAppButtonsColours()) {
                Text(text = "ejercicios")
            }
            Button(onClick = { navController.navigate("routines") }, colors = rutinAppButtonsColours()) {
                Text(text = "Rutinas")
            }
            Button(onClick = { navController.navigate("workouts") }, colors = rutinAppButtonsColours()) {
                Text(text = "entrenamientos")
            }
            Button(onClick = { navController.navigate("stats") }, colors = rutinAppButtonsColours()) {
                Text(text = "Estadisticas")
            }

            Button(onClick = { navController.navigate("Settings") }, colors = rutinAppButtonsColours()) {
                Text(text = "Configuración")
            }
        }
    }

}