package com.Sasfifu3.Wagraw.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Sasfifu3.Wagraw.core.theme.AccentChili
import com.Sasfifu3.Wagraw.core.theme.AccentEmber
import com.Sasfifu3.Wagraw.core.theme.ClayShadow
import com.Sasfifu3.Wagraw.core.theme.SurfaceAlt
import com.Sasfifu3.Wagraw.core.theme.SurfaceClay
import com.Sasfifu3.Wagraw.core.theme.TextPrimary
import com.Sasfifu3.Wagraw.core.theme.TextTertiary

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: Int? = null,
    iconDescription: String? = null
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 420f),
        label = "primaryButtonScale"
    )
    val shape = RoundedCornerShape(24.dp)
    val brush = if (enabled) {
        Brush.horizontalGradient(listOf(AccentChili, AccentEmber))
    } else {
        Brush.horizontalGradient(listOf(SurfaceClay, SurfaceAlt))
    }
    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interaction,
        shape = shape,
        contentPadding = PaddingValues(0.dp),
        elevation = null,
        colors = ButtonDefaults.buttonColors(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            disabledContainerColor = androidx.compose.ui.graphics.Color.Transparent
        ),
        modifier = modifier
            .height(60.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (enabled) 16.dp else 0.dp,
                shape = shape,
                clip = false,
                ambientColor = ClayShadow,
                spotColor = AccentChili
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(brush),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (leadingIcon != null) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(leadingIcon),
                        contentDescription = iconDescription,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                    color = if (enabled) TextPrimary else TextTertiary
                )
            }
        }
    }
}
