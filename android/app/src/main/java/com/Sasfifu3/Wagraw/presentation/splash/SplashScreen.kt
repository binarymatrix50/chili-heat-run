package com.Sasfifu3.Wagraw.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteRepeatableSpec
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Sasfifu3.Wagraw.R
import com.Sasfifu3.Wagraw.core.di.rememberAppFactory
import com.Sasfifu3.Wagraw.core.theme.AccentChili
import com.Sasfifu3.Wagraw.core.theme.AccentEmber
import com.Sasfifu3.Wagraw.core.theme.SurfaceClay
import com.Sasfifu3.Wagraw.core.theme.TextPrimary
import com.Sasfifu3.Wagraw.core.theme.TextSecondary
import com.Sasfifu3.Wagraw.core.theme.TextTertiary
import com.Sasfifu3.Wagraw.core.ui.ClayPanel
import com.Sasfifu3.Wagraw.core.ui.ScreenBackground

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val factory = rememberAppFactory()
    val splashViewModel: SplashViewModel = viewModel(factory = factory)
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) {
            onFinished()
        }
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    val transition = rememberInfiniteTransition(label = "splashLoop")
    val glow by transition.animateFloat(
        initialValue = 0.22f,
        targetValue = 0.55f,
        animationSpec = reverseSpec(1800),
        label = "emberGlow"
    )
    val breathe by transition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.035f,
        animationSpec = reverseSpec(2400),
        label = "plateBreathe"
    )
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "emberDrift"
    )

    ScreenBackground(
        overlay = listOf(
            Color(0xF00D2818),
            Color(0xF70A1F13),
            Color(0xEB000000)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                EmberOrbit(drift = drift, glow = glow)
                ClayPanel(
                    modifier = Modifier
                        .size(200.dp)
                        .graphicsLayer {
                            scaleX = breathe
                            scaleY = breathe
                        },
                    radius = 32.dp,
                    fill = SurfaceClay,
                    glow = AccentEmber,
                    elevation = 22.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(168.dp)
                                .alpha(glow)
                                .background(
                                    color = AccentEmber.copy(alpha = 0.18f),
                                    shape = RoundedCornerShape(28.dp)
                                )
                        )
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = stringResource(R.string.cd_logo),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(148.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.splash_tagline),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            LinearProgressIndicator(
                modifier = Modifier
                    .width(176.dp)
                    .height(6.dp),
                color = AccentChili,
                trackColor = SurfaceClay,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun EmberOrbit(drift: Float, glow: Float) {
    val sizes = listOf(28.dp, 20.dp, 16.dp)
    val offsetsX = listOf((-96).dp, 94.dp, (-70).dp)
    val offsetsY = listOf(66.dp, 40.dp, (-78).dp)
    val phases = listOf(0f, 0.33f, 0.66f)
    sizes.forEachIndexed { index, emberSize ->
        val phase = (drift + phases[index]) % 1f
        Image(
            painter = painterResource(R.drawable.sprite_5),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(emberSize)
                .offset(x = offsetsX[index], y = offsetsY[index] - (14.dp * phase))
                .alpha(((1f - phase) * glow).coerceIn(0f, 1f))
        )
    }
}

private fun reverseSpec(durationMillis: Int): InfiniteRepeatableSpec<Float> = infiniteRepeatable(
    animation = tween(durationMillis, easing = FastOutSlowInEasing),
    repeatMode = RepeatMode.Reverse
)
