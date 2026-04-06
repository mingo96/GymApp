package com.mintocode.rutinapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow

/**
 * Reusable top app bar for RutinApp v2.
 *
 * Supports two modes:
 * 1. Main screens (showBack = false): Title + optional action icons
 * 2. Detail screens (showBack = true): Back arrow + title
 *
 * @param title Screen title displayed centered
 * @param showBack Whether to show back navigation arrow
 * @param onBack Callback for back navigation
 * @param showSettings Whether to show settings icon
 * @param onSettingsClick Callback for settings tap
 * @param showNotifications Whether to show notifications icon
 * @param unreadNotifications Count of unread notifications for badge
 * @param onNotificationsClick Callback for notifications tap
 * @param actions Additional composable actions for the trailing side
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutinAppTopBar(
    title: String,
    showBack: Boolean = false,
    onBack: () -> Unit = {},
    showSettings: Boolean = false,
    onSettingsClick: () -> Unit = {},
    showNotifications: Boolean = false,
    unreadNotifications: Int = 0,
    onNotificationsClick: () -> Unit = {},
    actions: @Composable () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }
        },
        actions = {
            actions()

            if (showNotifications) {
                IconButton(onClick = onNotificationsClick) {
                    if (unreadNotifications > 0) {
                        BadgedBox(
                            badge = {
                                Badge {
                                    Text(
                                        text = if (unreadNotifications > 99) "99+"
                                        else unreadNotifications.toString()
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "Notificaciones"
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notificaciones"
                        )
                    }
                }
            }

            if (showSettings) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Ajustes"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
