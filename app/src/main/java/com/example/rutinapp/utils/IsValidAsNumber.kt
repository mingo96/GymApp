package com.example.rutinapp.utils

fun String.isValidAsNumber(): Boolean{
    if (this.count {it == '.'} > 1) return false
    if (this == "") return true
    try {
        this.toDouble()
        return true
    }catch (e:Exception){
        return false
    }
}