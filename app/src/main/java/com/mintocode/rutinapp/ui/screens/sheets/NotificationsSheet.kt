package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.runtime.Composable
import com.mintocode.rutinapp.ui.screens.NotificationsScreen
import com.mintocode.rutinapp.viewmodels.NotificationsViewModel

/**
 * Notifications sheet content.
 *
 * Wraps the NotificationsScreen composable inside the sheet context.
 * The NotificationsScreen handles its own KP-styled layout, filters, and actions.
 *
 * @param viewModel NotificationsViewModel for notification data and actions
 */
@Composable
fun NotificationsSheet(viewModel: NotificationsViewModel) {
    NotificationsScreen(viewModel = viewModel)
}
