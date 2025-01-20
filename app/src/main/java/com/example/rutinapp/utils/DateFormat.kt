package com.example.rutinapp.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@SuppressLint("SimpleDateFormat")
fun Date.dateString(): String = SimpleDateFormat("dd MMMM yyyy").format(this)

@SuppressLint("SimpleDateFormat")
fun Date.timeString(): String = SimpleDateFormat("HH:mm").format(this)

@SuppressLint("SimpleDateFormat")
fun Date.completeHourString(): String = SimpleDateFormat("HH:mm:ss").format(this)

@SuppressLint("SimpleDateFormat")
fun Date.simpleDateString(): String = SimpleDateFormat("dd/MM/yyyy").format(this)

@SuppressLint("SimpleDateFormat")
fun Date.dayOfWeekString() = SimpleDateFormat("EEEE").format(this)
    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
