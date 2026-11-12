package com.Sasfifu3.Wagraw.presentation.gameover

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Sasfifu3.Wagraw.R
import com.Sasfifu3.Wagraw.core.di.rememberAppFactory
import com.Sasfifu3.Wagraw.core.theme.AccentChili
import com.Sasfifu3.Wagraw.core.theme.AccentEmber
import com.Sasfifu3.Wagraw.core.theme.AccentFresh
import com.Sasfifu3.Wagraw.core.theme.SurfaceAlt
import com.Sasfifu3.Wagraw.core.theme.TextPrimary
import com.Sasfifu3.Wagraw.core.theme.TextSecondary
import com.Sasfifu3.Wagraw.core.theme.WarnAmber
import com.Sasfifu3.Wagraw.core.ui.ClayPanel
import com.Sasfifu3.Wagraw.core.ui.EmptyStateLine
import com.Sasfifu3.Wagraw.core.ui.PrimaryButton
import com.Sasfifu3.Wagraw.core.ui.ScreenBackground
import com.Sasfifu3.Wagraw.core.ui.SecondaryButton
import com.Sasfifu3.Wagraw.core.ui.StatCardRow
import com.Sasfifu3.Wagraw.core.ui.StatItem
import com.Sasfifu3.Wagraw.domain.model.RunEndReason

@Composable
fun ResultScreen(
    onPlayAgain: () -> Unit,
    onCollection: () -> Unit,
    onMenu: () -> Unit
) {
    val factory = rememberAppFactory()
    val resultViewModel: ResultViewModel = viewModel(factory = factory)
    val state by resultViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        resultViewModel.refresh()
    }

    ResultContent(
        state = state,
        onPlayAgain = onPlayAgain,
        onCollection = onCollection,
        onMenu = onMenu
    )
}

@Composable
private fun ResultContent(
    state: ResultUiState,
    onPlayAgain: () -> Unit,
    onCollection: () -> Unit,
    onMenu: () -> Unit
) {
    val cleared = state.reason == RunEndReason.ROUTE_SERVED
    val rim = if (cleared) AccentFresh else AccentChili
    val transition = rememberInfiniteTransition(label = "resultLoop")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bestPulse"
    )

    ScreenBackground(
        overlay = if (cleared) {
            listOf(Color(0xE60F3024), Color(0xF50D2818))
        } else {
            listOf(Color(0xE62A0E08), Color(0xF50D2818))
        },
        decor = {
            if (!cleared) {
                Image(
                    painter = painterResource(R.drawable.sprite_5),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 120.dp)
                        .size(200.dp)
                        .alpha(0.32f)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(if (cleared) R.drawable.sprite_3 else R.drawable.sprite_5),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(84.dp)
            )

            Text(
                text = when (state.reason) {
                    RunEndReason.ROUTE_SERVED -> stringResource(R.string.result_route_served)
                    RunEndReason.OVERHEAT -> stringResource(R.string.result_overheat)
                    RunEndReason.WALKOUTS -> stringResource(R.string.result_walkout)
                },
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 34.sp),
                color = rim,
                textAlign = TextAlign.Center
            )

            if (state.stopNameRes != 0) {
                Text(
                    text = stringResource(
                        R.string.result_sub,
                        state.stopsCleared,
                        state.totalStops,
                        stringResource(state.stopNameRes)
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            if (!state.hasResult) {
                EmptyStateLine(text = stringResource(R.string.result_no_run))
            }

            StatCardRow(
                items = listOf(
                    StatItem(
                        label = stringResource(R.string.result_coins),
                        value = state.coinsEarned.toString(),
                        accent = AccentEmber
                    ),
                    StatItem(
                        label = stringResource(R.string.result_perfect),
                        value = state.perfectServes.toString(),
                        accent = AccentFresh,
                        hidden = state.perfectServes <= 0
                    )
                )
            )

            StatCardRow(
                items = listOf(
                    StatItem(
                        label = stringResource(R.string.result_best_streak),
                        value = state.bestStreak.toString(),
                        accent = WarnAmber,
                        hidden = state.bestStreak <= 0
                    ),
                    StatItem(
                        label = stringResource(R.string.result_spice),
                        value = state.spiceEarned.toString(),
                        accent = AccentFresh,
                        hidden = state.spiceEarned <= 0
                    )
                )
            )

            if (state.newBest) {
                ClayPanel(
                    modifier = Modifier.graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                    },
                    radius = 18.dp,
                    fill = SurfaceAlt,
                    glow = AccentEmber,
                    elevation = 12.dp
                ) {
                    Text(
                        text = stringResource(R.string.result_new_best),
                        style = MaterialTheme.typography.titleMedium,
                        color = AccentEmber,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                    )
                }
            }

            val teaser = state.nextRecipeNameRes
            if (teaser != null) {
                ClayPanel(
                    modifier = Modifier.fillMaxWidth(),
                    radius = 18.dp,
                    fill = SurfaceAlt,
                    glow = AccentFresh,
                    elevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.sprite_2),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = stringResource(
                                R.string.result_unlock_teaser,
                                stringResource(teaser)
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentFresh
                        )
                    }
                }
            }

            if (state.assisted) {
                Text(
                    text = stringResource(R.string.result_assisted),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            Box(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = stringResource(R.string.play_again),
                onClick = onPlayAgain,
                leadingIcon = R.drawable.sprite_1,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.collection),
                    onClick = onCollection,
                    leadingIcon = R.drawable.ic_book,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.menu),
                    onClick = onMenu,
                    leadingIcon = R.drawable.icon_home,
                    iconDescription = stringResource(R.string.cd_home),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
