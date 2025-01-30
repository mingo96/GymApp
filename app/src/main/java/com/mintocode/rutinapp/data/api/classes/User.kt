package com.mintocode.rutinapp.data.api.classes

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