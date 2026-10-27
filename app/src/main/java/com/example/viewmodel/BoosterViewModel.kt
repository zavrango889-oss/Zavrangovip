package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SystemTelemetryRepository
import com.example.data.local.AppDatabase
import com.example.data.local.GameProfileEntity
import com.example.data.local.GameProfileRepository
import com.example.model.AimTrainerState
import com.example.model.BoosterPreferences
import com.example.model.HitFeedback
import com.example.model.HitType
import com.example.model.OptimizationResultItem
import com.example.model.OptimizationStatus
import com.example.model.RapidTapState
import com.example.model.ReactionPhase
import com.example.model.ReactionTestState
import com.example.model.SensitivityPresetType
import com.example.model.SensitivityState
import com.example.util.FpsCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.random.Random

class BoosterViewModel(application: Application) : AndroidViewModel(application) {

    val repository = SystemTelemetryRepository(application)
    private val fpsCalculator = FpsCalculator()

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val profileRepository = GameProfileRepository(database.gameProfileDao())

    val memoryInfo = repository.memoryInfo
    val storageInfo = repository.storageInfo
    val batteryInfo = repository.batteryInfo
    val deviceInfo = repository.deviceInfo
    val boosterPrefs = repository.boosterPrefs

    val realtimeFps: StateFlow<Int> = fpsCalculator.currentFps

    val gameProfiles: StateFlow<List<GameProfileEntity>> = profileRepository.allProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Sensitivity State (Strictly 0..200) ---
    private val _sensitivityState = MutableStateFlow(SensitivityState.createPreset(SensitivityPresetType.ONE_TAP_HEADSHOT))
    val sensitivityState: StateFlow<SensitivityState> = _sensitivityState.asStateFlow()

    // --- Ping State ---
    private val _pingMs = MutableStateFlow<Int?>(null)
    val pingMs: StateFlow<Int?> = _pingMs.asStateFlow()

    private val _pingStatus = MutableStateFlow<String?>("Ready to test")
    val pingStatus: StateFlow<String?> = _pingStatus.asStateFlow()

    private val _isPinging = MutableStateFlow(false)
    val isPinging: StateFlow<Boolean> = _isPinging.asStateFlow()

    // --- DND / System ---
    private val _isDndActive = MutableStateFlow(repository.isGamingDndActive())
    val isDndActive: StateFlow<Boolean> = _isDndActive.asStateFlow()

    private val _isDndGranted = MutableStateFlow(repository.isNotificationPolicyAccessGranted())
    val isDndGranted: StateFlow<Boolean> = _isDndGranted.asStateFlow()

    // --- Diagnostics ---
    private val _optimizationReports = MutableStateFlow<List<OptimizationResultItem>?>(null)
    val optimizationReports: StateFlow<List<OptimizationResultItem>?> = _optimizationReports.asStateFlow()

    private val _isOptimizing = MutableStateFlow(false)
    val isOptimizing: StateFlow<Boolean> = _isOptimizing.asStateFlow()

    // --- Touch & Reaction ---
    private val _reactionState = MutableStateFlow(ReactionTestState())
    val reactionState: StateFlow<ReactionTestState> = _reactionState.asStateFlow()
    private var reactionJob: Job? = null

    private val _rapidTapState = MutableStateFlow(RapidTapState())
    val rapidTapState: StateFlow<RapidTapState> = _rapidTapState.asStateFlow()
    private var rapidTapJob: Job? = null

    // --- Aim Trainer ---
    private val _aimTrainerState = MutableStateFlow(AimTrainerState())
    val aimTrainerState: StateFlow<AimTrainerState> = _aimTrainerState.asStateFlow()
    private var aimLoopJob: Job? = null
    private var aimTimerJob: Job? = null

