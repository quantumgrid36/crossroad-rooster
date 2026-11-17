package com.Gugcegu3.Rogzal.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Gugcegu3.Rogzal.core.config.GameConfig
import com.Gugcegu3.Rogzal.domain.model.RunResult
import com.Gugcegu3.Rogzal.domain.repository.CampaignRepository
import com.Gugcegu3.Rogzal.domain.repository.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class GameOverUiState(
    val result: RunResult? = null,
    val tickIndex: Int = 0,
    val playing: Boolean = false,
    val bestScore: Int = 0,
    val nextLevel: Int = 0
)

class GameOverViewModel(
    private val campaign: CampaignRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    private var bound: RunResult? = null
    private var replay: Job? = null

    fun bind(result: RunResult) {
        if (bound == result) return
        bound = result
        replay?.cancel()
        campaign.saveRun(result)
        val next = if (result.cleared && result.levelIndex < GameConfig.LEVEL_COUNT) {
            result.levelIndex + 1
        } else {
            0
        }
        _state.value = GameOverUiState(
            result = result,
            tickIndex = result.frames.lastIndex.coerceAtLeast(0),
            playing = false,
            bestScore = campaign.bestScore(result.levelIndex),
            nextLevel = next
        )
    }

    fun setTickIndex(index: Int) {
        replay?.cancel()
        _state.update { it.copy(tickIndex = index, playing = false) }
    }

    fun toggleReplay() {
        val current = _state.value
        val frames = current.result?.frames.orEmpty()
        if (frames.size < 2) return
        if (current.playing) {
            replay?.cancel()
            _state.update { it.copy(playing = false) }
            return
        }
        val step = settings.load().tickMs.toLong()
        _state.update { it.copy(playing = true, tickIndex = 0) }
        replay?.cancel()
        replay = viewModelScope.launch {
            var index = 0
            while (isActive && index < frames.lastIndex) {
                delay(step)
                index += 1
                _state.update { it.copy(tickIndex = index) }
            }
            _state.update { it.copy(playing = false) }
        }
    }

    override fun onCleared() {
        replay?.cancel()
        super.onCleared()
    }
}
