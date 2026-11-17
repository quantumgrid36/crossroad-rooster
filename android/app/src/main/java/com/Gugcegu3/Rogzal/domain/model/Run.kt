package com.Gugcegu3.Rogzal.domain.model

enum class RunOutcome { DELIVERED, ROUTE_COMPLETE, CLIPPED, OUT_OF_TICKS }

enum class TickEvent { START, MOVE, WAIT, PICKUP, DELIVER, NEAR_MISS, COLLISION, FREEZE, RECOVER }

enum class ClauseKind { DELIVER, SIGNS, TICKS }

data class ContractClause(
    val kind: ClauseKind,
    val target: Int,
    val actual: Int,
    val met: Boolean
)

data class VehicleView(
    val id: Int,
    val laneIndex: Int,
    val row: Int,
    val col: Int,
    val heavy: Boolean,
    val facingRight: Boolean,
    val frozen: Boolean
)

data class TickFrame(
    val tick: Int,
    val rooster: Cell,
    val vehicles: List<VehicleView>,
    val parcels: List<Cell>,
    val carrying: Boolean,
    val event: TickEvent,
    val laneIndex: Int
)

data class RunState(
    val tick: Int,
    val rooster: Cell,
    val routeIndex: Int,
    val carrying: Boolean,
    val parcels: List<Cell>,
    val delivered: Int,
    val moveCount: Int,
    val recoverPending: Boolean,
    val pendingWait: Boolean,
    val laneTicks: List<Int>,
    val lanePeriods: List<Int>,
    val frozenUntil: List<Int>,
    val signs: List<SignState>,
    val nearMisses: Int,
    val signsUsed: Int,
    val signRefunds: Int,
    val patienceUsed: Int,
    val windApplied: Boolean,
    val finished: Boolean,
    val outcome: RunOutcome?,
    val lastEvent: TickEvent,
    val lastLane: Int
)

data class RunResult(
    val levelIndex: Int,
    val levelNameRes: Int,
    val attempt: Int,
    val outcome: RunOutcome,
    val score: Int,
    val ticksUsed: Int,
    val ticksTarget: Int,
    val delivered: Int,
    val required: Int,
    val signsUsed: Int,
    val signTarget: Int,
    val nearMisses: Int,
    val patienceLeft: Int,
    val medal: Medal,
    val clauses: List<ContractClause>,
    val frames: List<TickFrame>,
    val collisionTick: Int,
    val weather: Weather,
    val ribbonOff: Boolean,
    val rows: List<RowKind>,
    val barn: Cell,
    val cleared: Boolean
)
