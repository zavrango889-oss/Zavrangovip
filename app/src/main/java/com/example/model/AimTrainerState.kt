package com.example.model

data class AimTrainerState(
    val isGameActive: Boolean = false,
    val isGameOver: Boolean = false,
    val targetNormX: Float = 0.5f,
    val targetNormY: Float = 0.4f,
    val targetVelocityX: Float = 0.012f,
    val targetVelocityY: Float = 0.009f,
    val targetRadiusDp: Float = 44f,
    val score: Int = 0,
    val headshots: Int = 0,
    val bodyHits: Int = 0,
    val totalShots: Int = 0,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    val remainingSeconds: Int = 30,
    val highScore: Int = 0,
    val lastHitFeedback: HitFeedback? = null
) {
    val accuracyPct: Int
        get() = if (totalShots > 0) {
            (((headshots + bodyHits).toDouble() / totalShots) * 100).toInt().coerceIn(0, 100)
        } else 0
}

enum class HitType {
    HEADSHOT,
    BODY,
    MISS
}

data class HitFeedback(
    val type: HitType,
    val text: String,
    val points: Int,
    val timestamp: Long = System.currentTimeMillis()
)
