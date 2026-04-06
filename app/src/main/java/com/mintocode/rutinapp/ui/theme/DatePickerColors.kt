package com.mintocode.rutinapp.ui.theme

import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * DatePicker colors using the app's Material3 color scheme.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rutinAppDatePickerColors(): DatePickerColors {
    return DatePickerDefaults.colors(
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        headlineContentColor = MaterialTheme.colorScheme.onSurface,
        weekdayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        dayContentColor = MaterialTheme.colorScheme.onSurface,
        selectedDayContainerColor = MaterialTheme.colorScheme.primary,
        selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
        todayContentColor = MaterialTheme.colorScheme.primary,
        subheadContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.primaryContainer,
        dayInSelectionRangeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}