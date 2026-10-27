package com.example.ui.screens

import android.content.Context
import android.os.Build
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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.GameProfileEntity
import com.example.ui.components.CyberCard
import com.example.ui.theme.CyberBlueBright
import com.example.ui.theme.CyberBlueNeon
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkNavyCardElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonRedBright
import com.example.ui.theme.NeonRedMuted
import com.example.ui.theme.PureBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.BoosterViewModel

@Composable
fun ProfilesAndSettingsScreen(
    viewModel: BoosterViewModel,
    onNavigateToSensi: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val profiles by viewModel.gameProfiles.collectAsState()
    val deviceInfo by viewModel.deviceInfo.collectAsState()
    val isDndGranted by viewModel.isDndGranted.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "RESTAURAR PADRÕES?",
                    fontWeight = FontWeight.Black,
                    color = NeonRedBright
                )
            },
            text = {
                Text(
                    text = "Isto irá restaurar os 4 perfis de sensibilidade padrão calibrados para o Samsung Galaxy M15 5G e limpar cache local.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllProfiles()
                        viewModel.repository.performPermittedLocalTrim()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
                    modifier = Modifier.testTag("confirm_reset_all_btn")
                ) {
                    Text("Redefinir", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancelar", color = TextMuted)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(top = 10.dp)
            .testTag("profiles_settings_screen")
    ) {
        // TOP HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(CyberBlueNeon)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "PERFIS & CONFIGURAÇÕES",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        letterSpacing = 0.8.sp
                    )
                )
                Text(
                    text = "Banco de dados local e controle de permissões do sistema",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // TAB ROW: Profiles vs Settings
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface,
            contentColor = CyberBlueNeon,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = if (selectedTab == 0) CyberBlueNeon else NeonRed,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = "PERFIS SALVOS (${profiles.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (selectedTab == 0) CyberBlueBright else TextSecondary
                    )
                },
                modifier = Modifier.testTag("tab_profiles")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = "SISTEMA & PERMISSÕES",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (selectedTab == 1) NeonRedBright else TextSecondary
                    )
                },
                modifier = Modifier.testTag("tab_settings")
            )
        }

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            if (selectedTab == 0) {
                // =========================================================================
                // TAB 0: SAVED GAME PROFILES (ROOM DATABASE)
                // =========================================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PERFIS LOCAIS DE SENSIBILIDADE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                    )
                    TextButton(
                        onClick = { showResetDialog = true },
                        modifier = Modifier.testTag("reset_profiles_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Restaurar Padrões", color = TextMuted, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (profiles.isEmpty()) {
                    CyberCard {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Nenhum perfil salvo no banco de dados.",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { viewModel.resetAllProfiles() },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlueNeon)
                            ) {
                                Text("Carregar Perfis Recomendados", color = PureBlack, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    profiles.forEach { profile ->
                        SavedProfileCard(
                            profile = profile,
                            onLoad = {
                                viewModel.loadProfileIntoSensitivity(profile)
                                onNavigateToSensi()
                            },
                            onDelete = { viewModel.deleteProfile(profile.id) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            } else {
                // =========================================================================
                // TAB 1: SYSTEM SPECS, PERMISSIONS & RESET CONTROLS
                // =========================================================================
                Text(
                    text = "DISPOSITIVO & HARDWARE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = CyberBlueNeon,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                CyberCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Devices,
                                contentDescription = null,
                                tint = CyberBlueBright,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Samsung Galaxy M15 5G",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Configuração Otimizada • 6GB RAM LPDDR4X",
                                    fontSize = 11.sp,
                                    color = NeonGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        SpecsDetailRow("SoC / Chipset", "MediaTek Dimensity 6100+ (6nm)")
                        SpecsDetailRow("Processador Gráfico", "ARM Mali-G57 MC2")
                        SpecsDetailRow("Memória RAM", "6 GB Física + RAM Plus")
                        SpecsDetailRow("Tela", "6.5\" Super AMOLED FHD+ (90Hz)")
                        SpecsDetailRow("Bateria", "6000 mAh (Suporta 25W)")
                        SpecsDetailRow("Sistema Operacional", "Android 16 • One UI")
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // PERMISSIONS MANAGER
                Text(
                    text = "CENTRAL DE PERMISSÕES DO ANDROID",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeonRedBright,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                // DND Policy Access Card
                PermissionControlCard(
                    title = "Acesso Não Perturbe (DND)",
                    description = "Permite ao ZAVRANGOFFZ silenciar notificações pop-up e chamadas durante partidas ranqueadas.",
                    isGranted = isDndGranted,
                    actionText = if (isDndGranted) "Permissão Concedida" else "Conceder no Sistema",
                    onAction = { viewModel.dispatchAction("DND_POLICY") },
                    testTag = "perm_dnd_btn"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Notifications
                PermissionControlCard(
                    title = "Alertas do Sistema",
                    description = "Permite enviar avisos térmicos e lembretes de calibração.",
                    isGranted = true,
                    actionText = "Configurar",
                    onAction = { viewModel.dispatchAction("NOTIFICATIONS") },
                    testTag = "perm_notif_btn"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Battery Optimization
                PermissionControlCard(
                    title = "Otimização de Bateria",
                    description = "Evite que o sistema mate o app em segundo plano durante partidas.",
                    isGranted = true,
                    actionText = "Verificar Bateria",
                    onAction = { viewModel.dispatchAction("BATTERY") },
                    testTag = "perm_battery_btn"
                )

                Spacer(modifier = Modifier.height(18.dp))

                // SYSTEM MAINTENANCE & CACHE CLEANING
                Text(
                    text = "MANUTENÇÃO & REDEFINIÇÃO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                CyberCard {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Limpeza de Cache do App",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Limpa dados temporários e solicita coleta de lixo JVM.",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            Button(
                                onClick = {
                                    val freed = viewModel.repository.performPermittedLocalTrim()
                                    val mb = freed / (1024 * 1024)
                                    viewModel.showStatusMessage("Cache limpo! ${mb}MB liberados.")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("clean_cache_btn")
                            ) {
                                Icon(Icons.Default.CleaningServices, contentDescription = null, tint = CyberBlueBright, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Limpar", color = TextPrimary, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Redefinir Todos os Perfis",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Restaura os perfis de fábrica para Free Fire MAX.",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            OutlinedButton(
                                onClick = { showResetDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonRedMuted),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonRedBright),
                                modifier = Modifier.testTag("reset_all_settings_btn")
                            ) {
                                Icon(Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Redefinir", fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SavedProfileCard(
    profile: GameProfileEntity,
    onLoad: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .testTag("profile_item_${profile.id}"),
        shape = RoundedCornerShape(14.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderHighlight)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = profile.profileName,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = profile.playstyleTag,
                        fontSize = 11.sp,
                        color = CyberBlueNeon,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp).testTag("delete_profile_${profile.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Values summary badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ProfileMiniStat("Geral", "${profile.general}")
                ProfileMiniStat("RedDot", "${profile.redDot}")
                ProfileMiniStat("2x", "${profile.scope2x}")
                ProfileMiniStat("4x", "${profile.scope4x}")
                ProfileMiniStat("AWM", "${profile.scopeSniper}")
                ProfileMiniStat("DPI", "${profile.dpi}")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onLoad,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("load_profile_${profile.id}"),
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlueNeon),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = null,
                    tint = PureBlack,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CARREGAR ESTA SENSIBILIDADE",
                    color = PureBlack,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ProfileMiniStat(label: String, value: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(DarkSurfaceVariant)
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = TextMuted, fontSize = 9.sp)
            Text(value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    }
}

@Composable
fun SpecsDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
        Text(text = value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun PermissionControlCard(
    title: String,
    description: String,
    isGranted: Boolean,
    actionText: String,
    onAction: () -> Unit,
    testTag: String
) {
    CyberCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isGranted) NeonGreen else NeonRed)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            OutlinedButton(
                onClick = onAction,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isGranted) DarkBorderHighlight else NeonRed
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isGranted) TextSecondary else NeonRedBright
                ),
                modifier = Modifier.testTag(testTag)
            ) {
                Text(actionText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
