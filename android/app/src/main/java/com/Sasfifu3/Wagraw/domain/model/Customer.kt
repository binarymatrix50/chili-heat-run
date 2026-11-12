package com.Sasfifu3.Wagraw.domain.model

data class Customer(
    val id: Int,
    val demand: Int,
    val patienceLeftMs: Long,
    val patienceTotalMs: Long
) {
    val patienceFraction: Float
        get() = if (patienceTotalMs <= 0L) 0f else (patienceLeftMs.toFloat() / patienceTotalMs.toFloat()).coerceIn(0f, 1f)
}
