package com.Sasfifu3.Wagraw.domain.model

data class RunConfig(
    val ventCharges: Int,
    val ventAmount: Int,
    val dishLimit: Int,
    val patienceFactor: Float,
    val coinFactor: Float,
    val extraStock: Int,
    val assisted: Boolean
)
