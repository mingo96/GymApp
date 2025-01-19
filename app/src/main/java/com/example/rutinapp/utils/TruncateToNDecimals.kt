package com.example.rutinapp.utils

fun Double.truncatedToNDecimals(n: Int): String {
    val values = this.toString().split(".")
    return if (values.size == 2 && values[1].length > n) {
        values[0] + "." + values[1].substring(0, n)
    } else {
        return this.toString()
    }
}