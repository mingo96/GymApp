package com.mintocode.rutinapp.data.models

/**
 * Domain model for a trainer–client relationship.
 *
 * Received from server via sync/trainer-data. This is read-only on the client;
 * modifications happen via dedicated API endpoints (redeem, approve, block, etc.).
 *
 * @property id Server-side ID
 * @property trainerUserId User ID of the trainer
 * @property clientUserId User ID of the client
 * @property status Relationship status: "pending", "approved", "blocked", "revoked"
 * @property notes Optional notes on the relationship
 */
data class TrainerRelationModel(
    val id: Long,
    val trainerUserId: Long,
    val clientUserId: Long,
    val status: String,
    val notes: String? = null
)

/**
 * Domain model for a planning grant (client grants trainer access to planning).
 *
 * @property id Server-side ID
 * @property clientUserId Client who grants access
 * @property trainerUserId Trainer who receives access
 * @property accessType "view" or "edit"
 * @property dateFrom Optional start date of the grant
 * @property dateTo Optional end date of the grant
 * @property isActive Whether the grant is currently active
 */
data class PlanningGrantModel(
    val id: Long,
    val clientUserId: Long,
    val trainerUserId: Long,
    val accessType: String,
    val dateFrom: String? = null,
    val dateTo: String? = null,
    val isActive: Boolean = true
)

/**
 * Domain model for a workout visibility grant.
 *
 * @property id Server-side ID
 * @property clientUserId Client who grants visibility
 * @property trainerUserId Trainer who can view workouts
 * @property canViewResults Whether trainer can see workout results/details
 * @property dateFrom Optional start date of the grant
 * @property dateTo Optional end date of the grant
 * @property isActive Whether the grant is currently active
 */
data class WorkoutVisibilityGrantModel(
    val id: Long,
    val clientUserId: Long,
    val trainerUserId: Long,
    val canViewResults: Boolean = false,
    val dateFrom: String? = null,
    val dateTo: String? = null,
    val isActive: Boolean = true
)
