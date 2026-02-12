package com.mintocode.rutinapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Notifications
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mintocode.rutinapp.data.daos.AppNotificationEntity
import com.mintocode.rutinapp.ui.theme.ContentColor
import com.mintocode.rutinapp.ui.theme.PrimaryColor
import com.mintocode.rutinapp.ui.theme.ScreenContainer
import com.mintocode.rutinapp.ui.theme.SecondaryColor
import com.mintocode.rutinapp.ui.theme.TextFieldColor
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.viewmodels.NotificationFilter
import com.mintocode.rutinapp.viewmodels.NotificationsViewModel
import java.time.Duration
import java.time.Instant

/**
 * Pantalla principal de notificaciones.
 *
 * Muestra la lista de notificaciones del usuario con filtros
 * y acciones de marcar como leída / eliminar.
 */
@Composable
fun NotificationsScreen(
    navController: NavHostController,
    viewModel: NotificationsViewModel
) {
    val allNotifications by viewModel.notifications.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    // Filtrar según el estado seleccionado
    val filteredNotifications = when (filter) {
        NotificationFilter.ALL -> allNotifications
        NotificationFilter.UNREAD -> allNotifications.filter { it.readAt == null }
        NotificationFilter.READ -> allNotifications.filter { it.readAt != null }
    }

    ScreenContainer(
        title = "Notificaciones",
        navController = navController
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // ---- Header con filtros y acciones ----
            NotificationHeader(
                filter = filter,
                unreadCount = unreadCount,
                isSyncing = isSyncing,
                onFilterChange = { viewModel.setFilter(it) },
                onMarkAllRead = { viewModel.markAllAsRead() },
                onRefresh = { viewModel.syncNotifications() }
            )

            Spacer(Modifier.height(12.dp))

            // ---- Lista de notificaciones ----
            if (filteredNotifications.isEmpty()) {
                EmptyNotificationsState(filter)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(
                        items = filteredNotifications,
                        key = { it.id }
                    ) { notification ->
                        NotificationCard(
                            notification = notification,
                            onMarkAsRead = { viewModel.markAsRead(notification) },
                            onDelete = { viewModel.deleteNotification(notification) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Header con chips de filtro y botón de marcar todas como leídas.
 */
@Composable
private fun NotificationHeader(
    filter: NotificationFilter,
    unreadCount: Int,
    isSyncing: Boolean,
    onFilterChange: (NotificationFilter) -> Unit,
    onMarkAllRead: () -> Unit,
    onRefresh: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Fila de filtros
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                NotificationFilterChip(
                    label = "Todas",
                    selected = filter == NotificationFilter.ALL,
                    onClick = { onFilterChange(NotificationFilter.ALL) }
                )
            }
            item {
                NotificationFilterChip(
                    label = "No leídas ($unreadCount)",
                    selected = filter == NotificationFilter.UNREAD,
                    onClick = { onFilterChange(NotificationFilter.UNREAD) }
                )
            }
            item {
                NotificationFilterChip(
                    label = "Leídas",
                    selected = filter == NotificationFilter.READ,
                    onClick = { onFilterChange(NotificationFilter.READ) }
                )
            }
        }

        // Fila de acciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (unreadCount > 0) {
                Button(
                    onClick = onMarkAllRead,
                    colors = rutinAppButtonsColours(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Marcar todas", fontSize = 13.sp)
                }
            } else {
                Spacer(Modifier)
            }

            AnimatedVisibility(visible = isSyncing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = SecondaryColor
                )
            }
        }
    }
}

/**
 * Chip de filtro para notificaciones.
 */
@Composable
private fun NotificationFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 13.sp) },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = TextFieldColor,
            selectedContainerColor = SecondaryColor.copy(alpha = 0.3f),
            labelColor = ContentColor,
            selectedLabelColor = ContentColor
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = SecondaryColor.copy(alpha = 0.3f),
            selectedBorderColor = SecondaryColor,
            enabled = true,
            selected = selected
        )
    )
}

/**
 * Tarjeta individual de notificación.
 */
@Composable
private fun NotificationCard(
    notification: AppNotificationEntity,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit
) {
    val isRead = notification.readAt != null
    val typeInfo = getTypeInfo(notification.type)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isRead) TextFieldColor.copy(alpha = 0.6f) else TextFieldColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icono de tipo
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(typeInfo.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = typeInfo.icon,
                    contentDescription = null,
                    tint = typeInfo.color,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Contenido
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontWeight = if (isRead) FontWeight.Normal else FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (!isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SecondaryColor)
                        )
                    }
                }

                if (notification.body.isNotBlank()) {
                    Text(
                        text = notification.body,
                        fontSize = 13.sp,
                        color = ContentColor.copy(alpha = 0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timeAgo(notification.createdAt),
                        fontSize = 11.sp,
                        color = ContentColor.copy(alpha = 0.5f)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (!isRead) {
                            IconButton(
                                onClick = onMarkAsRead,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.TwoTone.Check,
                                    contentDescription = "Marcar como leída",
                                    modifier = Modifier.size(18.dp),
                                    tint = SecondaryColor
                                )
                            }
                        }

                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = "Eliminar",
                                modifier = Modifier.size(18.dp),
                                tint = Color.Red.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Estado vacío cuando no hay notificaciones.
 */
@Composable
private fun EmptyNotificationsState(filter: NotificationFilter) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.Notifications,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = ContentColor.copy(alpha = 0.3f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = when (filter) {
                NotificationFilter.ALL -> "No tienes notificaciones"
                NotificationFilter.UNREAD -> "No tienes notificaciones sin leer"
                NotificationFilter.READ -> "No tienes notificaciones leídas"
            },
            fontSize = 16.sp,
            color = ContentColor.copy(alpha = 0.5f)
        )
    }
}

// ============================================================================
// Helpers
// ============================================================================

private data class TypeInfo(val icon: ImageVector, val color: Color)

private fun getTypeInfo(type: String): TypeInfo {
    return when (type) {
        "success" -> TypeInfo(Icons.TwoTone.Check, Color(0xFF22C55E))
        "warning" -> TypeInfo(Icons.TwoTone.Warning, Color(0xFFF59E0B))
        "workout_reminder" -> TypeInfo(Icons.TwoTone.Notifications, Color(0xFF3B82F6))
        "sync_complete" -> TypeInfo(Icons.Outlined.CheckCircle, Color(0xFF06B6D4))
        "achievement" -> TypeInfo(Icons.TwoTone.Check, Color(0xFFA855F7))
        "system" -> TypeInfo(Icons.TwoTone.Info, Color(0xFF6B7280))
        else -> TypeInfo(Icons.TwoTone.Info, Color(0xFF3B82F6)) // info
    }
}

/**
 * Convierte un timestamp ISO 8601 a un texto relativo ("hace 5 min", "hace 2h", etc.).
 */
private fun timeAgo(isoTimestamp: String): String {
    if (isoTimestamp.isBlank()) return ""
    return try {
        val instant = Instant.parse(isoTimestamp)
        val now = Instant.now()
        val duration = Duration.between(instant, now)

        when {
            duration.toMinutes() < 1 -> "ahora"
            duration.toMinutes() < 60 -> "hace ${duration.toMinutes()} min"
            duration.toHours() < 24 -> "hace ${duration.toHours()}h"
            duration.toDays() < 7 -> "hace ${duration.toDays()}d"
            duration.toDays() < 30 -> "hace ${duration.toDays() / 7} sem"
            else -> "hace ${duration.toDays() / 30} mes${if (duration.toDays() / 30 > 1) "es" else ""}"
        }
    } catch (_: Exception) {
        isoTimestamp
    }
}
