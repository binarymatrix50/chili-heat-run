package com.Sasfifu3.Wagraw.presentation.routeselect

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Sasfifu3.Wagraw.R
import com.Sasfifu3.Wagraw.core.di.rememberAppFactory
import com.Sasfifu3.Wagraw.core.theme.AccentChili
import com.Sasfifu3.Wagraw.core.theme.AccentEmber
import com.Sasfifu3.Wagraw.core.theme.AccentFresh
import com.Sasfifu3.Wagraw.core.theme.SurfaceAlt
import com.Sasfifu3.Wagraw.core.theme.SurfaceClay
import com.Sasfifu3.Wagraw.core.theme.TextPrimary
import com.Sasfifu3.Wagraw.core.theme.TextSecondary
import com.Sasfifu3.Wagraw.core.theme.TextTertiary
import com.Sasfifu3.Wagraw.core.ui.ClayPanel
import com.Sasfifu3.Wagraw.core.ui.EmptyStateLine
import com.Sasfifu3.Wagraw.core.ui.HeatPips
import com.Sasfifu3.Wagraw.core.ui.PrimaryButton
import com.Sasfifu3.Wagraw.core.ui.ScreenBackground
import com.Sasfifu3.Wagraw.core.ui.SecondaryButton
import com.Sasfifu3.Wagraw.core.ui.SectionHeader
import com.Sasfifu3.Wagraw.core.ui.WalletChip

@Composable
fun RouteSelectScreen(
    onBack: () -> Unit,
    onStartRoute: () -> Unit
) {
    val factory = rememberAppFactory()
    val routeViewModel: RouteSelectViewModel = viewModel(factory = factory)
    val state by routeViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        routeViewModel.refresh()
    }

    RouteSelectContent(
        state = state,
        onBack = onBack,
        onPick = routeViewModel::pick,
        onShuffle = routeViewModel::shuffle,
        onReset = routeViewModel::resetRoute,
        onStart = {
            routeViewModel.confirmRoute()
            onStartRoute()
        }
    )
}

@Composable
private fun RouteSelectContent(
    state: RouteSelectUiState,
    onBack: () -> Unit,
    onPick: (com.Sasfifu3.Wagraw.domain.model.RouteStop) -> Unit,
    onShuffle: () -> Unit,
    onReset: () -> Unit,
    onStart: () -> Unit
) {
    ScreenBackground(
        overlay = listOf(
            Color(0xDB0F3024),
            Color(0xF00D2818)
        ),
        decor = {
            LaneDots(modifier = Modifier.fillMaxSize())
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SectionHeader(
                title = stringResource(R.string.route_builder_title),
                onBack = onBack,
                trailing = {
                    WalletChip(
                        text = stringResource(R.string.chip_spice, state.spiceTokens),
                        accent = AccentFresh
                    )
                }
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                itemsIndexed(state.chosen) { index, stop ->
                    LockedSlot(
                        index = index + 1,
                        nameRes = stop.nameRes,
                        modifierRes = stop.modifierRes
                    )
                }
                if (!state.complete) {
                    item {
                        Text(
                            text = stringResource(R.string.route_pick_more, state.missing),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    item {
                        AnimatedVisibility(
                            visible = state.options.isNotEmpty(),
                            enter = fadeIn() + slideInVertically { full -> full / 6 },
                            exit = fadeOut()
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                state.options.forEach { option ->
                                    StopOptionCard(
                                        option = option,
                                        onPick = onPick,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
                if (state.lockedNotice) {
                    item {
                        EmptyStateLine(text = stringResource(R.string.route_locked_notice))
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SecondaryButton(
                    text = stringResource(R.string.route_shuffle),
                    onClick = onShuffle,
                    enabled = state.rerollsLeft > 0 && !state.complete,
                    accent = AccentEmber
                )
                SecondaryButton(
                    text = stringResource(R.string.route_reset),
                    onClick = onReset,
                    accent = TextSecondary
                )
            }

            PrimaryButton(
                text = if (state.complete) {
                    stringResource(R.string.route_start)
                } else {
                    stringResource(R.string.route_pick_more, state.missing)
                },
                onClick = onStart,
                enabled = state.complete,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
        }
    }
}

@Composable
private fun LockedSlot(index: Int, nameRes: Int, modifierRes: Int) {
    ClayPanel(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        radius = 22.dp,
        fill = SurfaceAlt,
        glow = AccentFresh,
        elevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_3),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(38.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.route_slot_locked, index),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
                Text(
                    text = stringResource(nameRes),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                    color = TextPrimary,
                    maxLines = 1
                )
            }
            Text(
                text = stringResource(modifierRes),
                style = MaterialTheme.typography.labelSmall,
                color = AccentEmber,
                maxLines = 2,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StopOptionCard(
    option: StopOptionUi,
    onPick: (com.Sasfifu3.Wagraw.domain.model.RouteStop) -> Unit,
    modifier: Modifier = Modifier
) {
    val lockedLabel = stringResource(R.string.sd_locked)
    val openLabel = stringResource(R.string.sd_open)
    Button(
        onClick = { onPick(option.stop) },
        shape = RoundedCornerShape(24.dp),
        elevation = null,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = modifier
            .height(168.dp)
            .semantics {
                stateDescription = if (option.locked) lockedLabel else openLabel
            }
    ) {
        ClayPanel(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (option.locked) 0.45f else 1f),
            radius = 24.dp,
            fill = SurfaceClay,
            glow = if (option.locked) TextTertiary else AccentChili,
            elevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Image(
                    painter = painterResource(if (option.locked) R.drawable.ic_lock else R.drawable.sprite_3),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = stringResource(option.stop.nameRes),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                    color = TextPrimary,
                    maxLines = 2
                )
                HeatPips(
                    value = option.stop.maxDemand,
                    max = option.stop.maxDemand,
                    showNumber = false,
                    pipSize = 10.dp
                )
                Text(
                    text = stringResource(R.string.route_customers, option.stop.customers),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (option.locked) {
                        stringResource(R.string.route_unlock_hint, option.stop.unlockRuns)
                    } else {
                        stringResource(option.stop.modifierRes)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (option.locked) TextTertiary else AccentEmber,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun LaneDots(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 48f
            val radius = 3f
            var y = step
            while (y < size.height) {
                var x = step
                while (x < size.width) {
                    drawCircle(
                        color = SurfaceClay.copy(alpha = 0.5f),
                        radius = radius,
                        center = androidx.compose.ui.geometry.Offset(x, y)
                    )
                    x += step
                }
                y += step
            }
        }
    }
}
