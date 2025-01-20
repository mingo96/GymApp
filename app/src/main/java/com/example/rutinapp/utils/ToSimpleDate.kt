package com.example.rutinapp.utils

import java.util.Date

fun Date.toSimpleDate(): Date = Date(this.year, this.month, this.date)