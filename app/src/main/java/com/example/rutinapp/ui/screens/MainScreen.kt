package com.example.rutinapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.rutinapp.ui.premade.RutinAppCalendar
import com.example.rutinapp.ui.theme.ScreenContainer
import com.example.rutinapp.ui.theme.rutinAppButtonsColours
import com.example.rutinapp.viewmodels.MainScreenViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController, mainScreenViewModel: MainScreenViewModel) {

    val plannings by mainScreenViewModel.plannings.collectAsState()

    ScreenContainer(title = "Menú principal", buttonText = "", floatingActionButton = {
        IconButton(onClick = { navController.navigate("Settings") }) {
            Icon(
                Icons.TwoTone.Settings,
                contentDescription = "Settings",
                modifier = Modifier.size(50.dp)
            )
        }
    }) {
        LazyVerticalGrid(modifier = Modifier
            .padding(it)
            .fillMaxSize(), columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {

                Button(
                    onClick = { navController.navigate("exercises") },
                    colors = rutinAppButtonsColours()
                ) {
                    Text(text = "Ejercicios")
                }
            }
            item {
                Button(
                    onClick = { navController.navigate("routines") },
                    colors = rutinAppButtonsColours()
                ) {
                    Text(text = "Rutinas")
                }
            }
            item {
                Button(
                    onClick = { navController.navigate("workouts") },
                    colors = rutinAppButtonsColours()
                ) {
                    Text(text = "Entrenamientos")
                }
            }
            item {
                Button(
                    onClick = { navController.navigate("stats") }, colors = rutinAppButtonsColours()
                ) {
                    Text(text = "Estadisticas")
                }
            }
            item(span = { GridItemSpan(2) }){

                RutinAppCalendar(plannings.map { it.date })

            }
        }
    }

}