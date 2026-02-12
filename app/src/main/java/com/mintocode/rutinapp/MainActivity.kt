package com.mintocode.rutinapp

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.mintocode.rutinapp.ui.screens.ExercisesScreen
import com.mintocode.rutinapp.ui.screens.LoadingScreen
import com.mintocode.rutinapp.ui.screens.MainScreen
import com.mintocode.rutinapp.ui.screens.NotificationsScreen
import com.mintocode.rutinapp.ui.screens.RoutinesScreen
import com.mintocode.rutinapp.ui.screens.SettinsScreen
import com.mintocode.rutinapp.ui.screens.StatsScreen
import com.mintocode.rutinapp.ui.screens.WorkoutsScreen
import com.mintocode.rutinapp.ui.theme.ContentColor
import com.mintocode.rutinapp.ui.theme.PrimaryColor
import com.mintocode.rutinapp.ui.theme.RutinAppTheme
import com.mintocode.rutinapp.utils.DataStoreManager
import com.mintocode.rutinapp.sync.SyncStateHolder
import android.widget.Toast
import com.mintocode.rutinapp.viewmodels.AdViewModel
import com.mintocode.rutinapp.viewmodels.ExercisesViewModel
import com.mintocode.rutinapp.viewmodels.MainScreenViewModel
import com.mintocode.rutinapp.viewmodels.NotificationsViewModel
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
    private val notificationsViewModel: NotificationsViewModel by viewModels()
    private val adViewModel: AdViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
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

                    val syncing by SyncStateHolder.isSyncing.collectAsState()
                    val lastError by SyncStateHolder.lastError.collectAsState()

                    LaunchedEffect(lastError) {
                        if (lastError != null) {
                            Toast.makeText(this@MainActivity, "Sync error: ${lastError}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    NavHost(navController = navController, startDestination = "loadingScreen") {

                        composable("loadingScreen", exitTransition = { onExit }) {
                            LaunchedEffect(true) {
                                while (!settingsViewModel.hasLoaded) {
                                    delay(100)
                                }
                                // Trigger background sync of pending local items once user/token loaded
                                exercisesViewModel.syncPendingExercises()
                                routinesViewModel.syncPendingRoutines()
                                navController.navigate("start")
                            }

                            LoadingScreen()
                        }

                        composable("start",
                            enterTransition = { onEnter },
                            exitTransition = { onExit }) {
                            BackHandler {
                                moveTaskToBack(true)
                            }

                            MainScreen(
                                navController = navController,
                                mainScreenViewModel = mainScreenViewModel
                            )
                            if (syncing) {
                                // Very lightweight indicator (could be improved with Compose Snackbar/Overlay)
                                Toast.makeText(this@MainActivity, "Sincronizando...", Toast.LENGTH_SHORT).show()
                            }
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

                        composable("notifications",
                            enterTransition = { onEnter },
                            exitTransition = { onExit }) {
                            NotificationsScreen(
                                navController = navController,
                                viewModel = notificationsViewModel
                            )
                        }

                    }

                }
            }
        }
    }

    private fun start() {

        val context = this.baseContext

        val datastore = DataStoreManager(context)

        settingsViewModel.initiateDataStore(datastore)

        // Inicializar notificaciones (crear canales)
        settingsViewModel.initNotificationHelper(context)

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

        // Registrar token FCM con el backend si hay permiso y el usuario está autenticado
        settingsViewModel.registerFcmTokenIfNeeded()

    }

}

fun isConnectedToInternet(context: Context): Boolean {
    var isConnected = false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    if (connectivityManager != null) {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        isConnected =
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    return isConnected
}