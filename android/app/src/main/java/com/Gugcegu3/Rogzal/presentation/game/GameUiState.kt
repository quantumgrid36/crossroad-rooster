package com.Gugcegu3.Rogzal.presentation.game

import com.Gugcegu3.Rogzal.core.config.GameConfig
import com.Gugcegu3.Rogzal.domain.model.BoardLayout
import com.Gugcegu3.Rogzal.domain.model.Cell
import com.Gugcegu3.Rogzal.domain.model.ContractClause
import com.Gugcegu3.Rogzal.domain.model.LanePreview
import com.Gugcegu3.Rogzal.domain.model.RouteNode
import com.Gugcegu3.Rogzal.domain.model.SignState
import com.Gugcegu3.Rogzal.domain.model.VehicleView
import com.Gugcegu3.Rogzal.domain.model.Weather

enum class GamePhase { LOADING, PLANNING, RUNNING, HELD }

enum class InputMode { ROUTE, SIGN }

data class GameUiState(
    val levelIndex: Int = 1,
    val levelNameRes: Int = 0,
    val phase: GamePhase = GamePhase.LOADING,
    val layout: BoardLayout? = null,
    val rooster: Cell = Cell(GameConfig.ROWS - 1, GameConfig.COLS / 2),
    val vehicles: List<VehicleView> = emptyList(),
    val parcels: List<Cell> = emptyList(),
    val carrying: Boolean = false,
    val route: List<RouteNode> = emptyList(),
    val signs: List<SignState> = emptyList(),
    val mode: InputMode = InputMode.ROUTE,
    val tick: Int = 0,
    val tickTarget: Int = 22,
    val signsLeft: Int = 3,
    val signBudget: Int = 3,
    val patienceLeft: Int = GameConfig.PATIENCE_TOKENS,
    val delivered: Int = 0,
    val required: Int = 1,
    val nearMisses: Int = 0,
    val ribbon: List<LanePreview> = emptyList(),
    val clauses: List<ContractClause> = emptyList(),
    val weather: Weather = Weather.CLEAR,
    val windBanner: Boolean = false,
    val collisionCell: Cell? = null,
    val pauseVisible: Boolean = false,
    val confirmVisible: Boolean = false,
    val highContrast: Boolean = false,
    val ribbonEnabled: Boolean = true,
    val tickMs: Int = GameConfig.TICK_STANDARD_MS
) {
    val routeLength: Int get() = route.size
    val canGo: Boolean get() = phase == GamePhase.PLANNING || phase == GamePhase.HELD
}
