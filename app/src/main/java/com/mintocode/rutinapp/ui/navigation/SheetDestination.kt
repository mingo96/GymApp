package com.mintocode.rutinapp.ui.navigation

/**
 * All possible sheet destinations in the app.
 *
 * Each destination maps to a composable rendered inside a ModalBottomSheet.
 * Destinations carry the minimum data needed to render (IDs, not full models)
 * since ViewModels are accessed via Hilt inside each sheet composable.
 *
 * Navigation flow: Root pages → Sheet → Sheet → ... (stacking)
 * Dismiss flow: Swipe down → previous sheet → ... → root page
 */
sealed class SheetDestination {

    // ── Exercises ──

    /** Full exercise list with search and filters. */
    data object ExerciseList : SheetDestination()

    /** Exercise detail view (read-only). */
    data class ExerciseDetail(val exerciseId: Int) : SheetDestination()

    /** Create a new exercise. */
    data object ExerciseCreate : SheetDestination()

    /** Edit an existing exercise. */
    data class ExerciseEdit(val exerciseId: Int) : SheetDestination()

    // ── Routines ──

    /** Full routine list with search. */
    data object RoutineList : SheetDestination()

    /** Routine detail view showing exercises. */
    data class RoutineDetail(val routineId: Int) : SheetDestination()

    /** Create a new routine. */
    data object RoutineCreate : SheetDestination()

    /** Edit an existing routine. */
    data class RoutineEdit(val routineId: Int) : SheetDestination()

    // ── Workouts ──

    /** Full workout history list. */
    data object WorkoutHistory : SheetDestination()

    /** Workout detail view (completed workout). */
    data class WorkoutDetail(val workoutId: Int) : SheetDestination()

    /** Active workout in progress. */
    data class ActiveWorkout(val workoutId: Int) : SheetDestination()

    /** Start a new workout from a routine. */
    data class StartWorkout(val routineId: Int? = null) : SheetDestination()

    // ── Planning ──

    /** Edit planning for a specific date. */
    data class PlanningEdit(val dateMillis: Long) : SheetDestination()

    // ── Profile & Settings ──

    /** Full settings sheet. */
    data object Settings : SheetDestination()

    /** App configuration (floating widget, etc.). */
    data object AppConfig : SheetDestination()

    /** Authentication (login/register). */
    data object Auth : SheetDestination()

    /** Notification list. */
    data object Notifications : SheetDestination()

    /** Trainer management. */
    data object TrainerManagement : SheetDestination()

    // ── Statistics ──

    /** Global stats overview. */
    data object StatsOverview : SheetDestination()

    /** Stats detail for the currently selected exercise (set via StatsViewModel). */
    data object ExerciseStats : SheetDestination()

    // ── Backup ──

    /** Backup management sheet. */
    data object Backup : SheetDestination()
}
