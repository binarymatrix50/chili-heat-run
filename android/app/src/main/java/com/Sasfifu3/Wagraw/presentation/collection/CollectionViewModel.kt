package com.Sasfifu3.Wagraw.presentation.collection

import androidx.lifecycle.ViewModel
import com.Sasfifu3.Wagraw.data.sample.SampleData
import com.Sasfifu3.Wagraw.domain.model.Recipe
import com.Sasfifu3.Wagraw.domain.repository.ProgressRepository
import com.Sasfifu3.Wagraw.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CollectionViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionUiState())
    val uiState: StateFlow<CollectionUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val progress = progressRepository.load()
        val settings = settingsRepository.load()
        _uiState.value = CollectionUiState(
            cards = SampleData.recipes.map { recipe ->
                RecipeCardUi(
                    recipe = recipe,
                    unlocked = progress.unlockedRecipes.contains(recipe.id),
                    equipped = progress.equippedRecipe == recipe.id,
                    affordable = progress.spiceTokens >= recipe.cost,
                    ingredientLabels = recipe.ingredients.map { SampleData.labelOf(it) }
                )
            },
            spiceTokens = progress.spiceTokens,
            heatNumbers = settings.heatNumbers,
            anyUnlocked = progress.unlockedRecipes.isNotEmpty()
        )
    }

    fun unlock(recipe: Recipe) {
        val progress = progressRepository.load()
        if (progress.unlockedRecipes.contains(recipe.id)) return
        if (progress.spiceTokens < recipe.cost) return
        progressRepository.save(
            progress.copy(
                spiceTokens = progress.spiceTokens - recipe.cost,
                unlockedRecipes = progress.unlockedRecipes + recipe.id,
                equippedRecipe = progress.equippedRecipe ?: recipe.id
            )
        )
        refresh()
    }

    fun equip(recipe: Recipe) {
        val progress = progressRepository.load()
        if (!progress.unlockedRecipes.contains(recipe.id)) return
        progressRepository.save(progress.copy(equippedRecipe = recipe.id))
        refresh()
    }
}
