package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.model.CrosshairColor
import com.example.model.CrosshairStyle

@Composable
fun CrosshairPreview(
    style: CrosshairStyle,
    color: CrosshairColor,
    sizeDp: Float,
    opacity: Float,
    modifier: Modifier = Modifier
) {
    val drawColor = Color(color.argb).copy(alpha = opacity.coerceIn(0.15f, 1f))
    val shadowColor = Color.Black.copy(alpha = 0.5f * opacity)

    Canvas(
        modifier = modifier.size(sizeDp.coerceIn(20f, 90f).dp)
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val half = size.width / 2f
        val strokeW = 2.4.dp.toPx()

        when (style) {
            CrosshairStyle.DOT -> {
                val dotRadius = (half * 0.42f).coerceIn(3.dp.toPx(), 12.dp.toPx())
                drawCircle(shadowColor, radius = dotRadius + 1.2.dp.toPx(), center = Offset(cx, cy))
                drawCircle(drawColor, radius = dotRadius, center = Offset(cx, cy))
            }
            CrosshairStyle.CIRCLE -> {
                val radius = half * 0.72f
                val dotRadius = 2.5.dp.toPx()
                drawCircle(shadowColor, radius = radius, center = Offset(cx, cy), style = Stroke(width = strokeW + 1.5.dp.toPx()))
                drawCircle(drawColor, radius = radius, center = Offset(cx, cy), style = Stroke(width = strokeW))
                drawCircle(drawColor, radius = dotRadius, center = Offset(cx, cy))
            }
            CrosshairStyle.TARGET -> {
                val radius = half * 0.65f
                val lineLen = half * 0.45f
                val dotRadius = 2.2.dp.toPx()

                // Circle
                drawCircle(shadowColor, radius = radius, center = Offset(cx, cy), style = Stroke(width = strokeW + 1.5.dp.toPx()))
                drawCircle(drawColor, radius = radius, center = Offset(cx, cy), style = Stroke(width = strokeW))

                // Ticks with shadow
                drawTick(cx - radius - lineLen, cy, cx - radius, cy, shadowColor, strokeW + 1.5.dp.toPx())
                drawTick(cx + radius, cy, cx + radius + lineLen, cy, shadowColor, strokeW + 1.5.dp.toPx())
                drawTick(cx, cy - radius - lineLen, cx, cy - radius, shadowColor, strokeW + 1.5.dp.toPx())
                drawTick(cx, cy + radius, cx, cy + radius + lineLen, shadowColor, strokeW + 1.5.dp.toPx())

                drawTick(cx - radius - lineLen, cy, cx - radius, cy, drawColor, strokeW)
                drawTick(cx + radius, cy, cx + radius + lineLen, cy, drawColor, strokeW)
                drawTick(cx, cy - radius - lineLen, cx, cy - radius, drawColor, strokeW)
                drawTick(cx, cy + radius, cx, cy + radius + lineLen, drawColor, strokeW)

                drawCircle(drawColor, radius = dotRadius, center = Offset(cx, cy))
            }
            CrosshairStyle.CROSS -> {
                val gap = (half * 0.28f).coerceAtLeast(3.dp.toPx())
                val lineLen = half * 0.72f

                // Drop shadows
                drawTick(cx - gap - lineLen, cy, cx - gap, cy, shadowColor, strokeW + 1.5.dp.toPx())
                drawTick(cx + gap, cy, cx + gap + lineLen, cy, shadowColor, strokeW + 1.5.dp.toPx())
                drawTick(cx, cy - gap - lineLen, cx, cy - gap, shadowColor, strokeW + 1.5.dp.toPx())
                drawTick(cx, cy + gap, cx, cy + gap + lineLen, shadowColor, strokeW + 1.5.dp.toPx())

                // Cross lines
                drawTick(cx - gap - lineLen, cy, cx - gap, cy, drawColor, strokeW)
                drawTick(cx + gap, cy, cx + gap + lineLen, cy, drawColor, strokeW)
                drawTick(cx, cy - gap - lineLen, cx, cy - gap, drawColor, strokeW)
                drawTick(cx, cy + gap, cx, cy + gap + lineLen, drawColor, strokeW)
            }
            CrosshairStyle.SQUARE -> {
                val box = half * 0.72f
                val corner = box * 0.38f
                val gap = (half * 0.22f).coerceAtLeast(2.5.dp.toPx())
                val tickLen = half * 0.35f

                val left = cx - box
                val right = cx + box
                val top = cy - box
                val bottom = cy + box

                // Corners
                drawCorner(left, top, corner, true, true, drawColor, strokeW)
                drawCorner(right, top, corner, false, true, drawColor, strokeW)
                drawCorner(left, bottom, corner, true, false, drawColor, strokeW)
                drawCorner(right, bottom, corner, false, false, drawColor, strokeW)

                // Inner cross ticks
                drawTick(cx - gap - tickLen, cy, cx - gap, cy, drawColor, strokeW)
                drawTick(cx + gap, cy, cx + gap + tickLen, cy, drawColor, strokeW)
                drawTick(cx, cy - gap - tickLen, cx, cy - gap, drawColor, strokeW)
                drawTick(cx, cy + gap, cx, cy + gap + tickLen, drawColor, strokeW)
            }
        }
    }
}

private fun DrawScope.drawTick(x1: Float, y1: Float, x2: Float, y2: Float, color: Color, strokeWidth: Float) {
    drawLine(
        color = color,
        start = Offset(x1, y1),
        end = Offset(x2, y2),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawCorner(
    px: Float,
    py: Float,
    len: Float,
    isLeft: Boolean,
    isTop: Boolean,
    color: Color,
    strokeWidth: Float
) {
    val hEnd = if (isLeft) px + len else px - len
    val vEnd = if (isTop) py + len else py - len

    drawLine(color = color, start = Offset(px, py), end = Offset(hEnd, py), strokeWidth = strokeWidth, cap = StrokeCap.Round)
    drawLine(color = color, start = Offset(px, py), end = Offset(px, vEnd), strokeWidth = strokeWidth, cap = StrokeCap.Round)
}
