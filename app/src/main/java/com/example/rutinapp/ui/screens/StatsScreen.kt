package com.example.rutinapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.rutinapp.ui.theme.ScreenContainer
import com.example.rutinapp.viewmodels.StatsViewModel

@Composable
fun StatsScreen(navController: NavHostController, statsViewModel: StatsViewModel) {

    ScreenContainer(title = "Tus estadisticas", onExit = { navController.navigateUp() }) {

    }

}