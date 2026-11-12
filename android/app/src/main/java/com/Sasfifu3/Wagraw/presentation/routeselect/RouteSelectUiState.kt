package com.Sasfifu3.Wagraw.presentation.routeselect

import com.Sasfifu3.Wagraw.core.config.GameConfig
import com.Sasfifu3.Wagraw.domain.model.RouteStop

data class StopOptionUi(
    val stop: RouteStop,
    val locked: Boolean
)

data class RouteSelectUiState(
    val chosen: List<RouteStop> = emptyList(),
    val options: List<StopOptionUi> = emptyList(),
    val totalSlots: Int = GameConfig.ROUTE_STOPS,
    val spiceTokens: Int = 0,
    val rerollsLeft: Int = 1,
    val runs: Int = 0,
    val lockedNotice: Boolean = false
) {
    val complete: Boolean
        get() = chosen.size >= totalSlots

    val missing: Int
        get() = (totalSlots - chosen.size).coerceAtLeast(0)
}
