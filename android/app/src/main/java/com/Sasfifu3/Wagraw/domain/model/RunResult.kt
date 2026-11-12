package com.Sasfifu3.Wagraw.domain.model

enum class RunEndReason {
    ROUTE_SERVED,
    OVERHEAT,
    WALKOUTS
}

data class RunResult(
    val reason: RunEndReason,
    val stopsCleared: Int,
    val totalStops: Int,
    val stopNameRes: Int,
    val coinsEarned: Int,
    val perfectServes: Int,
    val bestStreak: Int,
    val spiceEarned: Int,
    val assisted: Boolean,
    val newBest: Boolean,
    val nextRecipeNameRes: Int?
) {
    val cleared: Boolean
        get() = reason == RunEndReason.ROUTE_SERVED
}
