package com.example.service

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
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.CrosshairPreferencesRepository
import com.example.model.CrosshairColor
import com.example.model.CrosshairState
import com.example.model.CrosshairStyle
import com.example.ui.overlay.CrosshairOverlayView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class CrosshairOverlayService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private lateinit var windowManager: WindowManager
    private lateinit var repo: CrosshairPreferencesRepository

    private var crosshairView: CrosshairOverlayView? = null
    private var crosshairParams: WindowManager.LayoutParams? = null

    private var panelView: View? = null
    private var panelParams: WindowManager.LayoutParams? = null

    private var bubbleView: View? = null
    private var bubbleParams: WindowManager.LayoutParams? = null

    private var currentState: CrosshairState = CrosshairState()
    private val density get() = resources.displayMetrics.density

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        repo = CrosshairPreferencesRepository.getInstance(this)
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())

        if (Settings.canDrawOverlays(this)) {
            setupCrosshairView()
            setupFloatingPanel()
            setupBubbleView()
            observeState()
        } else {
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                repo.updateCrosshairEnabled(false)
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_TOGGLE_PANEL -> {
                val next = !currentState.isPanelOpen
                repo.updatePanelOpen(next)
            }
        }
        return START_STICKY
    }

    private fun observeState() {
        serviceScope.launch {
            repo.crosshairState.collectLatest { state ->
                currentState = state
                updateViews(state)
            }
        }
    }

    private fun setupCrosshairView() {
        val sizePx = (80 * density).roundToInt()
        crosshairParams = WindowManager.LayoutParams(
            sizePx,
            sizePx,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
            x = 0
            y = 0
        }

        crosshairView = CrosshairOverlayView(this).apply {
            updateState(repo.crosshairState.value)
        }

        try {
            windowManager.addView(crosshairView, crosshairParams)
        } catch (_: Exception) {}
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupFloatingPanel() {
        val panelWidth = (280 * density).roundToInt()
        panelParams = WindowManager.LayoutParams(
            panelWidth,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = (24 * density).roundToInt()
            y = (120 * density).roundToInt()
        }

        panelView = buildPanelLayout()

        try {
            windowManager.addView(panelView, panelParams)
        } catch (_: Exception) {}
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupBubbleView() {
        val bubbleSize = (46 * density).roundToInt()
        bubbleParams = WindowManager.LayoutParams(
            bubbleSize,
            bubbleSize,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = (12 * density).roundToInt()
            y = (200 * density).roundToInt()
        }

        val frame = FrameLayout(this).apply {
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#141E33"))
                setStroke((1.5f * density).roundToInt(), Color.parseColor("#00F0FF"))
            }
            val icon = ImageView(context).apply {
                setImageResource(android.R.drawable.ic_menu_compass)
                setColorFilter(Color.parseColor("#00F0FF"))
                val pad = (10 * density).roundToInt()
                setPadding(pad, pad, pad, pad)
            }
            addView(icon, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        }

        var initialX = 0
        var initialY = 0
        var touchStartX = 0f
        var touchStartY = 0f
        var isClick = true

        frame.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = bubbleParams?.x ?: 0
                    initialY = bubbleParams?.y ?: 0
                    touchStartX = event.rawX
                    touchStartY = event.rawY
                    isClick = true
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - touchStartX).toInt()
                    val dy = (event.rawY - touchStartY).toInt()
                    if (Math.abs(dx) > 10 || Math.abs(dy) > 10) isClick = false
                    bubbleParams?.x = initialX + dx
                    bubbleParams?.y = initialY + dy
                    windowManager.updateViewLayout(bubbleView, bubbleParams)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (isClick) {
                        repo.updateQuickHidden(false)
                        repo.updatePanelOpen(true)
                    }
                    true
                }
                else -> false
            }
        }

        bubbleView = frame
        bubbleView?.visibility = View.GONE
        try {
            windowManager.addView(bubbleView, bubbleParams)
        } catch (_: Exception) {}
    }

    private fun dp(value: Int): Int = (value * density).roundToInt()

    @SuppressLint("ClickableViewAccessibility")
    private fun buildPanelLayout(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val bg = GradientDrawable().apply {
                setColor(Color.parseColor("#EE0B1220"))
                cornerRadius = 18f * density
                setStroke((1.2f * density).roundToInt(), Color.parseColor("#263B5E"))
            }
            background = bg
            setPadding(dp(16), dp(12), dp(16), dp(14))
        }

        // --- Header Row ---
        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, dp(10))
        }

        val titleView = TextView(this).apply {
            text = "Aiming Assist"
            setTextColor(Color.WHITE)
            textSize = 15f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        val onOffSwitch = Switch(this).apply {
            isChecked = repo.crosshairState.value.isEnabled
            setOnCheckedChangeListener { _, isChecked ->
                repo.updateCrosshairEnabled(isChecked)
            }
        }

        val closeBtn = TextView(this).apply {
            text = "✕"
            setTextColor(Color.parseColor("#94A3B8"))
            textSize = 16f
            setPadding(dp(10), dp(4), 0, dp(4))
            setOnClickListener {
                repo.updatePanelOpen(false)
            }
        }

        headerRow.addView(titleView)
        headerRow.addView(onOffSwitch)
        headerRow.addView(closeBtn)

        // Drag panel on header
        var initialX = 0
        var initialY = 0
        var touchStartX = 0f
        var touchStartY = 0f

        headerRow.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = panelParams?.x ?: 0
                    initialY = panelParams?.y ?: 0
                    touchStartX = event.rawX
                    touchStartY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - touchStartX).toInt()
                    val dy = (event.rawY - touchStartY).toInt()
                    panelParams?.x = (initialX + dx).coerceAtLeast(0)
                    panelParams?.y = (initialY + dy).coerceAtLeast(0)
                    windowManager.updateViewLayout(panelView, panelParams)
                    true
                }
                else -> false
            }
        }

        root.addView(headerRow)

        // --- Quick Hide Row ---
        val quickHideRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(4), 0, dp(8))
        }
        val quickHideLabel = TextView(this).apply {
            text = "Quick Hide  ⓘ"
            setTextColor(Color.parseColor("#CBD5E1"))
            textSize = 13f
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        val quickHideSwitch = Switch(this).apply {
            isChecked = repo.crosshairState.value.isQuickHidden
            setOnCheckedChangeListener { _, isChecked ->
                repo.updateQuickHidden(isChecked)
            }
        }
        quickHideRow.addView(quickHideLabel)
        quickHideRow.addView(quickHideSwitch)
        root.addView(quickHideRow)

        // --- Style Section ---
        val styleLabel = TextView(this).apply {
            text = "Style"
            setTextColor(Color.parseColor("#94A3B8"))
            textSize = 12f
            setPadding(0, dp(2), 0, dp(6))
        }
        root.addView(styleLabel)

        val styleButtonsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, dp(10))
        }

        val styles = listOf(
            CrosshairStyle.DOT to "•",
            CrosshairStyle.CIRCLE to "⊙",
            CrosshairStyle.TARGET to "⌖",
            CrosshairStyle.CROSS to "┼",
            CrosshairStyle.SQUARE to "[+]"
        )

        val styleViews = mutableListOf<TextView>()
        styles.forEach { (style, symbol) ->
            val btn = TextView(this).apply {
                text = symbol
                gravity = Gravity.CENTER
                textSize = 16f
                val btnParams = LinearLayout.LayoutParams(dp(44), dp(44)).apply {
                    marginEnd = dp(8)
                }
                layoutParams = btnParams

                setOnClickListener {
                    repo.updateStyle(style)
                    styleViews.forEachIndexed { idx, v ->
                        val isSel = styles[idx].first == style
                        updateStyleButtonBorder(v, isSel)
                    }
                }
            }
            updateStyleButtonBorder(btn, style == repo.crosshairState.value.style)
            styleViews.add(btn)
            styleButtonsRow.addView(btn)
        }
        root.addView(styleButtonsRow)

        // --- Colour Section ---
        val colourLabel = TextView(this).apply {
            text = "Colour"
            setTextColor(Color.parseColor("#94A3B8"))
            textSize = 12f
            setPadding(0, dp(2), 0, dp(6))
        }
        root.addView(colourLabel)

        val colourRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, dp(12))
        }

        val colors = listOf(
            CrosshairColor.WHITE,
            CrosshairColor.RED,
            CrosshairColor.YELLOW,
            CrosshairColor.GREEN,
            CrosshairColor.CYAN
        )

        val colorViews = mutableListOf<View>()
        colors.forEach { c ->
            val colorCircle = View(this).apply {
                val circleParams = LinearLayout.LayoutParams(dp(36), dp(36)).apply {
                    marginEnd = dp(12)
                }
                layoutParams = circleParams
                setOnClickListener {
                    repo.updateColor(c)
                    colorViews.forEachIndexed { idx, v ->
                        val isSel = colors[idx] == c
                        updateColorCircleBorder(v, colors[idx], isSel)
                    }
                }
            }
            updateColorCircleBorder(colorCircle, c, c == repo.crosshairState.value.color)
            colorViews.add(colorCircle)
            colourRow.addView(colorCircle)
        }
        root.addView(colourRow)

        // --- Size Slider ---
        val sizeLabel = TextView(this).apply {
            text = "Size"
            setTextColor(Color.parseColor("#CBD5E1"))
            textSize = 13f
            setPadding(0, 0, 0, dp(2))
        }
        root.addView(sizeLabel)

        val sizeSeekBar = SeekBar(this).apply {
            max = 66 // from 14dp to 80dp
            progress = (repo.crosshairState.value.sizeDp - 14f).roundToInt().coerceIn(0, 66)
            setPadding(0, dp(4), 0, dp(8))
            // Red accent progress matching Image 2
            progressTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#FF1744"))
            thumbTintList = android.content.res.ColorStateList.valueOf(Color.WHITE)

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        val newSize = 14f + progress.toFloat()
                        repo.updateSize(newSize)
                    }
                }
                override fun onStartTrackingTouch(sb: SeekBar?) {}
                override fun onStopTrackingTouch(sb: SeekBar?) {}
            })
        }
        root.addView(sizeSeekBar)

        // --- Opacity Slider ---
        val opacityLabel = TextView(this).apply {
            text = "Opacity"
            setTextColor(Color.parseColor("#CBD5E1"))
            textSize = 13f
            setPadding(0, dp(4), 0, dp(2))
        }
        root.addView(opacityLabel)

        val opacitySeekBar = SeekBar(this).apply {
            max = 85 // from 15% to 100%
            progress = ((repo.crosshairState.value.opacity - 0.15f) * 100).roundToInt().coerceIn(0, 85)
            setPadding(0, dp(4), 0, dp(6))
            progressTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#FF1744"))
            thumbTintList = android.content.res.ColorStateList.valueOf(Color.WHITE)

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        val newOp = 0.15f + (progress.toFloat() / 100f)
                        repo.updateOpacity(newOp)
                    }
                }
                override fun onStartTrackingTouch(sb: SeekBar?) {}
                override fun onStopTrackingTouch(sb: SeekBar?) {}
            })
        }
        root.addView(opacitySeekBar)

        return root
    }

    private fun updateStyleButtonBorder(view: TextView, isSelected: Boolean) {
        val bg = GradientDrawable().apply {
            cornerRadius = 8f * density
            setColor(Color.parseColor(if (isSelected) "#22000000" else "#0F1A2E"))
            val strokeColor = if (isSelected) Color.parseColor("#FF1744") else Color.parseColor("#1E3250")
            val strokeW = if (isSelected) (2f * density).roundToInt() else (1f * density).roundToInt()
            setStroke(strokeW, strokeColor)
        }
        view.background = bg
        view.setTextColor(if (isSelected) Color.parseColor("#FF1744") else Color.WHITE)
    }

    private fun updateColorCircleBorder(view: View, color: CrosshairColor, isSelected: Boolean) {
        val bg = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color.argb.toInt())
            if (isSelected) {
                setStroke((3f * density).roundToInt(), Color.WHITE)
            } else {
                setStroke((1f * density).roundToInt(), Color.parseColor("#334155"))
            }
        }
        view.background = bg
    }

    private fun updateViews(state: CrosshairState) {
        crosshairView?.updateState(state)

        if (!state.isEnabled) {
            crosshairView?.visibility = View.GONE
            panelView?.visibility = View.GONE
            bubbleView?.visibility = View.GONE
            return
        }

        crosshairView?.visibility = View.VISIBLE

        if (state.isQuickHidden) {
            panelView?.visibility = View.GONE
            bubbleView?.visibility = View.VISIBLE
        } else {
            bubbleView?.visibility = View.GONE
            panelView?.visibility = if (state.isPanelOpen) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.launch {
            try { crosshairView?.let { windowManager.removeView(it) } } catch (_: Exception) {}
            try { panelView?.let { windowManager.removeView(it) } } catch (_: Exception) {}
            try { bubbleView?.let { windowManager.removeView(it) } } catch (_: Exception) {}
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "ZAVRANGOFFZ Overlay",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controle de Mira Flutuante ZAVRANGOFFZ"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val openIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val togglePanelIntent = PendingIntent.getService(
            this,
            1,
            Intent(this, CrosshairOverlayService::class.java).apply { action = ACTION_TOGGLE_PANEL },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = PendingIntent.getService(
            this,
            2,
            Intent(this, CrosshairOverlayService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ZAVRANGOFFZ — Mira Ativa")
            .setContentText("Auxílio de mira ativo sobre Free Fire MAX")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(openIntent)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_view, "Painel", togglePanelIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Desativar", stopIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    companion object {
        const val CHANNEL_ID = "zavrangoffz_overlay_channel"
        const val NOTIFICATION_ID = 2001
        const val ACTION_STOP = "com.example.action.STOP_CROSSHAIR"
        const val ACTION_TOGGLE_PANEL = "com.example.action.TOGGLE_PANEL"

        fun start(context: Context) {
            val intent = Intent(context, CrosshairOverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, CrosshairOverlayService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
