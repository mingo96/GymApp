package com.example.rutinapp

import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rutinapp.ui.screens.ExercisesScreen
import com.example.rutinapp.ui.theme.PrimaryColor
import com.example.rutinapp.ui.theme.RutinAppTheme
import com.example.rutinapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RutinAppApplication : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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
                    contentColor = Color.White
                ) {

                    val navController =rememberNavController()

                    NavHost(navController = navController, startDestination = "start") {

                        composable("start", enterTransition = { onEnter }, exitTransition = { onExit }) {
                            Button(onClick = { navController.navigate("exercises") }) {
                                Text(text = "ejercicios")
                            }
                        }

                        composable("exercises", enterTransition = { onEnter }, exitTransition = { onExit }) {
                            ExercisesScreen(viewModel = viewModel, navController = navController)
                        }

                        composable("routines", enterTransition = { onEnter }, exitTransition = { onExit }) {

                        }

                        composable("enter", enterTransition = { onEnter }, exitTransition = { onExit }) {

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