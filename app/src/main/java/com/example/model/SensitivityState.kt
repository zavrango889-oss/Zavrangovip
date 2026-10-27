package com.example.model

enum class SensitivityPresetType(val label: String, val tag: String) {
    ONE_TAP_HEADSHOT("One-Tap Full Red", "Headshot Precision"),
    RUSH_SHOTGUN("Rush SMG & Shotgun", "Aggressive Close-Range"),
    SNIPER_SUPPORT("AWM Long Range", "Sniper Support"),
    BALANCED_90HZ("Galaxy M15 90Hz", "AMOLED Balanced"),
    CUSTOM("Custom Calibration", "Player Custom")
}

data class SensitivityState(
    val general: Int = 195,
    val redDot: Int = 188,
    val scope2x: Int = 175,
    val scope4x: Int = 160,
    val scopeSniper: Int = 120,
    val freeLook: Int = 150,
    val dpi: Int = 720,
    val fireButtonSize: Int = 52,
    val presetType: SensitivityPresetType = SensitivityPresetType.ONE_TAP_HEADSHOT,
    val isSaved: Boolean = false
) {
    fun toClipboardText(deviceName: String = "Samsung Galaxy M15 5G"): String {
        return """
            🎯 ZAVRANGOFFZ - FREE FIRE MAX SENSI
            📱 Dispositivo: $deviceName
            ⚡ Estilo: ${presetType.label}
            ━━━━━━━━━━━━━━━━━━━━
            • Geral: $general
            • Red Dot: $redDot
            • Mira 2x: $scope2x
            • Mira 4x: $scope4x
            • Mira AWM: $scopeSniper
            • Olhadinha: $freeLook
            ━━━━━━━━━━━━━━━━━━━━
            ⚙️ DPI Recomendada: $dpi
            🔘 Botão de Tiro: $fireButtonSize%
            ━━━━━━━━━━━━━━━━━━━━
            ✅ ZAVRANGOFFZ PRO TUNED
        """.trimIndent()
    }

    companion object {
        fun createPreset(type: SensitivityPresetType): SensitivityState {
            return when (type) {
                SensitivityPresetType.ONE_TAP_HEADSHOT -> SensitivityState(
                    general = 198,
                    redDot = 194,
                    scope2x = 186,
                    scope4x = 175,
                    scopeSniper = 158,
                    freeLook = 180,
                    dpi = 720,
                    fireButtonSize = 52,
                    presetType = type
                )
                SensitivityPresetType.RUSH_SHOTGUN -> SensitivityState(
                    general = 200,
                    redDot = 198,
                    scope2x = 190,
                    scope4x = 178,
                    scopeSniper = 152,
                    freeLook = 195,
                    dpi = 840,
                    fireButtonSize = 62,
                    presetType = type
                )
                SensitivityPresetType.SNIPER_SUPPORT -> SensitivityState(
                    general = 175,
                    redDot = 168,
                    scope2x = 162,
                    scope4x = 158,
                    scopeSniper = 154,
                    freeLook = 160,
                    dpi = 560,
                    fireButtonSize = 48,
                    presetType = type
                )
                SensitivityPresetType.BALANCED_90HZ -> SensitivityState(
                    general = 188,
                    redDot = 182,
                    scope2x = 176,
                    scope4x = 168,
                    scopeSniper = 156,
                    freeLook = 175,
                    dpi = 600,
                    fireButtonSize = 55,
                    presetType = type
                )
                SensitivityPresetType.CUSTOM -> SensitivityState(
                    general = 185,
                    redDot = 180,
                    scope2x = 172,
                    scope4x = 165,
                    scopeSniper = 155,
                    freeLook = 170,
                    dpi = 640,
                    fireButtonSize = 54,
                    presetType = type
                )
            }
        }
    }
}
