package com.Sasfifu3.Wagraw.domain.model

data class StopPlan(
    val stop: RouteStop,
    val demands: List<Int>,
    val stock: Map<Ingredient, Int>,
    val patienceMs: Long
)

data class RunPlan(
    val seed: Long,
    val stops: List<StopPlan>
)
