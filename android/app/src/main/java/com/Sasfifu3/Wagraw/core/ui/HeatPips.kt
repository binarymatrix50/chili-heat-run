package com.Sasfifu3.Wagraw.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Sasfifu3.Wagraw.R
import com.Sasfifu3.Wagraw.core.theme.AccentEmber

@Composable
fun HeatPips(
    value: Int,
    max: Int,
    showNumber: Boolean,
    modifier: Modifier = Modifier,
    pipSize: Dp = 12.dp
) {
    val shown = max.coerceIn(1, 7)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        for (index in 1..shown) {
            Image(
                painter = painterResource(R.drawable.sprite_1),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(pipSize)
                    .alpha(if (index <= value) 1f else 0.22f)
            )
        }
        if (showNumber) {
            Text(
                text = stringResource(R.string.heat_value, value),
                style = MaterialTheme.typography.labelSmall,
                color = AccentEmber,
                maxLines = 1
            )
        }
    }
}
