package com.Sasfifu3.Wagraw.presentation.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.Sasfifu3.Wagraw.core.theme.WarnAmber
import com.Sasfifu3.Wagraw.core.ui.ClayPanel
import com.Sasfifu3.Wagraw.core.ui.EmptyStateLine
import com.Sasfifu3.Wagraw.core.ui.ScreenBackground
import com.Sasfifu3.Wagraw.core.ui.SecondaryButton
import com.Sasfifu3.Wagraw.core.ui.SectionHeader
import com.Sasfifu3.Wagraw.core.ui.StatCardRow
import com.Sasfifu3.Wagraw.core.ui.StatItem
import com.Sasfifu3.Wagraw.core.ui.WalletChip
import com.Sasfifu3.Wagraw.domain.model.Upgrade

@Composable
fun ProgressScreen(onBack: () -> Unit) {
    val factory = rememberAppFactory()
    val progressViewModel: ProgressViewModel = viewModel(factory = factory)
    val state by progressViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        progressViewModel.refresh()
    }

    ProgressContent(
        state = state,
        onBack = onBack,
        onBuy = progressViewModel::buy
    )
}

@Composable
private fun ProgressContent(
    state: ProgressUiState,
    onBack: () -> Unit,
    onBuy: (Upgrade) -> Unit
) {
    ScreenBackground(
        overlay = listOf(
            Color(0xE60D2818),
            Color(0xDB132D1E)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SectionHeader(
                title = stringResource(R.string.progress_title),
                onBack = onBack,
                trailing = {
                    WalletChip(
                        text = stringResource(R.string.chip_pantry, state.coins),
                        accent = AccentEmber
                    )
                }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    StatCardRow(
                        items = listOf(
                            StatItem(
                                label = stringResource(R.string.stat_runs),
                                value = state.stats.runs.toString(),
                                accent = AccentEmber
                            ),
                            StatItem(
                                label = stringResource(R.string.stat_best_stops),
                                value = state.stats.bestStops.toString(),
                                accent = AccentFresh,
                                hidden = state.stats.bestStops <= 0
                            ),
                            StatItem(
                                label = stringResource(R.string.result_perfect),
                                value = state.stats.perfectServes.toString(),
                                accent = WarnAmber,
                                hidden = state.stats.perfectServes <= 0
                            )
                        )
                    )
                }
                item {
                    StatCardRow(
                        items = listOf(
                            StatItem(
                                label = stringResource(R.string.stat_total_coins),
                                value = state.stats.totalCoins.toString(),
                                accent = AccentEmber,
                                hidden = state.stats.totalCoins <= 0
                            ),
                            StatItem(
                                label = stringResource(R.string.result_best_streak),
                                value = state.stats.bestStreak.toString(),
                                accent = AccentChili,
                                hidden = state.stats.bestStreak <= 0
                            ),
                            StatItem(
                                label = stringResource(R.string.stat_recipes),
                                value = state.recipes.toString(),
                                accent = AccentFresh,
                                hidden = state.recipes <= 0
                            )
                        )
                    )
                }
                if (!state.hasRuns) {
                    item {
                        EmptyStateLine(text = stringResource(R.string.progress_empty))
                    }
                }
                item {
                    Text(
                        text = stringResource(R.string.progress_milestones),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                items(state.milestones) { milestone ->
                    MilestoneRow(milestone = milestone)
                }
                item {
                    Text(
                        text = stringResource(R.string.progress_upgrades),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                items(state.upgrades) { upgrade ->
                    UpgradeRow(item = upgrade, onBuy = onBuy)
                }
            }
        }
    }
}

@Composable
private fun MilestoneRow(milestone: MilestoneUi) {
    val reachedLabel = stringResource(R.string.sd_reached)
    val pendingLabel = stringResource(R.string.sd_pending)
    ClayPanel(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .semantics {
                stateDescription = if (milestone.reached) reachedLabel else pendingLabel
            },
        radius = 20.dp,
        fill = SurfaceAlt,
        glow = if (milestone.reached) AccentFresh else TextTertiary,
        elevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(milestone.milestone.labelRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (milestone.reached) AccentFresh else TextSecondary
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(SurfaceClay)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(milestone.fraction)
                            .fillMaxHeight()
                            .background(if (milestone.reached) AccentFresh else AccentEmber)
                    )
                }
            }
            Text(
                text = stringResource(
                    R.string.milestone_progress,
                    milestone.current.coerceAtMost(milestone.milestone.target),
                    milestone.milestone.target
                ),
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun UpgradeRow(item: UpgradeUi, onBuy: (Upgrade) -> Unit) {
    ClayPanel(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp),
        radius = 22.dp,
        fill = SurfaceClay,
        glow = if (item.owned) AccentFresh else AccentEmber,
        elevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(item.upgrade.nameRes),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                    color = TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = stringResource(item.upgrade.effectRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 2
                )
                if (item.blocked) {
                    Text(
                        text = stringResource(R.string.upgrade_requires),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary,
                        maxLines = 1
                    )
                }
            }
            if (item.owned) {
                Text(
                    text = stringResource(R.string.upgrade_owned),
                    style = MaterialTheme.typography.labelSmall,
                    color = AccentFresh
                )
            } else {
                SecondaryButton(
                    text = stringResource(R.string.upgrade_price, item.upgrade.price),
                    onClick = { onBuy(item.upgrade) },
                    enabled = item.affordable && !item.blocked,
                    accent = AccentEmber,
                    modifier = Modifier.width(118.dp)
                )
            }
        }
    }
}
