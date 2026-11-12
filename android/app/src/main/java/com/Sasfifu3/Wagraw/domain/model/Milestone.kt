package com.Sasfifu3.Wagraw.domain.model

enum class MilestoneKind {
    RUNS,
    PERFECT_SERVES,
    ROUTES_CLEARED
}

data class Milestone(
    val id: Int,
    val labelRes: Int,
    val kind: MilestoneKind,
    val target: Int
)
