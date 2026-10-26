package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ReactionPhase
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
import com.example.ui.theme.NeonRedGlow
import com.example.ui.theme.NeonRedMuted
import com.example.ui.theme.PureBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextRedAccent
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.BoosterViewModel

@Composable
fun TouchSmoothnessScreen(
    viewModel: BoosterViewModel,
    modifier: Modifier = Modifier
) {
    val reactionState by viewModel.reactionState.collectAsState()
    val rapidTapState by viewModel.rapidTapState.collectAsState()
    val deviceInfo by viewModel.deviceInfo.collectAsState()

    val touchPoints = remember { mutableStateListOf<Offset>() }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .testTag("touch_smoothness_screen")
    ) {
        Text(
            text = "TOUCH & SMOOTHNESS TEST",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                fontSize = 20.sp,
                letterSpacing = 0.5.sp
            )
        )
        Text(
            text = "Measure tap latency, reaction speed, and optimize Samsung touch sensitivity",
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextSecondary,
                fontSize = 12.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // INTERACTIVE REACTION TIME TEST (Millisecond Precision)
        CyberCard(isHighlighted = reactionState.phase == ReactionPhase.READY_TO_TAP) {
            val targetColor by animateColorAsState(
                targetValue = when (reactionState.phase) {
                    ReactionPhase.IDLE -> DarkSurfaceVariant
                    ReactionPhase.WAITING_FOR_TRIGGER -> Color(0xFF4A1018) // Waiting dark red
                    ReactionPhase.READY_TO_TAP -> NeonRedBright // Vibrant neon flash
                    ReactionPhase.RESULT -> NeonGreen
                    ReactionPhase.TOO_EARLY -> NeonOrange
                },
                label = "reactionColor"
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = NeonRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "REACTION LATENCY TEST",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    if (reactionState.phase != ReactionPhase.IDLE) {
                        Text(
                            text = "RESET",
                            modifier = Modifier.clickable { viewModel.resetReactionTest() },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // The Interactive Tap Target
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(targetColor)
                        .border(
                            2.dp,
                            if (reactionState.phase == ReactionPhase.READY_TO_TAP) NeonRed else DarkBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            if (reactionState.phase == ReactionPhase.IDLE) {
                                viewModel.startReactionTest()
                            } else {
                                viewModel.onReactionTap()
                            }
                        }
                        .testTag("reaction_test_pad"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        when (reactionState.phase) {
                            ReactionPhase.IDLE -> {
                                Icon(
                                    imageVector = Icons.Default.TouchApp,
                                    contentDescription = null,
                                    tint = TextPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "TAP HERE TO START",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary,
                                        fontSize = 16.sp
                                    )
                                )
                                Text(
                                    text = "Wait for flash then tap immediately",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                                )
                            }
                            ReactionPhase.WAITING_FOR_TRIGGER -> {
                                Text(
                                    text = "WAIT FOR NEON RED...",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = "Do not tap yet!",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }
                            ReactionPhase.READY_TO_TAP -> {
                                Text(
                                    text = "TAP NOW!!",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        fontSize = 32.sp
                                    )
                                )
                            }
                            ReactionPhase.RESULT -> {
                                Text(
                                    text = "${reactionState.scoreMs} ms",
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = reactionState.message,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = PureBlack,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            ReactionPhase.TOO_EARLY -> {
                                Text(
                                    text = "TOO EARLY!",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Tap to try again",
                                    style = MaterialTheme.typography.bodySmall.copy(color = PureBlack)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // RAPID TAP SPEED (CPS) CHALLENGE
        CyberCard {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "RAPID TAP SPEED (5s CPS)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    if (rapidTapState.isActive) {
                        Text(
                            text = "${rapidTapState.remainingSeconds}s Left",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = NeonRedBright,
                                fontWeight = FontWeight.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (!rapidTapState.isActive && rapidTapState.tapCount == 0) {
                    Button(
                        onClick = { viewModel.startRapidTapTest() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonRed)
                    ) {
                        Text("Start 5-Second Tap Challenge", fontWeight = FontWeight.Bold)
                    }
                } else if (rapidTapState.isActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceVariant)
                            .border(1.dp, NeonRed, RoundedCornerShape(12.dp))
                            .clickable { viewModel.onRapidTap() }
                            .testTag("rapid_tap_surface"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${rapidTapState.tapCount}",
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = NeonRed
                                )
                            )
                            Text(
                                text = "TAP AS FAST AS YOU CAN!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Total Taps: ${rapidTapState.tapCount}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Speed: ${String.format("%.1f", rapidTapState.finalCps)} Taps/sec (CPS)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = NeonGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        OutlinedButton(
                            onClick = { viewModel.startRapidTapTest() },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonRed)
                        ) {
                            Text("Retry", color = NeonRed)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SAMSUNG GALAXY M15 5G TOUCH & SMOOTHNESS CHECKLIST
        Text(
            text = "GALAXY M15 5G LAG REDUCTION CHECKLIST",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                color = TextRedAccent,
                letterSpacing = 1.sp,
                fontSize = 12.sp
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        CyberCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ChecklistItem(
                    title = "Enable Touch Sensitivity (Screen Protectors)",
                    description = "Samsung Settings > Display > Touch Sensitivity. Essential if using tempered glass to eliminate missed headshots."
                )
                ChecklistItem(
                    title = "Set Motion Smoothness to 90Hz",
                    description = "Samsung Settings > Display > Motion Smoothness > High. Locks the display refresh rate to 90Hz for zero frame tearing."
                )
                ChecklistItem(
                    title = "Reduce Animation Scales to 0.5x",
                    description = "Developer Options > Window / Transition / Animator duration scale set to 0.5x. Doubles system navigation responsiveness."
                )
                ChecklistItem(
                    title = "Game Booster Priority Mode",
                    description = "Blocks incoming phone calls and closes non-essential background tasks for maximum MediaTek Dimensity bandwidth."
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SHORTCUT BUTTONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.dispatchAction("DISPLAY") },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderHighlight)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.DisplaySettings, contentDescription = null, tint = NeonRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Display (90Hz)", fontSize = 12.sp)
                }
            }

            OutlinedButton(
                onClick = { viewModel.dispatchAction("DEV_SETTINGS") },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderHighlight)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("0.5x Animations", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // HONEST DISCLOSURE
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = DarkSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = NeonGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Honest Hardware Disclosure: Android does not permit third-party applications to overclock touchscreen hardware sampling or rewrite kernel touch drivers. This app uses official Android touch callbacks and genuine Samsung display settings.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ChecklistItem(title: String, description: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(NeonGreen.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = NeonGreen,
                modifier = Modifier.size(13.dp)
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
