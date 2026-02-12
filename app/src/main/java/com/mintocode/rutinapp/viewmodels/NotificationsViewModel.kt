package com.mintocode.rutinapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mintocode.rutinapp.data.daos.AppNotificationEntity
import com.mintocode.rutinapp.data.repositories.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

/**
 * Filtro para la lista de notificaciones.
 */
enum class NotificationFilter {
    ALL, UNREAD, READ
}

/**
 * ViewModel para la pantalla de notificaciones.
 *
 * Gestiona la sincronización con el servidor, el listado local
 * y las acciones de marcar como leída / eliminar.
 */
@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    companion object {
        private const val TAG = "NotificationsVM"
    }

    /** Todas las notificaciones locales. */
    val notifications: StateFlow<List<AppNotificationEntity>> =
        repository.allNotifications.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    /** Conteo de no leídas (reactivo). */
    val unreadCount: StateFlow<Int> =
        repository.unreadCount.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    /** Filtro activo. */
    private val _filter = MutableStateFlow(NotificationFilter.ALL)
    val filter: StateFlow<NotificationFilter> = _filter.asStateFlow()

    /** Estado de carga. */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /** Estado de sincronización. */
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    init {
        // Sincronizar al crear el ViewModel
        syncNotifications()
    }

    /**
     * Cambia el filtro activo.
     */
    fun setFilter(newFilter: NotificationFilter) {
        _filter.value = newFilter
    }

    /**
     * Sincroniza las notificaciones desde el servidor.
     */
    fun syncNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            _isSyncing.value = true
            try {
                repository.syncFromServer()
            } catch (e: Exception) {
                Log.e(TAG, "Error sincronizando notificaciones", e)
            } finally {
                _isSyncing.value = false
            }
        }
    }

    /**
     * Marca una notificación como leída.
     */
    fun markAsRead(notification: AppNotificationEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val now = Instant.now().toString()
            repository.markAsRead(notification.id, notification.serverId, now)
        }
    }

    /**
     * Marca todas las notificaciones como leídas.
     */
    fun markAllAsRead() {
        viewModelScope.launch(Dispatchers.IO) {
            val now = Instant.now().toString()
            repository.markAllAsRead(now)
        }
    }

    /**
     * Elimina una notificación.
     */
    fun deleteNotification(notification: AppNotificationEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(notification.id, notification.serverId)
        }
    }
}
