package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Info
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CrosshairColor
import com.example.model.CrosshairState
import com.example.model.CrosshairStyle

@Composable
fun AimingAssistPanel(
    crosshairState: CrosshairState,
    onEnabledChange: (Boolean) -> Unit,
    onQuickHideChange: (Boolean) -> Unit,
    onStyleChange: (CrosshairStyle) -> Unit,
    onColorChange: (CrosshairColor) -> Unit,
    onSizeChange: (Float) -> Unit,
    onOpacityChange: (Float) -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    showPreview: Boolean = true
) {
    var showQuickHideInfo by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("aiming_assist_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xF00D1629)
        ),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF223554))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            // Header: "Aiming Assist", ON/OFF Switch, Close 'X'
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Aiming Assist",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // Neon switch
                Switch(
                    checked = crosshairState.isEnabled,
                    onCheckedChange = onEnabledChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00F0FF),
                        checkedTrackColor = Color(0xFF0E3854),
                        uncheckedThumbColor = Color(0xFF64748B),
                        uncheckedTrackColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.testTag("aiming_assist_switch")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("aiming_assist_close_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Fechar",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Hide Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showQuickHideInfo = !showQuickHideInfo },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Hide",
                        color = Color(0xFFCBD5E1),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info Quick Hide",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(15.dp)
                    )
                }

                Switch(
                    checked = crosshairState.isQuickHidden,
                    onCheckedChange = onQuickHideChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00F0FF),
                        checkedTrackColor = Color(0xFF0E3854),
                        uncheckedThumbColor = Color(0xFF64748B),
                        uncheckedTrackColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.testTag("quick_hide_switch")
                )
            }

            if (showQuickHideInfo) {
                Text(
                    text = "Minimiza o painel para um botão flutuante discreto na borda da tela sem fechar a mira.",
                    color = Color(0xFF38BDF8),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            // Live Preview Box if requested
            if (showPreview) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF070C16))
                        .border(1.dp, Color(0xFF192A45), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CrosshairPreview(
                        style = crosshairState.style,
                        color = crosshairState.color,
                        sizeDp = crosshairState.sizeDp,
                        opacity = crosshairState.opacity
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Style Section
            Text(
                text = "Style",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val styles = listOf(
                    CrosshairStyle.DOT to "•",
                    CrosshairStyle.CIRCLE to "⊙",
                    CrosshairStyle.TARGET to "⌖",
                    CrosshairStyle.CROSS to "┼",
                    CrosshairStyle.SQUARE to "[+]"
                )

                styles.forEach { (style, symbol) ->
                    val isSelected = crosshairState.style == style
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0x22FF1744) else Color(0xFF0F1A2E))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFFFF1744) else Color(0xFF1E3250),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onStyleChange(style) }
                            .testTag("style_${style.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = symbol,
                            color = if (isSelected) Color(0xFFFF1744) else Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Colour Section
            Text(
                text = "Colour",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val colors = listOf(
                    CrosshairColor.WHITE,
                    CrosshairColor.RED,
                    CrosshairColor.YELLOW,
                    CrosshairColor.GREEN,
                    CrosshairColor.CYAN
                )

                colors.forEach { c ->
                    val isSelected = crosshairState.color == c
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(c.argb))
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color.White else Color(0xFF334155),
                                shape = CircleShape
                            )
                            .clickable { onColorChange(c) }
                            .testTag("color_${c.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Size Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Size",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp
                )
                Text(
                    text = "${crosshairState.sizeDp.toInt()} dp",
                    color = Color(0xFFFF3358),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Slider(
                value = crosshairState.sizeDp,
                onValueChange = onSizeChange,
                valueRange = 16f..72f,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color(0xFFFF1744),
                    inactiveTrackColor = Color(0xFF2A1520)
                ),
                modifier = Modifier.testTag("size_slider")
            )

            // Opacity Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Opacity",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp
                )
                Text(
                    text = "${(crosshairState.opacity * 100).toInt()}%",
                    color = Color(0xFFFF3358),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Slider(
                value = crosshairState.opacity,
                onValueChange = onOpacityChange,
                valueRange = 0.2f..1.0f,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color(0xFFFF1744),
                    inactiveTrackColor = Color(0xFF2A1520)
                ),
                modifier = Modifier.testTag("opacity_slider")
            )
        }
    }
}
