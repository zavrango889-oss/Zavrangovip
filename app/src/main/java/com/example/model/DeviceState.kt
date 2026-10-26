package com.example.model

data class MemoryInfoData(
    val totalRamBytes: Long = 0L,
    val availableRamBytes: Long = 0L,
    val isLowMemory: Boolean = false,
    val thresholdBytes: Long = 0L
) {
    val totalRamGb: Float get() = (totalRamBytes / (1024f * 1024f * 1024f))
    val availableRamGb: Float get() = (availableRamBytes / (1024f * 1024f * 1024f))
    val usedRamGb: Float get() = (totalRamGb - availableRamGb).coerceAtLeast(0f)
    val usedPercentage: Int get() = if (totalRamBytes > 0) {
        (((totalRamBytes - availableRamBytes).toDouble() / totalRamBytes) * 100).toInt().coerceIn(0, 100)
    } else 0
}

data class StorageInfoData(
    val totalStorageBytes: Long = 0L,
    val freeStorageBytes: Long = 0L
) {
    val totalStorageGb: Float get() = (totalStorageBytes / (1024f * 1024f * 1024f))
    val freeStorageGb: Float get() = (freeStorageBytes / (1024f * 1024f * 1024f))
    val usedStorageGb: Float get() = (totalStorageGb - freeStorageGb).coerceAtLeast(0f)
    val usedPercentage: Int get() = if (totalStorageBytes > 0) {
        (((totalStorageBytes - freeStorageBytes).toDouble() / totalStorageBytes) * 100).toInt().coerceIn(0, 100)
    } else 0
}

data class BatteryInfoData(
    val level: Int = 0,
    val temperatureCelsius: Float = 0f,
    val voltageVolts: Float = 0f,
    val health: String = "Good",
    val status: String = "Discharging",
    val isCharging: Boolean = false,
    val pluggedSource: String = "Battery"
) {
    val thermalStateCategory: String
        get() = when {
            temperatureCelsius <= 34f -> "Cool"
            temperatureCelsius <= 39f -> "Optimal"
            temperatureCelsius <= 44f -> "Warm"
            else -> "High Heat"
        }
}

data class DeviceInfoData(
    val model: String = "",
    val manufacturer: String = "",
    val deviceName: String = "",
    val androidVersion: String = "",
    val apiLevel: Int = 0,
    val isSamsungGalaxyM15: Boolean = false,
    val refreshRateHz: Int = 60,
    val supportedRefreshRates: List<Int> = listOf(60)
)

data class BoosterPreferences(
    val performanceMode: Boolean = true,
    val touchOptimization: Boolean = true,
    val precisionMode: Boolean = true,
    val protectionSystem: Boolean = true,
    val fpsBooster: Boolean = true,
    val autoDndOnLaunch: Boolean = false,
    val targetFps: Int = 90
)

enum class OptimizationStatus {
    SUCCESS,
    MANUAL_ACTION_RECOMMENDED,
    INFO_NOTICE
}

data class OptimizationResultItem(
    val id: String,
    val title: String,
    val description: String,
    val status: OptimizationStatus,
    val statusText: String,
    val actionIntentType: String? = null
)

data class ReactionTestState(
    val phase: ReactionPhase = ReactionPhase.IDLE,
    val startTimeMs: Long = 0L,
    val scoreMs: Long? = null,
    val message: String = "Tap 'Start Test' and tap screen as fast as you see NEON RED!"
)

enum class ReactionPhase {
    IDLE,
    WAITING_FOR_TRIGGER,
    READY_TO_TAP,
    RESULT,
    TOO_EARLY
}

data class RapidTapState(
    val isActive: Boolean = false,
    val remainingSeconds: Int = 5,
    val tapCount: Int = 0,
    val finalCps: Float = 0f
)
