package com.Sasfifu3.Wagraw.presentation.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.Sasfifu3.Wagraw.R
import com.Sasfifu3.Wagraw.core.theme.AccentEmber
import com.Sasfifu3.Wagraw.core.theme.SurfaceAlt
import com.Sasfifu3.Wagraw.core.theme.TextPrimary
import com.Sasfifu3.Wagraw.core.theme.TextSecondary
import com.Sasfifu3.Wagraw.core.theme.TextTertiary
import com.Sasfifu3.Wagraw.core.ui.ClayPanel
import com.Sasfifu3.Wagraw.core.ui.HeatPips
import com.Sasfifu3.Wagraw.core.ui.PrimaryButton

@Composable
fun StopBriefDialog(
    stopIndex: Int,
    totalStops: Int,
    stopNameRes: Int,
    modifierRes: Int,
    guests: Int,
    minDemand: Int,
    maxDemand: Int,
    heatNumbers: Boolean,
    onGo: () -> Unit
) {
    Dialog(onDismissRequest = onGo) {
        ClayPanel(
            modifier = Modifier.fillMaxWidth(),
            radius = 30.dp,
            fill = SurfaceAlt,
            glow = AccentEmber,
            elevation = 20.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.stop_brief_title),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
                Image(
                    painter = painterResource(R.drawable.sprite_3),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = stringResource(stopNameRes),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.game_header_stop, stopIndex, totalStops),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = stringResource(modifierRes),
                    style = MaterialTheme.typography.bodyLarge,
                    color = AccentEmber,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.stop_brief_guests, guests),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                HeatPips(
                    value = maxDemand,
                    max = maxDemand,
                    showNumber = heatNumbers,
                    pipSize = 14.dp
                )
                Text(
                    text = stringResource(R.string.stop_brief_demand, minDemand, maxDemand),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
                PrimaryButton(
                    text = stringResource(R.string.go),
                    onClick = onGo,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
