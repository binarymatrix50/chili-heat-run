package com.Sasfifu3.Wagraw.presentation.game

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Sasfifu3.Wagraw.R
import com.Sasfifu3.Wagraw.core.di.rememberAppFactory
import com.Sasfifu3.Wagraw.core.theme.AccentChili
import com.Sasfifu3.Wagraw.core.theme.AccentEmber
import com.Sasfifu3.Wagraw.core.theme.AccentFresh
import com.Sasfifu3.Wagraw.core.theme.ClayHighlight
import com.Sasfifu3.Wagraw.core.theme.DangerRed
import com.Sasfifu3.Wagraw.core.theme.SurfaceAlt
import com.Sasfifu3.Wagraw.core.theme.SurfaceClay
import com.Sasfifu3.Wagraw.core.theme.TextPrimary
import com.Sasfifu3.Wagraw.core.theme.TextSecondary
import com.Sasfifu3.Wagraw.core.theme.TextTertiary
import com.Sasfifu3.Wagraw.core.theme.WarnAmber
import com.Sasfifu3.Wagraw.core.ui.ClayPanel
import com.Sasfifu3.Wagraw.core.ui.EmptyStateLine
import com.Sasfifu3.Wagraw.core.ui.HeatPips
import com.Sasfifu3.Wagraw.core.ui.PatienceRing
import com.Sasfifu3.Wagraw.core.ui.PrimaryButton
import com.Sasfifu3.Wagraw.core.ui.ScreenBackground
import com.Sasfifu3.Wagraw.core.ui.SecondaryButton
import com.Sasfifu3.Wagraw.core.ui.StatCardRow
import com.Sasfifu3.Wagraw.core.ui.StatItem
import com.Sasfifu3.Wagraw.domain.model.Ingredient
import com.Sasfifu3.Wagraw.domain.model.ServeOutcome
import com.Sasfifu3.Wagraw.presentation.dialog.PauseDialog
import com.Sasfifu3.Wagraw.presentation.dialog.StopBriefDialog

@Composable
fun GameScreen(
    runKey: Int,
    onFinished: () -> Unit,
    onMenu: () -> Unit,
    onSettings: () -> Unit
) {
    val factory = rememberAppFactory()
    val gameViewModel: GameViewModel = viewModel(factory = factory)
    val state by gameViewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffect(runKey) {
        gameViewModel.enter(runKey)
        onDispose { gameViewModel.leave() }
    }

    LaunchedEffect(Unit) {
        gameViewModel.events.collect { event ->
            when (event) {
                GameEvent.Finished -> onFinished()
            }
        }
    }

    BackHandler(enabled = state.phase == GamePhase.PLAYING && !state.paused) {
        gameViewModel.pause()
    }

    GameContent(
        state = state,
        onPause = gameViewModel::pause,
        onPick = gameViewModel::addIngredient,
        onClearPlate = gameViewModel::clearPlate,
        onServe = gameViewModel::serve,
        onVent = gameViewModel::vent,
        onSkip = gameViewModel::skipCustomer
    )

    if (state.ready && state.phase == GamePhase.BRIEF) {
        StopBriefDialog(
            stopIndex = state.stopIndex + 1,
            totalStops = state.totalStops,
            stopNameRes = state.stopNameRes,
            modifierRes = state.stopModifierRes,
            guests = state.stopGuests,
            minDemand = state.stopMinDemand,
            maxDemand = state.stopMaxDemand,
            heatNumbers = state.heatNumbers,
            onGo = gameViewModel::beginStop
        )
    }

    if (state.paused) {
        PauseDialog(
            stallHeat = state.stallHeat,
            onResume = gameViewModel::resume,
            onSettings = onSettings,
            onMenu = onMenu
        )
    }
}

