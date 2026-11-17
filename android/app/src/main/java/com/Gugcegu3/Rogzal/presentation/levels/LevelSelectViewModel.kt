package com.Gugcegu3.Rogzal.presentation.levels

import androidx.lifecycle.ViewModel
import com.Gugcegu3.Rogzal.core.config.GameConfig
import com.Gugcegu3.Rogzal.data.sample.SampleData
import com.Gugcegu3.Rogzal.domain.model.LevelProgress
import com.Gugcegu3.Rogzal.domain.repository.CampaignRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LevelSelectUiState(
    val levels: List<LevelProgress> = emptyList(),
    val medalPoints: Int = 0,
    val medalMax: Int = GameConfig.LEVEL_COUNT * 3,
    val fresh: Boolean = true
)

class LevelSelectViewModel(private val campaign: CampaignRepository) : ViewModel() {

    private val _state = MutableStateFlow(LevelSelectUiState())
    val state: StateFlow<LevelSelectUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val unlocked = campaign.unlockedLevels()
        val levels = SampleData.levels.map { spec ->
            LevelProgress(
                spec = spec,
                unlocked = spec.index <= unlocked,
                medal = campaign.medal(spec.index),
                bestScore = campaign.bestScore(spec.index),
                bestTicks = campaign.bestTicks(spec.index)
            )
        }
        _state.value = LevelSelectUiState(
            levels = levels,
            medalPoints = levels.sumOf { it.medal.ordinal },
            fresh = unlocked <= 1
        )
    }
}
