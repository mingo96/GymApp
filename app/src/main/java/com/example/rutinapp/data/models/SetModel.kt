package com.example.rutinapp.data.models

import java.util.Date

data class SetModel(
    val id: Int,
    val weight : Double,
    val reps : Int,
    val date : Date,
    val observations : String
)
