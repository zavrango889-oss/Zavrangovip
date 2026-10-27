package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.model.SensitivityPresetType
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
import com.example.ui.theme.TextRedAccent
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.BoosterViewModel

@Composable
fun SensitivityScreen(
    viewModel: BoosterViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sensiState by viewModel.sensitivityState.collectAsState()
    val scrollState = rememberScrollState()

    var showSaveDialog by remember { mutableStateOf(false) }
    var newProfileName by remember { mutableStateOf("") }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "SALVAR PERFIL DE SENSI",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                )
            },
            text = {
                Column {
                    Text(
                        text = "Salve esta configuração calibrada para Free Fire MAX no seu Samsung Galaxy M15 5G.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = newProfileName,
                        onValueChange = { newProfileName = it },
                        label = { Text("Nome do Perfil") },
                        placeholder = { Text("Ex: Sensi One-Tap 90Hz") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = CyberBlueNeon,
                            unfocusedBorderColor = DarkBorderHighlight
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveCurrentSensitivityAsProfile(newProfileName)
                        showSaveDialog = false
                        newProfileName = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
                    modifier = Modifier.testTag("confirm_save_profile_btn")
                ) {
                    Text("Salvar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancelar", color = TextMuted)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("sensitivity_screen")
    ) {
        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(CyberBlueNeon)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GERADOR DE SENSIBILIDADE",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            fontSize = 18.sp,
                            letterSpacing = 0.8.sp
                        )
                    )
                }
                Text(
                    text = "Calibração oficial Free Fire MAX (0–200) • Galaxy M15 5G",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // PRESET SELECTION CHIPS
        Text(
            text = "ESTILO DE JOGO",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = CyberBlueNeon,
                letterSpacing = 1.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SensitivityPresetChip(
                title = "One-Tap Full Red",
                isSelected = sensiState.presetType == SensitivityPresetType.ONE_TAP_HEADSHOT,
                onClick = { viewModel.selectSensitivityPreset(SensitivityPresetType.ONE_TAP_HEADSHOT) },
                modifier = Modifier.weight(1f),
                testTag = "preset_one_tap"
            )
            SensitivityPresetChip(
                title = "Rush SMG / 12",
                isSelected = sensiState.presetType == SensitivityPresetType.RUSH_SHOTGUN,
                onClick = { viewModel.selectSensitivityPreset(SensitivityPresetType.RUSH_SHOTGUN) },
                modifier = Modifier.weight(1f),
                testTag = "preset_rush"
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SensitivityPresetChip(
                title = "Sniper AWM Suporte",
                isSelected = sensiState.presetType == SensitivityPresetType.SNIPER_SUPPORT,
                onClick = { viewModel.selectSensitivityPreset(SensitivityPresetType.SNIPER_SUPPORT) },
                modifier = Modifier.weight(1f),
                testTag = "preset_sniper"
            )
            SensitivityPresetChip(
                title = "M15 90Hz Balanceada",
                isSelected = sensiState.presetType == SensitivityPresetType.BALANCED_90HZ,
                onClick = { viewModel.selectSensitivityPreset(SensitivityPresetType.BALANCED_90HZ) },
                modifier = Modifier.weight(1f),
                testTag = "preset_balanced"
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // GENERATE BUTTON
        Button(
            onClick = { viewModel.generateCalibratedSensitivity() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("generate_sensitivity_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CyberBlueNeon,
                contentColor = PureBlack
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GERAR SENSIBILIDADE CALIBRADA",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // RESULT DISPLAY CARDS (Reference inspired layout)
        CyberCard {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VALORES OFICIAIS FF MAX (0-200)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Escala estrita 0 a 200",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeonGreen,
                            fontSize = 11.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sliders for 6 Free Fire sensitivities
                SensitivityControlRow(
                    label = "Geral",
                    value = sensiState.general,
                    onValueChange = { viewModel.setSensitivityGeneral(it) },
                    testTag = "sensi_slider_general"
                )

                SensitivityControlRow(
                    label = "Ponto Vermelho (Red Dot)",
                    value = sensiState.redDot,
                    onValueChange = { viewModel.setSensitivityRedDot(it) },
                    testTag = "sensi_slider_red_dot"
                )

                SensitivityControlRow(
                    label = "Mira 2x",
                    value = sensiState.scope2x,
                    onValueChange = { viewModel.setSensitivityScope2x(it) },
                    testTag = "sensi_slider_2x"
                )

                SensitivityControlRow(
                    label = "Mira 4x",
                    value = sensiState.scope4x,
                    onValueChange = { viewModel.setSensitivityScope4x(it) },
                    testTag = "sensi_slider_4x"
                )

                SensitivityControlRow(
                    label = "Mira AWM (Sniper)",
                    value = sensiState.scopeSniper,
                    onValueChange = { viewModel.setSensitivityScopeSniper(it) },
                    testTag = "sensi_slider_sniper"
                )

                SensitivityControlRow(
                    label = "Olhadinha",
                    value = sensiState.freeLook,
                    onValueChange = { viewModel.setSensitivityFreeLook(it) },
                    testTag = "sensi_slider_free_look"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // HARDWARE EXTRAS: DPI & FIRE BUTTON
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // DPI Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderHighlight)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "DPI RECOMENDADA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${sensiState.dpi}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = CyberBlueBright,
                            fontSize = 24.sp
                        ),
                        modifier = Modifier.testTag("dpi_value_text")
                    )
                    Text(
                        text = "Samsung M15 Padrão: 411",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Fire Button Size Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderHighlight)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "BOTÃO DE TIRO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${sensiState.fireButtonSize}%",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = NeonRedBright,
                            fontSize = 24.sp
                        ),
                        modifier = Modifier.testTag("button_size_value_text")
                    )
                    Text(
                        text = "Tamanho ideal para puxada",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ACTION BUTTONS: COPY & SAVE PROFILE
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("ZAVRANGOFFZ Sensi", sensiState.toClipboardText())
                    clipboard.setPrimaryClip(clip)
                    viewModel.showStatusMessage("Sensibilidade copiada para a área de transferência!")
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("copy_sensi_btn"),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBlueNeon),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberBlueNeon)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copiar",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "COPIAR SENSI",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = {
                    newProfileName = "${sensiState.presetType.label} M15"
                    showSaveDialog = true
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("save_profile_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonRed,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkAdd,
                    contentDescription = "Salvar Perfil",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SALVAR PERFIL",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
    }
}

@Composable
fun SensitivityPresetChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) DarkNavyCardElevated else DarkSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) CyberBlueNeon else DarkBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) CyberBlueNeon else TextMuted)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) TextPrimary else TextSecondary,
                    fontSize = 12.sp
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
fun SensitivityControlRow(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    fontSize = 13.sp
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onValueChange((value - 1).coerceIn(0, 200)) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Diminuir",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .width(46.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkSurfaceVariant)
                        .padding(vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$value",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = if (value >= 180) NeonRedBright else CyberBlueBright,
                            fontSize = 14.sp
                        )
                    )
                }
                IconButton(
                    onClick = { onValueChange((value + 1).coerceIn(0, 200)) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Aumentar",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt().coerceIn(0, 200)) },
            valueRange = 0f..200f,
            steps = 199,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            colors = SliderDefaults.colors(
                thumbColor = if (value >= 180) NeonRed else CyberBlueNeon,
                activeTrackColor = if (value >= 180) NeonRed else CyberBlueNeon,
                inactiveTrackColor = DarkSurfaceVariant
            )
        )
    }
}
