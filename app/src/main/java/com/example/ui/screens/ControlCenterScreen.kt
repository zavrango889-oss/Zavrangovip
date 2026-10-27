package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberToggleCard
import com.example.ui.components.GamerActionButton
import com.example.ui.components.MetricGaugesRow
import com.example.ui.theme.CyberBlueBright
import com.example.ui.theme.CyberBlueNeon
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonRedBright
import com.example.ui.theme.NeonRedGlow
import com.example.ui.theme.PureBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextRedAccent
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.BoosterViewModel

@Composable
fun ControlCenterScreen(
    viewModel: BoosterViewModel,
    onNavigateToSensi: () -> Unit,
    onNavigateToSmartCheck: () -> Unit,
    modifier: Modifier = Modifier
) {
    val boosterPrefs by viewModel.boosterPrefs.collectAsState()
    val memoryInfo by viewModel.memoryInfo.collectAsState()
    val batteryInfo by viewModel.batteryInfo.collectAsState()
    val deviceInfo by viewModel.deviceInfo.collectAsState()
    val currentFps by viewModel.realtimeFps.collectAsState()
    val statusMsg by viewModel.statusMessage.collectAsState()
    val pingMs by viewModel.pingMs.collectAsState()
    val pingStatus by viewModel.pingStatus.collectAsState()
    val isPinging by viewModel.isPinging.collectAsState()

    val isFreeFireMax = viewModel.repository.isFreeFireMaxInstalled()
    val isFreeFireStd = viewModel.repository.isFreeFireStandardInstalled()
    val gameTitle = when {
        isFreeFireMax -> "ABRIR FREE FIRE MAX"
        isFreeFireStd -> "ABRIR FREE FIRE"
        else -> "ABRIR FREE FIRE"
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("control_center_screen")
    ) {
        // TOP BAR WITH AVATAR / VIP TAG & ONLINE BADGE (from reference screenshots)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Profile Avatar with Neon Glow
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .border(2.dp, NeonRed, CircleShape)
                        .background(DarkSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.zavrango_logo),
                        contentDescription = "ZAVRANGOFFZ Avatar",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "ZAVRANGOFFZ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            letterSpacing = 1.sp,
                            fontSize = 16.sp
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(CyberBlueNeon)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SISTEMA ONLINE • M15 5G",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = CyberBlueNeon,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // Ping Badge
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { viewModel.runPingCheck() }
                    .testTag("ping_status_badge"),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderHighlight)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NetworkCheck,
                        contentDescription = "Ping",
                        tint = if ((pingMs ?: 999) < 60) NeonGreen else CyberBlueNeon,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isPinging) "..." else if (pingMs != null) "${pingMs}ms" else "Ping",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // STATUS TOAST (dismissible)
        if (statusMsg != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("status_toast"),
                color = DarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonRedGlow)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = statusMsg ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { viewModel.clearStatusMessage() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // REAL TELEMETRY GAUGES (RAM 6GB, Battery %, Temp °C, FPS)
        MetricGaugesRow(
            memoryInfo = memoryInfo,
            batteryInfo = batteryInfo,
            currentFps = currentFps,
            targetFps = boosterPrefs.targetFps,
            refreshRateHz = deviceInfo.refreshRateHz
        )

        Spacer(modifier = Modifier.height(16.dp))

        // FREE FIRE MAX STATUS BADGE
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isFreeFireMax || isFreeFireStd) NeonGreen else TextMuted)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isFreeFireMax) "FREE FIRE MAX DETECTADO" else if (isFreeFireStd) "FREE FIRE DETECTADO" else "FREE FIRE NÃO DETECTADO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isFreeFireMax || isFreeFireStd) NeonGreen else TextMuted,
                        fontSize = 11.sp
                    )
                )
            }

            Text(
                text = "Samsung One UI 7",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 10.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // BIG RED ACTION BUTTON: "ABRIR FREE FIRE" (From reference screenshot)
        GamerActionButton(
            text = gameTitle,
            icon = Icons.Default.PlayArrow,
            onClick = { viewModel.launchGameOrStore() },
            modifier = Modifier.testTag("launch_free_fire_main_btn")
        )

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION: CONTROLS & TOGGLES (Inspired by reference screenshot)
        Text(
            text = "PAINEL DE CONTROLE GAMER",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 1. Performance Mode Toggle
        CyberToggleCard(
            title = "Performance Mode",
            subtitle = "Melhora estabilidade, compacta memória e reduz latência",
            icon = Icons.Default.Bolt,
            isChecked = boosterPrefs.performanceMode,
            onCheckedChange = { viewModel.togglePerformanceMode(it) },
            testTag = "toggle_performance_mode"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Touch Optimization Toggle
        CyberToggleCard(
            title = "Touch Optimization",
            subtitle = "Otimização de resposta do toque na tela Super AMOLED",
            icon = Icons.Default.Fingerprint,
            isChecked = boosterPrefs.touchOptimization,
            onCheckedChange = { viewModel.toggleTouchOptimization(it) },
            testTag = "toggle_touch_optimization"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Precision Mode Toggle
        CyberToggleCard(
            title = "Precision Mode",
            subtitle = "Ajustes avançados de controle, DPI e mira estrita 0-200",
            icon = Icons.Default.CenterFocusStrong,
            isChecked = boosterPrefs.precisionMode,
            onCheckedChange = { viewModel.togglePrecisionMode(it) },
            testTag = "toggle_precision_mode"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 4. Protection System Toggle
        CyberToggleCard(
            title = "Protection System",
            subtitle = "Monitor térmico do Dimensity 6100+ para evitar thermal throttling",
            icon = Icons.Default.Shield,
            isChecked = boosterPrefs.protectionSystem,
            onCheckedChange = { viewModel.toggleProtectionSystem(it) },
            testTag = "toggle_protection_system"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 5. FPS Booster Toggle
        CyberToggleCard(
            title = "FPS Booster (90Hz)",
            subtitle = "Alinhamento com taxa de atualização da tela Super AMOLED",
            icon = Icons.Default.RocketLaunch,
            isChecked = boosterPrefs.fpsBooster,
            onCheckedChange = { viewModel.toggleFpsBooster(it) },
            testTag = "toggle_fps_booster"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // QUICK SHORTCUT CARDS: Sensi Generator & Smart Diagnostic
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Sensi Generator Quick Card
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onNavigateToSensi)
                    .testTag("quick_sensi_card"),
                shape = RoundedCornerShape(14.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBlueNeon.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = CyberBlueBright,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "GERADOR SENSI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Escala 0–200 FF MAX",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            // Smart Diagnostic Quick Card
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onNavigateToSmartCheck)
                    .testTag("quick_diagnostic_card"),
                shape = RoundedCornerShape(14.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonRedGlow)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Icon(
                        imageVector = Icons.Default.CleaningServices,
                        contentDescription = null,
                        tint = NeonRedBright,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "SMART CHECK",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Diagnóstico do M15 5G",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
