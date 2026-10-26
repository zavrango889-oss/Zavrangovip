package com.example.data

import android.app.ActivityManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.os.StatFs
import android.provider.Settings
import android.view.Display
import android.view.WindowManager
import com.example.model.BatteryInfoData
import com.example.model.BoosterPreferences
import com.example.model.DeviceInfoData
import com.example.model.MemoryInfoData
import com.example.model.StorageInfoData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class SystemTelemetryRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("zavrango_boost_prefs", Context.MODE_PRIVATE)

    private val activityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val powerManager =
        context.getSystemService(Context.POWER_SERVICE) as? PowerManager

    private val _memoryInfo = MutableStateFlow(fetchMemoryInfo())
    val memoryInfo: StateFlow<MemoryInfoData> = _memoryInfo.asStateFlow()

    private val _storageInfo = MutableStateFlow(fetchStorageInfo())
    val storageInfo: StateFlow<StorageInfoData> = _storageInfo.asStateFlow()

    private val _batteryInfo = MutableStateFlow(fetchBatteryInfo())
    val batteryInfo: StateFlow<BatteryInfoData> = _batteryInfo.asStateFlow()

    private val _deviceInfo = MutableStateFlow(fetchDeviceInfo())
    val deviceInfo: StateFlow<DeviceInfoData> = _deviceInfo.asStateFlow()

    private val _boosterPrefs = MutableStateFlow(loadBoosterPreferences())
    val boosterPrefs: StateFlow<BoosterPreferences> = _boosterPrefs.asStateFlow()

    private var batteryReceiver: BroadcastReceiver? = null

    init {
        registerBatteryReceiver()
    }

    fun refreshAll() {
        _memoryInfo.value = fetchMemoryInfo()
        _storageInfo.value = fetchStorageInfo()
        _batteryInfo.value = fetchBatteryInfo()
        _deviceInfo.value = fetchDeviceInfo()
    }

    private fun registerBatteryReceiver() {
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                if (intent != null && intent.action == Intent.ACTION_BATTERY_CHANGED) {
                    _batteryInfo.value = parseBatteryIntent(intent)
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(batteryReceiver, filter)
    }

    fun unregister() {
        batteryReceiver?.let {
            try {
                context.unregisterReceiver(it)
            } catch (_: Exception) {}
        }
    }

    fun fetchMemoryInfo(): MemoryInfoData {
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        return MemoryInfoData(
            totalRamBytes = memInfo.totalMem,
            availableRamBytes = memInfo.availMem,
            isLowMemory = memInfo.lowMemory,
            thresholdBytes = memInfo.threshold
        )
    }

    fun fetchStorageInfo(): StorageInfoData {
        return try {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong
            StorageInfoData(
                totalStorageBytes = totalBlocks * blockSize,
                freeStorageBytes = availableBlocks * blockSize
            )
        } catch (e: Exception) {
            StorageInfoData(0L, 0L)
        }
    }

    fun fetchBatteryInfo(): BatteryInfoData {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryIntent = context.registerReceiver(null, filter)
        return parseBatteryIntent(batteryIntent)
    }

    private fun parseBatteryIntent(batteryIntent: Intent?): BatteryInfoData {
        if (batteryIntent == null) return BatteryInfoData()
        val level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = if (level >= 0 && scale > 0) ((level.toFloat() / scale.toFloat()) * 100).toInt() else 0

        val tempTenths = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
        val tempCelsius = tempTenths / 10.0f

        val voltageMv = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
        val voltageVolts = voltageMv / 1000.0f

        val healthCode = batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
        val health = when (healthCode) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            else -> "Unknown"
        }

        val statusCode = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = statusCode == BatteryManager.BATTERY_STATUS_CHARGING ||
                statusCode == BatteryManager.BATTERY_STATUS_FULL
        val status = when (statusCode) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
            else -> "Unknown"
        }

        val chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val pluggedSource = when (chargePlug) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC Wall Charger"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB Port"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless Dock"
            else -> "Battery Power"
        }

        return BatteryInfoData(
            level = batteryPct,
            temperatureCelsius = tempCelsius,
            voltageVolts = voltageVolts,
            health = health,
            status = status,
            isCharging = isCharging,
            pluggedSource = pluggedSource
        )
    }

    fun fetchDeviceInfo(): DeviceInfoData {
        val model = Build.MODEL ?: "Android Device"
        val manufacturer = Build.MANUFACTURER ?: "Generic"
        val device = Build.DEVICE ?: ""
        val androidVer = Build.VERSION.RELEASE ?: "14"
        val sdkInt = Build.VERSION.SDK_INT

        val isSamsung = manufacturer.contains("samsung", ignoreCase = true)
        val isGalaxyM15 = isSamsung && (model.contains("M15", ignoreCase = true) ||
                model.contains("SM-M156", ignoreCase = true) ||
                device.contains("m15", ignoreCase = true))

        var currentRefreshRate = 60
        val supportedRates = mutableListOf<Int>()

        try {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            val display: Display? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    context.display
                } catch (e: Exception) {
                    @Suppress("DEPRECATION")
                    windowManager?.defaultDisplay
                }
            } else {
                @Suppress("DEPRECATION")
                windowManager?.defaultDisplay
            }

            display?.let { d ->
                currentRefreshRate = d.mode?.refreshRate?.toInt() ?: d.refreshRate.toInt()
                d.supportedModes?.forEach { mode ->
                    val rate = mode.refreshRate.toInt()
                    if (!supportedRates.contains(rate)) {
                        supportedRates.add(rate)
                    }
                }
            }
        } catch (_: Exception) {}

        if (supportedRates.isEmpty()) {
            supportedRates.add(currentRefreshRate)
        }

        return DeviceInfoData(
            model = model,
            manufacturer = manufacturer,
            deviceName = "$manufacturer $model",
            androidVersion = androidVer,
            apiLevel = sdkInt,
            isSamsungGalaxyM15 = isGalaxyM15,
            refreshRateHz = currentRefreshRate,
            supportedRefreshRates = supportedRates.sorted()
        )
    }

    // --- Preferences ---
    private fun loadBoosterPreferences(): BoosterPreferences {
        return BoosterPreferences(
            performanceMode = prefs.getBoolean("perf_mode", true),
            touchOptimization = prefs.getBoolean("touch_opt", true),
            precisionMode = prefs.getBoolean("precision_mode", true),
            protectionSystem = prefs.getBoolean("protection_system", true),
            fpsBooster = prefs.getBoolean("fps_booster", true),
            autoDndOnLaunch = prefs.getBoolean("auto_dnd", false),
            targetFps = prefs.getInt("target_fps", 90)
        )
    }

    fun updateBoosterPreference(
        perfMode: Boolean? = null,
        touchOpt: Boolean? = null,
        precision: Boolean? = null,
        protection: Boolean? = null,
        fpsBoost: Boolean? = null,
        autoDnd: Boolean? = null,
        targetFps: Int? = null
    ) {
        val editor = prefs.edit()
        var current = _boosterPrefs.value

        perfMode?.let {
            editor.putBoolean("perf_mode", it)
            current = current.copy(performanceMode = it)
        }
        touchOpt?.let {
            editor.putBoolean("touch_opt", it)
            current = current.copy(touchOptimization = it)
        }
        precision?.let {
            editor.putBoolean("precision_mode", it)
            current = current.copy(precisionMode = it)
        }
        protection?.let {
            editor.putBoolean("protection_system", it)
            current = current.copy(protectionSystem = it)
        }
        fpsBoost?.let {
            editor.putBoolean("fps_booster", it)
            current = current.copy(fpsBooster = it)
        }
        autoDnd?.let {
            editor.putBoolean("auto_dnd", it)
            current = current.copy(autoDndOnLaunch = it)
        }
        targetFps?.let {
            editor.putInt("target_fps", it)
            current = current.copy(targetFps = it)
        }

        editor.apply()
        _boosterPrefs.value = current
    }

    // --- Game Detection & Launching ---
    val FREE_FIRE_MAX_PKG = "com.dts.freefiremax"
    val FREE_FIRE_STANDARD_PKG = "com.dts.freefireth"

    fun isFreeFireMaxInstalled(): Boolean {
        return isPackageInstalled(FREE_FIRE_MAX_PKG)
    }

    fun isFreeFireStandardInstalled(): Boolean {
        return isPackageInstalled(FREE_FIRE_STANDARD_PKG)
    }

    fun isPackageInstalled(packageName: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(packageName, 0)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getLaunchableGameIntent(): Intent? {
        val pm = context.packageManager
        // Check Free Fire MAX first
        if (isFreeFireMaxInstalled()) {
            return pm.getLaunchIntentForPackage(FREE_FIRE_MAX_PKG)
        }
        // Then check Free Fire Standard
        if (isFreeFireStandardInstalled()) {
            return pm.getLaunchIntentForPackage(FREE_FIRE_STANDARD_PKG)
        }
        return null
    }

    fun openPlayStoreForFreeFire(): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$FREE_FIRE_MAX_PKG")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            try {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$FREE_FIRE_MAX_PKG")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
                true
            } catch (e2: Exception) {
                false
            }
        }
    }

    // --- Genuine System Intent Shortcuts ---
    fun openSamsungDeviceCare(): Boolean {
        val intents = listOf(
            Intent().setClassName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity"),
            Intent().setClassName("com.samsung.android.lool", "com.samsung.android.sm.battery.ui.BatteryActivity"),
            Intent("com.samsung.android.sm.ACTION_BATTERY"),
            Intent("com.samsung.android.sm.ACTION_DEVICE_CARE"),
            Intent(Settings.ACTION_DEVICE_INFO_SETTINGS)
        )
        for (intent in intents) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return true
            } catch (_: Exception) {}
        }
        return false
    }

    fun openSamsungGameBooster(): Boolean {
        val intents = listOf(
            Intent().setClassName("com.samsung.android.game.gametools", "com.samsung.android.game.gametools.GameToolsMainActivity"),
            Intent().setClassName("com.samsung.android.game.gamehome", "com.samsung.android.game.gamehome.GameHomeActivity"),
            Intent("android.settings.GAME_SETTINGS"),
            Intent(Settings.ACTION_DISPLAY_SETTINGS)
        )
        for (intent in intents) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return true
            } catch (_: Exception) {}
        }
        return false
    }

    fun openDisplaySettings(): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_DISPLAY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun openBatterySettings(): Boolean {
        val intents = listOf(
            Intent(Intent.ACTION_POWER_USAGE_SUMMARY),
            Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS),
            Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        )
        for (intent in intents) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return true
            } catch (_: Exception) {}
        }
        return false
    }

    fun openStorageSettings(): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            try {
                val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallback)
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    fun openNotificationSettings(): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_ALL_APPS_NOTIFICATION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            try {
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    fun openDeveloperSettings(): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun isNotificationPolicyAccessGranted(): Boolean {
        return notificationManager.isNotificationPolicyAccessGranted
    }

    fun openNotificationPolicySettings(): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun setGamingDndMode(enabled: Boolean): Boolean {
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            return false
        }
        return try {
            if (enabled) {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
            } else {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isGamingDndActive(): Boolean {
        return try {
            val filter = notificationManager.currentInterruptionFilter
            filter == NotificationManager.INTERRUPTION_FILTER_PRIORITY ||
                    filter == NotificationManager.INTERRUPTION_FILTER_NONE ||
                    filter == NotificationManager.INTERRUPTION_FILTER_ALARMS
        } catch (_: Exception) {
            false
        }
    }

    // --- Permitted Local Optimizations ---
    fun performPermittedLocalTrim(): Long {
        var bytesFreed = 0L
        try {
            // Trim app's internal cache
            val cacheDir = context.cacheDir
            bytesFreed += deleteDirRecursive(cacheDir)
            // Trim external cache if present
            context.externalCacheDir?.let {
                bytesFreed += deleteDirRecursive(it)
            }
            // Request system garbage collection for the application process
            System.gc()
        } catch (_: Exception) {}
        refreshAll()
        return bytesFreed
    }

    private fun deleteDirRecursive(dir: File?): Long {
        var size: Long = 0
        if (dir != null && dir.isDirectory) {
            val children = dir.listFiles()
            if (children != null) {
                for (child in children) {
                    size += if (child.isDirectory) deleteDirRecursive(child) else child.length()
                    child.delete()
                }
            }
        }
        return size
    }
}
