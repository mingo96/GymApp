package com.mintocode.rutinapp.ui.floating

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
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
 * Servicio en primer plano que muestra un widget flotante estilo Kinetic Precision
 * sobre otras aplicaciones cuando hay un entrenamiento activo.
 *
 * El widget tiene dos estados:
 * - Burbuja colapsada: círculo 56dp con icono fitness, borde tertiary neón con animación
 *   pulse, timer badge superpuesto, arrastrable, se expande al tocar.
 * - Panel expandido: glass panel 340×520 con stepper inputs (reps/weight),
 *   chips de ejercicios, botón LOG SET con gradiente tertiary, footer de estadísticas,
 *   redimensionable y arrastrable.
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
        private const val MIN_EXPANDED_WIDTH = 300
        private const val MIN_EXPANDED_HEIGHT = 400
        private const val DEFAULT_EXPANDED_WIDTH = 340
        private const val DEFAULT_EXPANDED_HEIGHT = 520
        private const val COMPACT_WIDTH_THRESHOLD = 320

        // KP color palette
        private const val COLOR_TERTIARY = "#27E0A9"
        private const val COLOR_ON_TERTIARY = "#003827"
        private const val COLOR_ON_SURFACE = "#E4E1E9"
        private const val COLOR_ON_SURFACE_VARIANT = "#C4C5D7"
        private const val COLOR_SURFACE_CONTAINER_HIGH = "#2A292F"
        private const val COLOR_PRIMARY_CONTAINER = "#4361EE"
        private const val COLOR_ON_PRIMARY_CONTAINER = "#F4F2FF"
        private const val COLOR_ERROR = "#FFB4AB"

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

    // Stepper state
    private var currentReps = 10
    private var currentWeight = 0.0
    private val weightStep = 2.5

    // Workout start time for total timer
    private var workoutStartTime = 0L

    // Bubble animation
    private var bubblePulseAnimator: AnimatorSet? = null

    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            updateBubbleTimer()
            updatePanelTimer()
            updateFooterStats()
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
        workoutStartTime = System.currentTimeMillis()
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
        stopBubblePulse()
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
        stopBubblePulse()
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
            if (bubbleView != null) {
                bubbleView?.visibility = View.VISIBLE
                startBubblePulse()
            } else {
                showBubble()
            }
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

    // ── Utility ─────────────────────────────────────────────────────────

    /**
     * Convierte dp a píxeles.
     *
     * @param dp Valor en dp
     * @return Valor en píxeles
     */
    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics
        ).toInt()
    }

    /**
     * Formatea segundos en formato HH:MM:SS o M:SS.
     *
     * @param totalSeconds Segundos totales
     * @param full Si true, muestra siempre HH:MM:SS
     * @return Cadena formateada
     */
    private fun formatTime(totalSeconds: Long, full: Boolean = false): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (full || hours > 0) {
            "%02d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%d:%02d".format(minutes, seconds)
        }
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
        timerText.text = formatTime(elapsed)
    }

    /**
     * Actualiza el temporizador general del panel expandido con el tiempo
     * total del entrenamiento.
     */
    private fun updatePanelTimer() {
        val timerText = expandedView?.findViewById<TextView>(R.id.txt_panel_timer) ?: return
        val elapsed = (System.currentTimeMillis() - workoutStartTime) / 1000
        timerText.text = formatTime(elapsed, full = true)
    }

    /**
     * Actualiza las estadísticas del footer del panel expandido.
     */
    private fun updateFooterStats() {
        val view = expandedView ?: return
        val workout = ActiveWorkoutHolder.activeWorkout ?: return

        // Total sets
        var totalSets = 0
        var totalVolume = 0.0
        workout.exercisesAndSets.forEach { (_, sets) ->
            totalSets += sets.size
            sets.forEach { set -> totalVolume += set.weight * set.reps }
        }

        view.findViewById<TextView>(R.id.txt_stat_sets)?.text = "$totalSets"

        // Volume formatting
        val volumeText = if (totalVolume >= 1000) {
            "%.1fk kg".format(totalVolume / 1000)
        } else {
            "%.0f kg".format(totalVolume)
        }
        view.findViewById<TextView>(R.id.txt_stat_volume)?.text = volumeText

        // Time
        val elapsed = (System.currentTimeMillis() - workoutStartTime) / 1000 / 60
        view.findViewById<TextView>(R.id.txt_stat_time)?.text = "${elapsed}m"
    }

    // ── Bubble (collapsed) ──────────────────────────────────────────────

    /**
     * Inicia la animación neon-pulse de la burbuja: escala 0.95→1.0 con
     * un brillo tertiary pulsante, ciclo infinito de 2 segundos.
     */
    private fun startBubblePulse() {
        val circle = bubbleView?.findViewById<FrameLayout>(R.id.bubble_circle) ?: return

        val scaleX = ObjectAnimator.ofFloat(circle, "scaleX", 0.95f, 1.0f).apply {
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
        }
        val scaleY = ObjectAnimator.ofFloat(circle, "scaleY", 0.95f, 1.0f).apply {
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
        }
        val alpha = ObjectAnimator.ofFloat(circle, "alpha", 0.85f, 1.0f).apply {
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
        }

        bubblePulseAnimator = AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            duration = 2000L
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    /**
     * Detiene la animación neon-pulse de la burbuja.
     */
    private fun stopBubblePulse() {
        bubblePulseAnimator?.cancel()
        bubblePulseAnimator = null
    }

    /**
     * Muestra la burbuja colapsada flotante sobre otras aplicaciones.
     * Círculo 56dp con icono fitness, borde tertiary neón pulsante y timer badge.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun showBubble() {
        if (bubbleView != null) return

        val inflater = LayoutInflater.from(this)
        bubbleView = inflater.inflate(R.layout.floating_bubble, null)

        // Tint fitness icon to tertiary
        bubbleView?.findViewById<FrameLayout>(R.id.bubble_circle)
            ?.findViewById<android.widget.ImageView>(android.R.id.icon)
            ?: run {
                // The ImageView in the bubble doesn't have an ID, tint via the first ImageView child
                val circle = bubbleView?.findViewById<FrameLayout>(R.id.bubble_circle)
                if (circle != null && circle.childCount > 0) {
                    val imageView = circle.getChildAt(0) as? android.widget.ImageView
                    imageView?.setColorFilter(Color.parseColor(COLOR_TERTIARY))
                }
            }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = resources.displayMetrics.widthPixels - dpToPx(80f)
            y = resources.displayMetrics.heightPixels / 2
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
                    try { windowManager.updateViewLayout(bubbleView, params) } catch (_: Exception) {}
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!moved) {
                        stopBubblePulse()
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

        // Start animations and timer
        startBubblePulse()
        handler.removeCallbacks(timerRunnable)
        handler.post(timerRunnable)
    }

    /**
     * Elimina la burbuja colapsada del WindowManager.
     */
    private fun removeBubble() {
        handler.removeCallbacks(timerRunnable)
        stopBubblePulse()
        bubbleView?.let {
            try { windowManager.removeView(it) } catch (_: Exception) {}
        }
        bubbleView = null
    }

    // ── Expanded panel ──────────────────────────────────────────────────

    /**
     * Muestra el panel expandido KP glass con stepper inputs, chips de ejercicios,
     * botón LOG SET, y footer de estadísticas. Incluye animación de entrada suave.
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

        // Set workout title
        val workout = ActiveWorkoutHolder.activeWorkout
        val title = workout?.title?.ifBlank { workout.baseRoutine?.name }
            ?: workout?.baseRoutine?.name ?: "Entrenamiento"
        expandedView?.findViewById<TextView>(R.id.txt_title)?.text = title

        // Initialize stepper values from last set
        initializeStepperValues()

        setupMoveHandle(params)
        setupResizeEdges(params)
        setupStepperButtons()
        setupExerciseChips()
        setupButtons()
        updateSetProgress()
        updatePanelTimer()
        updateFooterStats()
        adaptLayoutToWidth(expandedWidth)

        // Entry animation: scale from 0.92 + fade in with smooth deceleration
        expandedView?.scaleX = 0.92f
        expandedView?.scaleY = 0.92f
        expandedView?.alpha = 0f

        windowManager.addView(expandedView, params)
        isExpanded = true

        expandedView?.animate()
            ?.scaleX(1f)
            ?.scaleY(1f)
            ?.alpha(1f)
            ?.setDuration(280)
            ?.setInterpolator(DecelerateInterpolator(2f))
            ?.start()

        // Start timer updates
        handler.removeCallbacks(timerRunnable)
        handler.post(timerRunnable)
    }

    /**
     * Inicializa los valores de los steppers basándose en el último set del ejercicio
     * seleccionado, o usa valores por defecto.
     */
    private fun initializeStepperValues() {
        val index = ActiveWorkoutHolder.selectedExerciseIndex
        val lastSet = ActiveWorkoutHolder.getLastSetForExercise(index)
        if (lastSet != null) {
            currentReps = lastSet.reps
            currentWeight = lastSet.weight
        }
        updateStepperDisplay()
    }

    /**
     * Actualiza la visualización de los valores en los steppers de reps y weight.
     */
    private fun updateStepperDisplay() {
        expandedView?.findViewById<TextView>(R.id.txt_reps_value)?.text = "$currentReps"
        val weightText = if (currentWeight == currentWeight.toLong().toDouble()) {
            "${currentWeight.toLong()}"
        } else {
            "%.1f".format(currentWeight)
        }
        expandedView?.findViewById<TextView>(R.id.txt_weight_value)?.text = weightText
    }

    /**
     * Adapta el layout del panel según el ancho actual.
     * Si el ancho es estrecho, apila los steppers verticalmente.
     *
     * @param widthDp Ancho actual del panel en dp
     */
    private fun adaptLayoutToWidth(widthDp: Int) {
        val stepperRow = expandedView?.findViewById<LinearLayout>(R.id.stepper_row) ?: return
        val isCompact = widthDp < COMPACT_WIDTH_THRESHOLD

        stepperRow.orientation = if (isCompact) {
            LinearLayout.VERTICAL
        } else {
            LinearLayout.HORIZONTAL
        }

        // Ajustar márgenes de los steppers hijos
        for (i in 0 until stepperRow.childCount) {
            val child = stepperRow.getChildAt(i)
            val lp = child.layoutParams as? LinearLayout.LayoutParams ?: continue
            if (isCompact) {
                lp.width = LinearLayout.LayoutParams.MATCH_PARENT
                lp.weight = 0f
                lp.marginStart = 0
                lp.marginEnd = 0
                lp.bottomMargin = dpToPx(8f)
            } else {
                lp.width = 0
                lp.weight = 1f
                lp.bottomMargin = 0
                if (i == 0) { lp.marginEnd = dpToPx(8f); lp.marginStart = 0 }
                else { lp.marginStart = dpToPx(8f); lp.marginEnd = 0 }
            }
            child.layoutParams = lp
        }
    }

    /**
     * Actualiza el texto de progreso de serie (ej: "Set 3 / 4").
     */
    private fun updateSetProgress() {
        val view = expandedView ?: return
        val index = ActiveWorkoutHolder.selectedExerciseIndex
        val sets = ActiveWorkoutHolder.getSetsForExercise(index)
        val currentSet = sets.size + 1
        view.findViewById<TextView>(R.id.txt_set_progress)?.text = "Serie $currentSet"
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
     * en todas las direcciones.
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
            adaptLayoutToWidth(expandedWidth)
        }

        expandedView?.findViewById<View>(R.id.resize_right)?.setOnTouchListener(
            createEdgeResizeListener(params) { dx, _, initW, _, _, _ ->
                params.width = initW + dx
                clampAndUpdate()
            }
        )

        expandedView?.findViewById<View>(R.id.resize_left)?.setOnTouchListener(
            createEdgeResizeListener(params) { dx, _, initW, _, initX, _ ->
                val newW = (initW - dx).coerceIn(minW, maxW)
                params.x = initX + (initW - newW)
                params.width = newW
                clampAndUpdate()
            }
        )

        expandedView?.findViewById<View>(R.id.resize_bottom)?.setOnTouchListener(
            createEdgeResizeListener(params) { _, dy, _, initH, _, _ ->
                params.height = initH + dy
                clampAndUpdate()
            }
        )

        expandedView?.findViewById<View>(R.id.resize_top)?.setOnTouchListener(
            createEdgeResizeListener(params) { _, dy, _, initH, _, initY ->
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
     * @param onDrag Lambda que recibe (dx, dy, initialWidth, initialHeight, initialX, initialY)
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

    // ── Stepper controls ────────────────────────────────────────────────

    /**
     * Configura los botones de stepper (+/-) para repeticiones y peso.
     * Incluye feedback visual con animación de escala al pulsar.
     */
    private fun setupStepperButtons() {
        val view = expandedView ?: return

        // Reps stepper
        view.findViewById<FrameLayout>(R.id.btn_reps_minus)?.setOnClickListener {
            animatePress(it)
            currentReps = (currentReps - 1).coerceAtLeast(1)
            updateStepperDisplay()
        }
        view.findViewById<FrameLayout>(R.id.btn_reps_plus)?.setOnClickListener {
            animatePress(it)
            currentReps++
            updateStepperDisplay()
        }

        // Weight stepper
        view.findViewById<FrameLayout>(R.id.btn_weight_minus)?.setOnClickListener {
            animatePress(it)
            currentWeight = (currentWeight - weightStep).coerceAtLeast(0.0)
            updateStepperDisplay()
        }
        view.findViewById<FrameLayout>(R.id.btn_weight_plus)?.setOnClickListener {
            animatePress(it)
            currentWeight += weightStep
            updateStepperDisplay()
        }
    }

    /**
     * Aplica una animación de escala pulsante al presionar un botón.
     *
     * @param view Vista a animar
     */
    private fun animatePress(view: View) {
        view.animate()
            .scaleX(0.85f).scaleY(0.85f)
            .setDuration(80)
            .withEndAction {
                view.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(120)
                    .setInterpolator(OvershootInterpolator(2f))
                    .start()
            }
            .start()
    }

    // ── Exercise chips ──────────────────────────────────────────────────

    /**
     * Crea los chips de ejercicio horizontales dinámicamente.
     * El chip activo tiene fondo primaryContainer, los inactivos surfaceContainerHigh.
     * Al tocar un chip, cambia la selección de ejercicio y actualiza los steppers.
     */
    private fun setupExerciseChips() {
        val view = expandedView ?: return
        val container = view.findViewById<LinearLayout>(R.id.exercise_chips_container) ?: return
        container.removeAllViews()

        val exerciseNames = ActiveWorkoutHolder.getExerciseNames()
        if (exerciseNames.isEmpty()) return

        val currentIndex = ActiveWorkoutHolder.selectedExerciseIndex
            .coerceIn(0, exerciseNames.size - 1)

        exerciseNames.forEachIndexed { index, name ->
            val chip = TextView(this).apply {
                text = name
                textSize = 12f
                setPadding(dpToPx(12f), dpToPx(6f), dpToPx(12f), dpToPx(6f))
                isSingleLine = true

                if (index == currentIndex) {
                    setBackgroundResource(R.drawable.exercise_chip_active_bg)
                    setTextColor(Color.parseColor(COLOR_ON_PRIMARY_CONTAINER))
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                } else {
                    setBackgroundResource(R.drawable.exercise_chip_bg)
                    setTextColor(Color.parseColor(COLOR_ON_SURFACE_VARIANT))
                    typeface = android.graphics.Typeface.DEFAULT
                }

                setOnClickListener {
                    animatePress(this)
                    ActiveWorkoutHolder.selectedExerciseIndex = index
                    setupExerciseChips()
                    updateExerciseDisplay()
                    initializeStepperValues()
                    updateSetProgress()
                }
            }

            val chipParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = dpToPx(8f)
            }
            container.addView(chip, chipParams)
        }

        // Scroll to active chip
        val scrollView = container.parent as? HorizontalScrollView
        handler.post {
            if (currentIndex > 0 && container.childCount > currentIndex) {
                val targetChip = container.getChildAt(currentIndex)
                scrollView?.smoothScrollTo(
                    (targetChip.left - dpToPx(20f)).coerceAtLeast(0), 0
                )
            }
        }

        updateExerciseDisplay()
    }

    /**
     * Actualiza el nombre del ejercicio y el progreso de serie en el panel.
     */
    private fun updateExerciseDisplay() {
        val view = expandedView ?: return
        val names = ActiveWorkoutHolder.getExerciseNames()
        val index = ActiveWorkoutHolder.selectedExerciseIndex
            .coerceIn(0, (names.size - 1).coerceAtLeast(0))

        view.findViewById<TextView>(R.id.exercise_name)?.text =
            if (names.isNotEmpty()) names[index] else "Sin ejercicios"
    }

    // ── Buttons ─────────────────────────────────────────────────────────

    /**
     * Configura los listeners de los botones del panel expandido:
     * minimizar, cerrar overlay, enviar set y abrir pantalla completa.
     */
    private fun setupButtons() {
        val view = expandedView ?: return

        // Minimize → collapse to bubble with smooth exit
        view.findViewById<ImageButton>(R.id.btn_minimize)?.setOnClickListener {
            animatePress(it)
            expandedView?.animate()
                ?.scaleX(0.85f)?.scaleY(0.85f)
                ?.alpha(0f)
                ?.setDuration(200)
                ?.setInterpolator(AccelerateInterpolator(2f))
                ?.withEndAction {
                    removeExpanded()
                    showBubble()
                }
                ?.start()
        }

        // Close overlay → minimize to bubble, keep service alive for the active workout
        view.findViewById<ImageButton>(R.id.btn_close)?.setOnClickListener {
            animatePress(it)
            expandedView?.animate()
                ?.scaleX(0.85f)?.scaleY(0.85f)
                ?.alpha(0f)
                ?.setDuration(200)
                ?.setInterpolator(AccelerateInterpolator(2f))
                ?.withEndAction {
                    removeExpanded()
                    isExpanded = false
                    showBubble()
                }
                ?.start()
        }

        // Log Set
        view.findViewById<FrameLayout>(R.id.btn_send)?.setOnClickListener {
            animatePress(it)
            sendSet()
        }

        // Fullscreen → animate out then resume app
        view.findViewById<ImageButton>(R.id.btn_fullscreen)?.setOnClickListener {
            animatePress(it)
            expandedView?.animate()
                ?.scaleX(1.05f)?.scaleY(1.05f)
                ?.alpha(0f)
                ?.setDuration(200)
                ?.setInterpolator(AccelerateInterpolator(1.5f))
                ?.withEndAction {
                    val intent = Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(intent)
                    removeExpanded()
                    showBubble()
                }
                ?.start()
        }
    }

    /**
     * Recoge los datos de los steppers y crea un nuevo set en el entrenamiento activo.
     * Muestra feedback visual con el resultado.
     */
    private fun sendSet() {
        val view = expandedView ?: return
        val statusText = view.findViewById<TextView>(R.id.txt_status)

        val selectedIndex = ActiveWorkoutHolder.selectedExerciseIndex
        val exercise = ActiveWorkoutHolder.getExerciseAt(selectedIndex)

        if (exercise == null) {
            showStatus(statusText, "No hay ejercicio seleccionado", isError = true)
            return
        }

        if (currentReps <= 0) {
            showStatus(statusText, "Introduce las repeticiones", isError = true)
            return
        }

        val workout = ActiveWorkoutHolder.activeWorkout ?: return

        val newSet = SetModel(
            weight = currentWeight,
            reps = currentReps,
            date = Date.from(Instant.now()),
            observations = "",
            exercise = exercise,
            workoutDone = workout
        )

        ActiveWorkoutHolder.onSetAdded?.invoke(newSet)
        ActiveWorkoutHolder.recordSetAdded(selectedIndex)

        // Success feedback
        val weightStr = if (currentWeight > 0) " × ${currentWeight}kg" else ""
        showStatus(statusText, "✓ ${exercise.name} — ${currentReps} reps$weightStr", isError = false)

        // Flash the LOG SET button
        val sendBtn = view.findViewById<FrameLayout>(R.id.btn_send)
        sendBtn?.animate()
            ?.scaleX(1.05f)?.scaleY(1.05f)
            ?.setDuration(100)
            ?.withEndAction {
                sendBtn.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(150)
                    .start()
            }
            ?.start()

        // Update chips and stats
        setupExerciseChips()
        updateSetProgress()
        updateFooterStats()
    }

    /**
     * Muestra un mensaje de estado con animación de fade, que se oculta
     * automáticamente después de 3 segundos.
     *
     * @param textView TextView de estado
     * @param message Mensaje a mostrar
     * @param isError Si true, muestra en color error; si false, en tertiary
     */
    private fun showStatus(textView: TextView?, message: String, isError: Boolean) {
        textView ?: return
        textView.text = message
        textView.setTextColor(
            Color.parseColor(if (isError) COLOR_ERROR else COLOR_TERTIARY)
        )
        textView.alpha = 0f
        textView.visibility = View.VISIBLE
        textView.animate()
            .alpha(1f)
            .setDuration(200)
            .start()

        handler.postDelayed({
            textView.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction { textView.visibility = View.GONE }
                .start()
        }, 3000)
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
