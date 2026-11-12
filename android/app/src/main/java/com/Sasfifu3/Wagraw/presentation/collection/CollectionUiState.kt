package com.Sasfifu3.Wagraw.presentation.collection

import com.Sasfifu3.Wagraw.domain.model.Recipe

data class RecipeCardUi(
    val recipe: Recipe,
    val unlocked: Boolean,
    val equipped: Boolean,
    val affordable: Boolean,
    val ingredientLabels: List<Int>
)

data class CollectionUiState(
    val cards: List<RecipeCardUi> = emptyList(),
    val spiceTokens: Int = 0,
    val heatNumbers: Boolean = true,
    val anyUnlocked: Boolean = false
)
