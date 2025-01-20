package com.example.rutinapp

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rutinapp.ui.screens.ExercisesScreen
import com.example.rutinapp.ui.screens.MainScreen
import com.example.rutinapp.ui.screens.RoutinesScreen
import com.example.rutinapp.ui.screens.SettinsScreen
import com.example.rutinapp.ui.screens.StatsScreen
import com.example.rutinapp.ui.screens.WorkoutsScreen
import com.example.rutinapp.ui.theme.ContentColor
import com.example.rutinapp.ui.theme.PrimaryColor
import com.example.rutinapp.ui.theme.RutinAppTheme
import com.example.rutinapp.utils.DataStoreManager
import com.example.rutinapp.viewmodels.ExercisesViewModel
import com.example.rutinapp.viewmodels.MainScreenViewModel
import com.example.rutinapp.viewmodels.RoutinesViewModel
import com.example.rutinapp.viewmodels.SettingsViewModel
import com.example.rutinapp.viewmodels.StatsViewModel
import com.example.rutinapp.viewmodels.WorkoutsViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RutinAppApplication : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val exercisesViewModel: ExercisesViewModel by viewModels()
    private val routinesViewModel: RoutinesViewModel by viewModels()
    private val workoutsViewModel: WorkoutsViewModel by viewModels()
    private val statsViewModel: StatsViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val mainScreenViewModel: MainScreenViewModel by viewModels()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        settingsViewModel.initiateDataStore(DataStoreManager(this))

        workoutsViewModel.exercisesViewModel = exercisesViewModel

        // Screen transitions
        val onEnter = slideInHorizontally { -it } + scaleIn()
        val onExit = slideOutHorizontally {
            it
        } + scaleOut()

        setContent {
            RutinAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PrimaryColor,
                    contentColor = ContentColor
                ) {

                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "start") {

                        composable("start",
                            enterTransition = { onEnter },
                            exitTransition = { onExit }) {
                            MainScreen(
                                navController = navController,
                                mainScreenViewModel = mainScreenViewModel
                            )
                        }

                        composable("exercises",
                            enterTransition = { onEnter },
                            exitTransition = { onExit }) {
                            ExercisesScreen(
                                viewModel = exercisesViewModel, navController = navController
                            )
                        }

                        composable("routines",
                            enterTransition = { onEnter },
                            exitTransition = { onExit }) {
                            RoutinesScreen(
                                viewModel = routinesViewModel, navController = navController
                            )
                        }

                        composable("workouts",
                            enterTransition = { onEnter },
                            exitTransition = { onExit }) {
                            WorkoutsScreen(
                                viewModel = workoutsViewModel, navController = navController
                            )
                        }

                        composable("stats",
                            enterTransition = { onEnter },
                            exitTransition = { onExit }) {
                            StatsScreen(
                                navController = navController, statsViewModel = statsViewModel
                            )
                        }

                        composable("settings",
                            enterTransition = { onEnter },
                            exitTransition = { onExit }) {
                            SettinsScreen(
                                navController = navController, settingsViewModel = settingsViewModel
                            )
                        }

                    }

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RutinAppTheme {
        Greeting("Android")
    }
}