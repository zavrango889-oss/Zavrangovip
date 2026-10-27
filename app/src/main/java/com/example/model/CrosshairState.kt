package com.example.model

enum class CrosshairStyle(val id: String, val label: String) {
    DOT("dot", "Dot"),
    CIRCLE("circle", "Circle"),
    TARGET("target", "Target"),
    CROSS("cross", "Cross"),
    SQUARE("square", "Square")
}

enum class CrosshairColor(val argb: Long, val hexString: String, val label: String) {
    WHITE(0xFFFFFFFF, "#FFFFFF", "White"),
    RED(0xFFFF1744, "#FF1744", "Red"),
    YELLOW(0xFFFFEB3B, "#FFEB3B", "Yellow"),
    GREEN(0xFF00E676, "#00E676", "Green"),
    CYAN(0xFF00E5FF, "#00E5FF", "Cyan")
}

data class CrosshairState(
    val isEnabled: Boolean = false,
    val isPanelOpen: Boolean = false,
    val isQuickHidden: Boolean = false,
    val style: CrosshairStyle = CrosshairStyle.DOT,
    val color: CrosshairColor = CrosshairColor.RED,
    val sizeDp: Float = 36f,
    val opacity: Float = 0.95f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val stabilized: Boolean = true
)

data class FreeFireSensiItem(
    val id: String,
    val label: String,
    val scopeName: String,
    val value: Int // Strictly 150..200
)

data class FreeFireSensitivityProfile(
    val geral: Int = 196,
    val pontoVermelho: Int = 188,
    val mira2x: Int = 176,
    val mira4x: Int = 168,
    val miraAwm: Int = 155,
    val olhadinha: Int = 182,
    val dpi: Int = 600,
    val fireButtonPercent: Int = 52
) {
    init {
        require(geral in 150..200) { "Geral must be between 150 and 200" }
        require(pontoVermelho in 150..200) { "Ponto Vermelho must be between 150 and 200" }
        require(mira2x in 150..200) { "Mira 2x must be between 150 and 200" }
        require(mira4x in 150..200) { "Mira 4x must be between 150 and 200" }
        require(miraAwm in 150..200) { "Mira AWM must be between 150 and 200" }
        require(olhadinha in 150..200) { "Olhadinha must be between 150 and 200" }
    }

    fun toList(): List<FreeFireSensiItem> = listOf(
        FreeFireSensiItem("geral", "Geral", "Visão geral e movimentação", geral),
        FreeFireSensiItem("red_dot", "Ponto Vermelho", "Mira sem zoom / Red Dot", pontoVermelho),
        FreeFireSensiItem("mira_2x", "Mira 2x", "Precisão média distância", mira2x),
        FreeFireSensiItem("mira_4x", "Mira 4x", "Fuzil de assalto e DMRs", mira4x),
        FreeFireSensiItem("mira_awm", "Mira AWM", "Snipers e controle de arrasto", miraAwm),
        FreeFireSensiItem("olhadinha", "Olhadinha", "Visão periférica de 360°", olhadinha)
    )

    fun toShareableText(): String {
        return """
            🎯 ZAVRANGOFFZ — SENSIBILIDADE CALIBRADA
            📱 Samsung Galaxy M15 5G (90Hz Super AMOLED)
            🎮 Garena Free Fire MAX (Escala 150-200)
            ━━━━━━━━━━━━━━━━━━━━━━
            • Geral: $geral
            • Ponto Vermelho: $pontoVermelho
            • Mira 2x: $mira2x
            • Mira 4x: $mira4x
            • Mira AWM: $miraAwm
            • Olhadinha: $olhadinha
            ━━━━━━━━━━━━━━━━━━━━━━
            ⚙️ DPI: $dpi | Botão de Tiro: $fireButtonPercent%
            🚀 SISTEMA ONLINE — ZAVRANGOFFZ
        """.trimIndent()
    }
}
