package com.Gugcegu3.Rogzal.presentation.menu

import com.Gugcegu3.Rogzal.core.config.GameConfig
import com.Gugcegu3.Rogzal.domain.model.ContractClause
import com.Gugcegu3.Rogzal.domain.model.GameSettings
import com.Gugcegu3.Rogzal.domain.model.LevelSpec
import com.Gugcegu3.Rogzal.domain.model.Weather

data class MenuUiState(
    val nextLevel: Int = 1,
    val spec: LevelSpec? = null,
    val medals: Int = 0,
    val bestScore: Int = 0,
    val totalParcels: Int = 0,
    val weather: Weather = Weather.CLEAR,
    val clauses: List<ContractClause> = emptyList(),
    val settings: GameSettings = GameSettings(
        tickMs = GameConfig.TICK_STANDARD_MS,
        ribbon = true,
        highContrast = false,
        confirmGo = false
    ),
    val settingsVisible: Boolean = false
)
