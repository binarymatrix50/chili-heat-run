package com.Sasfifu3.Wagraw.data.repository

import com.Sasfifu3.Wagraw.core.config.GameConfig
import com.Sasfifu3.Wagraw.data.local.ChiliPreferences
import com.Sasfifu3.Wagraw.domain.model.CareerStats
import com.Sasfifu3.Wagraw.domain.model.PlayerProgress
import com.Sasfifu3.Wagraw.domain.model.UpgradeId
import com.Sasfifu3.Wagraw.domain.repository.ProgressRepository

class ProgressRepositoryImpl(private val preferences: ChiliPreferences) : ProgressRepository {

    override fun load(): PlayerProgress {
        val initialised = preferences.readBoolean(KEY_INITIALISED, false)
        if (!initialised) {
            preferences.writeBoolean(KEY_INITIALISED, true)
            preferences.writeInt(KEY_COINS, GameConfig.STARTING_PANTRY_COINS)
        }
        val equipped = preferences.readInt(KEY_EQUIPPED, NO_RECIPE)
        return PlayerProgress(
            coins = preferences.readInt(KEY_COINS, GameConfig.STARTING_PANTRY_COINS),
            spiceTokens = preferences.readInt(KEY_SPICE, 0),
            unlockedRecipes = preferences.readIdSet(KEY_RECIPES),
            equippedRecipe = if (equipped == NO_RECIPE) null else equipped,
            ownedUpgrades = preferences.readNames(KEY_UPGRADES)
                .mapNotNull { name -> UpgradeId.entries.firstOrNull { it.name == name } }
                .toSet(),
            stats = CareerStats(
                runs = preferences.readInt(KEY_RUNS, 0),
                bestStops = preferences.readInt(KEY_BEST_STOPS, 0),
                perfectServes = preferences.readInt(KEY_PERFECT, 0),
                totalCoins = preferences.readInt(KEY_TOTAL_COINS, 0),
                bestStreak = preferences.readInt(KEY_BEST_STREAK, 0),
                routesCleared = preferences.readInt(KEY_ROUTES_CLEARED, 0),
                lastStops = preferences.readInt(KEY_LAST_STOPS, 0),
                lastCoins = preferences.readInt(KEY_LAST_COINS, 0)
            )
        )
    }

    override fun save(progress: PlayerProgress) {
        preferences.writeBoolean(KEY_INITIALISED, true)
        preferences.writeInt(KEY_COINS, progress.coins)
        preferences.writeInt(KEY_SPICE, progress.spiceTokens)
        preferences.writeIdSet(KEY_RECIPES, progress.unlockedRecipes)
        preferences.writeInt(KEY_EQUIPPED, progress.equippedRecipe ?: NO_RECIPE)
        preferences.writeNames(KEY_UPGRADES, progress.ownedUpgrades.map { it.name }.toSet())
        preferences.writeInt(KEY_RUNS, progress.stats.runs)
        preferences.writeInt(KEY_BEST_STOPS, progress.stats.bestStops)
        preferences.writeInt(KEY_PERFECT, progress.stats.perfectServes)
        preferences.writeInt(KEY_TOTAL_COINS, progress.stats.totalCoins)
        preferences.writeInt(KEY_BEST_STREAK, progress.stats.bestStreak)
        preferences.writeInt(KEY_ROUTES_CLEARED, progress.stats.routesCleared)
        preferences.writeInt(KEY_LAST_STOPS, progress.stats.lastStops)
        preferences.writeInt(KEY_LAST_COINS, progress.stats.lastCoins)
    }

    override fun bestScore(): Int = preferences.readInt(KEY_BEST_SCORE, 0)

    override fun saveBestScore(score: Int) {
        preferences.writeInt(KEY_BEST_SCORE, score)
    }

    override fun clear() {
        preferences.clearAll()
        preferences.writeBoolean(KEY_INITIALISED, true)
        preferences.writeInt(KEY_COINS, GameConfig.STARTING_PANTRY_COINS)
    }

    private companion object {
        const val NO_RECIPE = -1
        const val KEY_INITIALISED = "initialised"
        const val KEY_COINS = "pantry_coins"
        const val KEY_SPICE = "spice_tokens"
        const val KEY_RECIPES = "unlocked_recipes"
        const val KEY_EQUIPPED = "equipped_recipe"
        const val KEY_UPGRADES = "owned_upgrades"
        const val KEY_RUNS = "career_runs"
        const val KEY_BEST_STOPS = "career_best_stops"
        const val KEY_PERFECT = "career_perfect"
        const val KEY_TOTAL_COINS = "career_total_coins"
        const val KEY_BEST_STREAK = "career_best_streak"
        const val KEY_ROUTES_CLEARED = "career_routes_cleared"
        const val KEY_LAST_STOPS = "career_last_stops"
        const val KEY_LAST_COINS = "career_last_coins"
        const val KEY_BEST_SCORE = "best_score"
    }
}
