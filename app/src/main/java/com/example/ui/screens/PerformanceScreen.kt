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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
fun PerformanceScreen(
    viewModel: BoosterViewModel,
    modifier: Modifier = Modifier
) {
    val memoryInfo by viewModel.memoryInfo.collectAsState()
    val storageInfo by viewModel.storageInfo.collectAsState()
    val batteryInfo by viewModel.batteryInfo.collectAsState()
    val deviceInfo by viewModel.deviceInfo.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .testTag("performance_screen")
    ) {
        Text(
            text = "SMART PERFORMANCE CENTER",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                fontSize = 20.sp,
                letterSpacing = 0.5.sp
            )
        )
        Text(
            text = "Real hardware telemetry from Android System APIs",
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextSecondary,
                fontSize = 12.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // RAM TELEMETRY CARD (Specifically highlighting 6GB RAM on M15 5G)
        CyberCard(isHighlighted = memoryInfo.usedPercentage > 80) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = null,
                                tint = NeonRed,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "PHYSICAL RAM",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = if (deviceInfo.isSamsungGalaxyM15) "Samsung M15 5G (6GB LPDDR4X)" else "ActivityManager System Metric",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Text(
                        text = "${memoryInfo.usedPercentage}%",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = if (memoryInfo.usedPercentage > 80) NeonRed else NeonGreen
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = { memoryInfo.usedPercentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = if (memoryInfo.usedPercentage > 80) NeonRed else NeonGreen,
                    trackColor = DarkSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Used RAM", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp))
                        Text(
                            text = "${String.format("%.2f", memoryInfo.usedRamGb)} GB",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                    }
                    Column {
                        Text(text = "Available RAM", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp))
                        Text(
                            text = "${String.format("%.2f", memoryInfo.availableRamGb)} GB",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = NeonGreen)
                        )
                    }
                    Column {
                        Text(text = "Total System RAM", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp))
                        Text(
                            text = "${String.format("%.2f", memoryInfo.totalRamGb)} GB",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // STORAGE TELEMETRY CARD
        CyberCard {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SdStorage,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "INTERNAL STORAGE",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "StatFs UFS 2.2 Telemetry",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Text(
                        text = "${storageInfo.usedPercentage}%",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = if (storageInfo.usedPercentage > 90) NeonOrange else NeonCyan
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = { storageInfo.usedPercentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = NeonCyan,
                    trackColor = DarkSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Free Flash Space", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp))
                        Text(
                            text = "${String.format("%.1f", storageInfo.freeStorageGb)} GB",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = NeonCyan)
                        )
                    }
                    Column {
                        Text(text = "Used Storage", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp))
                        Text(
                            text = "${String.format("%.1f", storageInfo.usedStorageGb)} GB",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                    }
                    Column {
                        Text(text = "Total Storage", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp))
                        Text(
                            text = "${String.format("%.1f", storageInfo.totalStorageGb)} GB",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // BATTERY & THERMAL SENSOR CARD
        CyberCard {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeviceThermostat,
                                contentDescription = null,
                                tint = NeonOrange,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "BATTERY & THERMAL",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "${batteryInfo.status} • ${batteryInfo.pluggedSource}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Text(
                        text = "${batteryInfo.level}%",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = NeonGreen
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Temperature", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp))
                        Text(
                            text = "${batteryInfo.temperatureCelsius} °C",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (batteryInfo.temperatureCelsius > 40f) NeonRed else TextPrimary
                            )
                        )
                    }
                    Column {
                        Text(text = "Thermal State", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp))
                        Text(
                            text = batteryInfo.thermalStateCategory,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = NeonGreen)
                        )
                    }
                    Column {
                        Text(text = "Cell Voltage", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp))
                        Text(
                            text = "${String.format("%.2f", batteryInfo.voltageVolts)} V",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                    }
                    Column {
                        Text(text = "Health", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp))
                        Text(
                            text = batteryInfo.health,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = NeonGreen)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // HONEST ARCHITECTURE & DISCLOSURE CARD (Mandatory per prompt)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = DarkSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Real Android Memory Management Notice",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Android 14/15/16 keeps recently opened apps in RAM on purpose to launch them instantly without wasting battery reloading from flash. Aggressive RAM clearing can actually cause apps to restart in the background, consuming MORE battery and CPU cycles. For genuine optimization on Samsung Galaxy M15 5G, manage deep sleeping apps in Samsung Device Care.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SHORTCUTS TO GENUINE SYSTEM SETTINGS
        Text(
            text = "SYSTEM SETTINGS SHORTCUTS",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                color = TextRedAccent,
                letterSpacing = 1.sp,
                fontSize = 12.sp
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { viewModel.dispatchAction("DEVICE_CARE") },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextPrimary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderHighlight)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = NeonRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Open Samsung Device Care (Memory & Deep Sleep)")
                    }
                    Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }

            OutlinedButton(
                onClick = { viewModel.dispatchAction("BATTERY") },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextPrimary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderHighlight)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.BatteryChargingFull, contentDescription = null, tint = NeonOrange, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Open Battery Optimization Settings")
                    }
                    Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }

            OutlinedButton(
                onClick = { viewModel.dispatchAction("STORAGE") },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextPrimary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderHighlight)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.SdStorage, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Open Storage Settings & Clean Downloads")
                    }
                    Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
