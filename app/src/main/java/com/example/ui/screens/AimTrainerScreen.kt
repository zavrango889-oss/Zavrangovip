package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HitType
import com.example.model.ReactionPhase
import com.example.ui.components.CyberCard
import com.example.ui.theme.CyberBlueBright
import com.example.ui.theme.CyberBlueNeon
import com.example.ui.theme.CyberBlueSubtle
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkNavyCardElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonGreen
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
fun AimTrainerScreen(
    viewModel: BoosterViewModel,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableIntStateOf(0) }
    val aimState by viewModel.aimTrainerState.collectAsState()
    val reactionState by viewModel.reactionState.collectAsState()
    val rapidTapState by viewModel.rapidTapState.collectAsState()
    val touchPoints = remember { mutableStateListOf<Offset>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(top = 10.dp)
            .testTag("aim_trainer_screen")
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
                    .background(NeonRed)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "AIM & TOUCH TRAINER",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        letterSpacing = 0.8.sp
                    )
                )
                Text(
                    text = "Treino de reflexo, mira em movimento e resposta de toque",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // TAB ROW (Aim Practice vs Touch & Reflex Tester)
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = DarkSurface,
            contentColor = NeonRed,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                    color = if (selectedSubTab == 0) NeonRed else CyberBlueNeon,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = {
                    Text(
                        text = "MIRA EM MOVIMENTO",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (selectedSubTab == 0) NeonRedBright else TextSecondary
                    )
                },
                modifier = Modifier.testTag("tab_moving_aim")
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = {
                    Text(
                        text = "REFLEXO & CPS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (selectedSubTab == 1) CyberBlueBright else TextSecondary
                    )
                },
                modifier = Modifier.testTag("tab_touch_reflex")
            )
        }

        if (selectedSubTab == 0) {
            // =========================================================================
            // SUB-TAB 0: MOVING TARGET AIM TRAINER (HEADSHOT MULTIPLIER)
            // =========================================================================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // SCOREBOARD ROW
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScoreStatCard(
                        title = "PONTOS",
                        value = "${aimState.score}",
                        highlightColor = NeonRedBright,
                        modifier = Modifier.weight(1f)
                    )
                    ScoreStatCard(
                        title = "CAPA (HS)",
                        value = "${aimState.headshots}",
                        highlightColor = NeonRed,
                        modifier = Modifier.weight(1f)
                    )
                    ScoreStatCard(
                        title = "PRECISÃO",
                        value = "${aimState.accuracyPct}%",
                        highlightColor = CyberBlueNeon,
                        modifier = Modifier.weight(1f)
                    )
                    ScoreStatCard(
                        title = "TEMPO",
                        value = "${aimState.remainingSeconds}s",
                        highlightColor = if (aimState.remainingSeconds <= 5) NeonRed else TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // INTERACTIVE MOVING TARGET CANVAS
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkNavyCardElevated)
                        .border(1.dp, if (aimState.isGameActive) NeonRedGlow else DarkBorder, RoundedCornerShape(16.dp))
                        .pointerInput(aimState.isGameActive) {
                            detectTapGestures { offset ->
                                viewModel.onAimTargetTap(
                                    canvasWidthPx = size.width.toFloat(),
                                    canvasHeightPx = size.height.toFloat(),
                                    tapXPx = offset.x,
                                    tapYPx = offset.y
                                )
                            }
                        }
                        .testTag("aim_target_canvas")
                ) {
                    // Canvas grid and Target rendering
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Subtle tactical grid lines
                        val gridStep = 40.dp.toPx()
                        var x = 0f
                        while (x < w) {
                            drawLine(
                                color = DarkBorder.copy(alpha = 0.35f),
                                start = Offset(x, 0f),
                                end = Offset(x, h),
                                strokeWidth = 1f
                            )
                            x += gridStep
                        }
                        var y = 0f
                        while (y < h) {
                            drawLine(
                                color = DarkBorder.copy(alpha = 0.35f),
                                start = Offset(0f, y),
                                end = Offset(w, y),
                                strokeWidth = 1f
                            )
                            y += gridStep
                        }

                        if (aimState.isGameActive) {
                            val centerX = aimState.targetNormX * w
                            val centerY = aimState.targetNormY * h
                            val radius = aimState.targetRadiusDp.dp.toPx()
                            val headshotRadius = radius * 0.35f

                            // Outer Body Target Circle (Dark Blue/Cyan)
                            drawCircle(
                                color = CyberBlueNeon.copy(alpha = 0.25f),
                                radius = radius,
                                center = Offset(centerX, centerY)
                            )
                            drawCircle(
                                color = CyberBlueNeon,
                                radius = radius,
                                center = Offset(centerX, centerY),
                                style = Stroke(width = 2.dp.toPx())
                            )

                            // Inner Headshot Circle (Neon Red Full Capa)
                            drawCircle(
                                color = NeonRed.copy(alpha = 0.7f),
                                radius = headshotRadius,
                                center = Offset(centerX, centerY)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = headshotRadius,
                                center = Offset(centerX, centerY),
                                style = Stroke(width = 2.dp.toPx())
                            )

                            // Tactical Reticle Crosshairs
                            val crossLen = radius * 1.3f
                            drawLine(
                                color = NeonRedBright,
                                start = Offset(centerX - crossLen, centerY),
                                end = Offset(centerX + crossLen, centerY),
                                strokeWidth = 1.5.dp.toPx()
                            )
                            drawLine(
                                color = NeonRedBright,
                                start = Offset(centerX, centerY - crossLen),
                                end = Offset(centerX, centerY + crossLen),
                                strokeWidth = 1.5.dp.toPx()
                            )
                        }
                    }

                    // OVERLAYS: Ready State or Game Over
                    if (!aimState.isGameActive) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(PureBlack.copy(alpha = 0.75f)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CenterFocusStrong,
                                contentDescription = null,
                                tint = NeonRedBright,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (aimState.isGameOver) "FIM DO TREINO!" else "TREINADOR DE MIRA FF MAX",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary,
                                    fontSize = 18.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (aimState.isGameOver)
                                    "Pontuação: ${aimState.score} • Capas: ${aimState.headshots} • Recorde: ${aimState.highScore}"
                                else
                                    "Acerte o centro vermelho para 'FULL VERMELHO' (+100pts)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.startAimTrainer() },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.testTag("start_aim_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (aimState.isGameOver) "JOGAR NOVAMENTE" else "INICIAR TREINO (30s)",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // HIT FEEDBACK BADGE
                    aimState.lastHitFeedback?.let { feedback ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when (feedback.type) {
                                        HitType.HEADSHOT -> NeonRed
                                        HitType.BODY -> CyberBlueNeon
                                        HitType.MISS -> DarkSurfaceVariant
                                    }
                                )
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = feedback.text,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = if (feedback.type == HitType.HEADSHOT) Color.White else PureBlack,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // CONTROLS & RECORD BAR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Recorde",
                            tint = CyberBlueBright,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Recorde: ${aimState.highScore} pts",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }

                    if (aimState.isGameActive) {
                        OutlinedButton(
                            onClick = { viewModel.stopAimTrainer() },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonRedMuted),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonRedBright),
                            modifier = Modifier.testTag("stop_aim_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Parar", fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            // =========================================================================
            // SUB-TAB 1: REACTION SPEED & RAPID CPS TESTER
            // =========================================================================
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // 1. REACTION LATENCY TEST
                Text(
                    text = "1. TESTE DE REFLEXO E LATÊNCIA",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeonRedBright,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                val reactionBoxColor by animateColorAsState(
                    targetValue = when (reactionState.phase) {
                        ReactionPhase.IDLE -> DarkSurface
                        ReactionPhase.WAITING_FOR_TRIGGER -> DarkSurfaceVariant
                        ReactionPhase.READY_TO_TAP -> NeonRed
                        ReactionPhase.RESULT -> DarkNavyCardElevated
                        ReactionPhase.TOO_EARLY -> Color(0xFF6B1A1A)
                    },
                    label = "reactionColor"
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { viewModel.onReactionTap() }
                        .testTag("reaction_pad"),
                    shape = RoundedCornerShape(18.dp),
                    color = reactionBoxColor,
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        if (reactionState.phase == ReactionPhase.READY_TO_TAP) Color.White else DarkBorderHighlight
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = if (reactionState.phase == ReactionPhase.READY_TO_TAP) PureBlack else NeonRedBright,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = reactionState.message,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = if (reactionState.phase == ReactionPhase.READY_TO_TAP) PureBlack else TextPrimary,
                                fontSize = 15.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.startReactionTest() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("start_reaction_test_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Iniciar Teste", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { viewModel.resetReactionTest() },
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderHighlight),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("reset_reaction_test_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Redefinir", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 2. RAPID TAP / CPS TEST
                Text(
                    text = "2. TESTE DE CLICKS POR SEGUNDO (CPS)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = CyberBlueNeon,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                CyberCard {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tempo: ${rapidTapState.remainingSeconds}s",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Total de Toques: ${rapidTapState.tapCount}",
                                color = CyberBlueBright,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Big Tap Button
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { viewModel.onRapidTap() }
                                .testTag("rapid_tap_surface"),
                            color = if (rapidTapState.isActive) CyberBlueSubtle else DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (rapidTapState.isActive) CyberBlueNeon else DarkBorder
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (rapidTapState.isActive) "TOQUE RÁPIDO AQUI!" else "Pressione 'Iniciar Desafio'",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = if (rapidTapState.isActive) CyberBlueBright else TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (!rapidTapState.isActive && rapidTapState.finalCps > 0f) {
                            Text(
                                text = "Resultado: ${String.format("%.1f", rapidTapState.finalCps)} CPS (Toques/segundo)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = NeonGreen,
                                    fontSize = 16.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Button(
                            onClick = { viewModel.startRapidTapTest() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberBlueNeon),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("start_rapid_tap_btn")
                        ) {
                            Text("Iniciar Desafio 5s", color = PureBlack, fontWeight = FontWeight.Black)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3. MULTI-TOUCH COORD SAMPLER
                Text(
                    text = "3. MULTI-TOUCH COORDENADAS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurface)
                        .border(1.dp, DarkBorderHighlight, RoundedCornerShape(14.dp))
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                touchPoints.add(offset)
                                if (touchPoints.size > 8) touchPoints.removeAt(0)
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        touchPoints.forEach { pt ->
                            drawCircle(color = NeonRed, radius = 18f, center = pt)
                            drawCircle(color = Color.White, radius = 8f, center = pt)
                        }
                    }
                    Text(
                        text = "Toque nesta área para testar precisão do display 90Hz",
                        color = TextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ScoreStatCard(
    title: String,
    value: String,
    highlightColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderHighlight)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    fontSize = 9.sp
                ),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = highlightColor,
                    fontSize = 16.sp
                ),
                maxLines = 1
            )
        }
    }
}
