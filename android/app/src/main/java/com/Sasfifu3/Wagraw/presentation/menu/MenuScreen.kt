package com.Sasfifu3.Wagraw.presentation.menu

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.Sasfifu3.Wagraw.core.theme.ClayShadow
import com.Sasfifu3.Wagraw.core.theme.SurfaceClay
import com.Sasfifu3.Wagraw.core.theme.TextPrimary
import com.Sasfifu3.Wagraw.core.theme.TextSecondary
import com.Sasfifu3.Wagraw.core.theme.TextTertiary
import com.Sasfifu3.Wagraw.core.ui.ClayPanel
import com.Sasfifu3.Wagraw.core.ui.ScreenBackground
import com.Sasfifu3.Wagraw.core.ui.WalletChip

@Composable
fun MenuScreen(
    onPlay: () -> Unit,
    onRouteSelect: () -> Unit,
    onProgress: () -> Unit,
    onCollection: () -> Unit,
    onSettings: () -> Unit
) {
    val factory = rememberAppFactory()
    val menuViewModel: MenuViewModel = viewModel(factory = factory)
    val state by menuViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        menuViewModel.refresh()
    }

    MenuContent(
        state = state,
        onPlay = onPlay,
        onRouteSelect = onRouteSelect,
        onProgress = onProgress,
        onCollection = onCollection,
        onSettings = onSettings
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onPlay: () -> Unit,
    onRouteSelect: () -> Unit,
    onProgress: () -> Unit,
    onCollection: () -> Unit,
    onSettings: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "menuLoop")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ctaGradient"
    )

    ScreenBackground(
        overlay = listOf(
            Color(0xCC0D2818),
            Color(0xB3132D1E),
            Color(0xE60F3024)
        ),
        decor = {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.TopEnd)
                    .background(
                        Brush.radialGradient(
                            listOf(AccentEmber.copy(alpha = 0.16f), Color.Transparent)
                        )
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .widthIn(max = 420.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MenuHeader(state = state)

            MenuHero(modifier = Modifier.weight(1f))

            PlayTile(
                state = state,
                shift = shift,
                onPlay = onPlay
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BentoTile(
                    label = stringResource(R.string.levels),
                    caption = stringResource(
                        R.string.menu_levels_caption,
                        state.unlockedStops,
                        state.totalStops
                    ),
                    icon = R.drawable.ic_route,
                    accent = AccentEmber,
                    onClick = onRouteSelect,
                    modifier = Modifier.weight(1f)
                )
                BentoTile(
                    label = stringResource(R.string.progress),
                    caption = stringResource(R.string.menu_progress_caption, state.runs),
                    icon = R.drawable.ic_chart,
                    accent = AccentFresh,
                    onClick = onProgress,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BentoTile(
                    label = stringResource(R.string.collection),
                    caption = stringResource(
                        R.string.menu_collection_caption,
                        state.unlockedRecipes,
                        state.totalRecipes
                    ),
                    icon = R.drawable.ic_book,
                    accent = AccentChili,
                    onClick = onCollection,
                    modifier = Modifier.weight(1f)
                )
                BentoTile(
                    label = stringResource(R.string.settings),
                    caption = stringResource(R.string.menu_settings_caption),
                    icon = R.drawable.ic_gear,
                    accent = TextSecondary,
                    onClick = onSettings,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = if (state.hasRuns) {
                    stringResource(R.string.menu_footer, state.lastStops, state.lastCoins)
                } else {
                    stringResource(R.string.menu_footer_empty)
                },
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MenuHeader(state: MenuUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(R.string.cd_logo),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = stringResource(R.string.app_title),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        if (state.coins > 0 && state.spiceTokens > 0) {
            WalletChip(
                text = stringResource(R.string.chip_pantry, state.coins),
                accent = AccentEmber
            )
            WalletChip(
                text = stringResource(R.string.chip_spice, state.spiceTokens),
                accent = AccentFresh
            )
        } else if (state.coins > 0) {
            WalletChip(
                text = stringResource(R.string.chip_pantry, state.coins),
                accent = AccentEmber
            )
        }
    }
}

@Composable
private fun MenuHero(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.menu_hero_title),
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 34.sp),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.menu_hero_tagline),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
        }
        Image(
            painter = painterResource(R.drawable.sprite_3),
            contentDescription = stringResource(R.string.cd_cart),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 124.dp)
                .padding(start = 8.dp)
        )
    }
}

@Composable
private fun PlayTile(
    state: MenuUiState,
    shift: Float,
    onPlay: () -> Unit
) {
    val shape = RoundedCornerShape(30.dp)
    Button(
        onClick = onPlay,
        shape = shape,
        elevation = null,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .height(146.dp)
            .shadow(18.dp, shape, clip = false, ambientColor = ClayShadow, spotColor = AccentChili)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            AccentChili,
                            AccentEmber,
                            AccentChili
                        ),
                        start = androidx.compose.ui.geometry.Offset(shift * 260f, 0f),
                        end = androidx.compose.ui.geometry.Offset(560f + shift * 260f, 320f)
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(ClayHighlight)
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.sprite_1),
                    contentDescription = stringResource(R.string.cd_chili),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(56.dp)
                )
                Column {
                    Text(
                        text = stringResource(R.string.play),
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            R.string.menu_play_sub,
                            state.routeNumber,
                            state.bestScore
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimary.copy(alpha = 0.78f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BentoTile(
    label: String,
    caption: String,
    icon: Int,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(26.dp),
        elevation = null,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = modifier.height(100.dp)
    ) {
        ClayPanel(
            modifier = Modifier.fillMaxSize(),
            radius = 26.dp,
            fill = SurfaceClay.copy(alpha = 0.94f),
            glow = accent,
            elevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                        color = TextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = caption,
                        style = MaterialTheme.typography.labelSmall,
                        color = accent,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
