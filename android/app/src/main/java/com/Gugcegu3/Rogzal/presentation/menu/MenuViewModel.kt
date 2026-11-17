package com.Gugcegu3.Rogzal.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Gugcegu3.Rogzal.core.di.ServiceLocator
import com.Gugcegu3.Rogzal.data.sample.SampleData
import com.Gugcegu3.Rogzal.domain.model.ClauseKind
import com.Gugcegu3.Rogzal.domain.model.ContractClause
import com.Gugcegu3.Rogzal.domain.model.GameSettings
import com.Gugcegu3.Rogzal.domain.model.LevelSpec
import com.Gugcegu3.Rogzal.domain.repository.CampaignRepository
import com.Gugcegu3.Rogzal.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuViewModel(
    private val campaign: CampaignRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val nextLevel = campaign.unlockedLevels()
        val spec = SampleData.levelAt(nextLevel)
        _state.update {
            it.copy(
                nextLevel = nextLevel,
                spec = spec,
                medals = campaign.medalCount(),
                bestScore = campaign.bestOverallScore(),
                totalParcels = campaign.totalParcels(),
                clauses = contractOf(spec),
                settings = settings.load()
            )
        }
        loadWeather(spec)
    }

    private fun loadWeather(spec: LevelSpec) {
        viewModelScope.launch {
            val layout = withContext(Dispatchers.Default) {
                ServiceLocator.generateLayout(spec, 1)
            }
            _state.update { it.copy(weather = layout.weather) }
        }
    }

    private fun contractOf(spec: LevelSpec): List<ContractClause> = listOf(
        ContractClause(ClauseKind.DELIVER, spec.parcels, 0, false),
        ContractClause(ClauseKind.SIGNS, spec.signTarget, 0, false),
        ContractClause(ClauseKind.TICKS, spec.tickTarget, 0, false)
    )

    fun openSettings() {
        _state.update { it.copy(settingsVisible = true) }
    }

    fun closeSettings() {
        _state.update { it.copy(settingsVisible = false) }
    }

    fun setTickMs(value: Int) {
        updateSettings { it.copy(tickMs = value) }
    }

    fun setRibbon(value: Boolean) {
        updateSettings { it.copy(ribbon = value) }
    }

    fun setHighContrast(value: Boolean) {
        updateSettings { it.copy(highContrast = value) }
    }

    fun setConfirmGo(value: Boolean) {
        updateSettings { it.copy(confirmGo = value) }
    }

    private fun updateSettings(
        transform: (GameSettings) -> GameSettings
    ) {
        val updated = transform(_state.value.settings)
        settings.save(updated)
        _state.update { it.copy(settings = updated) }
    }
}
