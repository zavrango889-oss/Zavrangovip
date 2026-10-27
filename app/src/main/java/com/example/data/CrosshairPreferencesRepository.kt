package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.CrosshairColor
import com.example.model.CrosshairState
import com.example.model.CrosshairStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MainPanelPreferences(
    val fovDegrees: Int = 98,
    val optimizeGame: Boolean = true,
    val stabilizeAim: Boolean = true,
    val miraEnabled: Boolean = false,
    val sensibilidadeBaixa: Boolean = false
)

class CrosshairPreferencesRepository private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("zavrangoffz_crosshair_prefs", Context.MODE_PRIVATE)

    private val _crosshairState = MutableStateFlow(loadCrosshairState())
    val crosshairState: StateFlow<CrosshairState> = _crosshairState.asStateFlow()

    private val _panelPrefs = MutableStateFlow(loadPanelPreferences())
    val panelPrefs: StateFlow<MainPanelPreferences> = _panelPrefs.asStateFlow()

    private fun loadCrosshairState(): CrosshairState {
        val enabled = prefs.getBoolean("crosshair_enabled", false)
        val panelOpen = prefs.getBoolean("crosshair_panel_open", true)
        val quickHidden = prefs.getBoolean("crosshair_quick_hidden", false)
        val styleStr = prefs.getString("crosshair_style", CrosshairStyle.DOT.name) ?: CrosshairStyle.DOT.name
        val style = try { CrosshairStyle.valueOf(styleStr) } catch (_: Exception) { CrosshairStyle.DOT }
        val colorStr = prefs.getString("crosshair_color", CrosshairColor.RED.name) ?: CrosshairColor.RED.name
        val color = try { CrosshairColor.valueOf(colorStr) } catch (_: Exception) { CrosshairColor.RED }
        val size = prefs.getFloat("crosshair_size", 36f).coerceIn(14f, 80f)
        val opacity = prefs.getFloat("crosshair_opacity", 0.95f).coerceIn(0.15f, 1.0f)
        val stabilized = prefs.getBoolean("crosshair_stabilized", true)

        return CrosshairState(
            isEnabled = enabled,
            isPanelOpen = panelOpen,
            isQuickHidden = quickHidden,
            style = style,
            color = color,
            sizeDp = size,
            opacity = opacity,
            stabilized = stabilized
        )
    }

    private fun loadPanelPreferences(): MainPanelPreferences {
        val fov = prefs.getInt("main_fov", 98).coerceIn(60, 120)
        val optimize = prefs.getBoolean("main_optimize", true)
        val stabilize = prefs.getBoolean("main_stabilize", true)
        val mira = prefs.getBoolean("crosshair_enabled", false)
        val lowSens = prefs.getBoolean("main_low_sens", false)

        return MainPanelPreferences(
            fovDegrees = fov,
            optimizeGame = optimize,
            stabilizeAim = stabilize,
            miraEnabled = mira,
            sensibilidadeBaixa = lowSens
        )
    }

    fun updateCrosshairEnabled(enabled: Boolean) {
        prefs.edit()
            .putBoolean("crosshair_enabled", enabled)
            .apply()
        _crosshairState.value = _crosshairState.value.copy(isEnabled = enabled)
        _panelPrefs.value = _panelPrefs.value.copy(miraEnabled = enabled)
    }

    fun updatePanelOpen(open: Boolean) {
        prefs.edit().putBoolean("crosshair_panel_open", open).apply()
        _crosshairState.value = _crosshairState.value.copy(isPanelOpen = open)
    }

    fun updateQuickHidden(hidden: Boolean) {
        prefs.edit().putBoolean("crosshair_quick_hidden", hidden).apply()
        _crosshairState.value = _crosshairState.value.copy(isQuickHidden = hidden)
    }

    fun updateStyle(style: CrosshairStyle) {
        prefs.edit().putString("crosshair_style", style.name).apply()
        _crosshairState.value = _crosshairState.value.copy(style = style)
    }

    fun updateColor(color: CrosshairColor) {
        prefs.edit().putString("crosshair_color", color.name).apply()
        _crosshairState.value = _crosshairState.value.copy(color = color)
    }

    fun updateSize(sizeDp: Float) {
        val clamped = sizeDp.coerceIn(14f, 80f)
        prefs.edit().putFloat("crosshair_size", clamped).apply()
        _crosshairState.value = _crosshairState.value.copy(sizeDp = clamped)
    }

    fun updateOpacity(opacity: Float) {
        val clamped = opacity.coerceIn(0.15f, 1.0f)
        prefs.edit().putFloat("crosshair_opacity", clamped).apply()
        _crosshairState.value = _crosshairState.value.copy(opacity = clamped)
    }

    fun updateFov(fov: Int) {
        val clamped = fov.coerceIn(60, 120)
        prefs.edit().putInt("main_fov", clamped).apply()
        _panelPrefs.value = _panelPrefs.value.copy(fovDegrees = clamped)
    }

    fun updateOptimizeGame(enabled: Boolean) {
        prefs.edit().putBoolean("main_optimize", enabled).apply()
        _panelPrefs.value = _panelPrefs.value.copy(optimizeGame = enabled)
    }

    fun updateStabilizeAim(enabled: Boolean) {
        prefs.edit()
            .putBoolean("main_stabilize", enabled)
            .putBoolean("crosshair_stabilized", enabled)
            .apply()
        _panelPrefs.value = _panelPrefs.value.copy(stabilizeAim = enabled)
        _crosshairState.value = _crosshairState.value.copy(stabilized = enabled)
    }

    fun updateSensibilidadeBaixa(enabled: Boolean) {
        prefs.edit().putBoolean("main_low_sens", enabled).apply()
        _panelPrefs.value = _panelPrefs.value.copy(sensibilidadeBaixa = enabled)
    }

    companion object {
        @Volatile
        private var INSTANCE: CrosshairPreferencesRepository? = null

        fun getInstance(context: Context): CrosshairPreferencesRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CrosshairPreferencesRepository(context).also { INSTANCE = it }
            }
        }
    }
}
