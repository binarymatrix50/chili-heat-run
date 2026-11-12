package com.Sasfifu3.Wagraw.presentation.game

import com.Sasfifu3.Wagraw.core.config.GameConfig
import com.Sasfifu3.Wagraw.domain.model.Ingredient
import com.Sasfifu3.Wagraw.domain.model.ServeOutcome

enum class GamePhase {
    BRIEF,
    PLAYING,
    SERVING,
    STOP_CLEARED,
    FINISHING
}

data class TraySlotUi(
    val ingredient: Ingredient,
    val labelRes: Int,
    val artRes: Int,
    val heat: Int,
    val stock: Int
)

data class QueueCardUi(
    val id: Int,
    val demand: Int,
    val patience: Float
)

data class PlateChipUi(
    val ingredient: Ingredient,
    val labelRes: Int,
    val artRes: Int
)

data class GameUiState(
    val ready: Boolean = false,
    val phase: GamePhase = GamePhase.BRIEF,
    val stopIndex: Int = 0,
    val totalStops: Int = GameConfig.ROUTE_STOPS,
    val stopNameRes: Int = 0,
    val stopModifierRes: Int = 0,
    val stopGuests: Int = 0,
    val stopMinDemand: Int = 0,
    val stopMaxDemand: Int = 0,
    val stallHeat: Int = 0,
    val coins: Int = 0,
    val streak: Int = 0,
    val perfectServes: Int = 0,
    val queue: List<QueueCardUi> = emptyList(),
    val tray: List<TraySlotUi> = emptyList(),
    val plate: List<PlateChipUi> = emptyList(),
    val dishHeat: Int = 0,
    val dishLimit: Int = GameConfig.DISH_MAX_INGREDIENTS,
    val frontDemand: Int? = null,
    val ventCharges: Int = 0,
    val ventMax: Int = 0,
    val skipTokens: Int = 0,
    val lastOutcome: ServeOutcome? = null,
    val heatNumbers: Boolean = true,
    val leftHanded: Boolean = false,
    val paused: Boolean = false,
    val stopCoins: Int = 0,
    val signatureReady: Boolean = false,
    val signatureNameRes: Int? = null,
    val overheated: Boolean = false
)

sealed interface GameEvent {
    data object Finished : GameEvent
}
