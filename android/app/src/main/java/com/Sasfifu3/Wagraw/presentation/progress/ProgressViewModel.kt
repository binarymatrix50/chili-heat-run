package com.Sasfifu3.Wagraw.presentation.progress

import androidx.lifecycle.ViewModel
import com.Sasfifu3.Wagraw.data.sample.SampleData
import com.Sasfifu3.Wagraw.domain.model.MilestoneKind
import com.Sasfifu3.Wagraw.domain.model.Upgrade
import com.Sasfifu3.Wagraw.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProgressViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val progress = progressRepository.load()
        val stats = progress.stats
        _uiState.value = ProgressUiState(
            stats = stats,
            coins = progress.coins,
            recipes = progress.unlockedRecipes.size,
            milestones = SampleData.milestones.map { milestone ->
                val current = when (milestone.kind) {
                    MilestoneKind.RUNS -> stats.runs
                    MilestoneKind.PERFECT_SERVES -> stats.perfectServes
                    MilestoneKind.ROUTES_CLEARED -> stats.routesCleared
                }
                MilestoneUi(milestone, current, current >= milestone.target)
            },
            upgrades = SampleData.upgrades.map { upgrade ->
                val owned = progress.ownedUpgrades.contains(upgrade.id)
                val required = upgrade.requires
                UpgradeUi(
                    upgrade = upgrade,
                    owned = owned,
                    affordable = progress.coins >= upgrade.price,
                    blocked = required != null && !progress.ownedUpgrades.contains(required)
                )
            },
            hasRuns = stats.runs > 0
        )
    }

    fun buy(upgrade: Upgrade) {
        val progress = progressRepository.load()
        if (progress.ownedUpgrades.contains(upgrade.id)) return
        val required = upgrade.requires
        if (required != null && !progress.ownedUpgrades.contains(required)) return
        if (progress.coins < upgrade.price) return
        progressRepository.save(
            progress.copy(
                coins = progress.coins - upgrade.price,
                ownedUpgrades = progress.ownedUpgrades + upgrade.id
            )
        )
        refresh()
    }
}
