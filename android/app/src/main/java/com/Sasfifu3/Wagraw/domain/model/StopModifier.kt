package com.Sasfifu3.Wagraw.domain.model

enum class StopModifier(
    val extraCustomers: Int,
    val patienceFactor: Float,
    val coinFactor: Float,
    val demandBonus: Int,
    val stockPenalty: Int
) {
    STEADY(0, 1.0f, 1.0f, 0, 0),
    FESTIVAL(1, 1.0f, 2.0f, 0, 0),
    RAINY(0, 1.3f, 0.5f, 0, 0),
    FIRE_PIT(0, 1.0f, 1.5f, 1, 0),
    LATE_SHIFT(0, 1.0f, 1.0f, 0, 1)
}
