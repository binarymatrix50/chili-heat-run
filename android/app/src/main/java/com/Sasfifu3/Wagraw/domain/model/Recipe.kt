package com.Sasfifu3.Wagraw.domain.model

data class Recipe(
    val id: Int,
    val nameRes: Int,
    val heat: Int,
    val ingredients: List<Ingredient>,
    val cost: Int,
    val artRes: Int
)
