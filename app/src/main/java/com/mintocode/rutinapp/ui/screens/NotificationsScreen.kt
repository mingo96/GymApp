package com.mintocode.rutinapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.EmojiEvents
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Notifications
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.data.daos.AppNotificationEntity
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.viewmodels.NotificationFilter
import com.mintocode.rutinapp.viewmodels.NotificationsViewModel
import java.time.Duration
import java.time.Instant

// ── KP Color constants ──
private val KP_PRIMARY = Color(0xFFBAC3FF)
private val KP_PRIMARY_CONTAINER = Color(0xFF4361EE)
private val KP_ON_PRIMARY = Color(0xFF00218D)
private val KP_ON_PRIMARY_CONTAINER = Color(0xFFF4F2FF)
private val KP_TERTIARY = Color(0xFF27E0A9)
private val KP_SURFACE_BG = Color(0xFF1A1A24)
private val KP_SURFACE_CONTAINER_LOW = Color(0xFF1B1B20)
private val KP_SURFACE_CONTAINER_HIGH = Color(0xFF2A292F)
private val KP_ON_SURFACE = Color(0xFFE4E1E9)
private val KP_ON_SURFACE_VARIANT = Color(0xFFC4C5D7)
private val KP_OUTLINE = Color(0xFF8E8FA1)
private val KP_ERROR = Color(0xFFFFB4AB)
private val KP_ERROR_CONTAINER = Color(0xFF93000A)

/**
 * Notifications screen with KP design system.
 *
 * Shows filter chips (Todas/No leídas/Leídas), notification cards with
 * 3 variants (unread primary, unread tertiary, read with swipe-to-delete),
 * motivation banner, and styled "Marcar todas" button.
 *
 * @param viewModel NotificationsViewModel for notification data and actions
 */
@Composable
fun NotificationsScreen(viewModel: NotificationsViewModel) {
    val allNotifications by viewModel.notifications.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    val filteredNotifications = when (filter) {
        NotificationFilter.ALL -> allNotifications
        NotificationFilter.UNREAD -> allNotifications.filter { it.readAt == null }
        NotificationFilter.READ -> allNotifications.filter { it.readAt != null }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KP_SURFACE_BG)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // ── Drag Handle ──
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(48.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(50))
                .background(KP_ON_SURFACE_VARIANT.copy(alpha = 0.3f))
        )

        Spacer(Modifier.height(16.dp))

        // ── Header ──
        NotificationHeader(
            filter = filter,
            unreadCount = unreadCount,
            isSyncing = isSyncing,
            onFilterChange = { viewModel.setFilter(it) },
            onMarkAllRead = { viewModel.markAllAsRead() }
        )

        Spacer(Modifier.height(24.dp))

        // ── List ──
        if (filteredNotifications.isEmpty()) {
            EmptyNotificationsState(filter)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 48.dp)
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

                // ── Motivation Banner ──
                item { MotivationBanner() }
            }
        }
    }
}

/**
 * Header with title, "Marcar todas como leídas" button, and filter chips.
 */
@Composable
private fun NotificationHeader(
    filter: NotificationFilter,
    unreadCount: Int,
    isSyncing: Boolean,
    onFilterChange: (NotificationFilter) -> Unit,
    onMarkAllRead: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // ── Title row ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notificaciones",
                fontFamily = SpaceGroteskFont,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = KP_ON_SURFACE,
                letterSpacing = (-0.5).sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedVisibility(visible = isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = KP_PRIMARY
                    )
                }
                if (unreadCount > 0) {
                    Spacer(Modifier.width(8.dp))
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        animationSpec = tween(100), label = "mark_all_scale"
                    )
                    Text(
                        text = "MARCAR TODAS COMO LEÍDAS",
                        fontFamily = ManropeFont,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp,
                        color = KP_PRIMARY,
                        modifier = Modifier
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                            .clickable(interactionSource, indication = null, onClick = onMarkAllRead)
                    )
                }
            }
        }

        // ── Filter Chips ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                label = "Todas",
                isActive = filter == NotificationFilter.ALL,
                onClick = { onFilterChange(NotificationFilter.ALL) }
            )
            FilterChip(
                label = "No leídas ($unreadCount)",
                isActive = filter == NotificationFilter.UNREAD,
                onClick = { onFilterChange(NotificationFilter.UNREAD) }
            )
            FilterChip(
                label = "Leídas",
                isActive = filter == NotificationFilter.READ,
                onClick = { onFilterChange(NotificationFilter.READ) }
            )
        }
    }
}

/**
 * Filter chip with KP styling.
 *
 * Active: primary bg with shadow, onPrimary text.
 * Inactive: surfaceContainerHigh bg, white/5 border, onSurfaceVariant text.
 *
 * @param label Chip text
 * @param isActive Whether this chip is selected
 * @param onClick Callback when tapped
 */
@Composable
private fun FilterChip(label: String, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .then(
                if (isActive) Modifier.shadow(
                    8.dp, RoundedCornerShape(50),
                    spotColor = KP_PRIMARY.copy(alpha = 0.2f)
                ) else Modifier
            )
            .clip(RoundedCornerShape(50))
            .background(
                if (isActive) KP_PRIMARY
                else KP_SURFACE_CONTAINER_HIGH
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            fontFamily = ManropeFont,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) KP_ON_PRIMARY else KP_ON_SURFACE_VARIANT
        )
    }
}

