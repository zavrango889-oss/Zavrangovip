package com.example.ui.screens

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.CrosshairPreferencesRepository
import com.example.model.CrosshairColor
import com.example.model.CrosshairState
import com.example.model.CrosshairStyle
import com.example.service.CrosshairOverlayService
import com.example.ui.components.AimingAssistPanel
import com.example.ui.components.SensitivityGeneratorSheet

@Composable
fun PainelDeAuxilioScreen(
    repository: CrosshairPreferencesRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val panelPrefs by repository.panelPrefs.collectAsState()
    val crosshairState by repository.crosshairState.collectAsState()

    var showAimingAssistDialog by remember { mutableStateOf(false) }
    var showSensiGeneratorDialog by remember { mutableStateOf(false) }
    var statusFeedback by remember { mutableStateOf<String?>(null) }

    val hasOverlayPerm = Settings.canDrawOverlays(context)

    // Helper to start or stop overlay service based on mira switch
    val toggleMira: (Boolean) -> Unit = { enabled ->
        if (enabled) {
            if (!Settings.canDrawOverlays(context)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                context.startActivity(intent)
                Toast.makeText(context, "Conceda a permissão de sobreposição para exibir a mira", Toast.LENGTH_LONG).show()
            } else {
                repository.updateCrosshairEnabled(true)
                CrosshairOverlayService.start(context)
                statusFeedback = "Mira flutuante ativada sobre a tela!"
            }
        } else {
            repository.updateCrosshairEnabled(false)
            CrosshairOverlayService.stop(context)
            statusFeedback = "Mira flutuante desativada."
        }
    }

    val launchFreeFireMax: () -> Unit = {
        val pm = context.packageManager
        val maxIntent = pm.getLaunchIntentForPackage("com.dts.freefiremax")
            ?: pm.getLaunchIntentForPackage("com.dts.freefireth")

        if (maxIntent != null) {
            maxIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(maxIntent)
            Toast.makeText(context, "Iniciando Free Fire MAX...", Toast.LENGTH_SHORT).show()
        } else {
            try {
                val storeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.dts.freefiremax")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(storeIntent)
            } catch (_: Exception) {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.dts.freefiremax")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // =========================================================================
            // TOP HEADER: AVATAR + ZAVRANGOFFZ + SISTEMA ONLINE
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Avatar with Cyan Glow Ring
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F1E33))
                        .border(2.dp, Color(0xFF00F0FF), CircleShape)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.zavrango_logo),
                        contentDescription = "ZAVRANGOFFZ Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "ZAVRANGOFFZ",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00F0FF))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SISTEMA ONLINE",
                            color = Color(0xFF00F0FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // =========================================================================
            // MAIN CARD CONTAINER: PAINEL DE AUXÍLIO
            // =========================================================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("painel_de_auxilio_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xF20B1324)
                ),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF1B2E4D))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Top banner row inside card: "CONFIGURAÇÃO ATIVA" + Launch Game Icon
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CONFIGURAÇÃO ATIVA",
                            color = Color(0xFF00F0FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        IconButton(
                            onClick = launchFreeFireMax,
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("header_launch_game_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Abrir Free Fire MAX",
                                tint = Color(0xFF00F0FF),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Section Title
                    Text(
                        text = "Painel de auxílio",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                    )

                    // -----------------------------------------------------------------
                    // 1. FOV SLIDER (Starting at 98°)
                    // -----------------------------------------------------------------
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .testTag("fov_control_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1A2F)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF192A45))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Speed,
                                        contentDescription = "FOV Icon",
                                        tint = Color(0xFF00F0FF),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "FOV",
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Campo de visão",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                // Badge displaying degrees (e.g. 98°)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF091424))
                                        .border(1.dp, Color(0xFF00F0FF), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 14.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = "${panelPrefs.fovDegrees}°",
                                        color = Color(0xFF00F0FF),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Teal Slider
                            Slider(
                                value = panelPrefs.fovDegrees.toFloat(),
                                onValueChange = { repository.updateFov(it.toInt()) },
                                valueRange = 60f..120f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF00F0FF),
                                    activeTrackColor = Color(0xFF00F0FF),
                                    inactiveTrackColor = Color(0xFF1E3250)
                                ),
                                modifier = Modifier.testTag("fov_slider")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "60°", color = Color(0xFF64748B), fontSize = 10.sp)
                                Text(text = "Referência de enquadramento", color = Color(0xFF475569), fontSize = 10.sp)
                                Text(text = "120°", color = Color(0xFF64748B), fontSize = 10.sp)
                            }
                        }
                    }

                    // -----------------------------------------------------------------
                    // 2. OTIMIZAR JOGO
                    // -----------------------------------------------------------------
                    ControlToggleItem(
                        icon = Icons.Filled.Bolt,
                        title = "Otimizar jogo",
                        subtitle = "Prioriza fluidez e resposta",
                        isChecked = panelPrefs.optimizeGame,
                        onCheckedChange = { active ->
                            repository.updateOptimizeGame(active)
                            if (active) {
                                Runtime.getRuntime().gc()
                                statusFeedback = "Otimização ativada: Cache liberado e prioridade alta."
                            } else {
                                statusFeedback = "Otimização em modo padrão."
                            }
                        },
                        testTag = "otimizar_jogo_toggle"
                    )

                    // -----------------------------------------------------------------
                    // 3. ESTABILIZAR MIRA
                    // -----------------------------------------------------------------
                    ControlToggleItem(
                        icon = Icons.Filled.CenterFocusStrong,
                        title = "Estabilizar mira",
                        subtitle = "Movimento mais consistente",
                        isChecked = panelPrefs.stabilizeAim,
                        onCheckedChange = { active ->
                            repository.updateStabilizeAim(active)
                            statusFeedback = if (active) {
                                "Estabilização ativa: amortecimento de micro-tremores na mira."
                            } else {
                                "Estabilização desativada."
                            }
                        },
                        testTag = "estabilizar_mira_toggle"
                    )

                    // -----------------------------------------------------------------
                    // 4. MIRA (Cruz customizada no meio da tela)
                    // -----------------------------------------------------------------
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clickable { showAimingAssistDialog = true }
                            .testTag("mira_control_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1A2F)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF192A45))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Adjust,
                                    contentDescription = "Mira Icon",
                                    tint = Color(0xFF00F0FF),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Mira",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Cruz customizada no meio da tela",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = crosshairState.isEnabled,
                                    onCheckedChange = toggleMira,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color(0xFF00F0FF),
                                        checkedTrackColor = Color(0xFF0E3854),
                                        uncheckedThumbColor = Color(0xFF64748B),
                                        uncheckedTrackColor = Color(0xFF1E293B)
                                    ),
                                    modifier = Modifier.testTag("mira_switch")
                                )
                            }
                        }
                    }

                    // -----------------------------------------------------------------
                    // 5. SENSIBILIDADE BAIXA
                    // -----------------------------------------------------------------
                    ControlToggleItem(
                        icon = Icons.Filled.ArrowDownward,
                        title = "Sensibilidade baixa",
                        subtitle = "Controle mais preciso no disparo",
                        isChecked = panelPrefs.sensibilidadeBaixa,
                        onCheckedChange = { active ->
                            repository.updateSensibilidadeBaixa(active)
                            statusFeedback = if (active) {
                                "Perfil de sensibilidade baixa aplicado para tiros precisos."
                            } else {
                                "Sensibilidade dinâmica normal ativada."
                            }
                        },
                        testTag = "sensibilidade_baixa_toggle"
                    )

                    // -----------------------------------------------------------------
                    // 6. GERADOR DE SENSIBILIDADE (Strictly 150..200)
                    // -----------------------------------------------------------------
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clickable { showSensiGeneratorDialog = true }
                            .testTag("gerador_sensibilidade_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1A2F)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF192A45))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PhoneAndroid,
                                    contentDescription = "Gerador Icon",
                                    tint = Color(0xFF00F0FF),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Gerador de sensibilidade",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Garena Free Fire • Calibração 150-200",
                                        color = Color(0xFF00F0FF),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Abrir Gerador",
                                tint = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // OVERLAY PERMISSION HELPER (If not granted)
            // =========================================================================
            if (!hasOverlayPerm) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x33F59E0B)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.WarningAmber,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Permissão de Sobreposição Necessária",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Necessária para exibir a mira assistida e o painel sobre o Free Fire MAX.",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:${context.packageName}")
                                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Ativar", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // =========================================================================
            // ACTION: JOGAR FREE FIRE MAX
            // =========================================================================
            Button(
                onClick = launchFreeFireMax,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("launch_free_fire_max_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00F0FF),
                    contentColor = Color(0xFF060D19)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "JOGAR FREE FIRE MAX",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }

            // Status message toast/banner
            statusFeedback?.let { msg ->
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A2238)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F0FF))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF00F0FF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = msg,
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // =========================================================================
        // AIMING ASSIST MODAL DIALOG (Reference Image 2)
        // =========================================================================
        if (showAimingAssistDialog) {
            Dialog(
                onDismissRequest = { showAimingAssistDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AimingAssistPanel(
                        crosshairState = crosshairState,
                        onEnabledChange = toggleMira,
                        onQuickHideChange = { repository.updateQuickHidden(it) },
                        onStyleChange = { repository.updateStyle(it) },
                        onColorChange = { repository.updateColor(it) },
                        onSizeChange = { repository.updateSize(it) },
                        onOpacityChange = { repository.updateOpacity(it) },
                        onCloseClick = { showAimingAssistDialog = false }
                    )
                }
            }
        }

        // =========================================================================
        // SENSITIVITY GENERATOR MODAL DIALOG (Strictly 150..200)
        // =========================================================================
        if (showSensiGeneratorDialog) {
            Dialog(
                onDismissRequest = { showSensiGeneratorDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SensitivityGeneratorSheet(
                        onDismiss = { showSensiGeneratorDialog = false }
                    )
                }
            }
        }
    }
}

@Composable
fun ControlToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { onCheckedChange(!isChecked) }
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1A2F)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF192A45))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF00F0FF),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }
            }

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF00F0FF),
                    checkedTrackColor = Color(0xFF0E3854),
                    uncheckedThumbColor = Color(0xFF64748B),
                    uncheckedTrackColor = Color(0xFF1E293B)
                )
            )
        }
    }
}
