package com.Gugcegu3.Rogzal.domain.model

data class LevelSpec(
    val index: Int,
    val nameRes: Int,
    val laneCount: Int,
    val minPeriod: Int,
    val maxPeriod: Int,
    val parcels: Int,
    val signBudget: Int,
    val signTarget: Int,
    val tickTarget: Int,
    val shadows: Int
)

data class GameSettings(
    val tickMs: Int,
    val ribbon: Boolean,
    val highContrast: Boolean,
    val confirmGo: Boolean
)

data class LevelProgress(
    val spec: LevelSpec,
    val unlocked: Boolean,
    val medal: Medal,
    val bestScore: Int,
    val bestTicks: Int
)

data class LanePreview(
    val laneIndex: Int,
    val row: Int,
    val heavy: Boolean,
    val hidden: Boolean,
    val pips: List<Boolean>
)