    // Status Toast/Bar
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        fpsCalculator.start()
        startPeriodicRefresh()
        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.ensureDefaultsExist()
        }
        runPingCheck()
    }

    override fun onCleared() {
        super.onCleared()
        fpsCalculator.stop()
        repository.unregister()
        aimLoopJob?.cancel()
        aimTimerJob?.cancel()
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

    // =========================================================================
    // 1. FREE FIRE MAX LAUNCHER & BOOST
    // =========================================================================

    fun launchGameOrStore() {
        val intent = repository.getLaunchableGameIntent()
        if (intent != null) {
            if (boosterPrefs.value.autoDndOnLaunch && repository.isNotificationPolicyAccessGranted()) {
                repository.setGamingDndMode(true)
            }
            repository.performPermittedLocalTrim()
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            getApplication<Application>().startActivity(intent)
            showStatusMessage("Launching Free Fire MAX with ZAVRANGOFFZ Boost Profile...")
        } else {
            showStatusMessage("Free Fire MAX not found. Opening Google Play Store...")
            repository.openPlayStoreForFreeFire()
        }
    }

    fun runPingCheck() {
        if (_isPinging.value) return
        viewModelScope.launch {
            _isPinging.value = true
            _pingStatus.value = "Pinging game gateway..."
            val (ms, status) = repository.checkNetworkPingMs()
            _pingMs.value = if (ms > 0) ms else null
            _pingStatus.value = status
            _isPinging.value = false
        }
    }

    // =========================================================================
    // 2. SENSITIVITY GENERATOR (0..200 STRICT)
    // =========================================================================

    fun setSensitivityGeneral(value: Int) {
        _sensitivityState.value = _sensitivityState.value.copy(
            general = value.coerceIn(0, 200),
            isSaved = false
        )
    }

    fun setSensitivityRedDot(value: Int) {
        _sensitivityState.value = _sensitivityState.value.copy(
            redDot = value.coerceIn(0, 200),
            isSaved = false
        )
    }

    fun setSensitivityScope2x(value: Int) {
        _sensitivityState.value = _sensitivityState.value.copy(
            scope2x = value.coerceIn(0, 200),
            isSaved = false
        )
    }

    fun setSensitivityScope4x(value: Int) {
        _sensitivityState.value = _sensitivityState.value.copy(
            scope4x = value.coerceIn(0, 200),
            isSaved = false
        )
    }

    fun setSensitivityScopeSniper(value: Int) {
        _sensitivityState.value = _sensitivityState.value.copy(
            scopeSniper = value.coerceIn(0, 200),
            isSaved = false
        )
    }

    fun setSensitivityFreeLook(value: Int) {
        _sensitivityState.value = _sensitivityState.value.copy(
            freeLook = value.coerceIn(0, 200),
            isSaved = false
        )
    }

    fun setSensitivityDpi(value: Int) {
        _sensitivityState.value = _sensitivityState.value.copy(
            dpi = value.coerceIn(360, 1200),
            isSaved = false
        )
    }

    fun setSensitivityFireButton(value: Int) {
        _sensitivityState.value = _sensitivityState.value.copy(
            fireButtonSize = value.coerceIn(10, 100),
            isSaved = false
        )
    }

    fun selectSensitivityPreset(type: SensitivityPresetType) {
        _sensitivityState.value = SensitivityState.createPreset(type)
        showStatusMessage("Applied preset: ${type.label}")
    }

    fun generateCalibratedSensitivity() {
        val currentPreset = _sensitivityState.value.presetType
        val baseGen = when (currentPreset) {
            SensitivityPresetType.ONE_TAP_HEADSHOT -> Random.nextInt(194, 201)
            SensitivityPresetType.RUSH_SHOTGUN -> Random.nextInt(196, 201)
            SensitivityPresetType.SNIPER_SUPPORT -> Random.nextInt(170, 185)
            SensitivityPresetType.BALANCED_90HZ -> Random.nextInt(182, 195)
            SensitivityPresetType.CUSTOM -> Random.nextInt(175, 198)
        }.coerceIn(150, 200)

        val redDot = (baseGen - Random.nextInt(2, 8)).coerceIn(150, 200)
        val s2x = (redDot - Random.nextInt(2, 10)).coerceIn(150, 200)
        val s4x = (s2x - Random.nextInt(2, 12)).coerceIn(150, 200)
        val sniper = when (currentPreset) {
            SensitivityPresetType.SNIPER_SUPPORT -> Random.nextInt(155, 172)
            else -> Random.nextInt(150, 165)
        }.coerceIn(150, 200)
        val freeLook = Random.nextInt(165, 198).coerceIn(150, 200)
        val dpi = listOf(411, 480, 560, 600, 720, 840).random()
        val btn = Random.nextInt(48, 58)

        _sensitivityState.value = SensitivityState(
            general = baseGen.coerceIn(150, 200),
            redDot = redDot.coerceIn(150, 200),
            scope2x = s2x.coerceIn(150, 200),
            scope4x = s4x.coerceIn(150, 200),
            scopeSniper = sniper.coerceIn(150, 200),
            freeLook = freeLook.coerceIn(150, 200),
            dpi = dpi,
            fireButtonSize = btn,
            presetType = currentPreset,
            isSaved = false
        )
        showStatusMessage("Gerado: Sensi calibrada na faixa 150-200 para Galaxy M15 5G!")
    }

    fun saveCurrentSensitivityAsProfile(profileName: String) {
        val current = _sensitivityState.value
        val name = if (profileName.isNotBlank()) profileName else "Profile ${System.currentTimeMillis() % 1000}"
        viewModelScope.launch(Dispatchers.IO) {
            val entity = GameProfileEntity(
                profileName = name,
                playstyleTag = current.presetType.tag,
                general = current.general,
                redDot = current.redDot,
                scope2x = current.scope2x,
                scope4x = current.scope4x,
                scopeSniper = current.scopeSniper,
                freeLook = current.freeLook,
                dpi = current.dpi,
                fireButtonSize = current.fireButtonSize,
                isFavorite = false
            )
            profileRepository.insertProfile(entity)
            _sensitivityState.value = _sensitivityState.value.copy(isSaved = true)
            showStatusMessage("Profile '$name' saved to local database!")
        }
    }

    fun loadProfileIntoSensitivity(profile: GameProfileEntity) {
        _sensitivityState.value = SensitivityState(
            general = profile.general.coerceIn(0, 200),
            redDot = profile.redDot.coerceIn(0, 200),
            scope2x = profile.scope2x.coerceIn(0, 200),
            scope4x = profile.scope4x.coerceIn(0, 200),
            scopeSniper = profile.scopeSniper.coerceIn(0, 200),
            freeLook = profile.freeLook.coerceIn(0, 200),
            dpi = profile.dpi,
            fireButtonSize = profile.fireButtonSize,
            presetType = SensitivityPresetType.CUSTOM,
            isSaved = true
        )
        showStatusMessage("Loaded profile '${profile.profileName}'!")
    }

    fun deleteProfile(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.deleteById(id)
            showStatusMessage("Profile removed.")
        }
    }

    fun resetAllProfiles() {
        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.resetToDefaults()
            showStatusMessage("Profiles reset to default presets.")
        }
    }

    // =========================================================================
    // 5. TOUCH & REACTION TESTER
    // =========================================================================

    fun startReactionTest() {
        reactionJob?.cancel()
        _reactionState.value = ReactionTestState(
            phase = ReactionPhase.WAITING_FOR_TRIGGER,
            message = "WAIT FOR NEON RED... DO NOT TAP YET!"
        )

        val delayMs = Random.nextLong(1400, 3500)
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
                    message = "Too early! Tapped before target appeared. Tap Reset."
                )
            }
            ReactionPhase.READY_TO_TAP -> {
                val latency = System.currentTimeMillis() - _reactionState.value.startTimeMs
                val rating = when {
                    latency < 180 -> "Esports Pro God Reflex! (<180ms)"
                    latency < 240 -> "Fast Competitive Reflex (Good)"
                    latency < 310 -> "Average Gamer Reflex"
                    else -> "Slow Touch Response (Check Touch Sensitivity)"
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

    // =========================================================================
    // 8. AIM PRACTICE TRAINER (MOVING TARGET WITH HEADSHOT MULTIPLIER)
    // =========================================================================

    fun startAimTrainer() {
        aimLoopJob?.cancel()
        aimTimerJob?.cancel()

        _aimTrainerState.value = AimTrainerState(
            isGameActive = true,
            isGameOver = false,
            targetNormX = 0.5f,
            targetNormY = 0.4f,
            targetVelocityX = 0.016f,
            targetVelocityY = 0.012f,
            score = 0,
            headshots = 0,
            bodyHits = 0,
            totalShots = 0,
            currentStreak = 0,
            maxStreak = 0,
            remainingSeconds = 30,
            highScore = _aimTrainerState.value.highScore,
            lastHitFeedback = null
        )

        // 60FPS motion loop
        aimLoopJob = viewModelScope.launch {
            while (_aimTrainerState.value.isGameActive) {
                delay(16)
                val curr = _aimTrainerState.value
                var nx = curr.targetNormX + curr.targetVelocityX
                var ny = curr.targetNormY + curr.targetVelocityY
                var vx = curr.targetVelocityX
                var vy = curr.targetVelocityY

                // Bounce off screen bounds
                if (nx <= 0.12f || nx >= 0.88f) {
                    vx = -vx
                    nx = nx.coerceIn(0.12f, 0.88f)
                }
                if (ny <= 0.15f || ny >= 0.82f) {
                    vy = -vy
                    ny = ny.coerceIn(0.15f, 0.82f)
                }

                _aimTrainerState.value = curr.copy(
                    targetNormX = nx,
                    targetNormY = ny,
                    targetVelocityX = vx,
                    targetVelocityY = vy
                )
            }
        }

        // 30s countdown timer
        aimTimerJob = viewModelScope.launch {
            while (_aimTrainerState.value.remainingSeconds > 0 && _aimTrainerState.value.isGameActive) {
                delay(1000)
                val nextSec = _aimTrainerState.value.remainingSeconds - 1
                if (nextSec <= 0) {
                    val finalScore = _aimTrainerState.value.score
                    val newHigh = maxOf(finalScore, _aimTrainerState.value.highScore)
                    _aimTrainerState.value = _aimTrainerState.value.copy(
                        isGameActive = false,
                        isGameOver = true,
                        remainingSeconds = 0,
                        highScore = newHigh
                    )
                    aimLoopJob?.cancel()
                } else {
                    _aimTrainerState.value = _aimTrainerState.value.copy(remainingSeconds = nextSec)
                }
            }
        }
    }

    fun onAimTargetTap(
        canvasWidthPx: Float,
        canvasHeightPx: Float,
        tapXPx: Float,
        tapYPx: Float
    ) {
        val curr = _aimTrainerState.value
        if (!curr.isGameActive) return

        val targetCenterX = curr.targetNormX * canvasWidthPx
        val targetCenterY = curr.targetNormY * canvasHeightPx
        val targetRadiusPx = curr.targetRadiusDp * (canvasWidthPx / 360f).coerceIn(1.0f, 3.0f)
        val headshotRadiusPx = targetRadiusPx * 0.35f

        val dist = hypot(tapXPx - targetCenterX, tapYPx - targetCenterY)
        val totalShots = curr.totalShots + 1

        if (dist <= headshotRadiusPx) {
            // CRITICAL HEADSHOT (+100 PTS)
            val streak = curr.currentStreak + 1
            val pts = 100 + (streak * 10)
            val newScore = curr.score + pts
            val newMax = maxOf(streak, curr.maxStreak)
            // Teleport target slightly on hit for challenge
            val newX = Random.nextFloat().coerceIn(0.15f, 0.85f)
            val newY = Random.nextFloat().coerceIn(0.18f, 0.80f)
            val vx = if (Random.nextBoolean()) 0.018f else -0.018f
            val vy = if (Random.nextBoolean()) 0.014f else -0.014f

            _aimTrainerState.value = curr.copy(
                score = newScore,
                headshots = curr.headshots + 1,
                totalShots = totalShots,
                currentStreak = streak,
                maxStreak = newMax,
                targetNormX = newX,
                targetNormY = newY,
                targetVelocityX = vx,
                targetVelocityY = vy,
                lastHitFeedback = HitFeedback(HitType.HEADSHOT, "FULL VERMELHO! +$pts", pts)
            )
        } else if (dist <= targetRadiusPx) {
            // BODY HIT (+25 PTS)
            val streak = curr.currentStreak + 1
            val pts = 25
            val newScore = curr.score + pts
            val newMax = maxOf(streak, curr.maxStreak)
            _aimTrainerState.value = curr.copy(
                score = newScore,
                bodyHits = curr.bodyHits + 1,
                totalShots = totalShots,
                currentStreak = streak,
                maxStreak = newMax,
                lastHitFeedback = HitFeedback(HitType.BODY, "BODY HIT +$pts", pts)
            )
        } else {
            // MISS
            _aimTrainerState.value = curr.copy(
                totalShots = totalShots,
                currentStreak = 0,
                lastHitFeedback = HitFeedback(HitType.MISS, "ERROU!", 0)
            )
        }
    }

    fun stopAimTrainer() {
        aimLoopJob?.cancel()
        aimTimerJob?.cancel()
        _aimTrainerState.value = _aimTrainerState.value.copy(
            isGameActive = false,
            isGameOver = false
        )
    }

    // =========================================================================
    // 6. NOTIFICATION ASSISTANT & SHORTCUTS
    // =========================================================================

    fun openAppNotificationSettings(packageName: String) {
        val success = repository.openAppNotificationSettings(packageName)
        if (!success) {
            showStatusMessage("Could not open settings for $packageName")
        }
    }

    fun toggleDnd() {
        if (!repository.isNotificationPolicyAccessGranted()) {
            repository.openNotificationPolicySettings()
            showStatusMessage("Grant Do Not Disturb permission in Android Settings.")
        } else {
            val newState = !repository.isGamingDndActive()
            val ok = repository.setGamingDndMode(newState)
            if (ok) {
                _isDndActive.value = newState
                showStatusMessage(if (newState) "Zen Gaming Mode: Pop-up notifications blocked." else "Normal notification mode restored.")
            } else {
                showStatusMessage("Could not change DND mode.")
            }
        }
    }

    // =========================================================================
    // 7. PERFORMANCE & SMART CHECKS
    // =========================================================================

    fun togglePerformanceMode(checked: Boolean) {
        repository.updateBoosterPreference(perfMode = checked)
        if (checked) {
            repository.performPermittedLocalTrim()
            showStatusMessage("Performance Mode active: Heap trimmed & prioritized.")
        } else {
            showStatusMessage("Performance Mode disabled.")
        }
    }

    fun toggleTouchOptimization(checked: Boolean) {
        repository.updateBoosterPreference(touchOpt = checked)
        showStatusMessage(if (checked) "Touch Sampling active: Rapid touch feedback enabled." else "Touch Sampling disabled.")
    }

    fun togglePrecisionMode(checked: Boolean) {
        repository.updateBoosterPreference(precision = checked)
        showStatusMessage(if (checked) "Precision Mode active: Custom aim profiles ready." else "Precision Mode disabled.")
    }

    fun toggleProtectionSystem(checked: Boolean) {
        repository.updateBoosterPreference(protection = checked)
        showStatusMessage(if (checked) "Protection System active: Thermal guardian on." else "Protection System disabled.")
    }

    fun toggleFpsBooster(checked: Boolean) {
        repository.updateBoosterPreference(fpsBoost = checked)
        showStatusMessage(if (checked) "FPS Booster active: 90Hz frame alignment on." else "FPS Booster disabled.")
    }

    fun toggleAutoDnd(checked: Boolean) {
        repository.updateBoosterPreference(autoDnd = checked)
    }

    fun setTargetFps(target: Int) {
        repository.updateBoosterPreference(targetFps = target)
    }

    fun runSmartCheck() {
        viewModelScope.launch {
            _isOptimizing.value = true
            delay(1000)

            val reports = mutableListOf<OptimizationResultItem>()

            val mem = repository.fetchMemoryInfo()
            val freedBytes = repository.performPermittedLocalTrim()
            val freedMb = freedBytes / (1024 * 1024)
            if (freedMb > 0) {
                reports.add(
                    OptimizationResultItem(
                        id = "mem_trim",
                        title = "App Cache Cleaned",
                        description = "Trimmed ${freedMb}MB from local app caches. Garbage collection triggered.",
                        status = OptimizationStatus.SUCCESS,
                        statusText = "Cleaned"
                    )
                )
            } else {
                reports.add(
                    OptimizationResultItem(
                        id = "mem_ok",
                        title = "RAM Headroom (6GB Model)",
                        description = "Available: ${String.format("%.1f", mem.availableRamGb)}GB out of ${String.format("%.1f", mem.totalRamGb)}GB (${mem.usedPercentage}% used).",
                        status = OptimizationStatus.SUCCESS,
                        statusText = "Optimal"
                    )
                )
            }

            val storage = repository.fetchStorageInfo()
            if (storage.usedPercentage > 88) {
                reports.add(
                    OptimizationResultItem(
                        id = "storage_warn",
                        title = "Storage Above 88%",
                        description = "Low flash storage impacts game map loading. Clear large files in Samsung Storage settings.",
                        status = OptimizationStatus.MANUAL_ACTION_RECOMMENDED,
                        statusText = "Clean Storage",
                        actionIntentType = "STORAGE"
                    )
                )
            } else {
                reports.add(
                    OptimizationResultItem(
                        id = "storage_ok",
                        title = "UFS Storage Status",
                        description = "${String.format("%.1f", storage.freeStorageGb)}GB free out of ${String.format("%.1f", storage.totalStorageGb)}GB.",
                        status = OptimizationStatus.SUCCESS,
                        statusText = "Optimal"
                    )
                )
            }

            val battery = repository.fetchBatteryInfo()
            if (battery.temperatureCelsius >= 41.5f) {
                reports.add(
                    OptimizationResultItem(
                        id = "bat_hot",
                        title = "Thermal Alert",
                        description = "Battery is at ${battery.temperatureCelsius}°C. MediaTek Dimensity 6100+ may thermal throttle. Cool device before ranked matches.",
                        status = OptimizationStatus.MANUAL_ACTION_RECOMMENDED,
                        statusText = "Cool Down",
                        actionIntentType = "BATTERY"
                    )
                )
            } else {
                reports.add(
                    OptimizationResultItem(
                        id = "bat_ok",
                        title = "Thermal Equilibrium",
                        description = "Battery at ${battery.temperatureCelsius}°C (${battery.thermalStateCategory}), ${String.format("%.2f", battery.voltageVolts)}V.",
                        status = OptimizationStatus.SUCCESS,
                        statusText = "Healthy"
                    )
                )
            }

            val dev = repository.fetchDeviceInfo()
            if (dev.refreshRateHz >= 90) {
                reports.add(
                    OptimizationResultItem(
                        id = "refresh_90",
                        title = "90Hz Super AMOLED Refresh",
                        description = "Display is running at 90Hz. Ultra smooth aiming enabled.",
                        status = OptimizationStatus.SUCCESS,
                        statusText = "90Hz Active"
                    )
                )
            } else {
                reports.add(
                    OptimizationResultItem(
                        id = "refresh_60",
                        title = "Display at 60Hz",
                        description = "Galaxy M15 5G supports 90Hz. Set Motion Smoothness to High in Samsung Display settings for competitive advantage.",
                        status = OptimizationStatus.MANUAL_ACTION_RECOMMENDED,
                        statusText = "Enable 90Hz",
                        actionIntentType = "DISPLAY"
                    )
                )
            }

            _optimizationReports.value = reports
            _isOptimizing.value = false
            showStatusMessage("Diagnostics complete: ${reports.count { it.status == OptimizationStatus.SUCCESS }} checks passed.")
        }
    }

    // =========================================================================
    // 10. INTENT SHORTCUT DISPATCH
    // =========================================================================

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
}
