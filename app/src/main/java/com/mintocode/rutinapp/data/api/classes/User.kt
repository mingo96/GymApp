package com.mintocode.rutinapp.data.api.classes

/**
 * Legacy DTO for old auth endpoints; retained until new auth integration is wired.
 */

import com.google.gson.annotations.SerializedName


data class User(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("email")
    val email: String = "",
    @SerializedName("authId")
    val authId: String = ""
)