package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberCard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonRedBright
import com.example.ui.theme.PureBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextRedAccent
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.BoosterViewModel

@Composable
fun FpsGraphicsScreen(
    viewModel: BoosterViewModel,
    modifier: Modifier = Modifier
) {
    val currentFps by viewModel.realtimeFps.collectAsState()
    val deviceInfo by viewModel.deviceInfo.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .testTag("fps_graphics_screen")
    ) {
        Text(
            text = "FPS & GAME PERFORMANCE",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                fontSize = 20.sp,
                letterSpacing = 0.5.sp
            )
        )
        Text(
            text = "Real frame telemetry, display refresh rate & Free Fire graphics advisor",
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextSecondary,
                fontSize = 12.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // REAL-TIME DISPLAY RENDER FPS METER
        CyberCard(isHighlighted = true) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "REAL-TIME RENDER METER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonRed,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Android Choreographer API",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Display: ${deviceInfo.refreshRateHz}Hz",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$currentFps",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 64.sp,
                            color = NeonRed
                        )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "FPS",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = if (currentFps >= 55) "Smooth Sync" else "Minor Jitter",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (currentFps >= 55) NeonGreen else NeonOrange,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Measures exact real-time UI/Surface rendering frame output without synthetic emulation.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SAMSUNG HARDWARE IN-GAME FPS MONITORING GUIDE
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "How to Monitor FPS Inside Free Fire MAX",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Android application sandboxing strictly forbids third-party apps from reading GPU framebuffers of other applications. To view true in-game hardware FPS inside Free Fire MAX on your Samsung Galaxy M15 5G:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    MethodStepItem(
                        step = "1",
                        title = "Samsung Game Plugins (Perf Z)",
                        description = "Install Perf Z from Samsung Galaxy Store to get Samsung's official hardware FPS, GPU, and CPU overlay."
                    )
                    MethodStepItem(
                        step = "2",
                        title = "Developer Options: GPUWatch",
                        description = "Samsung's built-in GPUWatch provides real-time Vulkan/OpenGL framerate directly on screen with 0% battery penalty."
                    )
                    MethodStepItem(
                        step = "3",
                        title = "Honest Hardware Disclosure",
                        description = "No third-party app can unlock 90/120 FPS if the game engine does not support it for your chipset. Beware of fake booster claims."
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.dispatchAction("GAME_BOOSTER") },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderHighlight)
                    ) {
                        Text("Game Booster", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { viewModel.dispatchAction("DEV_SETTINGS") },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderHighlight)
                    ) {
                        Text("GPUWatch Settings", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // FREE FIRE / FREE FIRE MAX GRAPHICS ADVISOR FOR M15 5G
        Text(
            text = "FREE FIRE MAX RECOMMENDED GRAPHICS",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                color = TextRedAccent,
                letterSpacing = 1.sp,
                fontSize = 12.sp
            )
        )
        Text(
            text = "Optimized for MediaTek Dimensity 6100+ & 6GB RAM",
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextSecondary,
                fontSize = 11.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        CyberCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GraphicSettingRow(
                    settingName = "Graphics Mode",
                    recommendedValue = "Smooth or Standard",
                    reason = "Keeps GPU temperature below 40°C and prevents thermal throttling during squad fights."
                )
                GraphicSettingRow(
                    settingName = "High FPS",
                    recommendedValue = "High (Enabled)",
                    reason = "Unlocks the 60 FPS cap to match your Galaxy M15 5G high refresh rate display."
                )
                GraphicSettingRow(
                    settingName = "Shadows",
                    recommendedValue = "OFF",
                    reason = "Dynamic real-time shadows consume up to 35% more GPU fill-rate. Keep OFF for zero stutter."
                )
                GraphicSettingRow(
                    settingName = "High Res (HD Textures)",
                    recommendedValue = "Normal",
                    reason = "Saves VRAM bandwidth on the 6GB LPDDR4X memory bus."
                )
                GraphicSettingRow(
                    settingName = "Visual Filter",
                    recommendedValue = "Vivid",
                    reason = "Enhances enemy visibility in grass without degrading frame rate."
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun MethodStepItem(
    step: String,
    title: String,
    description: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(NeonRedMuted(0.8f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = NeonRedBright,
                    fontSize = 11.sp
                )
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 13.sp
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            )
        }
    }
}

@Composable
fun GraphicSettingRow(
    settingName: String,
    recommendedValue: String,
    reason: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = settingName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = reason,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(DarkSurfaceVariant)
                .border(1.dp, NeonRed.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = recommendedValue,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = NeonRedBright,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp
                )
            )
        }
    }
}

private fun NeonRedMuted(alpha: Float): Color = Color(0xFF381018).copy(alpha = alpha)
