package com.Sasfifu3.Wagraw.data.sample

import com.Sasfifu3.Wagraw.R
import com.Sasfifu3.Wagraw.domain.model.Ingredient
import com.Sasfifu3.Wagraw.domain.model.Milestone
import com.Sasfifu3.Wagraw.domain.model.MilestoneKind
import com.Sasfifu3.Wagraw.domain.model.Recipe
import com.Sasfifu3.Wagraw.domain.model.RouteStop
import com.Sasfifu3.Wagraw.domain.model.StopModifier
import com.Sasfifu3.Wagraw.domain.model.Upgrade
import com.Sasfifu3.Wagraw.domain.model.UpgradeId

object SampleData {

    val stops: List<RouteStop> = listOf(
        RouteStop(1, R.string.stop_market_gate, StopModifier.STEADY, R.string.mod_steady, 1, 1, 3, 4, 0),
        RouteStop(2, R.string.stop_lantern_alley, StopModifier.STEADY, R.string.mod_steady, 1, 1, 4, 4, 0),
        RouteStop(3, R.string.stop_festival_rush, StopModifier.FESTIVAL, R.string.mod_festival, 2, 2, 5, 4, 0),
        RouteStop(4, R.string.stop_rainy_lane, StopModifier.RAINY, R.string.mod_rainy, 2, 2, 5, 5, 0),
        RouteStop(5, R.string.stop_spice_bazaar, StopModifier.STEADY, R.string.mod_steady, 3, 3, 6, 5, 0),
        RouteStop(6, R.string.stop_fire_pit_row, StopModifier.FIRE_PIT, R.string.mod_fire_pit, 3, 2, 6, 5, 2),
        RouteStop(7, R.string.stop_late_shift_dock, StopModifier.LATE_SHIFT, R.string.mod_late_shift, 4, 3, 7, 5, 4),
        RouteStop(8, R.string.stop_reaper_corner, StopModifier.STEADY, R.string.mod_steady, 5, 4, 8, 6, 6)
    )

    val defaultRoute: List<RouteStop> = listOf(1, 2, 3, 4, 5).mapNotNull { id -> stops.firstOrNull { it.id == id } }

    val fallbackRoute: List<RouteStop> = listOf(1, 2, 2, 3, 5).mapNotNull { id -> stops.firstOrNull { it.id == id } }

    val recipes: List<Recipe> = listOf(
        Recipe(
            1, R.string.recipe_market_mild_bao, 1,
            listOf(Ingredient.BELL_BASE, Ingredient.BELL_BASE, Ingredient.LIME_WEDGE), 3, R.drawable.sprite_2
        ),
        Recipe(
            2, R.string.recipe_paprika_fries, 2,
            listOf(Ingredient.BELL_BASE, Ingredient.BELL_BASE), 5, R.drawable.sprite_2
        ),
        Recipe(
            3, R.string.recipe_smoked_skewer, 2,
            listOf(Ingredient.JALAPENO), 8, R.drawable.sprite_2
        ),
        Recipe(
            4, R.string.recipe_ember_taco, 3,
            listOf(Ingredient.JALAPENO, Ingredient.BELL_BASE), 12, R.drawable.sprite_3
        ),
        Recipe(
            5, R.string.recipe_lime_cooler, 1,
            listOf(Ingredient.HABANERO, Ingredient.LIME_WEDGE, Ingredient.LIME_WEDGE), 16, R.drawable.sprite_2
        ),
        Recipe(
            6, R.string.recipe_ghost_curry, 4,
            listOf(Ingredient.HABANERO, Ingredient.BELL_BASE), 22, R.drawable.sprite_3
        ),
        Recipe(
            7, R.string.recipe_reaper_ramen, 5,
            listOf(Ingredient.REAPER_DUST), 30, R.drawable.sprite_1
        ),
        Recipe(
            8, R.string.recipe_double_fire, 7,
            listOf(Ingredient.GHOST_PEPPER, Ingredient.HABANERO), 42, R.drawable.sprite_1
        )
    )

    val upgrades: List<Upgrade> = listOf(
        Upgrade(UpgradeId.VENT_HOOD_I, R.string.upgrade_vent_hood_i, R.string.upgrade_vent_hood_i_effect, 120, null),
        Upgrade(UpgradeId.CHILI_FRIDGE, R.string.upgrade_chili_fridge, R.string.upgrade_chili_fridge_effect, 180, null),
        Upgrade(UpgradeId.PREP_BOARD, R.string.upgrade_prep_board, R.string.upgrade_prep_board_effect, 260, null),
        Upgrade(
            UpgradeId.VENT_HOOD_II, R.string.upgrade_vent_hood_ii, R.string.upgrade_vent_hood_ii_effect, 300,
            UpgradeId.VENT_HOOD_I
        )
    )

    val milestones: List<Milestone> = listOf(
        Milestone(1, R.string.milestone_three_runs, MilestoneKind.RUNS, 3),
        Milestone(2, R.string.milestone_ten_perfect, MilestoneKind.PERFECT_SERVES, 10),
        Milestone(3, R.string.milestone_clear_route, MilestoneKind.ROUTES_CLEARED, 1),
        Milestone(4, R.string.milestone_twentyfive_perfect, MilestoneKind.PERFECT_SERVES, 25),
        Milestone(5, R.string.milestone_fifty_runs, MilestoneKind.RUNS, 50)
    )

    fun labelOf(ingredient: Ingredient): Int = when (ingredient) {
        Ingredient.LIME_WEDGE -> R.string.ing_lime
        Ingredient.BELL_BASE -> R.string.ing_bell
        Ingredient.JALAPENO -> R.string.ing_jalapeno
        Ingredient.HABANERO -> R.string.ing_habanero
        Ingredient.GHOST_PEPPER -> R.string.ing_ghost
        Ingredient.REAPER_DUST -> R.string.ing_reaper
    }

    fun artOf(ingredient: Ingredient): Int = when (ingredient) {
        Ingredient.LIME_WEDGE, Ingredient.BELL_BASE, Ingredient.JALAPENO -> R.drawable.sprite_2
        else -> R.drawable.sprite_1
    }

    fun recipeById(id: Int?): Recipe? = recipes.firstOrNull { it.id == id }
}