@Composable
private fun GameContent(
    state: GameUiState,
    onPause: () -> Unit,
    onPick: (Ingredient) -> Unit,
    onClearPlate: () -> Unit,
    onServe: () -> Unit,
    onVent: () -> Unit,
    onSkip: () -> Unit
) {
    val heatFraction by animateFloatAsState(
        targetValue = state.stallHeat / 100f,
        animationSpec = tween(420, easing = FastOutSlowInEasing),
        label = "heatFill"
    )
    val heatColor by animateColorAsState(
        targetValue = heatTone(state.stallHeat),
        animationSpec = tween(420),
        label = "heatColor"
    )
    val shake by animateFloatAsState(
        targetValue = if (state.phase == GamePhase.FINISHING && state.overheated) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.25f, stiffness = 900f),
        label = "shake"
    )
    val heatDescription = stringResource(R.string.sd_heat, state.stallHeat)

    ScreenBackground(
        overlay = listOf(
            Color(0xB80D2818),
            Color(0xE00F3024)
        ),
        decor = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(
                                AccentChili.copy(alpha = 0.05f + state.stallHeat * 0.0029f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
                .graphicsLayer { translationX = shake * 10f },
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GameHeader(state = state, onPause = onPause)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HeatGauge(
                    fraction = heatFraction,
                    color = heatColor,
                    value = state.stallHeat,
                    description = heatDescription
                )

                Box(modifier = Modifier.weight(1f)) {
                    if (state.queue.isEmpty()) {
                        EmptyStateLine(
                            text = stringResource(R.string.game_queue_empty),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        CustomerQueue(state = state)
                    }

                    StopClearedRibbon(
                        visible = state.phase == GamePhase.STOP_CLEARED,
                        coins = state.stopCoins,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    OutcomeOverlay(
                        visible = state.phase == GamePhase.SERVING && state.lastOutcome != null,
                        outcome = state.lastOutcome,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )

                    if (state.phase == GamePhase.FINISHING && state.overheated) {
                        Image(
                            painter = painterResource(R.drawable.sprite_5),
                            contentDescription = stringResource(R.string.cd_ember),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(180.dp)
                                .alpha(0.75f)
                        )
                    }
                }

                DishPlate(state = state, onClearPlate = onClearPlate)

                Text(
                    text = stringResource(R.string.game_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            StatCardRow(
                items = listOf(
                    StatItem(
                        label = stringResource(R.string.label_heat),
                        value = state.stallHeat.toString(),
                        accent = heatColor
                    ),
                    StatItem(
                        label = stringResource(R.string.label_streak),
                        value = state.streak.toString(),
                        accent = AccentFresh,
                        hidden = state.streak <= 0
                    ),
                    StatItem(
                        label = stringResource(R.string.label_coins),
                        value = state.coins.toString(),
                        accent = AccentEmber
                    )
                )
            )

            ControlsPanel(
                state = state,
                onPick = onPick,
                onServe = onServe,
                onVent = onVent,
                onSkip = onSkip
            )
        }
    }
}

@Composable
private fun GameHeader(state: GameUiState, onPause: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        IconButton(onClick = onPause, modifier = Modifier.size(48.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SurfaceClay),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_pause),
                    contentDescription = stringResource(R.string.cd_pause),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(
                    R.string.game_header_stop,
                    state.stopIndex + 1,
                    state.totalStops
                ),
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
            Text(
                text = if (state.stopNameRes != 0) stringResource(state.stopNameRes) else "",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                color = TextPrimary,
                maxLines = 1
            )
        }
        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = state.coins.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = AccentEmber
        )
    }
}

@Composable
private fun HeatGauge(
    fraction: Float,
    color: Color,
    value: Int,
    description: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(R.string.game_stall_heat, value),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.semantics {
                liveRegion = LiveRegionMode.Polite
                stateDescription = description
            }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(22.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(SurfaceAlt)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(
                            listOf(AccentFresh, WarnAmber, AccentChili, DangerRed)
                        )
                    )
            )
            Image(
                painter = painterResource(R.drawable.sprite_5),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 2.dp)
                    .size(20.dp)
                    .alpha(if (fraction > 0.04f) 1f else 0.25f)
            )
        }
    }
}

