package com.example.ui.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.model.CrosshairColor
import com.example.model.CrosshairState
import com.example.model.CrosshairStyle
import kotlin.math.roundToInt

class CrosshairOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var state: CrosshairState = CrosshairState(isEnabled = true)

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = 0x88000000.toInt()
        strokeCap = Paint.Cap.ROUND
    }

    private val density = context.resources.displayMetrics.density

    fun updateState(newState: CrosshairState) {
        this.state = newState
        val baseColor = newState.color.argb.toInt()
        val alphaInt = (newState.opacity.coerceIn(0.1f, 1f) * 255).roundToInt()
        val colorWithAlpha = (baseColor and 0x00FFFFFF) or (alphaInt shl 24)

        fillPaint.color = colorWithAlpha
        strokePaint.color = colorWithAlpha
        strokePaint.strokeWidth = (2.2f * density).coerceAtLeast(2f)
        shadowPaint.strokeWidth = strokePaint.strokeWidth + 2f * density

        visibility = if (newState.isEnabled) VISIBLE else GONE
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!state.isEnabled) return

        val cx = width / 2f
        val cy = height / 2f
        val sizePx = state.sizeDp * density
        val half = sizePx / 2f

        when (state.style) {
            CrosshairStyle.DOT -> {
                val dotRadius = (half * 0.45f).coerceIn(3f * density, 12f * density)
                // Drop shadow
                canvas.drawCircle(cx, cy, dotRadius + 1f * density, shadowPaint)
                canvas.drawCircle(cx, cy, dotRadius, fillPaint)
            }
            CrosshairStyle.CIRCLE -> {
                val radius = half * 0.75f
                val centerDot = (2.5f * density)
                // Outline
                canvas.drawCircle(cx, cy, radius, shadowPaint)
                canvas.drawCircle(cx, cy, radius, strokePaint)
                // Center micro dot
                canvas.drawCircle(cx, cy, centerDot, fillPaint)
            }
            CrosshairStyle.TARGET -> {
                val radius = half * 0.65f
                val lineLen = half * 0.45f
                val centerDot = (2f * density)

                // Shadow
                canvas.drawCircle(cx, cy, radius, shadowPaint)
                canvas.drawLine(cx - radius - lineLen, cy, cx - radius, cy, shadowPaint)
                canvas.drawLine(cx + radius, cy, cx + radius + lineLen, cy, shadowPaint)
                canvas.drawLine(cx, cy - radius - lineLen, cx, cy - radius, shadowPaint)
                canvas.drawLine(cx, cy + radius, cx, cy + radius + lineLen, shadowPaint)

                // Main circle & ticks
                canvas.drawCircle(cx, cy, radius, strokePaint)
                canvas.drawLine(cx - radius - lineLen, cy, cx - radius, cy, strokePaint)
                canvas.drawLine(cx + radius, cy, cx + radius + lineLen, cy, strokePaint)
                canvas.drawLine(cx, cy - radius - lineLen, cx, cy - radius, strokePaint)
                canvas.drawLine(cx, cy + radius, cx, cy + radius + lineLen, strokePaint)
                canvas.drawCircle(cx, cy, centerDot, fillPaint)
            }
            CrosshairStyle.CROSS -> {
                val gap = (half * 0.28f).coerceAtLeast(3f * density)
                val lineLen = half * 0.72f

                // Drop shadow
                canvas.drawLine(cx - gap - lineLen, cy, cx - gap, cy, shadowPaint)
                canvas.drawLine(cx + gap, cy, cx + gap + lineLen, cy, shadowPaint)
                canvas.drawLine(cx, cy - gap - lineLen, cx, cy - gap, shadowPaint)
                canvas.drawLine(cx, cy + gap, cx, cy + gap + lineLen, shadowPaint)

                // Ticks
                canvas.drawLine(cx - gap - lineLen, cy, cx - gap, cy, strokePaint)
                canvas.drawLine(cx + gap, cy, cx + gap + lineLen, cy, strokePaint)
                canvas.drawLine(cx, cy - gap - lineLen, cx, cy - gap, strokePaint)
                canvas.drawLine(cx, cy + gap, cx, cy + gap + lineLen, strokePaint)
            }
            CrosshairStyle.SQUARE -> {
                val bracketSize = half * 0.75f
                val cornerLen = bracketSize * 0.38f
                val gap = (half * 0.22f).coerceAtLeast(2.5f * density)
                val tickLen = half * 0.35f

                // 4 corners [ ]
                val left = cx - bracketSize
                val right = cx + bracketSize
                val top = cy - bracketSize
                val bottom = cy + bracketSize

                // Top-left corner
                canvas.drawLine(left, top, left + cornerLen, top, strokePaint)
                canvas.drawLine(left, top, left, top + cornerLen, strokePaint)
                // Top-right corner
                canvas.drawLine(right - cornerLen, top, right, top, strokePaint)
                canvas.drawLine(right, top, right, top + cornerLen, strokePaint)
                // Bottom-left corner
                canvas.drawLine(left, bottom, left + cornerLen, bottom, strokePaint)
                canvas.drawLine(left, bottom - cornerLen, left, bottom, strokePaint)
                // Bottom-right corner
                canvas.drawLine(right - cornerLen, bottom, right, bottom, strokePaint)
                canvas.drawLine(right, bottom - cornerLen, right, bottom, strokePaint)

                // Inner cross ticks
                canvas.drawLine(cx - gap - tickLen, cy, cx - gap, cy, strokePaint)
                canvas.drawLine(cx + gap, cy, cx + gap + tickLen, cy, strokePaint)
                canvas.drawLine(cx, cy - gap - tickLen, cx, cy - gap, strokePaint)
                canvas.drawLine(cx, cy + gap, cx, cy + gap + tickLen, strokePaint)
            }
        }
    }
}
