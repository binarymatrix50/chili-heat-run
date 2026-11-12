package com.Sasfifu3.Wagraw.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Sasfifu3.Wagraw.core.theme.ClayHighlight
import com.Sasfifu3.Wagraw.core.theme.ClayShadow
import com.Sasfifu3.Wagraw.core.theme.SurfaceClay

@Composable
fun ClayPanel(
    modifier: Modifier = Modifier,
    radius: Dp = 24.dp,
    fill: Color = SurfaceClay,
    fillBrush: Brush? = null,
    glow: Color = ClayShadow,
    elevation: Dp = 12.dp,
    borderColor: Color = ClayHighlight,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(radius)
    Box(
        modifier = modifier
            .shadow(elevation, shape, clip = false, ambientColor = ClayShadow, spotColor = glow)
            .clip(shape)
            .then(
                if (fillBrush != null) {
                    Modifier.background(fillBrush)
                } else {
                    Modifier.background(fill)
                }
            )
            .border(2.dp, borderColor, shape)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ClayHighlight, Color.Transparent),
                        startY = 0f,
                        endY = 44f
                    )
                )
        )
        content()
    }
}
