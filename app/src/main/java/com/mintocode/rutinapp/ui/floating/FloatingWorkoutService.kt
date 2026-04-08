package com.mintocode.rutinapp.ui.floating

import android.annotation.SuppressLint
import android.app.ForegroundServiceStartNotAllowedException
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.mintocode.rutinapp.MainActivity
import com.mintocode.rutinapp.R
import com.mintocode.rutinapp.data.models.SetModel
import java.time.Instant
import java.util.Date

/**
 * Servicio en primer plano que muestra un widget flotante sobre otras aplicaciones
 * cuando hay un entrenamiento activo.
 *
 * El widget tiene dos estados:
 * - Burbuja colapsada: icono circular arrastrable que se expande al tocarlo.
 * - Panel expandido: selector de ejercicio, inputs de reps/peso, botón enviar y pantalla completa.
 */
class FloatingWorkoutService : Service() {

    companion object {
        private const val CHANNEL_ID = "rutinapp_floating_workout"
        private const val CHANNEL_NAME = "Entrenamiento flotante"
        private const val NOTIFICATION_ID = 2001

        /**
         * Inicia el servicio de widget flotante si el permiso de overlay está concedido.
         *
         * @param context Contexto desde el que se lanza el servicio
         */
        fun start(context: Context) {
            if (!Settings.canDrawOverlays(context)) return
            try {
                val intent = Intent(context, FloatingWorkoutService::class.java)
                context.startForegroundService(intent)
            } catch (e: Exception) {
                Log.e("FloatingWorkout", "Cannot start floating service", e)
            }
        }

        /**
         * Detiene el servicio de widget flotante.
         *
         * @param context Contexto desde el que se detiene el servicio
         */
        fun stop(context: Context) {
            val intent = Intent(context, FloatingWorkoutService::class.java)
            context.stopService(intent)
        }
    }

