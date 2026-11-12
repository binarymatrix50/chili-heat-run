package com.Sasfifu3.Wagraw.domain.model

data class RouteStop(
    val id: Int,
    val nameRes: Int,
    val modifier: StopModifier,
    val modifierRes: Int,
    val difficulty: Int,
    val minDemand: Int,
    val maxDemand: Int,
    val customers: Int,
    val unlockRuns: Int
)
