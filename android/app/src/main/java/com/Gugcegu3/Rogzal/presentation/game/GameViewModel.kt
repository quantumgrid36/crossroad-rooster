package com.Gugcegu3.Rogzal.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Gugcegu3.Rogzal.core.config.GameConfig
import com.Gugcegu3.Rogzal.data.sample.SampleData
import com.Gugcegu3.Rogzal.domain.model.BoardLayout
import com.Gugcegu3.Rogzal.domain.model.Cell
import com.Gugcegu3.Rogzal.domain.model.ClauseKind
import com.Gugcegu3.Rogzal.domain.model.ContractClause
import com.Gugcegu3.Rogzal.domain.model.LevelSpec
import com.Gugcegu3.Rogzal.domain.model.Move
import com.Gugcegu3.Rogzal.domain.model.RouteNode
import com.Gugcegu3.Rogzal.domain.model.RowKind
import com.Gugcegu3.Rogzal.domain.model.RunOutcome
import com.Gugcegu3.Rogzal.domain.model.RunResult
import com.Gugcegu3.Rogzal.domain.model.RunState
import com.Gugcegu3.Rogzal.domain.model.SignState
import com.Gugcegu3.Rogzal.domain.model.TickEvent
import com.Gugcegu3.Rogzal.domain.model.TickFrame
import com.Gugcegu3.Rogzal.domain.model.Weather
import com.Gugcegu3.Rogzal.domain.repository.SettingsRepository
import com.Gugcegu3.Rogzal.domain.usecase.AdvanceTickUseCase
import com.Gugcegu3.Rogzal.domain.usecase.EvaluateRunUseCase
import com.Gugcegu3.Rogzal.domain.usecase.GenerateLayoutUseCase
import com.Gugcegu3.Rogzal.domain.usecase.ProjectBoardUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface GameEvent {
    data class Finished(val result: RunResult) : GameEvent
}

