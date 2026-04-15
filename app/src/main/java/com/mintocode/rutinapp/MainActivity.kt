package com.mintocode.rutinapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.ads.MobileAds
import com.mintocode.rutinapp.sync.SyncStateHolder
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.navigation.SheetDestination
import com.mintocode.rutinapp.ui.navigation.SheetHost
import com.mintocode.rutinapp.ui.navigation.SheetNavigator
import com.mintocode.rutinapp.ui.screens.LoadingScreen
import com.mintocode.rutinapp.ui.screens.root.HomePage
import com.mintocode.rutinapp.ui.screens.root.ProfilePage
import com.mintocode.rutinapp.ui.screens.root.RootPager
import com.mintocode.rutinapp.ui.screens.root.TrainPage
import com.mintocode.rutinapp.ui.screens.sheets.ActiveWorkoutSheet
import com.mintocode.rutinapp.ui.screens.sheets.AppConfigSheet
import com.mintocode.rutinapp.ui.screens.sheets.AuthSheet
import com.mintocode.rutinapp.ui.screens.sheets.BackupSheet
import com.mintocode.rutinapp.ui.screens.sheets.ExerciseCreateSheet
import com.mintocode.rutinapp.ui.screens.sheets.ExerciseDetailSheet
import com.mintocode.rutinapp.ui.screens.sheets.ExerciseEditSheet
import com.mintocode.rutinapp.ui.screens.sheets.ExerciseListSheet
import com.mintocode.rutinapp.ui.screens.sheets.ExerciseStatsSheet
import com.mintocode.rutinapp.ui.screens.sheets.NotificationsSheet
import com.mintocode.rutinapp.ui.screens.sheets.PlanningEditSheet
import com.mintocode.rutinapp.ui.screens.sheets.RoutineCreateSheet
import com.mintocode.rutinapp.ui.screens.sheets.RoutineDetailSheet
import com.mintocode.rutinapp.ui.screens.sheets.RoutineEditSheet
import com.mintocode.rutinapp.ui.screens.sheets.RoutineListSheet
import com.mintocode.rutinapp.ui.screens.sheets.SettingsSheet
import com.mintocode.rutinapp.ui.screens.sheets.StatsSheet
import com.mintocode.rutinapp.ui.screens.sheets.TrainerManagementSheet
import com.mintocode.rutinapp.ui.screens.sheets.WorkoutHistorySheet
import com.mintocode.rutinapp.ui.theme.RutinAppTheme
import com.mintocode.rutinapp.utils.DataStoreManager
import com.mintocode.rutinapp.viewmodels.AdViewModel
import com.mintocode.rutinapp.viewmodels.BackupViewModel
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
    private val backupViewModel: BackupViewModel by viewModels()

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
                    RutinAppContent()
                }
            }
        }
    }

    /**
     * Main content using Trade Republic-style navigation.
     *
     * Shows a loading screen until settings are loaded, then displays a
     * HorizontalPager with 3 root pages (Home, Train, Profile) and a
     * SheetHost for stacking ModalBottomSheets.
     *
     * Back press closes the topmost sheet. If no sheets are open,
     * the app goes to background.
     */
    @Composable
    private fun RutinAppContent() {
        val sheetNavigator = remember { SheetNavigator() }
        var isLoaded by remember { mutableStateOf(false) }

        // Sync error toast
        val lastError by SyncStateHolder.lastError.collectAsState()
        LaunchedEffect(lastError) {
            if (lastError != null) {
                Toast.makeText(this@MainActivity, "Sync error: $lastError", Toast.LENGTH_SHORT).show()
            }
        }

        // Wait for settings to load
        LaunchedEffect(Unit) {
            while (!settingsViewModel.hasLoaded) {
                delay(100)
            }
            exercisesViewModel.syncPendingExercises()
            routinesViewModel.syncPendingRoutines()
            isLoaded = true
        }

        if (!isLoaded) {
            LoadingScreen()
            return
        }

        // Back handler: close topmost sheet, or move app to background
        BackHandler {
            if (sheetNavigator.hasSheets) {
                sheetNavigator.close()
            } else {
                moveTaskToBack(true)
            }
        }

        CompositionLocalProvider(LocalSheetNavigator provides sheetNavigator) {
            RootPager(
                page0 = { HomePage(viewModel = mainScreenViewModel) },
                page1 = {
                    TrainPage(
                        workoutsViewModel = workoutsViewModel,
                        exercisesViewModel = exercisesViewModel,
                        routinesViewModel = routinesViewModel,
                        settingsViewModel = settingsViewModel
                    )
                },
                page2 = { ProfilePage(settingsViewModel = settingsViewModel) }
            )

            SheetHost(navigator = sheetNavigator) { destination ->
                SheetContent(destination)
            }
        }
    }

    /**
     * Maps each [SheetDestination] to its composable content.
     *
     * Each sheet delegates to the corresponding sheet composable,
     * passing the appropriate ViewModel.
     */
    @Composable
    private fun SheetContent(destination: SheetDestination) {
        val navigator = LocalSheetNavigator.current

        when (destination) {
            is SheetDestination.ExerciseList -> {
                ExerciseListSheet(viewModel = exercisesViewModel)
            }

            is SheetDestination.RoutineList -> {
                RoutineListSheet(viewModel = routinesViewModel)
            }

            is SheetDestination.WorkoutHistory -> {
                WorkoutHistorySheet(viewModel = workoutsViewModel)
            }

            is SheetDestination.ActiveWorkout -> {
                ActiveWorkoutSheet(
                    viewModel = workoutsViewModel,
                    onNavigateToExercises = {
                        navigator.open(SheetDestination.ExerciseList)
                    }
                )
            }

            is SheetDestination.StartWorkout -> {
                // Start workout then show active workout sheet
                LaunchedEffect(destination) {
                    if (destination.routineId != null) {
                        // RoutineId-based start handled by ViewModel
                    } else {
                        workoutsViewModel.startFromEmpty()
                    }
                }
                ActiveWorkoutSheet(
                    viewModel = workoutsViewModel,
                    onNavigateToExercises = {
                        navigator.open(SheetDestination.ExerciseList)
                    }
                )
            }

            is SheetDestination.PlanningEdit -> {
                PlanningEditSheet(viewModel = mainScreenViewModel)
            }

            is SheetDestination.Settings -> {
                SettingsSheet(viewModel = settingsViewModel)
            }

            is SheetDestination.AppConfig -> {
                AppConfigSheet(viewModel = settingsViewModel)
            }

            is SheetDestination.Auth -> {
                AuthSheet(viewModel = settingsViewModel)
            }

            is SheetDestination.Notifications -> {
                NotificationsSheet(viewModel = notificationsViewModel)
            }

            is SheetDestination.TrainerManagement -> {
                TrainerManagementSheet(viewModel = settingsViewModel)
            }

            is SheetDestination.StatsOverview -> {
                StatsSheet(viewModel = statsViewModel)
            }

            is SheetDestination.ExerciseDetail -> {
                ExerciseDetailSheet(viewModel = exercisesViewModel)
            }

            is SheetDestination.ExerciseCreate -> {
                ExerciseCreateSheet(viewModel = exercisesViewModel)
            }

            is SheetDestination.ExerciseEdit -> {
                ExerciseEditSheet(viewModel = exercisesViewModel)
            }

            is SheetDestination.RoutineDetail -> {
                RoutineDetailSheet(viewModel = routinesViewModel)
            }

            is SheetDestination.RoutineCreate -> {
                RoutineCreateSheet(viewModel = routinesViewModel)
            }

            is SheetDestination.RoutineEdit -> {
                RoutineEditSheet(viewModel = routinesViewModel)
            }

            is SheetDestination.WorkoutDetail -> {
                WorkoutHistorySheet(viewModel = workoutsViewModel)
            }

            is SheetDestination.ExerciseStats -> {
                ExerciseStatsSheet(viewModel = statsViewModel)
            }

            is SheetDestination.Backup -> {
                BackupSheet(viewModel = backupViewModel)
            }
        }
    }

    /**
     * Initializes datastores, ad SDKs, and cross-ViewModel dependencies.
     */
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
