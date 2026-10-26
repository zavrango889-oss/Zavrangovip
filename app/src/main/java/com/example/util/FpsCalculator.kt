package com.example.util

import android.view.Choreographer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FpsCalculator {

    private val _currentFps = MutableStateFlow(60)
    val currentFps: StateFlow<Int> = _currentFps.asStateFlow()

    private var isRunning = false
    private val frameTimestamps = ArrayDeque<Long>()
    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (!isRunning) return

            frameTimestamps.addLast(frameTimeNanos)

            // Keep timestamps within the last 1 second (1_000_000_000 nanos)
            val oneSecAgo = frameTimeNanos - 1_000_000_000L
            while (frameTimestamps.isNotEmpty() && frameTimestamps.first() < oneSecAgo) {
                frameTimestamps.removeFirst()
            }

            _currentFps.value = frameTimestamps.size

            if (isRunning) {
                Choreographer.getInstance().postFrameCallback(this)
            }
        }
    }

    fun start() {
        if (isRunning) return
        isRunning = true
        frameTimestamps.clear()
        Choreographer.getInstance().postFrameCallback(frameCallback)
    }

    fun stop() {
        isRunning = false
        frameTimestamps.clear()
        Choreographer.getInstance().removeFrameCallback(frameCallback)
    }
}