class GameViewModel(
    private val settings: SettingsRepository,
    private val generateLayout: GenerateLayoutUseCase,
    private val advanceTick: AdvanceTickUseCase,
    private val projectBoard: ProjectBoardUseCase,
    private val evaluateRun: EvaluateRunUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val eventChannel = Channel<GameEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private var spec: LevelSpec = SampleData.levels.first()
    private var layout: BoardLayout? = null
    private var runState: RunState? = null
    private var loop: Job? = null
    private var ribbonOff = false
    private var attemptNumber = 1
    private val frames = mutableListOf<TickFrame>()

    fun prepare(levelIndex: Int, attempt: Int) {
        loop?.cancel()
        attemptNumber = attempt
        spec = SampleData.levelAt(levelIndex)
        val current = settings.load()
        ribbonOff = !current.ribbon
        _state.value = GameUiState(
            levelIndex = spec.index,
            levelNameRes = spec.nameRes,
            phase = GamePhase.LOADING,
            tickTarget = spec.tickTarget,
            signBudget = spec.signBudget,
            signsLeft = spec.signBudget,
            required = spec.parcels,
            highContrast = current.highContrast,
            ribbonEnabled = current.ribbon,
            tickMs = current.tickMs,
            clauses = evaluateRunClausesPreview()
        )
        viewModelScope.launch {
            val generated = withContext(Dispatchers.Default) {
                generateLayout(spec, attempt)
            }
            layout = generated
            val initial = advanceTick.initialState(generated, emptyList())
            runState = initial
            frames.clear()
            frames.add(frameOf(generated, initial))
            _state.update {
                it.copy(
                    phase = GamePhase.PLANNING,
                    layout = generated,
                    rooster = generated.start,
                    parcels = generated.parcels,
                    vehicles = projectBoard.vehicles(generated, initial),
                    ribbon = ribbonOf(generated, initial),
                    weather = generated.weather,
                    route = emptyList(),
                    signs = emptyList(),
                    signsLeft = spec.signBudget,
                    patienceLeft = GameConfig.PATIENCE_TOKENS,
                    tick = 0,
                    delivered = 0,
                    carrying = false,
                    nearMisses = 0,
                    collisionCell = null
                )
            }
        }
    }

    private fun evaluateRunClausesPreview(): List<ContractClause> = listOf(
        ContractClause(ClauseKind.DELIVER, spec.parcels, 0, false),
        ContractClause(ClauseKind.SIGNS, spec.signTarget, 0, false),
        ContractClause(ClauseKind.TICKS, spec.tickTarget, 0, false)
    )

    fun setMode(mode: InputMode) {
        if (_state.value.phase != GamePhase.PLANNING) return
        _state.update { it.copy(mode = mode) }
    }

    fun onCellTap(cell: Cell) {
        val current = _state.value
        val board = layout ?: return
        if (current.phase == GamePhase.RUNNING) {
            if (cell == current.rooster) spendPatience()
            return
        }
        if (current.phase != GamePhase.PLANNING) return
        when (current.mode) {
            InputMode.ROUTE -> appendRoute(board, cell)
            InputMode.SIGN -> toggleSign(board, cell)
        }
    }

    private fun appendRoute(board: BoardLayout, cell: Cell) {
        val current = _state.value
        if (current.route.size >= spec.tickTarget) return
        val end = current.route.lastOrNull()?.cell ?: board.start
        if (cell == end) {
            _state.update { it.copy(route = it.route + RouteNode(Move.WAIT, end)) }
            return
        }
        val deltaRow = cell.row - end.row
        val deltaCol = cell.col - end.col
        val move = when {
            deltaRow == -1 && deltaCol == 0 -> Move.UP
            deltaRow == 1 && deltaCol == 0 -> Move.DOWN
            deltaRow == 0 && deltaCol == -1 -> Move.LEFT
            deltaRow == 0 && deltaCol == 1 -> Move.RIGHT
            else -> null
        } ?: return
        _state.update { it.copy(route = it.route + RouteNode(move, cell)) }
    }

    private fun toggleSign(board: BoardLayout, cell: Cell) {
        if (board.rows.getOrNull(cell.row) != RowKind.ROAD) return
        val laneIndex = board.laneIndexAt(cell.row)
        if (laneIndex < 0) return
        val current = _state.value
        val existing = current.signs.firstOrNull { it.laneIndex == laneIndex }
        val updated = when {
            existing != null && existing.col == cell.col -> current.signs - existing
            existing != null -> current.signs - existing + SignState(cell.row, cell.col, laneIndex)
            current.signs.size >= current.signBudget -> return
            else -> current.signs + SignState(cell.row, cell.col, laneIndex)
        }
        _state.update {
            it.copy(
                signs = updated,
                signsLeft = (it.signBudget - updated.size).coerceAtLeast(0),
                ribbon = refreshRibbon(board, updated)
            )
        }
    }

    fun undo() {
        val current = _state.value
        if (current.phase != GamePhase.PLANNING) return
        val board = layout ?: return
        when (current.mode) {
            InputMode.ROUTE -> if (current.route.isNotEmpty()) {
                _state.update { it.copy(route = it.route.dropLast(1)) }
            }
            InputMode.SIGN -> if (current.signs.isNotEmpty()) {
                val updated = current.signs.dropLast(1)
                _state.update {
                    it.copy(
                        signs = updated,
                        signsLeft = (it.signBudget - updated.size).coerceAtLeast(0),
                        ribbon = refreshRibbon(board, updated)
                    )
                }
            }
        }
    }

    fun clearPlan() {
        val board = layout ?: return
        if (_state.value.phase != GamePhase.PLANNING) return
        _state.update {
            it.copy(
                route = emptyList(),
                signs = emptyList(),
                signsLeft = it.signBudget,
                ribbon = refreshRibbon(board, emptyList())
            )
        }
    }

    fun onGoPressed() {
        val current = _state.value
        if (current.phase == GamePhase.RUNNING) {
            hold()
            return
        }
        if (current.phase != GamePhase.PLANNING && current.phase != GamePhase.HELD) return
        val needsConfirm = settings.load().confirmGo &&
            (current.route.isEmpty() || current.signsLeft > 0)
        if (needsConfirm && current.phase == GamePhase.PLANNING) {
            _state.update { it.copy(confirmVisible = true) }
            return
        }
        startRun()
    }

    fun confirmGo() {
        _state.update { it.copy(confirmVisible = false) }
        startRun()
    }

    fun dismissConfirm() {
        _state.update { it.copy(confirmVisible = false) }
    }

    private fun startRun() {
        val board = layout ?: return
        val current = _state.value
        if (current.phase == GamePhase.HELD) {
            _state.update { it.copy(phase = GamePhase.RUNNING) }
            launchLoop()
            return
        }
        val initial = advanceTick.initialState(board, current.signs)
        runState = initial
        frames.clear()
        frames.add(frameOf(board, initial))
        _state.update {
            it.copy(
                phase = GamePhase.RUNNING,
                tick = 0,
                collisionCell = null,
                rooster = board.start,
                parcels = board.parcels,
                carrying = false,
                delivered = 0,
                nearMisses = 0,
                vehicles = projectBoard.vehicles(board, initial),
                patienceLeft = GameConfig.PATIENCE_TOKENS
            )
        }
        launchLoop()
    }

    private fun launchLoop() {
        loop?.cancel()
        val interval = _state.value.tickMs.toLong()
        loop = viewModelScope.launch {
            while (isActive) {
                delay(interval)
                if (!step()) break
            }
        }
    }

    fun hold() {
        loop?.cancel()
        loop = null
        if (_state.value.phase == GamePhase.RUNNING) {
            _state.update { it.copy(phase = GamePhase.HELD) }
        }
    }

    fun openPause() {
        hold()
        _state.update { it.copy(pauseVisible = true) }
    }

    fun resumeFromPause() {
        _state.update { it.copy(pauseVisible = false) }
        if (_state.value.phase == GamePhase.HELD) startRun()
    }

    fun restartLevel() {
        _state.update { it.copy(pauseVisible = false) }
        prepare(spec.index, attemptNumber + 1)
    }

    private fun spendPatience() {
        val current = runState ?: return
        if (current.pendingWait) return
        val used = current.patienceUsed
        if (used >= GameConfig.PATIENCE_TOKENS) return
        runState = current.copy(pendingWait = true)
        _state.update {
            it.copy(patienceLeft = (GameConfig.PATIENCE_TOKENS - used - 1).coerceAtLeast(0))
        }
    }

    private fun step(): Boolean {
        val board = layout ?: return false
        val current = runState ?: return false
        val limit = spec.tickTarget + GameConfig.TICK_GRACE
        val next = advanceTick(board, _state.value.route, current, spec.parcels, limit)
        runState = next
        if (next.tick != current.tick) frames.add(frameOf(board, next))
        val collision = if (next.lastEvent == TickEvent.COLLISION) next.rooster else null
        _state.update {
            it.copy(
                tick = next.tick,
                rooster = next.rooster,
                parcels = next.parcels,
                carrying = next.carrying,
                delivered = next.delivered,
                nearMisses = next.nearMisses,
                vehicles = projectBoard.vehicles(board, next),
                ribbon = ribbonOf(board, next),
                signsLeft = (spec.signBudget + next.signRefunds - next.signs.size)
                    .coerceAtLeast(0),
                patienceLeft = (GameConfig.PATIENCE_TOKENS - next.patienceUsed)
                    .coerceAtLeast(0),
                windBanner = next.windApplied && board.weather == Weather.WIND,
                collisionCell = collision
            )
        }
        if (next.finished) {
            finish(next)
            return false
        }
        return true
    }

    private fun finish(finalState: RunState) {
        loop?.cancel()
        loop = null
        val board = layout ?: return
        val clauses = evaluateRun.clauses(spec, finalState)
        val medal = evaluateRun.medal(clauses)
        val score = evaluateRun.score(spec, finalState, ribbonOff)
        val outcome = finalState.outcome ?: RunOutcome.OUT_OF_TICKS
        val result = RunResult(
            levelIndex = spec.index,
            levelNameRes = spec.nameRes,
            attempt = attemptNumber,
            outcome = outcome,
            score = score,
            ticksUsed = finalState.tick,
            ticksTarget = spec.tickTarget,
            delivered = finalState.delivered,
            required = spec.parcels,
            signsUsed = finalState.signs.count { it.consumed },
            signTarget = spec.signTarget,
            nearMisses = finalState.nearMisses,
            patienceLeft = (GameConfig.PATIENCE_TOKENS - finalState.patienceUsed)
                .coerceAtLeast(0),
            medal = medal,
            clauses = clauses,
            frames = frames.toList(),
            collisionTick = if (outcome == RunOutcome.CLIPPED) finalState.tick else -1,
            weather = board.weather,
            ribbonOff = ribbonOff,
            rows = board.rows,
            barn = board.barn,
            cleared = finalState.delivered >= spec.parcels
        )
        viewModelScope.launch {
            delay(FINISH_DELAY_MS)
            eventChannel.send(GameEvent.Finished(result))
        }
    }

    private fun frameOf(board: BoardLayout, source: RunState): TickFrame = TickFrame(
        tick = source.tick,
        rooster = source.rooster,
        vehicles = projectBoard.vehicles(board, source),
        parcels = source.parcels,
        carrying = source.carrying,
        event = source.lastEvent,
        laneIndex = source.lastLane
    )

    private fun ribbonOf(board: BoardLayout, source: RunState) =
        projectBoard.ribbon(
            layout = board,
            state = source,
            ticksAhead = if (board.weather == Weather.FOG) {
                GameConfig.FOG_RIBBON_TICKS
            } else {
                GameConfig.RIBBON_TICKS
            },
            revealedLanes = revealedLanes(board, source.signs)
        )

    private fun refreshRibbon(board: BoardLayout, signs: List<SignState>) =
        runState?.let { current -> ribbonOf(board, current.copy(signs = signs)) } ?: emptyList()

    private fun revealedLanes(board: BoardLayout, signs: List<SignState>): Set<Int> =
        signs.filter { sign ->
            board.shadowCells.any { it.row == sign.row && it.col == sign.col }
        }.map { it.laneIndex }.toSet()

    override fun onCleared() {
        loop?.cancel()
        loop = null
        super.onCleared()
    }

    private companion object {
        const val FINISH_DELAY_MS = 420L
    }
}
