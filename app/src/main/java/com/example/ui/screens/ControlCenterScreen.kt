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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
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
    onNavigateToSmartCheck: () -> Unit,
    modifier: Modifier = Modifier
) {
    val boosterPrefs by viewModel.boosterPrefs.collectAsState()
    val memoryInfo by viewModel.memoryInfo.collectAsState()
    val batteryInfo by viewModel.batteryInfo.collectAsState()
    val deviceInfo by viewModel.deviceInfo.collectAsState()
    val currentFps by viewModel.realtimeFps.collectAsState()
    val statusMsg by viewModel.statusMessage.collectAsState()

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
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .testTag("control_center_screen")
    ) {
        // VIP Header matching the user screenshot
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Demon/Gamer Avatar with glowing neon red ring
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                        .border(2.dp, NeonRed, CircleShape)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.zavrango_logo),
                        contentDescription = "Gamer Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ZAVRANGO",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                fontSize = 18.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonRed)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "VIP",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (deviceInfo.isSamsungGalaxyM15) "Galaxy M15 5G • Acesso permanente" else "${deviceInfo.model} • Acesso permanente",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // Status chip
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(NeonGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ONLINE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        // Live Telemetry bar
        Spacer(modifier = Modifier.height(12.dp))
        MetricGaugesRow(
            ramText = "${String.format("%.1f", memoryInfo.availableRamGb)} GB Free",
            ramPct = memoryInfo.usedPercentage,
            tempText = "${batteryInfo.temperatureCelsius}°C",
            tempCategory = batteryInfo.thermalStateCategory,
            fpsText = "$currentFps FPS"
        )

        // Status banner if any
        if (statusMsg != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = DarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonRed.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = NeonRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = statusMsg ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                        )
                    }
                    IconButton(
                        onClick = { viewModel.clearStatusMessage() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section Title
        Text(
            text = "GAMING CONTROL CENTER",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                color = TextRedAccent,
                letterSpacing = 1.sp,
                fontSize = 12.sp
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        // THE 5 TOGGLES MATCHING THE SCREENSHOT EXACTLY
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Performance Mode
            CyberToggleCard(
                title = "Performance Mode",
                subtitle = "Melhora estabilidade e desempenho do dispositivo",
                icon = Icons.Default.Bolt,
                isChecked = boosterPrefs.performanceMode,
                onCheckedChange = { viewModel.togglePerformanceMode(it) },
                testTag = "toggle_performance_mode"
            )

            // 2. Touch Optimization
            CyberToggleCard(
                title = "Touch Optimization",
                subtitle = "Otimização de resposta do toque na tela",
                icon = Icons.Default.Fingerprint,
                isChecked = boosterPrefs.touchOptimization,
                onCheckedChange = { viewModel.toggleTouchOptimization(it) },
                testTag = "toggle_touch_optimization"
            )

            // 3. Precision Mode
            CyberToggleCard(
                title = "Precision Mode",
                subtitle = "Ajustes avançados de controle e precisão",
                icon = Icons.Default.CenterFocusStrong,
                isChecked = boosterPrefs.precisionMode,
                onCheckedChange = { viewModel.togglePrecisionMode(it) },
                testTag = "toggle_precision_mode"
            )

            // 4. Protection System
            CyberToggleCard(
                title = "Protection System",
                subtitle = "Sistema de proteção e estabilidade térmica",
                icon = Icons.Default.Security,
                isChecked = boosterPrefs.protectionSystem,
                onCheckedChange = { viewModel.toggleProtectionSystem(it) },
                testTag = "toggle_protection_system"
            )

            // 5. FPS Booster
            CyberToggleCard(
                title = "FPS Booster",
                subtitle = "Otimização para maior taxa de quadros e sincronização",
                icon = Icons.Default.RocketLaunch,
                isChecked = boosterPrefs.fpsBooster,
                onCheckedChange = { viewModel.toggleFpsBooster(it) },
                testTag = "toggle_fps_booster"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Primary Action Button matching user screenshot: "▶ ABRIR FREE FIRE"
        GamerActionButton(
            text = gameTitle,
            icon = Icons.Default.PlayArrow,
            onClick = { viewModel.launchGameOrStore() },
            testTag = "btn_abrir_free_fire"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Assistant Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CyberCard(
                modifier = Modifier.weight(1f),
                onClick = onNavigateToSmartCheck
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CleaningServices,
                        contentDescription = null,
                        tint = NeonRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "SMART CHECK",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Auto Diagnose",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            CyberCard(
                modifier = Modifier.weight(1f),
                onClick = { viewModel.dispatchAction("DEVICE_CARE") }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "SAMSUNG CARE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Deep Sleep Apps",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
