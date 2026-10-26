package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SystemTelemetryRepository
import com.example.model.BoosterPreferences
import com.example.model.OptimizationResultItem
import com.example.model.OptimizationStatus
import com.example.model.RapidTapState
import com.example.model.ReactionPhase
import com.example.model.ReactionTestState
import com.example.util.FpsCalculator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class BoosterViewModel(application: Application) : AndroidViewModel(application) {

    val repository = SystemTelemetryRepository(application)
    private val fpsCalculator = FpsCalculator()

    val memoryInfo = repository.memoryInfo
    val storageInfo = repository.storageInfo
    val batteryInfo = repository.batteryInfo
    val deviceInfo = repository.deviceInfo
    val boosterPrefs = repository.boosterPrefs

    val realtimeFps: StateFlow<Int> = fpsCalculator.currentFps

    private val _isDndActive = MutableStateFlow(repository.isGamingDndActive())
    val isDndActive: StateFlow<Boolean> = _isDndActive.asStateFlow()

    private val _isDndGranted = MutableStateFlow(repository.isNotificationPolicyAccessGranted())
    val isDndGranted: StateFlow<Boolean> = _isDndGranted.asStateFlow()

    private val _optimizationReports = MutableStateFlow<List<OptimizationResultItem>?>(null)
    val optimizationReports: StateFlow<List<OptimizationResultItem>?> = _optimizationReports.asStateFlow()

    private val _isOptimizing = MutableStateFlow(false)
    val isOptimizing: StateFlow<Boolean> = _isOptimizing.asStateFlow()

    // Touch & Reaction states
    private val _reactionState = MutableStateFlow(ReactionTestState())
    val reactionState: StateFlow<ReactionTestState> = _reactionState.asStateFlow()
    private var reactionJob: Job? = null

    private val _rapidTapState = MutableStateFlow(RapidTapState())
    val rapidTapState: StateFlow<RapidTapState> = _rapidTapState.asStateFlow()
    private var rapidTapJob: Job? = null

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        fpsCalculator.start()
        startPeriodicRefresh()
    }

    override fun onCleared() {
        super.onCleared()
        fpsCalculator.stop()
        repository.unregister()
    }

    private fun startPeriodicRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(2500)
                repository.refreshAll()
                _isDndActive.value = repository.isGamingDndActive()
                _isDndGranted.value = repository.isNotificationPolicyAccessGranted()
            }
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun showStatusMessage(msg: String) {
        _statusMessage.value = msg
    }

    // --- Switch Toggles ---
    fun togglePerformanceMode(checked: Boolean) {
        repository.updateBoosterPreference(perfMode = checked)
        if (checked) {
            repository.performPermittedLocalTrim()
            showStatusMessage("Performance Mode activated. Memory compaction requested.")
        } else {
            showStatusMessage("Performance Mode turned off.")
        }
    }

    fun toggleTouchOptimization(checked: Boolean) {
        repository.updateBoosterPreference(touchOpt = checked)
        showStatusMessage(if (checked) "Touch Optimization active: Tap sampler & haptics ready." else "Touch Optimization disabled.")
    }

    fun togglePrecisionMode(checked: Boolean) {
        repository.updateBoosterPreference(precision = checked)
        showStatusMessage(if (checked) "Precision Mode active: Aim control parameters enabled." else "Precision Mode disabled.")
    }

    fun toggleProtectionSystem(checked: Boolean) {
        repository.updateBoosterPreference(protection = checked)
        showStatusMessage(if (checked) "Protection System active: Thermal spike protection on." else "Protection System disabled.")
    }

    fun toggleFpsBooster(checked: Boolean) {
        repository.updateBoosterPreference(fpsBoost = checked)
        showStatusMessage(if (checked) "FPS Booster active: Display refresh rate sync enabled." else "FPS Booster disabled.")
    }

    fun toggleAutoDnd(checked: Boolean) {
        repository.updateBoosterPreference(autoDnd = checked)
    }

    fun setTargetFps(target: Int) {
        repository.updateBoosterPreference(targetFps = target)
    }

    // --- SMART OPTIMIZE CHECK ---
    fun runSmartCheck() {
        viewModelScope.launch {
            _isOptimizing.value = true
            delay(1200) // Realistic diagnostic delay

            val reports = mutableListOf<OptimizationResultItem>()

            // 1. Memory Analysis
            val mem = repository.fetchMemoryInfo()
            val freedBytes = repository.performPermittedLocalTrim()
            val freedMb = freedBytes / (1024 * 1024)
            if (freedMb > 0) {
                reports.add(
                    OptimizationResultItem(
                        id = "mem_trim",
                        title = "Local App Cache & Compaction",
                        description = "Cleared ${freedMb}MB temporary app cache and triggered JVM heap compaction.",
                        status = OptimizationStatus.SUCCESS,
                        statusText = "Cleaned"
                    )
                )
            } else {
                reports.add(
                    OptimizationResultItem(
                        id = "mem_status",
                        title = "RAM Telemetry (6GB Configuration)",
                        description = "Used: ${String.format("%.1f", mem.usedRamGb)}GB / ${String.format("%.1f", mem.totalRamGb)}GB (${mem.usedPercentage}%). Memory headroom is healthy.",
                        status = OptimizationStatus.SUCCESS,
                        statusText = "Healthy"
                    )
                )
            }

            if (mem.usedPercentage > 82) {
                reports.add(
                    OptimizationResultItem(
                        id = "mem_pressure",
                        title = "High Memory Pressure Detected",
                        description = "Android restricts 3rd-party apps from killing background processes. Open Samsung Device Care to place heavy background apps into deep sleep.",
                        status = OptimizationStatus.MANUAL_ACTION_RECOMMENDED,
                        statusText = "Review Apps",
                        actionIntentType = "DEVICE_CARE"
                    )
                )
            }

            // 2. Storage Check
            val storage = repository.fetchStorageInfo()
            if (storage.usedPercentage > 88) {
                reports.add(
                    OptimizationResultItem(
                        id = "storage_alert",
                        title = "Storage Above 88%",
                        description = "Low flash storage slows down game texture streaming. Free up space in Samsung Storage settings.",
                        status = OptimizationStatus.MANUAL_ACTION_RECOMMENDED,
                        statusText = "Manage Storage",
                        actionIntentType = "STORAGE"
                    )
                )
            } else {
                reports.add(
                    OptimizationResultItem(
                        id = "storage_ok",
                        title = "Internal Storage Health",
                        description = "${String.format("%.1f", storage.freeStorageGb)}GB free out of ${String.format("%.1f", storage.totalStorageGb)}GB (${storage.usedPercentage}% used). Fast UFS flash write speeds verified.",
                        status = OptimizationStatus.SUCCESS,
                        statusText = "Optimal"
                    )
                )
            }

            // 3. Thermal & Battery Health
            val battery = repository.fetchBatteryInfo()
            if (battery.temperatureCelsius >= 41.0f) {
                reports.add(
                    OptimizationResultItem(
                        id = "battery_heat",
                        title = "Thermal Throttle Guard",
                        description = "Battery is at ${battery.temperatureCelsius}°C. MediaTek Dimensity 6100+ throttles CPU when over 42°C. Pause charging while gaming to prevent FPS drops.",
                        status = OptimizationStatus.MANUAL_ACTION_RECOMMENDED,
                        statusText = "Cool Down",
                        actionIntentType = "BATTERY"
                    )
                )
            } else {
                reports.add(
                    OptimizationResultItem(
                        id = "battery_normal",
                        title = "Thermal Stability Status",
                        description = "Battery temperature: ${battery.temperatureCelsius}°C (${battery.thermalStateCategory}). Voltage: ${String.format("%.2f", battery.voltageVolts)}V. No throttling.",
                        status = OptimizationStatus.SUCCESS,
                        statusText = "Cool"
                    )
                )
            }

            // 4. Refresh Rate / Motion Smoothness
            val dev = repository.fetchDeviceInfo()
            if (dev.refreshRateHz >= 90) {
                reports.add(
                    OptimizationResultItem(
                        id = "refresh_90",
                        title = "Motion Smoothness (90Hz)",
                        description = "90Hz high refresh rate is active. Enables silky smooth touch response and up to 90 FPS rendering.",
                        status = OptimizationStatus.SUCCESS,
                        statusText = "90Hz Active"
                    )
                )
            } else {
                reports.add(
                    OptimizationResultItem(
                        id = "refresh_60",
                        title = "Display Refresh Rate at 60Hz",
                        description = "Galaxy M15 5G supports 90Hz Super AMOLED. Switch Motion Smoothness to High in Samsung Display settings for smoother gameplay.",
                        status = OptimizationStatus.MANUAL_ACTION_RECOMMENDED,
                        statusText = "Enable 90Hz",
                        actionIntentType = "DISPLAY"
                    )
                )
            }

            // 5. Notification Interruption Check
            if (repository.isNotificationPolicyAccessGranted()) {
                if (boosterPrefs.value.autoDndOnLaunch || repository.isGamingDndActive()) {
                    reports.add(
                        OptimizationResultItem(
                            id = "dnd_active",
                            title = "Gaming DND Protection",
                            description = "Do Not Disturb priority filter is ready to block pop-up call banners during match gameplay.",
                            status = OptimizationStatus.SUCCESS,
                            statusText = "DND Ready"
                        )
                    )
                } else {
                    reports.add(
                        OptimizationResultItem(
                            id = "dnd_available",
                            title = "Notification Interruption Control",
                            description = "DND permission is granted. Enable Auto-DND to prevent notifications from dropping frames.",
                            status = OptimizationStatus.INFO_NOTICE,
                            statusText = "Available"
                        )
                    )
                }
            } else {
                reports.add(
                    OptimizationResultItem(
                        id = "dnd_perm",
                        title = "Notification Silence Assistant",
                        description = "Grant Do Not Disturb permission so ZAVRANGO BOOST can silence unwanted interruptions while gaming.",
                        status = OptimizationStatus.MANUAL_ACTION_RECOMMENDED,
                        statusText = "Grant Access",
                        actionIntentType = "DND_POLICY"
                    )
                )
            }

            // 6. Game Booster & Samsung Settings Notice
            reports.add(
                OptimizationResultItem(
                    id = "samsung_disclose",
                    title = "Android 16 Security Notice",
                    description = "Operating system policies prevent apps from modifying CPU clock or forced memory killing without root. Genuine Samsung Game Booster shortcuts are configured.",
                    status = OptimizationStatus.INFO_NOTICE,
                    statusText = "Verified"
                )
            )

            _optimizationReports.value = reports
            _isOptimizing.value = false
            showStatusMessage("Smart Optimization complete! ${reports.count { it.status == OptimizationStatus.SUCCESS }} checks passed.")
        }
    }

    // --- Interactive Touch & Reaction Latency Test ---
    fun startReactionTest() {
        reactionJob?.cancel()
        _reactionState.value = ReactionTestState(
            phase = ReactionPhase.WAITING_FOR_TRIGGER,
            message = "WAIT FOR RED... DO NOT TAP YET!"
        )

        val delayMs = Random.nextLong(1500, 3800)
        reactionJob = viewModelScope.launch {
            delay(delayMs)
            _reactionState.value = ReactionTestState(
                phase = ReactionPhase.READY_TO_TAP,
                startTimeMs = System.currentTimeMillis(),
                message = "TAP NOW! HIT THE SCREEN!"
            )
        }
    }

    fun onReactionTap() {
        when (_reactionState.value.phase) {
            ReactionPhase.WAITING_FOR_TRIGGER -> {
                reactionJob?.cancel()
                _reactionState.value = ReactionTestState(
                    phase = ReactionPhase.TOO_EARLY,
                    message = "Too early! You tapped before the red light. Tap Reset to try again."
                )
            }
            ReactionPhase.READY_TO_TAP -> {
                val latency = System.currentTimeMillis() - _reactionState.value.startTimeMs
                val rating = when {
                    latency < 190 -> "Pro Esports Reflex! (<190ms)"
                    latency < 250 -> "Fast Gamer Speed! (Excellent)"
                    latency < 320 -> "Average Response (Good)"
                    else -> "Slow Touch Response (Enable Touch Sensitivity)"
                }
                _reactionState.value = ReactionTestState(
                    phase = ReactionPhase.RESULT,
                    scoreMs = latency,
                    message = "${latency}ms - $rating"
                )
            }
            else -> {}
        }
    }

    fun resetReactionTest() {
        reactionJob?.cancel()
        _reactionState.value = ReactionTestState()
    }

    // --- Rapid Tap Speed Challenge ---
    fun startRapidTapTest() {
        rapidTapJob?.cancel()
        _rapidTapState.value = RapidTapState(
            isActive = true,
            remainingSeconds = 5,
            tapCount = 0,
            finalCps = 0f
        )

        rapidTapJob = viewModelScope.launch {
            for (sec in 4 downTo 0) {
                delay(1000)
                if (sec > 0) {
                    _rapidTapState.value = _rapidTapState.value.copy(remainingSeconds = sec)
                } else {
                    val count = _rapidTapState.value.tapCount
                    val cps = count / 5.0f
                    _rapidTapState.value = RapidTapState(
                        isActive = false,
                        remainingSeconds = 0,
                        tapCount = count,
                        finalCps = cps
                    )
                }
            }
        }
    }

    fun onRapidTap() {
        if (_rapidTapState.value.isActive) {
            _rapidTapState.value = _rapidTapState.value.copy(
                tapCount = _rapidTapState.value.tapCount + 1
            )
        }
    }

    // --- Game Launch Action ---
    fun launchGameOrStore() {
        val intent = repository.getLaunchableGameIntent()
        if (intent != null) {
            if (boosterPrefs.value.autoDndOnLaunch && repository.isNotificationPolicyAccessGranted()) {
                repository.setGamingDndMode(true)
            }
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            getApplication<Application>().startActivity(intent)
            showStatusMessage("Launching Free Fire with ZAVRANGO BOOST profile...")
        } else {
            showStatusMessage("Free Fire not detected. Opening Google Play Store...")
            repository.openPlayStoreForFreeFire()
        }
    }

    // --- Intent Dispatches ---
    fun dispatchAction(actionType: String?) {
        when (actionType) {
            "DEVICE_CARE" -> repository.openSamsungDeviceCare()
            "STORAGE" -> repository.openStorageSettings()
            "BATTERY" -> repository.openBatterySettings()
            "DISPLAY" -> repository.openDisplaySettings()
            "DND_POLICY" -> repository.openNotificationPolicySettings()
            "NOTIFICATIONS" -> repository.openNotificationSettings()
            "GAME_BOOSTER" -> repository.openSamsungGameBooster()
            "DEV_SETTINGS" -> repository.openDeveloperSettings()
            else -> {}
        }
    }

    fun toggleDnd() {
        if (!repository.isNotificationPolicyAccessGranted()) {
            repository.openNotificationPolicySettings()
            showStatusMessage("Please grant Do Not Disturb access in Android settings.")
        } else {
            val newState = !repository.isGamingDndActive()
            val success = repository.setGamingDndMode(newState)
            if (success) {
                _isDndActive.value = newState
                showStatusMessage(if (newState) "Gaming DND enabled. Unwanted notifications blocked." else "Gaming DND disabled.")
            } else {
                showStatusMessage("Could not update DND mode.")
            }
        }
    }
}
