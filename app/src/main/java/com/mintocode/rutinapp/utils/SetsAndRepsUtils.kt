package com.mintocode.rutinapp.utils

import kotlin.math.max

/**
 * Returns "0x0" if the string is empty or blank, otherwise returns the original string.
 * Used to provide a default sets-and-reps value.
 *
 * @returns The original string or "0x0" as default.
 */
fun String.orSetsAndReps(): String {
    if (this.isEmpty() || this.isBlank()) return "0x0"
    else return this
}

/**
 * Checks if the string is a valid sets-and-reps format ("NxM" where N and M are integers).
 *
 * @returns True if the string matches the "NxM" format with valid integers.
 */
fun String.isSetsAndReps(): Boolean {
    if (this.isEmpty()) return false
    if (this.split("x").size != 2) return false
    if (this.split("x")[0].toIntOrNull() == null) return false
    if (this.split("x")[1].toIntOrNull() == null) return false
    return true
}

/**
 * Increments or decrements the sets or reps value in a "NxM" formatted string.
 *
 * @param firstValue If true, modifies the sets (first number); if false, modifies the reps (second number).
 * @param addOrDelete If true, increments; if false, decrements (minimum 0).
 * @returns The modified "NxM" string, or empty string if format is invalid.
 */
fun String.changeValue(firstValue: Boolean, addOrDelete: Boolean): String {
    if (this.isSetsAndReps()) {
        var firstNumber = this.split("x")[0].toInt()
        var secondNumber = this.split("x")[1].toInt()
        if (firstValue) if (addOrDelete) firstNumber++
        else firstNumber--
        else if (addOrDelete) secondNumber++
        else secondNumber--
        return "${max(firstNumber, 0)}x${max(secondNumber, 0)}"
    } else return ""
}
