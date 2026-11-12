package com.Sasfifu3.Wagraw.domain.usecase

import com.Sasfifu3.Wagraw.core.config.GameConfig
import com.Sasfifu3.Wagraw.domain.model.GameSettings
import com.Sasfifu3.Wagraw.domain.model.RunConfig
import com.Sasfifu3.Wagraw.domain.model.UpgradeId

class ApplyUpgradesUseCase {

    operator fun invoke(owned: Set<UpgradeId>, settings: GameSettings): RunConfig {
        val fridge = if (owned.contains(UpgradeId.CHILI_FRIDGE)) 1.2f else 1.0f
        val assist = if (settings.patienceAssist) 1.25f else 1.0f
        return RunConfig(
            ventCharges = if (owned.contains(UpgradeId.VENT_HOOD_I)) {
                GameConfig.VENT_CHARGES_UPGRADED
            } else {
                GameConfig.VENT_CHARGES
            },
            ventAmount = if (owned.contains(UpgradeId.VENT_HOOD_II)) {
                GameConfig.VENT_AMOUNT_UPGRADED
            } else {
                GameConfig.VENT_AMOUNT
            },
            dishLimit = if (owned.contains(UpgradeId.PREP_BOARD)) {
                GameConfig.DISH_MAX_UPGRADED
            } else {
                GameConfig.DISH_MAX_INGREDIENTS
            },
            patienceFactor = fridge * assist,
            coinFactor = if (settings.patienceAssist) 0.8f else 1.0f,
            extraStock = if (owned.contains(UpgradeId.PREP_BOARD)) 1 else 0,
            assisted = settings.patienceAssist
        )
    }
}
