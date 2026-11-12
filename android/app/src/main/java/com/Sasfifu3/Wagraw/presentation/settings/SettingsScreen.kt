package com.Sasfifu3.Wagraw.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Sasfifu3.Wagraw.R
import com.Sasfifu3.Wagraw.core.di.rememberAppFactory
import com.Sasfifu3.Wagraw.core.theme.AccentFresh
import com.Sasfifu3.Wagraw.core.theme.DangerRed
import com.Sasfifu3.Wagraw.core.theme.SurfaceAlt
import com.Sasfifu3.Wagraw.core.theme.SurfaceClay
import com.Sasfifu3.Wagraw.core.theme.TextPrimary
import com.Sasfifu3.Wagraw.core.theme.TextSecondary
import com.Sasfifu3.Wagraw.core.theme.TextTertiary
import com.Sasfifu3.Wagraw.core.ui.ClayPanel
import com.Sasfifu3.Wagraw.core.ui.ScreenBackground
import com.Sasfifu3.Wagraw.core.ui.SecondaryButton
import com.Sasfifu3.Wagraw.core.ui.SectionHeader
import com.Sasfifu3.Wagraw.presentation.dialog.ConfirmDialog

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val factory = rememberAppFactory()
    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
    val state by settingsViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        settingsViewModel.refresh()
    }

    SettingsContent(
        state = state,
        onBack = onBack,
        onHeatNumbers = settingsViewModel::toggleHeatNumbers,
        onLeftTray = settingsViewModel::toggleLeftHandedTray,
        onAssist = settingsViewModel::togglePatienceAssist,
        onAskReset = settingsViewModel::askReset
    )

    if (state.confirmReset) {
        ConfirmDialog(
            title = stringResource(R.string.reset_title),
            message = stringResource(R.string.reset_message),
            onConfirm = settingsViewModel::confirmReset,
            onCancel = settingsViewModel::dismissReset
        )
    }
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onBack: () -> Unit,
    onHeatNumbers: (Boolean) -> Unit,
    onLeftTray: (Boolean) -> Unit,
    onAssist: (Boolean) -> Unit,
    onAskReset: () -> Unit
) {
    ScreenBackground(
        overlay = listOf(
            Color(0xEB0F3024),
            Color(0xF00D2818)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionHeader(
                title = stringResource(R.string.settings_title),
                onBack = onBack
            )

            SettingRow(
                title = stringResource(R.string.setting_heat_numbers),
                hint = stringResource(R.string.setting_heat_numbers_hint),
                checked = state.settings.heatNumbers,
                onCheckedChange = onHeatNumbers
            )
            SettingRow(
                title = stringResource(R.string.setting_left_tray),
                hint = stringResource(R.string.setting_left_tray_hint),
                checked = state.settings.leftHandedTray,
                onCheckedChange = onLeftTray
            )
            SettingRow(
                title = stringResource(R.string.setting_assist),
                hint = stringResource(R.string.setting_assist_hint),
                checked = state.settings.patienceAssist,
                onCheckedChange = onAssist
            )

            Box(modifier = Modifier.weight(1f))

            SecondaryButton(
                text = stringResource(R.string.settings_reset),
                onClick = onAskReset,
                accent = DangerRed,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(R.string.settings_version, state.version),
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    hint: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val onLabel = stringResource(R.string.state_on)
    val offLabel = stringResource(R.string.state_off)
    ClayPanel(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp),
        radius = 22.dp,
        fill = SurfaceAlt,
        glow = if (checked) AccentFresh else TextTertiary,
        elevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                    color = TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = hint,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 2
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = TextPrimary,
                    checkedTrackColor = AccentFresh,
                    uncheckedThumbColor = TextTertiary,
                    uncheckedTrackColor = SurfaceClay
                ),
                modifier = Modifier.semantics {
                    stateDescription = if (checked) onLabel else offLabel
                }
            )
        }
    }
}
