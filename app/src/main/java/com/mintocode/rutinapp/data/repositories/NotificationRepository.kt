package com.mintocode.rutinapp.data.repositories

import android.util.Log
import com.mintocode.rutinapp.data.api.v2.ApiV2Service
import com.mintocode.rutinapp.data.api.v2.dto.NotificationDto
import com.mintocode.rutinapp.data.daos.AppNotificationDao
import com.mintocode.rutinapp.data.daos.AppNotificationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repositorio para notificaciones.
 *
 * Combina el almacenamiento local (Room) con la API remota.
 * Las notificaciones se almacenan localmente para acceso offline
 * y se sincronizan con el backend cuando hay conexión.
 */
class NotificationRepository @Inject constructor(
    private val dao: AppNotificationDao,
    private val apiV2: ApiV2Service
) {

    companion object {
        private const val TAG = "NotificationRepo"
    }

    // ========== Local (Room) ==========

    /** Todas las notificaciones locales como Flow reactivo. */
    val allNotifications: Flow<List<AppNotificationEntity>> = dao.getAllAsFlow()

    /** Notificaciones no leídas como Flow reactivo. */
    val unreadNotifications: Flow<List<AppNotificationEntity>> = dao.getUnreadAsFlow()

    /** Conteo de no leídas como Flow reactivo. */
    val unreadCount: Flow<Int> = dao.getUnreadCountAsFlow()

    /** Inserta una notificación localmente (desde push o API). */
    suspend fun insertLocal(notification: AppNotificationEntity): Long {
        return dao.insert(notification)
    }

    /** Marca una notificación como leída localmente. */
    suspend fun markAsReadLocal(id: Long, readAt: String) {
        dao.markAsRead(id, readAt)
    }

    /** Marca todas como leídas localmente. */
    suspend fun markAllAsReadLocal(readAt: String) {
        dao.markAllAsRead(readAt)
    }

    /** Elimina una notificación localmente. */
    suspend fun deleteLocal(id: Long) {
        dao.deleteById(id)
    }

    /** Elimina todas las notificaciones locales. */
    suspend fun deleteAllLocal() {
        dao.deleteAll()
    }

    // ========== Remote (API) + sync to local ==========

    /**
     * Sincroniza las notificaciones del servidor con la base de datos local.
     *
     * Descarga todas las notificaciones del backend y las inserta/actualiza
     * en Room. Si la notificación ya existe (por serverId), se actualiza.
     *
     * @return Número de notificaciones sincronizadas, o -1 en caso de error
     */
    suspend fun syncFromServer(): Int {
        return try {
            val response = apiV2.getNotifications(perPage = 50)
            val notifications = response.data

            val entities = notifications.map { it.toEntity() }
            for (entity in entities) {
                val existing = dao.getByServerId(entity.serverId)
                if (existing != null) {
                    // Update: keep local ID, update fields
                    dao.insert(entity.copy(id = existing.id))
                } else {
                    dao.insert(entity)
                }
            }

            Log.d(TAG, "Sincronizadas ${entities.size} notificaciones desde servidor")
            entities.size
        } catch (e: Exception) {
            Log.e(TAG, "Error sincronizando notificaciones", e)
            -1
        }
    }

    /**
     * Marca una notificación como leída tanto en local como en el servidor.
     *
     * @param localId ID local de la notificación
     * @param serverId ID del servidor (0 si no tiene)
     * @param readAt Timestamp ISO 8601
     */
    suspend fun markAsRead(localId: Long, serverId: Long, readAt: String): Boolean {
        return try {
            dao.markAsRead(localId, readAt)
            if (serverId > 0) {
                apiV2.markNotificationAsRead(serverId)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error marcando notificación como leída", e)
            // Al menos la marcamos localmente
            false
        }
    }

    /**
     * Marca todas las notificaciones como leídas (local + servidor).
     */
    suspend fun markAllAsRead(readAt: String): Boolean {
        return try {
            dao.markAllAsRead(readAt)
            apiV2.markAllNotificationsAsRead()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error marcando todas como leídas", e)
            false
        }
    }

    /**
     * Elimina una notificación (local + servidor).
     */
    suspend fun delete(localId: Long, serverId: Long): Boolean {
        return try {
            dao.deleteById(localId)
            if (serverId > 0) {
                apiV2.deleteNotification(serverId)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error eliminando notificación", e)
            false
        }
    }
}

/**
 * Convierte un DTO de la API a una entidad Room.
 */
fun NotificationDto.toEntity(): AppNotificationEntity {
    return AppNotificationEntity(
        serverId = this.id,
        title = this.title,
        body = this.body ?: "",
        type = this.type,
        data = null, // data map not stored as string for simplicity
        readAt = this.readAt,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
