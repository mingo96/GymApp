package com.mintocode.rutinapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.InsertChart
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * All navigation routes used in the app.
 *
 * Top-level destinations appear in the bottom navigation bar.
 * Secondary routes are navigated to from within top-level screens.
 */
object Routes {
    // Top-level (bottom nav)
    const val HOME = "home"
    const val EXERCISES = "exercises"
    const val TRAIN = "train"
    const val STATS = "stats"
    const val PROFILE = "profile"

    // Secondary screens
    const val LOADING = "loading"
    const val SETTINGS = "settings"
    const val NOTIFICATIONS = "notifications"
    const val ROUTINES = "routines"
    const val TRAINER = "trainer"
    const val ONBOARDING = "onboarding"
    const val REPORTS = "reports"
}

/**
 * Represents a top-level destination shown in the bottom navigation bar.
 *
 * @param route Navigation route string
 * @param label Display label for the nav item
 * @param selectedIcon Icon when this destination is active
 * @param unselectedIcon Icon when this destination is inactive
 */
data class TopLevelDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

/**
 * The 5 top-level destinations for the bottom navigation bar.
 * Order: Home | Ejercicios | Entrenar (center) | Estadísticas | Perfil
 */
val topLevelDestinations = listOf(
    TopLevelDestination(
        route = Routes.HOME,
        label = "Inicio",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    TopLevelDestination(
        route = Routes.EXERCISES,
        label = "Ejercicios",
        selectedIcon = Icons.Filled.FitnessCenter,
        unselectedIcon = Icons.Outlined.FitnessCenter
    ),
    TopLevelDestination(
        route = Routes.TRAIN,
        label = "Entrenar",
        selectedIcon = Icons.Filled.PlayArrow,
        unselectedIcon = Icons.Outlined.PlayArrow
    ),
    TopLevelDestination(
        route = Routes.STATS,
        label = "Estadísticas",
        selectedIcon = Icons.Filled.InsertChart,
        unselectedIcon = Icons.Outlined.InsertChart
    ),
    TopLevelDestination(
        route = Routes.PROFILE,
        label = "Perfil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)
