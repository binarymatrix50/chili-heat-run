package com.Sasfifu3.Wagraw.presentation.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Sasfifu3.Wagraw.R
import com.Sasfifu3.Wagraw.core.di.rememberAppFactory
import com.Sasfifu3.Wagraw.core.theme.AccentEmber
import com.Sasfifu3.Wagraw.core.theme.AccentFresh
import com.Sasfifu3.Wagraw.core.theme.SurfaceClay
import com.Sasfifu3.Wagraw.core.theme.TextPrimary
import com.Sasfifu3.Wagraw.core.theme.TextSecondary
import com.Sasfifu3.Wagraw.core.theme.TextTertiary
import com.Sasfifu3.Wagraw.core.ui.ClayPanel
import com.Sasfifu3.Wagraw.core.ui.EmptyStateLine
import com.Sasfifu3.Wagraw.core.ui.HeatPips
import com.Sasfifu3.Wagraw.core.ui.ScreenBackground
import com.Sasfifu3.Wagraw.core.ui.SecondaryButton
import com.Sasfifu3.Wagraw.core.ui.SectionHeader
import com.Sasfifu3.Wagraw.core.ui.WalletChip
import com.Sasfifu3.Wagraw.domain.model.Recipe

@Composable
fun CollectionScreen(onBack: () -> Unit) {
    val factory = rememberAppFactory()
    val collectionViewModel: CollectionViewModel = viewModel(factory = factory)
    val state by collectionViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        collectionViewModel.refresh()
    }

    CollectionContent(
        state = state,
        onBack = onBack,
        onUnlock = collectionViewModel::unlock,
        onEquip = collectionViewModel::equip
    )
}

@Composable
private fun CollectionContent(
    state: CollectionUiState,
    onBack: () -> Unit,
    onUnlock: (Recipe) -> Unit,
    onEquip: (Recipe) -> Unit
) {
    ScreenBackground(
        overlay = listOf(
            Color(0xE0132D1E),
            Color(0xF00F3024)
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
                title = stringResource(R.string.collection_title),
                onBack = onBack,
                trailing = {
                    WalletChip(
                        text = stringResource(R.string.chip_spice, state.spiceTokens),
                        accent = AccentFresh
                    )
                }
            )

            if (!state.anyUnlocked) {
                EmptyStateLine(text = stringResource(R.string.collection_banner))
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(state.cards) { card ->
                    RecipeCard(
                        card = card,
                        heatNumbers = state.heatNumbers,
                        onUnlock = onUnlock,
                        onEquip = onEquip
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipeCard(
    card: RecipeCardUi,
    heatNumbers: Boolean,
    onUnlock: (Recipe) -> Unit,
    onEquip: (Recipe) -> Unit
) {
    val lockedLabel = stringResource(R.string.sd_locked)
    val equippedLabel = stringResource(R.string.collection_equipped)
    val openLabel = stringResource(R.string.sd_open)
    ClayPanel(
        modifier = Modifier
            .fillMaxWidth()
            .height(228.dp)
            .semantics {
                stateDescription = when {
                    card.equipped -> equippedLabel
                    card.unlocked -> openLabel
                    else -> lockedLabel
                }
            },
        radius = 26.dp,
        fill = SurfaceClay,
        glow = if (card.equipped) AccentFresh else AccentEmber,
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Image(
                painter = painterResource(card.recipe.artRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(40.dp)
                    .alpha(if (card.unlocked) 1f else 0.4f)
            )
            Text(
                text = stringResource(card.recipe.nameRes),
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                color = TextPrimary,
                maxLines = 2
            )
            HeatPips(
                value = card.recipe.heat,
                max = card.recipe.heat,
                showNumber = heatNumbers,
                pipSize = 10.dp
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                card.ingredientLabels.take(3).forEach { labelRes ->
                    Text(
                        text = stringResource(labelRes),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                when {
                    card.equipped -> Text(
                        text = equippedLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentFresh
                    )

                    card.unlocked -> SecondaryButton(
                        text = stringResource(R.string.collection_equip),
                        onClick = { onEquip(card.recipe) },
                        accent = AccentFresh,
                        modifier = Modifier.fillMaxWidth()
                    )

                    else -> SecondaryButton(
                        text = stringResource(R.string.collection_unlock, card.recipe.cost),
                        onClick = { onUnlock(card.recipe) },
                        enabled = card.affordable,
                        accent = AccentEmber,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (!card.unlocked && !card.affordable) {
                    Text(
                        text = stringResource(R.string.collection_need_spice),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = TextTertiary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
