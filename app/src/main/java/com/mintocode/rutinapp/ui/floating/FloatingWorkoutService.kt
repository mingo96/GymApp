package com.mintocode.rutinapp.ui.floating

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
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
 * - Burbuja colapsada: muestra el tiempo desde el último set, arrastrable, se expande al tocar.
 * - Panel expandido: selector de ejercicio, inputs de reps/peso, estadísticas del ejercicio,
 *   redimensionable arrastrando la ficha de agarre superior.
 *
 * El widget se oculta automáticamente cuando la app está en primer plano y se muestra
 * al salir de la app.
 */
class FloatingWorkoutService : Service() {

    companion object {
        private const val CHANNEL_ID = "rutinapp_floating_workout"
        private const val CHANNEL_NAME = "Entrenamiento flotante"
        private const val NOTIFICATION_ID = 2001
        private const val TIMER_INTERVAL_MS = 1000L
        private const val MIN_EXPANDED_WIDTH = 280
        private const val MIN_EXPANDED_HEIGHT = 350
        private const val DEFAULT_EXPANDED_WIDTH = 340
        private const val DEFAULT_EXPANDED_HEIGHT = 480
        private const val RESIZE_TOUCH_AREA = 12

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
    private var isAppInForeground = false
    private var expandedWidth = DEFAULT_EXPANDED_WIDTH
    private var expandedHeight = DEFAULT_EXPANDED_HEIGHT
    private var expandedParams: WindowManager.LayoutParams? = null

    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            updateBubbleTimer()
            handler.postDelayed(this, TIMER_INTERVAL_MS)
        }
    }

    /**
     * Observador del ciclo de vida de la app para ocultar/mostrar el widget
     * según si la app está en primer plano o no.
     */
    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            isAppInForeground = true
            hideOverlay()
        }

        override fun onStop(owner: LifecycleOwner) {
            isAppInForeground = false
            showOverlay()
        }
    }

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
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
        if (!isAppInForeground) {
            showBubble()
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(timerRunnable)
        removeBubble()
        removeExpanded()
        try {
            ProcessLifecycleOwner.get().lifecycle.removeObserver(lifecycleObserver)
        } catch (_: Exception) {}
        super.onDestroy()
    }

    /**
     * Oculta todas las vistas de overlay (burbuja y panel expandido).
     */
    private fun hideOverlay() {
        bubbleView?.visibility = View.GONE
        expandedView?.visibility = View.GONE
    }

    /**
     * Muestra la vista de overlay apropiada según el estado actual.
     * Si la vista no existe aún, la crea.
     */
    private fun showOverlay() {
        if (isExpanded) {
            if (expandedView != null) expandedView?.visibility = View.VISIBLE
            else showExpanded()
        } else {
            if (bubbleView != null) bubbleView?.visibility = View.VISIBLE
            else showBubble()
        }
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
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
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

    // ── Timer ───────────────────────────────────────────────────────────

    /**
     * Actualiza el texto del temporizador en la burbuja con el tiempo
     * transcurrido desde el último set.
     */
    private fun updateBubbleTimer() {
        val timerText = bubbleView?.findViewById<TextView>(R.id.txt_bubble_timer) ?: return
        val lastSet = ActiveWorkoutHolder.lastSetTime
        if (lastSet == 0L) {
            timerText.text = "–"
            return
        }
        val elapsed = (System.currentTimeMillis() - lastSet) / 1000
        val minutes = elapsed / 60
        val seconds = elapsed % 60
        timerText.text = "$minutes:%02d".format(seconds)
    }

    // ── Bubble (collapsed) ──────────────────────────────────────────────

    /**
     * Muestra la burbuja colapsada flotante sobre otras aplicaciones.
     * Muestra el tiempo desde el último set y se expande al tocarla sin arrastrar.
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

        // Start timer updates
        handler.removeCallbacks(timerRunnable)
        handler.post(timerRunnable)
    }

    /**
     * Elimina la burbuja colapsada del WindowManager.
     */
    private fun removeBubble() {
        handler.removeCallbacks(timerRunnable)
        bubbleView?.let {
            try { windowManager.removeView(it) } catch (_: Exception) {}
        }
        bubbleView = null
    }

    // ── Expanded panel ──────────────────────────────────────────────────

    /**
     * Muestra el panel expandido con slider de ejercicios, inputs, stats.
     * Configura drag handle para mover y bordes para redimensionar.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun showExpanded() {
        if (expandedView != null) return

        val inflater = LayoutInflater.from(this)
        expandedView = inflater.inflate(R.layout.floating_expanded, null)

        val dp = resources.displayMetrics.density
        val screenW = resources.displayMetrics.widthPixels
        val screenH = resources.displayMetrics.heightPixels
        val maxW = (screenW * 0.9).toInt()
        val maxH = (screenH * 0.85).toInt()
        val w = (expandedWidth * dp).toInt().coerceIn((MIN_EXPANDED_WIDTH * dp).toInt(), maxW)
        val h = (expandedHeight * dp).toInt().coerceIn((MIN_EXPANDED_HEIGHT * dp).toInt(), maxH)

        val params = WindowManager.LayoutParams(
            w, h,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = (screenW - w) / 2
            y = (screenH - h) / 2
        }
        expandedParams = params

        setupMoveHandle(params)
        setupResizeEdges(params)
        setupExerciseSlider()
        setupButtons()
        updateStats()

        windowManager.addView(expandedView, params)
        isExpanded = true
    }

    /**
     * Configura la ficha de agarre superior para mover el panel
     * arrastrando en cualquier dirección.
     *
     * @param params LayoutParams del panel expandido
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupMoveHandle(params: WindowManager.LayoutParams) {
        val dragHandle = expandedView?.findViewById<FrameLayout>(R.id.drag_handle) ?: return
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        dragHandle.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    try { windowManager.updateViewLayout(expandedView, params) } catch (_: Exception) {}
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Configura las zonas de toque en los bordes del panel para redimensionar
     * en todas las direcciones (arriba, abajo, izquierda, derecha).
     *
     * @param params LayoutParams del panel expandido
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupResizeEdges(params: WindowManager.LayoutParams) {
        val dp = resources.displayMetrics.density
        val screenW = resources.displayMetrics.widthPixels
        val screenH = resources.displayMetrics.heightPixels
        val maxW = (screenW * 0.95).toInt()
        val maxH = (screenH * 0.90).toInt()
        val minW = (MIN_EXPANDED_WIDTH * dp).toInt()
        val minH = (MIN_EXPANDED_HEIGHT * dp).toInt()

        fun clampAndUpdate() {
            params.width = params.width.coerceIn(minW, maxW)
            params.height = params.height.coerceIn(minH, maxH)
            expandedWidth = (params.width / dp).toInt()
            expandedHeight = (params.height / dp).toInt()
            try { windowManager.updateViewLayout(expandedView, params) } catch (_: Exception) {}
            updateStatsVisibility(params.height)
        }

        // Right edge: resize width from right
        expandedView?.findViewById<View>(R.id.resize_right)?.setOnTouchListener(
            createEdgeResizeListener(params) { dx, dy, initW, initH, initX, initY ->
                params.width = initW + dx
                clampAndUpdate()
            }
        )

        // Left edge: resize width from left (move x + shrink width)
        expandedView?.findViewById<View>(R.id.resize_left)?.setOnTouchListener(
            createEdgeResizeListener(params) { dx, dy, initW, initH, initX, initY ->
                val newW = (initW - dx).coerceIn(minW, maxW)
                params.x = initX + (initW - newW)
                params.width = newW
                clampAndUpdate()
            }
        )

        // Bottom edge: resize height from bottom
        expandedView?.findViewById<View>(R.id.resize_bottom)?.setOnTouchListener(
            createEdgeResizeListener(params) { dx, dy, initW, initH, initX, initY ->
                params.height = initH + dy
                clampAndUpdate()
            }
        )

        // Top edge: resize height from top (move y + shrink height)
        expandedView?.findViewById<View>(R.id.resize_top)?.setOnTouchListener(
            createEdgeResizeListener(params) { dx, dy, initW, initH, initX, initY ->
                val newH = (initH - dy).coerceIn(minH, maxH)
                params.y = initY + (initH - newH)
                params.height = newH
                clampAndUpdate()
            }
        )
    }

    /**
     * Crea un OnTouchListener para redimensionar desde un borde.
     *
     * @param params LayoutParams del panel
     * @param onDrag Lambda que recibe (dx, dy, initialWidth, initialHeight, initialX, initialY) en el ACTION_MOVE
     * @return View.OnTouchListener configurado
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun createEdgeResizeListener(
        params: WindowManager.LayoutParams,
        onDrag: (dx: Int, dy: Int, initW: Int, initH: Int, initX: Int, initY: Int) -> Unit
    ): View.OnTouchListener {
        var initW = 0; var initH = 0; var initX = 0; var initY = 0
        var touchX = 0f; var touchY = 0f

        return View.OnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initW = params.width; initH = params.height
                    initX = params.x; initY = params.y
                    touchX = event.rawX; touchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - touchX).toInt()
                    val dy = (event.rawY - touchY).toInt()
                    onDrag(dx, dy, initW, initH, initX, initY)
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Muestra u oculta la sección de estadísticas según la altura del panel.
     *
     * @param height Altura actual del panel en píxeles
     */
    private fun updateStatsVisibility(height: Int) {
        val statsSection = expandedView?.findViewById<LinearLayout>(R.id.stats_section)
        val threshold = (resources.displayMetrics.density * 380).toInt()
        if (height > threshold) {
            statsSection?.visibility = View.VISIBLE
        } else {
            statsSection?.visibility = View.GONE
        }
    }

    /**
     * Configura el selector de ejercicios por deslizamiento.
     * Deslizar a la izquierda avanza al siguiente ejercicio,
     * deslizar a la derecha retrocede al anterior.
     * Los botones de flecha también cambian la selección.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupExerciseSlider() {
        val view = expandedView ?: return
        val swipeArea = view.findViewById<FrameLayout>(R.id.exercise_swipe) ?: return
        val nameView = view.findViewById<TextView>(R.id.exercise_name) ?: return
        val arrowLeft = view.findViewById<TextView>(R.id.exercise_arrow_left)
        val arrowRight = view.findViewById<TextView>(R.id.exercise_arrow_right)
        val exerciseNames = ActiveWorkoutHolder.getExerciseNames()

        if (exerciseNames.isEmpty()) {
            nameView.text = "Sin ejercicios"
            arrowLeft?.visibility = View.INVISIBLE
            arrowRight?.visibility = View.INVISIBLE
            return
        }

        val currentIndex = ActiveWorkoutHolder.selectedExerciseIndex
            .coerceIn(0, exerciseNames.size - 1)
        ActiveWorkoutHolder.selectedExerciseIndex = currentIndex
        nameView.text = exerciseNames[currentIndex]
        updateArrowVisibility(arrowLeft, arrowRight, currentIndex, exerciseNames.size)

        // Swipe gesture detection
        var startX = 0f
        val swipeThreshold = resources.displayMetrics.density * 50

        swipeArea.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.rawX
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val dx = event.rawX - startX
                    if (dx < -swipeThreshold) {
                        selectExercise(1, exerciseNames, nameView, arrowLeft, arrowRight)
                    } else if (dx > swipeThreshold) {
                        selectExercise(-1, exerciseNames, nameView, arrowLeft, arrowRight)
                    }
                    true
                }
                else -> true
            }
        }

        arrowLeft?.setOnClickListener {
            selectExercise(-1, exerciseNames, nameView, arrowLeft, arrowRight)
        }
        arrowRight?.setOnClickListener {
            selectExercise(1, exerciseNames, nameView, arrowLeft, arrowRight)
        }
    }

    /**
     * Cambia la selección de ejercicio en la dirección indicada.
     *
     * @param direction +1 para siguiente, -1 para anterior
     * @param names Lista de nombres de ejercicios
     * @param nameView TextView que muestra el nombre del ejercicio
     * @param arrowLeft Flecha izquierda para mostrar/ocultar
     * @param arrowRight Flecha derecha para mostrar/ocultar
     */
    private fun selectExercise(
        direction: Int,
        names: List<String>,
        nameView: TextView,
        arrowLeft: TextView?,
        arrowRight: TextView?
    ) {
        val newIndex = (ActiveWorkoutHolder.selectedExerciseIndex + direction)
            .coerceIn(0, names.size - 1)
        if (newIndex == ActiveWorkoutHolder.selectedExerciseIndex) return
        ActiveWorkoutHolder.selectedExerciseIndex = newIndex
        nameView.text = names[newIndex]
        updateArrowVisibility(arrowLeft, arrowRight, newIndex, names.size)
        updateStats()
    }

    /**
     * Muestra u oculta las flechas del selector según la posición actual.
     *
     * @param left Flecha izquierda
     * @param right Flecha derecha
     * @param index Índice actual
     * @param count Número total de ejercicios
     */
    private fun updateArrowVisibility(left: TextView?, right: TextView?, index: Int, count: Int) {
        left?.visibility = if (index > 0) View.VISIBLE else View.INVISIBLE
        right?.visibility = if (index < count - 1) View.VISIBLE else View.INVISIBLE
    }

    /**
     * Actualiza la sección de estadísticas con datos del ejercicio seleccionado:
     * último set, peso máximo y total de sets.
     */
    private fun updateStats() {
        val view = expandedView ?: return
        val index = ActiveWorkoutHolder.selectedExerciseIndex
        val sets = ActiveWorkoutHolder.getSetsForExercise(index)
        val lastSet = ActiveWorkoutHolder.getLastSetForExercise(index)
        val maxWeight = ActiveWorkoutHolder.getMaxWeightForExercise(index)

        val txtLastSet = view.findViewById<TextView>(R.id.txt_last_set)
        val txtMaxWeight = view.findViewById<TextView>(R.id.txt_max_weight)
        val txtTotalSets = view.findViewById<TextView>(R.id.txt_total_sets)

        if (lastSet != null) {
            txtLastSet.text = "Último set: ${lastSet.reps} reps × ${lastSet.weight} kg"
        } else {
            txtLastSet.text = "Último set: –"
        }
        txtMaxWeight.text = "Peso máximo: ${maxWeight} kg"
        txtTotalSets.text = "Sets totales: ${sets.size}"

        updateStatsVisibility(expandedHeight)
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

        // Fullscreen → resume app (not restart)
        view.findViewById<ImageButton>(R.id.btn_fullscreen).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
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
        val repsInput = view.findViewById<EditText>(R.id.input_reps)
        val weightInput = view.findViewById<EditText>(R.id.input_weight)
        val statusText = view.findViewById<TextView>(R.id.txt_status)

        val selectedIndex = ActiveWorkoutHolder.selectedExerciseIndex
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
        ActiveWorkoutHolder.recordSetAdded(selectedIndex)

        // Feedback
        statusText.text = "✓ Set añadido: ${exercise.name} ${reps}x${weight}kg"
        statusText.visibility = View.VISIBLE
        repsInput.text.clear()
        weightInput.text.clear()

        // Update slider and stats
        setupExerciseSlider()
        updateStats()
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