@Composable
private fun CustomerQueue(state: GameUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        state.queue.forEachIndexed { index, card ->
            val front = index == 0
            ClayPanel(
                modifier = Modifier
                    .weight(1f)
                    .height(132.dp)
                    .graphicsLayer {
                        scaleX = if (front) 1f else 0.94f
                        scaleY = if (front) 1f else 0.94f
                        alpha = if (front) 1f else 0.78f
                    },
                radius = 22.dp,
                fill = SurfaceClay,
                glow = if (front) AccentChili else TextTertiary,
                elevation = if (front) 14.dp else 8.dp,
                borderColor = if (front) AccentChili.copy(alpha = 0.55f) else ClayHighlight
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        PatienceRing(
                            progress = card.patience,
                            modifier = Modifier.size(52.dp)
                        )
                        Image(
                            painter = painterResource(R.drawable.sprite_4),
                            contentDescription = stringResource(R.string.cd_guest),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    HeatPips(
                        value = card.demand,
                        max = card.demand,
                        showNumber = state.heatNumbers,
                        pipSize = 9.dp
                    )
                }
            }
        }
        val missing = 4 - state.queue.size
        if (missing > 0) {
            Spacer(modifier = Modifier.weight(missing.toFloat()))
        }
    }
}

@Composable
private fun StopClearedRibbon(
    visible: Boolean,
    coins: Int,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally { full -> -full } + fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        ClayPanel(
            radius = 20.dp,
            fill = SurfaceAlt,
            glow = AccentFresh,
            elevation = 14.dp
        ) {
            Text(
                text = stringResource(R.string.stop_cleared, coins),
                style = MaterialTheme.typography.titleMedium,
                color = AccentFresh,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
private fun OutcomeOverlay(
    visible: Boolean,
    outcome: ServeOutcome?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(initialScale = 0.7f) + fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        OutcomeChip(outcome = outcome)
    }
}

@Composable
private fun OutcomeChip(outcome: ServeOutcome?) {
    if (outcome == null) return
    val label = when (outcome) {
        ServeOutcome.PERFECT -> stringResource(R.string.outcome_perfect)
        ServeOutcome.PASSABLE -> stringResource(R.string.outcome_passable)
        ServeOutcome.BLAND -> stringResource(R.string.outcome_bland)
        ServeOutcome.BURN -> stringResource(R.string.outcome_burn)
    }
    val tone = when (outcome) {
        ServeOutcome.PERFECT -> AccentFresh
        ServeOutcome.PASSABLE -> WarnAmber
        ServeOutcome.BLAND -> TextSecondary
        ServeOutcome.BURN -> DangerRed
    }
    ClayPanel(radius = 18.dp, fill = SurfaceAlt, glow = tone, elevation = 12.dp) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = tone,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun DishPlate(state: GameUiState, onClearPlate: () -> Unit) {
    val tone = when {
        state.frontDemand == null -> TextSecondary
        state.dishHeat == state.frontDemand -> AccentFresh
        state.dishHeat - state.frontDemand == 1 || state.frontDemand - state.dishHeat == 1 -> WarnAmber
        else -> DangerRed
    }
    ClayPanel(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp),
        radius = 28.dp,
        fill = SurfaceAlt,
        glow = tone,
        elevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                state.plate.forEach { chip ->
                    Image(
                        painter = painterResource(chip.artRes),
                        contentDescription = stringResource(chip.labelRes),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(34.dp)
                    )
                }
                if (state.plate.isEmpty()) {
                    Text(
                        text = stringResource(R.string.game_plate_empty),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(R.string.game_dish_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    maxLines = 1
                )
                Text(
                    text = stringResource(R.string.heat_value, state.dishHeat),
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                    color = tone,
                    maxLines = 1
                )
                if (state.signatureReady) {
                    Text(
                        text = stringResource(R.string.signature_ready),
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentFresh,
                        maxLines = 1
                    )
                }
            }
            TextButton(
                onClick = onClearPlate,
                enabled = state.plate.isNotEmpty(),
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = stringResource(R.string.game_clear),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (state.plate.isNotEmpty()) AccentEmber else TextTertiary
                )
            }
        }
    }
}

@Composable
private fun ControlsPanel(
    state: GameUiState,
    onPick: (Ingredient) -> Unit,
    onServe: () -> Unit,
    onVent: () -> Unit,
    onSkip: () -> Unit
) {
    val shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
    val rows = state.tray.chunked(3).map { row ->
        if (state.leftHanded) row.reversed() else row
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceAlt.copy(alpha = 0.96f))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { slot ->
                    TrayTile(
                        slot = slot,
                        enabled = slot.stock > 0 &&
                            state.phase == GamePhase.PLAYING &&
                            state.plate.size < state.dishLimit,
                        onPick = onPick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        val serveText = stringResource(R.string.serve)
        val ventText = stringResource(R.string.vent, state.ventCharges)
        val skipText = stringResource(R.string.skip)
        val serveEnabled = state.phase == GamePhase.PLAYING &&
            state.plate.isNotEmpty() &&
            state.queue.isNotEmpty()
        val ventEnabled = state.ventCharges > 0 && state.phase == GamePhase.PLAYING
        val skipEnabled = state.skipTokens > 0 &&
            state.queue.size > 1 &&
            state.phase == GamePhase.PLAYING

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.leftHanded) {
                PrimaryButton(
                    text = serveText,
                    onClick = onServe,
                    enabled = serveEnabled,
                    modifier = Modifier.weight(1.4f)
                )
                SecondaryButton(
                    text = ventText,
                    onClick = onVent,
                    enabled = ventEnabled,
                    accent = AccentFresh,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = skipText,
                    onClick = onSkip,
                    enabled = skipEnabled,
                    accent = AccentEmber,
                    modifier = Modifier.weight(0.9f)
                )
            } else {
                SecondaryButton(
                    text = ventText,
                    onClick = onVent,
                    enabled = ventEnabled,
                    accent = AccentFresh,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = skipText,
                    onClick = onSkip,
                    enabled = skipEnabled,
                    accent = AccentEmber,
                    modifier = Modifier.weight(0.9f)
                )
                PrimaryButton(
                    text = serveText,
                    onClick = onServe,
                    enabled = serveEnabled,
                    modifier = Modifier.weight(1.4f)
                )
            }
        }
    }
}

