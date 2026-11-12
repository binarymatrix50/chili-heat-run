package com.Sasfifu3.Wagraw.domain.model

enum class UpgradeId {
    VENT_HOOD_I,
    CHILI_FRIDGE,
    PREP_BOARD,
    VENT_HOOD_II
}

data class Upgrade(
    val id: UpgradeId,
    val nameRes: Int,
    val effectRes: Int,
    val price: Int,
    val requires: UpgradeId?
)
