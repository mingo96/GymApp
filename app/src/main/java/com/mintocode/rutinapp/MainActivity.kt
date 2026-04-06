package com.mintocode.rutinapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.mintocode.rutinapp.ui.components.RutinAppBottomBar
import com.mintocode.rutinapp.ui.navigation.Routes
import com.mintocode.rutinapp.ui.navigation.topLevelDestinations
import com.mintocode.rutinapp.ui.screens.ExercisesScreen
import com.mintocode.rutinapp.ui.screens.LoadingScreen
import com.mintocode.rutinapp.ui.screens.MainScreen
import com.mintocode.rutinapp.ui.screens.NotificationsScreen
import com.mintocode.rutinapp.ui.screens.RoutinesScreen
import com.mintocode.rutinapp.ui.screens.SettingsScreen
import com.mintocode.rutinapp.ui.screens.StatsScreen
import com.mintocode.rutinapp.ui.screens.WorkoutsScreen
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

        setContent {
            val userDetails by settingsViewModel.data.observeAsState()
            val isDarkTheme = userDetails?.isDarkTheme ?: true

            RutinAppTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    RutinAppNavHost(navController = navController)
                }
            }
        }
    }

    /**
     * Main navigation host with bottom nav bar.
     *
     * Shows bottom bar only on top-level destinations (Home, Exercises, Train, Stats, Profile).
     * Loading screen and secondary screens (Settings, Notifications, Routines) hide the bar.
     */
    @Composable
    private fun RutinAppNavHost(navController: NavHostController) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val topLevelRoutes = topLevelDestinations.map { it.route }
        val showBottomBar = currentRoute in topLevelRoutes

        val syncing by SyncStateHolder.isSyncing.collectAsState()
        val lastError by SyncStateHolder.lastError.collectAsState()

        LaunchedEffect(lastError) {
            if (lastError != null) {
                Toast.makeText(this@MainActivity, "Sync error: $lastError", Toast.LENGTH_SHORT).show()
            }
        }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (showBottomBar) {
                    RutinAppBottomBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(Routes.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            val animDuration = 300

            NavHost(
                navController = navController,
                startDestination = Routes.LOADING,
                modifier = Modifier.padding(innerPadding),
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(animDuration)
                    ) + fadeIn(animationSpec = tween(animDuration))
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(animDuration)
                    ) + fadeOut(animationSpec = tween(animDuration))
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(animDuration)
                    ) + fadeIn(animationSpec = tween(animDuration))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(animDuration)
                    ) + fadeOut(animationSpec = tween(animDuration))
                }
            ) {
                // Loading / splash
                composable(Routes.LOADING) {
                    LaunchedEffect(true) {
                        while (!settingsViewModel.hasLoaded) {
                            delay(100)
                        }
                        exercisesViewModel.syncPendingExercises()
                        routinesViewModel.syncPendingRoutines()
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOADING) { inclusive = true }
                        }
                    }
                    LoadingScreen()
                }

                // ── Top-level destinations ──

                composable(Routes.HOME) {
                    BackHandler { moveTaskToBack(true) }
                    MainScreen(
                        onNavigateToTrain = { navController.navigate(Routes.TRAIN) },
                        mainScreenViewModel = mainScreenViewModel
                    )
                }

                composable(Routes.EXERCISES) {
                    ExercisesScreen(
                        viewModel = exercisesViewModel
                    )
                }

                composable(Routes.TRAIN) {
                    WorkoutsScreen(
                        viewModel = workoutsViewModel,
                        onNavigateToExercises = { navController.navigate(Routes.EXERCISES) }
                    )
                }

                composable(Routes.STATS) {
                    StatsScreen(
                        statsViewModel = statsViewModel
                    )
                }

                composable(Routes.PROFILE) {
                    SettingsScreen(
                        settingsViewModel = settingsViewModel
                    )
                }

                // ── Secondary screens ──

                composable(Routes.SETTINGS) {
                    SettingsScreen(
                        settingsViewModel = settingsViewModel
                    )
                }

                composable(Routes.ROUTINES) {
                    RoutinesScreen(
                        viewModel = routinesViewModel
                    )
                }

                composable(Routes.NOTIFICATIONS) {
                    NotificationsScreen(
                        viewModel = notificationsViewModel
                    )
                }
            }
        }
    }

    private fun start() {
        val context = this.baseContext
        val datastore = DataStoreManager(context)

        settingsViewModel.initiateDataStore(datastore)
        settingsViewModel.initNotificationHelper(context)

        workoutsViewModel.provideAdsViewModel(adViewModel)
        statsViewModel.provideAdsViewModel(adViewModel)
        workoutsViewModel.exercisesViewModel = exercisesViewModel

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            MobileAds.initialize(this@MainActivity) {
                adViewModel.initiateObjects(this@MainActivity, DataStoreManager(context))
            }
        }

        settingsViewModel.registerFcmTokenIfNeeded()
    }
}
