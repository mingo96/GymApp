package com.mintocode.rutinapp.data.models

/**
 * Domain model for an exercise expectation within a planning entry.
 *
 * Represents what a user (or their trainer) expects to do for a specific
 * exercise on a planned day: sets, reps, weight targets, etc.
 *
 * @property id Server-side ID (0 if not yet synced)
 * @property exerciseId Server-side exercise ID this expectation references
 * @property exerciseName Display name of the exercise (resolved from DTO)
 * @property expectationText Free-text description (e.g., "4x10 @ 80kg")
 * @property position Order within the planning entry
 * @property notes Additional notes for this exercise expectation
 */
data class PlanningExerciseModel(
    val id: Long = 0L,
    val exerciseId: Long,
    val exerciseName: String = "",
    val expectationText: String? = null,
    val position: Int = 0,
    val notes: String? = null
)
