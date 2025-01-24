package com.mintocode.rutinapp

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.mintocode.rutinapp.ui.screens.ExercisesScreen
import com.mintocode.rutinapp.ui.screens.LoadingScreen
import com.mintocode.rutinapp.ui.screens.MainScreen
import com.mintocode.rutinapp.ui.screens.RoutinesScreen
import com.mintocode.rutinapp.ui.screens.SettinsScreen
import com.mintocode.rutinapp.ui.screens.StatsScreen
import com.mintocode.rutinapp.ui.screens.WorkoutsScreen
import com.mintocode.rutinapp.ui.theme.ContentColor
import com.mintocode.rutinapp.ui.theme.PrimaryColor
import com.mintocode.rutinapp.ui.theme.RutinAppTheme
import com.mintocode.rutinapp.utils.DataStoreManager
import com.mintocode.rutinapp.viewmodels.AdViewModel
import com.mintocode.rutinapp.viewmodels.ExercisesViewModel
import com.mintocode.rutinapp.viewmodels.MainScreenViewModel
import com.mintocode.rutinapp.viewmodels.RoutinesViewModel
import com.mintocode.rutinapp.viewmodels.SettingsViewModel
import com.mintocode.rutinapp.viewmodels.StatsViewModel
import com.mintocode.rutinapp.viewmodels.WorkoutsViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    private val adViewModel: AdViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        start()

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

                    NavHost(navController = navController, startDestination = "loadingScreen") {

                        composable("loadingScreen", exitTransition = { onExit }) {
                            LaunchedEffect(true) {
                                while (!settingsViewModel.hasLoaded) {
                                    delay(100)
                                }
                                navController.navigate("start")
                            }

                            LoadingScreen()
                        }

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

    private fun start() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        val context = this.baseContext

        settingsViewModel.initiateDataStore(DataStoreManager(context))

        workoutsViewModel.provideAdsViewModel(adViewModel)

        statsViewModel.provideAdsViewModel(adViewModel)

        workoutsViewModel.exercisesViewModel = exercisesViewModel

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {
                adViewModel.initiateObjects(this@MainActivity, DataStoreManager(context))
            }
        }

    }

}