    private lateinit var windowManager: WindowManager
    private var bubbleView: View? = null
    private var expandedView: View? = null
    private var isExpanded = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        try {
            startForeground(NOTIFICATION_ID, buildNotification())
        } catch (e: Exception) {
            Log.e("FloatingWorkout", "startForeground failed", e)
            stopSelf()
            return
        }
        showBubble()
    }

    override fun onDestroy() {
        removeBubble()
        removeExpanded()
        super.onDestroy()
    }

    /**
     * Crea el canal de notificaciones necesario para el servicio en primer plano.
     */
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notificación del widget flotante de entrenamiento"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    /**
     * Construye la notificación persistente del servicio en primer plano.
     *
     * @return Notificación configurada con acción para abrir la app
     */
    private fun buildNotification(): Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Entrenamiento en curso")
            .setContentText("Toca para abrir RutinApp")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    // ── Bubble (collapsed) ──────────────────────────────────────────────

    /**
     * Muestra la burbuja colapsada flotante sobre otras aplicaciones.
     * La burbuja es arrastrable y se expande al tocarla sin arrastrar.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun showBubble() {
        if (bubbleView != null) return

        val inflater = LayoutInflater.from(this)
        bubbleView = inflater.inflate(R.layout.floating_bubble, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 300
        }

        // Drag + tap logic
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var moved = false

        bubbleView?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    moved = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - initialTouchX
                    val dy = event.rawY - initialTouchY
                    if (dx * dx + dy * dy > 25) moved = true
                    params.x = initialX + dx.toInt()
                    params.y = initialY + dy.toInt()
                    windowManager.updateViewLayout(bubbleView, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!moved) {
                        removeBubble()
                        showExpanded()
                    }
                    true
                }
                else -> false
            }
        }

        windowManager.addView(bubbleView, params)
        isExpanded = false
    }

    /**
     * Elimina la burbuja colapsada del WindowManager.
     */
    private fun removeBubble() {
        bubbleView?.let {
            try { windowManager.removeView(it) } catch (_: Exception) {}
        }
        bubbleView = null
    }

    // ── Expanded panel ──────────────────────────────────────────────────

    /**
     * Muestra el panel expandido con selector de ejercicio, inputs y botones.
     * Configura todos los listeners para los controles del panel.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun showExpanded() {
        if (expandedView != null) return

        val inflater = LayoutInflater.from(this)
        expandedView = inflater.inflate(R.layout.floating_expanded, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        setupExerciseSpinner()
        setupButtons()

        windowManager.addView(expandedView, params)
        isExpanded = true
    }

    /**
     * Configura el Spinner de ejercicios con los ejercicios del entrenamiento activo.
     */
    private fun setupExerciseSpinner() {
        val spinner = expandedView?.findViewById<Spinner>(R.id.spinner_exercise) ?: return
        val exerciseNames = ActiveWorkoutHolder.getExerciseNames()

        if (exerciseNames.isEmpty()) {
            spinner.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                listOf("Sin ejercicios")
            )
            return
        }

        spinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            exerciseNames
        )
    }

    /**
     * Configura los listeners de los botones del panel expandido:
     * minimizar, enviar set y abrir pantalla completa.
     */
    private fun setupButtons() {
        val view = expandedView ?: return

        // Minimize → collapse to bubble
        view.findViewById<ImageButton>(R.id.btn_minimize).setOnClickListener {
            removeExpanded()
            showBubble()
        }

        // Send set
        view.findViewById<Button>(R.id.btn_send).setOnClickListener {
            sendSet()
        }

        // Fullscreen → open MainActivity
        view.findViewById<ImageButton>(R.id.btn_fullscreen).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
            removeExpanded()
            showBubble()
        }
    }

    /**
     * Recoge los datos del formulario y crea un nuevo set en el entrenamiento activo
     * a través del ActiveWorkoutHolder.
     */
    private fun sendSet() {
        val view = expandedView ?: return
        val spinner = view.findViewById<Spinner>(R.id.spinner_exercise)
        val repsInput = view.findViewById<EditText>(R.id.input_reps)
        val weightInput = view.findViewById<EditText>(R.id.input_weight)
        val statusText = view.findViewById<TextView>(R.id.txt_status)

        val selectedIndex = spinner.selectedItemPosition
        val exercise = ActiveWorkoutHolder.getExerciseAt(selectedIndex)

        if (exercise == null) {
            statusText.text = "No hay ejercicio seleccionado"
            statusText.visibility = View.VISIBLE
            return
        }

        val repsStr = repsInput.text.toString().trim()
        val weightStr = weightInput.text.toString().trim()

        if (repsStr.isEmpty()) {
            statusText.text = "Introduce las repeticiones"
            statusText.visibility = View.VISIBLE
            return
        }

        val reps = repsStr.toIntOrNull()
        if (reps == null || reps <= 0) {
            statusText.text = "Repeticiones inválidas"
            statusText.visibility = View.VISIBLE
            return
        }

        val weight = if (weightStr.isEmpty()) 0.0 else weightStr.toDoubleOrNull()
        if (weight == null || weight < 0) {
            statusText.text = "Peso inválido"
            statusText.visibility = View.VISIBLE
            return
        }

        val workout = ActiveWorkoutHolder.activeWorkout ?: return

        val newSet = SetModel(
            weight = weight,
            reps = reps,
            date = Date.from(Instant.now()),
            observations = "",
            exercise = exercise,
            workoutDone = workout
        )

        ActiveWorkoutHolder.onSetAdded?.invoke(newSet)

        // Feedback
        statusText.text = "✓ Set añadido: ${exercise.name} ${reps}x${weight}kg"
        statusText.visibility = View.VISIBLE
        repsInput.text.clear()
        weightInput.text.clear()

        // Update spinner in case exercises changed
        setupExerciseSpinner()
    }

    /**
     * Elimina el panel expandido del WindowManager.
     */
    private fun removeExpanded() {
        expandedView?.let {
            try { windowManager.removeView(it) } catch (_: Exception) {}
        }
        expandedView = null
    }
}
