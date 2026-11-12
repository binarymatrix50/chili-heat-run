package com.Sasfifu3.Wagraw.domain.model

enum class ServeOutcome {
    PERFECT,
    PASSABLE,
    BLAND,
    BURN
}

data class ServeResult(
    val outcome: ServeOutcome,
    val coins: Int,
    val heatDelta: Int,
    val streak: Int,
    val spice: Int
)
