package com.mintocode.rutinapp.data.api.v1.dto

data class Envelope<T>(
    val data: T?,
    val meta: Meta?,
    val errors: Any?
)

data class Meta(
    val timestamp: String?,
    val request_id: String?
)

// Specialized auth envelope (login/register) which also returns access_token at root
data class AuthEnvelope(
    val data: AuthData?,
    val meta: Meta?,
    val errors: Any?,
    val access_token: String?,
    val token_type: String?
)

data class AuthData(
    val user: AuthUser?
)

data class AuthUser(
    val id: Long?,
    val name: String?,
    val email: String?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)

data class ExercisesMineData(
    val exercises: List<ExerciseMineDto>
)

data class ExerciseMineDto(
    val id: Long,
    val name: String,
    val description: String?,
    val targeted_body_part: String?,
    val observations: String?,
    val user_id: Long,
    val created_at: String?,
    val updated_at: String?
)

data class RoutinesMineData(
    val routines: List<RoutineMineDto>
)

data class RoutineMineDto(
    val id: Long,
    val name: String,
    val targeted_body_part: String?,
    val user_id: Long,
    val created_at: String?,
    val updated_at: String?
)