@Composable
private fun TrayTile(
    slot: TraySlotUi,
    enabled: Boolean,
    onPick: (Ingredient) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onPick(slot.ingredient) },
        enabled = enabled,
        shape = RoundedCornerShape(22.dp),
        elevation = null,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        modifier = modifier.height(72.dp)
    ) {
        ClayPanel(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (enabled) 1f else 0.38f),
            radius = 22.dp,
            fill = SurfaceClay,
            glow = if (slot.heat > 2) AccentChili else AccentFresh,
            elevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Image(
                    painter = painterResource(slot.artRes),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(28.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(slot.labelRes),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = TextSecondary,
                        maxLines = 2
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (slot.heat >= 0) {
                                stringResource(R.string.heat_plus, slot.heat)
                            } else {
                                stringResource(R.string.heat_minus, -slot.heat)
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                            color = if (slot.heat >= 0) AccentChili else AccentFresh
                        )
                        Text(
                            text = stringResource(R.string.tray_stock, slot.stock),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (slot.stock > 0) TextTertiary else DangerRed
                        )
                    }
                }
            }
        }
    }
}

private fun heatTone(value: Int): Color = when {
    value >= 80 -> DangerRed
    value >= 55 -> AccentChili
    value >= 30 -> WarnAmber
    else -> AccentFresh
}
