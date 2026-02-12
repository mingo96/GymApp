package com.mintocode.rutinapp.data.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.mintocode.rutinapp.data.api.v2.ApiV2Service
import com.mintocode.rutinapp.data.api.v2.dto.RegisterFcmTokenRequest
import kotlinx.coroutines.tasks.await

/**
 * Helper centralizado para la gestión de notificaciones push.
 *
 * Responsable de:
 * - Crear canales de notificación
 * - Verificar permisos de notificación
 * - Obtener y registrar el token FCM con el backend
 *
 * @param context Contexto de la aplicación
 * @param apiV2 Servicio Retrofit para la API v2
 */
class NotificationHelper(
    private val context: Context,
    private val apiV2: ApiV2Service
) {

    companion object {
        private const val TAG = "NotificationHelper"
    }

    /**
     * Crea los canales de notificación necesarios.
     *
     * Debe llamarse al iniciar la app (en MainActivity.onCreate o Application.onCreate).
     * En Android 8.0+ (API 26+) los canales son obligatorios para mostrar notificaciones.
     */
    fun createNotificationChannels() {
        val channel = NotificationChannel(
            RutinAppMessagingService.CHANNEL_ID_GENERAL,
            RutinAppMessagingService.CHANNEL_NAME_GENERAL,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones generales de RutinApp"
            enableVibration(true)
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        Log.d(TAG, "Canal de notificaciones creado: ${RutinAppMessagingService.CHANNEL_ID_GENERAL}")
    }

    /**
     * Verifica si el permiso de notificaciones está concedido.
     *
     * En Android 13+ (API 33+) se requiere POST_NOTIFICATIONS explícitamente.
     * En versiones anteriores siempre retorna true.
     *
     * @return true si el permiso está concedido
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Obtiene el token FCM actual del dispositivo.
     *
     * @return Token FCM o null si no se pudo obtener
     */
    suspend fun getFcmToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo token FCM", e)
            null
        }
    }

    /**
     * Registra el token FCM del dispositivo en el backend.
     *
     * Solo registra si el usuario está autenticado (tiene token de API)
     * y si se pudo obtener el token FCM.
     *
     * @return true si el registro fue exitoso
     */
    suspend fun registerTokenWithBackend(): Boolean {
        return try {
            val fcmToken = getFcmToken() ?: return false

            apiV2.registerFcmToken(RegisterFcmTokenRequest(fcmToken = fcmToken))
            Log.d(TAG, "Token FCM registrado en backend")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error registrando token FCM en backend", e)
            false
        }
    }
}