/**
 * Notification card with 3 variants based on type and read status.
 *
 * Unread primary: primaryContainer/10 bg + 4px primary left border.
 * Unread tertiary: surfaceContainerLow + 4px tertiary left border.
 * Read: surfaceContainerLow, opacity 0.6, with swipe-to-delete.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationCard(
    notification: AppNotificationEntity,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit
) {
    val isRead = notification.readAt != null
    val typeInfo = getTypeInfo(notification.type)

    if (isRead) {
        // Swipeable read card
        val dismissState = rememberSwipeToDismissBoxState()

        LaunchedEffect(dismissState.currentValue) {
            if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
            }
        }

        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(KP_ERROR_CONTAINER.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Eliminar",
                        tint = KP_ERROR,
                        modifier = Modifier
                            .padding(end = 24.dp)
                            .size(24.dp)
                    )
                }
            },
            enableDismissFromStartToEnd = false
        ) {
            NotificationCardContent(
                notification = notification,
                typeInfo = typeInfo,
                isRead = true,
                onMarkAsRead = {},
                onDelete = onDelete
            )
        }
    } else {
        NotificationCardContent(
            notification = notification,
            typeInfo = typeInfo,
            isRead = false,
            onMarkAsRead = onMarkAsRead,
            onDelete = onDelete
        )
    }
}

/**
 * Inner notification card content with border-left variant styling.
 */
@Composable
private fun NotificationCardContent(
    notification: AppNotificationEntity,
    typeInfo: TypeInfo,
    isRead: Boolean,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit
) {
    val isPrimary = typeInfo.borderColor == KP_PRIMARY

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isRead) 0.6f else 1f)
            .then(if (isRead) Modifier.offset(x = (-40).dp) else Modifier)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (!isRead && isPrimary) KP_PRIMARY_CONTAINER.copy(alpha = 0.1f)
                else KP_SURFACE_CONTAINER_LOW
            )
            .clickable { if (!isRead) onMarkAsRead() }
            .padding(start = 0.dp),
        verticalAlignment = Alignment.Top
    ) {
        // ── Left border accent ──
        if (!isRead) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(80.dp)
                    .background(typeInfo.borderColor)
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // ── Icon box ──
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when {
                            isRead -> Color(0xFF35343A)
                            isPrimary -> KP_PRIMARY_CONTAINER
                            else -> KP_TERTIARY.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    typeInfo.icon,
                    contentDescription = null,
                    tint = when {
                        isRead -> KP_OUTLINE
                        isPrimary -> KP_ON_PRIMARY_CONTAINER
                        else -> KP_TERTIARY
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            // ── Content ──
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = notification.title,
                    fontFamily = ManropeFont,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!isRead && isPrimary) KP_ON_PRIMARY_CONTAINER else KP_ON_SURFACE,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = timeAgo(notification.createdAt).uppercase(),
                    fontFamily = ManropeFont,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    color = if (!isRead && isPrimary) KP_PRIMARY
                    else KP_ON_SURFACE_VARIANT.copy(alpha = 0.5f)
                )

                if (notification.body.isNotBlank()) {
                    Text(
                        text = notification.body,
                        fontFamily = ManropeFont,
                        fontSize = 14.sp,
                        color = KP_ON_SURFACE_VARIANT.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // ── Actions ──
            if (!isRead) {
                IconButton(
                    onClick = onMarkAsRead,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.TwoTone.Check,
                        contentDescription = "Marcar como leída",
                        modifier = Modifier.size(18.dp),
                        tint = KP_PRIMARY
                    )
                }
            }
        }
    }
}

/**
 * Motivation banner with gradient overlay and quote text.
 */
@Composable
private fun MotivationBanner() {
    Spacer(Modifier.height(16.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(Color.Transparent, KP_SURFACE_BG)
                )
            )
            .background(KP_SURFACE_CONTAINER_LOW)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 16.dp)
        ) {
            Text(
                text = "MOTIVACIÓN DEL DÍA",
                fontFamily = ManropeFont,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = KP_PRIMARY
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "\"El dolor de hoy es la fuerza de mañana\"",
                fontFamily = SpaceGroteskFont,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = KP_ON_SURFACE
            )
        }
    }
}

/**
 * Empty state with icon and descriptive text.
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
            tint = KP_ON_SURFACE_VARIANT
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = when (filter) {
                NotificationFilter.ALL -> "No tienes notificaciones"
                NotificationFilter.UNREAD -> "No tienes notificaciones sin leer"
                NotificationFilter.READ -> "No tienes notificaciones leídas"
            },
            fontFamily = ManropeFont,
            fontSize = 16.sp,
            color = KP_ON_SURFACE_VARIANT
        )
    }
}

// ============================================================================
// Helpers
// ============================================================================

private data class TypeInfo(val icon: ImageVector, val borderColor: Color)

/**
 * Determines the icon and border color for a notification type.
 *
 * @param type The notification type string
 * @return TypeInfo with icon and border color
 */
private fun getTypeInfo(type: String): TypeInfo {
    return when (type) {
        "trainer", "workout_reminder" ->
            TypeInfo(Icons.TwoTone.Notifications, Color(0xFFBAC3FF))
        "achievement", "pr" ->
            TypeInfo(Icons.TwoTone.EmojiEvents, Color(0xFF27E0A9))
        "warning" ->
            TypeInfo(Icons.TwoTone.Warning, Color(0xFFBAC3FF))
        "social", "person" ->
            TypeInfo(Icons.TwoTone.Person, Color(0xFFBAC3FF))
        else ->
            TypeInfo(Icons.TwoTone.Info, Color(0xFFBAC3FF))
    }
}

/**
 * Converts an ISO 8601 timestamp to a relative time string.
 *
 * @param isoTimestamp ISO 8601 timestamp string
 * @return Relative time string (e.g. "hace 5 min", "hace 2h")
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
