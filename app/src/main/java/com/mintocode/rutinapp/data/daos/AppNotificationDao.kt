package com.mintocode.rutinapp.data.daos

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Entidad Room para notificaciones persistentes.
 *
 * Almacena localmente las notificaciones recibidas (push o de la API)
 * para que el usuario pueda consultarlas sin conexión.
 *
 * El [serverId] corresponde al ID del backend (tabla app_notifications).
 * Si la notificación fue creada localmente (push sin sync), serverId puede ser 0.
 */
@Entity(
    tableName = "app_notifications",
    indices = [
        Index("readAt"),
        Index("createdAt")
    ]
)
data class AppNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(defaultValue = "0") val serverId: Long = 0,
    val title: String,
    @ColumnInfo(defaultValue = "") val body: String = "",
    @ColumnInfo(defaultValue = "info") val type: String = "info",
    val data: String? = null,
    val readAt: String? = null,
    @ColumnInfo(defaultValue = "") val createdAt: String = "",
    @ColumnInfo(defaultValue = "") val updatedAt: String = ""
)

/**
 * DAO para operaciones CRUD sobre notificaciones locales.
 */
@Dao
interface AppNotificationDao {

    /**
     * Obtiene todas las notificaciones ordenadas por fecha de creación descendente.
     */
    @Query("SELECT * FROM app_notifications ORDER BY createdAt DESC")
    fun getAllAsFlow(): Flow<List<AppNotificationEntity>>

    /**
     * Obtiene las notificaciones no leídas.
     */
    @Query("SELECT * FROM app_notifications WHERE readAt IS NULL ORDER BY createdAt DESC")
    fun getUnreadAsFlow(): Flow<List<AppNotificationEntity>>

    /**
     * Cuenta las notificaciones no leídas.
     */
    @Query("SELECT COUNT(*) FROM app_notifications WHERE readAt IS NULL")
    fun getUnreadCountAsFlow(): Flow<Int>

    /**
     * Cuenta las notificaciones no leídas (suspending, para uso puntual).
     */
    @Query("SELECT COUNT(*) FROM app_notifications WHERE readAt IS NULL")
    suspend fun getUnreadCount(): Int

    /**
     * Obtiene una notificación por su ID local.
     */
    @Query("SELECT * FROM app_notifications WHERE id = :id")
    suspend fun getById(id: Long): AppNotificationEntity?

    /**
     * Obtiene una notificación por su ID del servidor.
     */
    @Query("SELECT * FROM app_notifications WHERE serverId = :serverId LIMIT 1")
    suspend fun getByServerId(serverId: Long): AppNotificationEntity?

    /**
     * Inserta una notificación. Si ya existe una con el mismo ID, la reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: AppNotificationEntity): Long

    /**
     * Inserta varias notificaciones a la vez.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<AppNotificationEntity>)

    /**
     * Marca una notificación como leída por su ID local.
     */
    @Query("UPDATE app_notifications SET readAt = :readAt WHERE id = :id")
    suspend fun markAsRead(id: Long, readAt: String)

    /**
     * Marca una notificación como leída por su ID del servidor.
     */
    @Query("UPDATE app_notifications SET readAt = :readAt WHERE serverId = :serverId")
    suspend fun markAsReadByServerId(serverId: Long, readAt: String)

    /**
     * Marca todas las notificaciones como leídas.
     */
    @Query("UPDATE app_notifications SET readAt = :readAt WHERE readAt IS NULL")
    suspend fun markAllAsRead(readAt: String)

    /**
     * Elimina una notificación por su ID local.
     */
    @Query("DELETE FROM app_notifications WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Elimina una notificación por su ID del servidor.
     */
    @Query("DELETE FROM app_notifications WHERE serverId = :serverId")
    suspend fun deleteByServerId(serverId: Long)

    /**
     * Elimina todas las notificaciones.
     */
    @Query("DELETE FROM app_notifications")
    suspend fun deleteAll()

    /**
     * Elimina notificaciones antiguas (más de N días).
     */
    @Query("DELETE FROM app_notifications WHERE createdAt < :cutoff")
    suspend fun deleteOlderThan(cutoff: String)
}
