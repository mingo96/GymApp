package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mintocode.rutinapp.ui.screens.NotificationsScreen
import com.mintocode.rutinapp.viewmodels.NotificationsViewModel

/**
 * Notifications sheet content.
 *
 * Wraps the existing NotificationsScreen composable inside the sheet context.
 * The NotificationsScreen already handles its own layout, filters, and actions.
 *
 * @param viewModel NotificationsViewModel for notification data and actions
 */
@Composable
fun NotificationsSheet(viewModel: NotificationsViewModel) {
    NotificationsScreen(viewModel = viewModel)
}
