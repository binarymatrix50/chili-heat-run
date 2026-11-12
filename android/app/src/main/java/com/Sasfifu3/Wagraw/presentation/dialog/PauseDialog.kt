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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.Sasfifu3.Wagraw.R
import com.Sasfifu3.Wagraw.core.theme.AccentFresh
import com.Sasfifu3.Wagraw.core.theme.SurfaceAlt
import com.Sasfifu3.Wagraw.core.theme.TextPrimary
import com.Sasfifu3.Wagraw.core.theme.TextSecondary
import com.Sasfifu3.Wagraw.core.ui.ClayPanel
import com.Sasfifu3.Wagraw.core.ui.PrimaryButton
import com.Sasfifu3.Wagraw.core.ui.SecondaryButton

@Composable
fun PauseDialog(
    stallHeat: Int,
    onResume: () -> Unit,
    onSettings: () -> Unit,
    onMenu: () -> Unit
) {
    Dialog(onDismissRequest = onResume) {
        ClayPanel(
            modifier = Modifier.fillMaxWidth(),
            radius = 30.dp,
            fill = SurfaceAlt,
            glow = AccentFresh,
            elevation = 20.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_pause),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = stringResource(R.string.pause_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Text(
                    text = stringResource(R.string.game_stall_heat, stallHeat),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                PrimaryButton(
                    text = stringResource(R.string.resume),
                    onClick = onResume,
                    modifier = Modifier.fillMaxWidth()
                )
                SecondaryButton(
                    text = stringResource(R.string.settings),
                    onClick = onSettings,
                    leadingIcon = R.drawable.ic_gear,
                    modifier = Modifier.fillMaxWidth()
                )
                SecondaryButton(
                    text = stringResource(R.string.menu),
                    onClick = onMenu,
                    leadingIcon = R.drawable.icon_home,
                    iconDescription = stringResource(R.string.cd_home),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
