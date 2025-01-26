package com.mintocode.rutinapp.ui.theme

import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rutinAppDatePickerColors(): DatePickerColors {
    return DatePickerDefaults.colors(
        titleContentColor = ContentColor,
        headlineContentColor = ContentColor,
        weekdayContentColor = ContentColor,
        dayContentColor = ContentColor,
        selectedDayContainerColor = SecondaryColor,
        selectedDayContentColor = ContentColor,
        todayContentColor = ContentColor,
        subheadContentColor = ContentColor,
        dayInSelectionRangeContainerColor = SecondaryColor.copy(alpha = 0.3f),
        dayInSelectionRangeContentColor = ContentColor,
    )
}