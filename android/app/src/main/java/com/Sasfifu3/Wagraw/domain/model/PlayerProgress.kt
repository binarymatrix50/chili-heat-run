package com.Sasfifu3.Wagraw.domain.model

data class PlayerProgress(
    val coins: Int = 0,
    val spiceTokens: Int = 0,
    val unlockedRecipes: Set<Int> = emptySet(),
    val equippedRecipe: Int? = null,
    val ownedUpgrades: Set<UpgradeId> = emptySet(),
    val stats: CareerStats = CareerStats()
)
