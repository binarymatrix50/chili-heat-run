package com.Sasfifu3.Wagraw.domain.usecase

import com.Sasfifu3.Wagraw.core.config.GameConfig
import com.Sasfifu3.Wagraw.domain.model.ServeOutcome
import com.Sasfifu3.Wagraw.domain.model.ServeResult
import kotlin.math.abs
import kotlin.math.roundToInt

class ResolveServeUseCase {

    operator fun invoke(
        dishHeat: Int,
        demand: Int,
        streak: Int,
        coinFactor: Float,
        signature: Boolean
    ): ServeResult {
        val delta = dishHeat - demand
        return when {
            delta == 0 -> ServeResult(
                outcome = ServeOutcome.PERFECT,
                coins = (GameConfig.COINS_PERFECT * comboMultiplier(streak) * coinFactor).roundToInt(),
                heatDelta = -(GameConfig.PERFECT_VENT + if (signature) GameConfig.SIGNATURE_VENT_BONUS else 0),
                streak = streak + 1,
                spice = GameConfig.SPICE_PER_PERFECT + if (signature) GameConfig.SPICE_SIGNATURE_BONUS else 0
            )

            abs(delta) == 1 -> ServeResult(
                outcome = ServeOutcome.PASSABLE,
                coins = (GameConfig.COINS_PASSABLE * coinFactor).roundToInt(),
                heatDelta = 0,
                streak = 0,
                spice = 0
            )

            delta <= -2 -> ServeResult(
                outcome = ServeOutcome.BLAND,
                coins = 0,
                heatDelta = GameConfig.WALKOUT_HEAT,
                streak = 0,
                spice = 0
            )

            else -> ServeResult(
                outcome = ServeOutcome.BURN,
                coins = 0,
                heatDelta = delta * GameConfig.OVERSHOOT_HEAT_PER_PIP,
                streak = 0,
                spice = 0
            )
        }
    }

    fun comboMultiplier(streak: Int): Float = when {
        streak >= 8 -> 3.0f
        streak >= 5 -> 2.0f
        streak >= 3 -> 1.5f
        else -> 1.0f
    }
}
