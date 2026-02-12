package com.mintocode.rutinapp.data.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mintocode.rutinapp.MainActivity
import com.mintocode.rutinapp.R
import com.mintocode.rutinapp.data.daos.AppNotificationDao
import com.mintocode.rutinapp.data.daos.AppNotificationEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

/**
 * Servicio de Firebase Cloud Messaging para recibir notificaciones push.
 *
 * Gestiona la recepción de mensajes push, la renovación del token FCM
 * y la persistencia local de las notificaciones recibidas.
 * El canal de notificaciones se crea al iniciar la app en [NotificationHelper].
 */
@AndroidEntryPoint
class RutinAppMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "RutinAppFCM"

        /** ID del canal de notificaciones general. */
        const val CHANNEL_ID_GENERAL = "rutinapp_general"

        /** Nombre visible del canal general. */
        const val CHANNEL_NAME_GENERAL = "General"
    }

    @Inject
    lateinit var notificationDao: AppNotificationDao

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * Callback cuando se recibe un nuevo token FCM.
     *
     * Se invoca al instalar la app o cuando el token se renueva.
     * El registro con el backend se realiza desde [NotificationHelper]
     * cuando el usuario está autenticado.
     *
     * @param token Nuevo token FCM
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Nuevo token FCM: $token")
        // El token se registrará con el backend desde NotificationHelper
        // cuando el usuario esté autenticado y se solicite el token.
    }

    /**
     * Callback cuando se recibe un mensaje push.
     *
     * Muestra una notificación local si el mensaje contiene datos de notificación
     * y persiste la notificación en la base de datos Room.
     *
     * @param remoteMessage Mensaje recibido de Firebase
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Mensaje recibido de: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: getString(R.string.app_name)

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: ""

        val type = remoteMessage.data["type"] ?: "info"

        if (body.isNotBlank()) {
            showNotification(title, body, remoteMessage.data)
            persistNotification(title, body, type)
        }
    }

    /**
     * Persiste la notificación recibida en Room para que se muestre
     * en la lista de notificaciones de la app.
     */
    private fun persistNotification(title: String, body: String, type: String) {
        serviceScope.launch {
            try {
                val now = Instant.now().toString()
                notificationDao.insert(
                    AppNotificationEntity(
                        serverId = 0, // Se sincronizará con el servidor después
                        title = title,
                        body = body,
                        type = type,
                        createdAt = now,
                        updatedAt = now
                    )
                )
                Log.d(TAG, "Notificación persistida localmente: $title")
            } catch (e: Exception) {
                Log.e(TAG, "Error persistiendo notificación", e)
            }
        }
    }

    /**
     * Muestra una notificación local en la bandeja del sistema.
     *
     * @param title Título de la notificación
     * @param body Cuerpo del mensaje
     * @param data Datos adicionales del mensaje
     */
    private fun showNotification(title: String, body: String, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data.forEach { (key, value) -> putExtra(key, value) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID_GENERAL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
