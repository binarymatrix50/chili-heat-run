package com.Sasfifu3.Wagraw.presentation.progress

import com.Sasfifu3.Wagraw.domain.model.CareerStats
import com.Sasfifu3.Wagraw.domain.model.Milestone
import com.Sasfifu3.Wagraw.domain.model.Upgrade

data class MilestoneUi(
    val milestone: Milestone,
    val current: Int,
    val reached: Boolean
) {
    val fraction: Float
        get() = if (milestone.target <= 0) {
            1f
        } else {
            (current.toFloat() / milestone.target.toFloat()).coerceIn(0f, 1f)
        }
}

data class UpgradeUi(
    val upgrade: Upgrade,
    val owned: Boolean,
    val affordable: Boolean,
    val blocked: Boolean
)

data class ProgressUiState(
    val stats: CareerStats = CareerStats(),
    val coins: Int = 0,
    val recipes: Int = 0,
    val milestones: List<MilestoneUi> = emptyList(),
    val upgrades: List<UpgradeUi> = emptyList(),
    val hasRuns: Boolean = false
)
