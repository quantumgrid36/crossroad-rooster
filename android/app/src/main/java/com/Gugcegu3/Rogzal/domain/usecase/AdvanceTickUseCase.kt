package com.Gugcegu3.Rogzal.domain.usecase

import com.Gugcegu3.Rogzal.domain.model.BoardLayout
import com.Gugcegu3.Rogzal.domain.model.Cell
import com.Gugcegu3.Rogzal.domain.model.LaneRhythm
import com.Gugcegu3.Rogzal.domain.model.Move
import com.Gugcegu3.Rogzal.domain.model.RouteNode
import com.Gugcegu3.Rogzal.domain.model.RunOutcome
import com.Gugcegu3.Rogzal.domain.model.RunState
import com.Gugcegu3.Rogzal.domain.model.SignState
import com.Gugcegu3.Rogzal.domain.model.TickEvent
import com.Gugcegu3.Rogzal.domain.model.Weather

class AdvanceTickUseCase(
    private val cols: Int,
    private val rows: Int,
    private val signHoldTicks: Int,
    private val rainMovesPerRecover: Int,
    private val nearMissRefund: Int
) {

    fun initialState(layout: BoardLayout, signs: List<SignState>): RunState =
        RunState(
            tick = 0,
            rooster = layout.start,
            routeIndex = 0,
            carrying = false,
            parcels = layout.parcels,
            delivered = 0,
            moveCount = 0,
            recoverPending = false,
            pendingWait = false,
            laneTicks = layout.lanes.map { 0 },
            lanePeriods = layout.lanes.map { it.period },
            frozenUntil = layout.lanes.map { -1 },
            signs = signs,
            nearMisses = 0,
            signsUsed = signs.size,
            signRefunds = 0,
            patienceUsed = 0,
            windApplied = false,
            finished = false,
            outcome = null,
            lastEvent = TickEvent.START,
            lastLane = -1
        )

    operator fun invoke(
        layout: BoardLayout,
        route: List<RouteNode>,
        state: RunState,
        required: Int,
        tickLimit: Int
    ): RunState {
        if (state.finished) return state
        val tick = state.tick + 1
        val forcedRecover = state.recoverPending
        val waitRequested = state.pendingWait

        if (!forcedRecover && !waitRequested && state.routeIndex >= route.size) {
            return state.copy(
                finished = true,
                outcome = endingForExhaustedRoute(state.delivered, required),
                lastEvent = TickEvent.WAIT
            )
        }
        if (tick > tickLimit) {
            return state.copy(
                finished = true,
                outcome = RunOutcome.OUT_OF_TICKS,
                lastEvent = TickEvent.WAIT
            )
        }

        val periods = state.lanePeriods.toMutableList()
        var windApplied = state.windApplied
        if (layout.weather == Weather.WIND && !windApplied && layout.windLane in periods.indices &&
            tick >= tickLimit / 2
        ) {
            periods[layout.windLane] = (periods[layout.windLane] - 1).coerceAtLeast(3)
            windApplied = true
        }

        var move = Move.WAIT
        var routeIndex = state.routeIndex
        var target = state.rooster
        if (!forcedRecover && !waitRequested) {
            val node = route[routeIndex]
            move = node.move
            routeIndex += 1
            val candidate = Cell(state.rooster.row + move.dRow, state.rooster.col + move.dCol)
            target = if (candidate.row in 0 until rows && candidate.col in 0 until cols) candidate else state.rooster
        }

        val signs = state.signs.toMutableList()
        val frozenUntil = state.frozenUntil.toMutableList()
        var event = if (forcedRecover) TickEvent.RECOVER else if (move == Move.WAIT) TickEvent.WAIT else TickEvent.MOVE
        val targetLane = layout.laneIndexAt(target.row)
        if (targetLane >= 0) {
            val index = signs.indexOfFirst { it.laneIndex == targetLane && !it.consumed }
            if (index >= 0) {
                signs[index] = signs[index].copy(consumed = true, activeUntil = tick + signHoldTicks - 1)
                frozenUntil[targetLane] = tick + signHoldTicks - 1
                event = TickEvent.FREEZE
            }
        }

        val laneTicks = state.laneTicks.toMutableList()
        for (index in laneTicks.indices) {
            if (tick > frozenUntil[index]) laneTicks[index] = laneTicks[index] + 1
        }

        var rooster = target
        var carrying = state.carrying
        var parcels = state.parcels
        var delivered = state.delivered
        var moveCount = state.moveCount
        val moved = rooster != state.rooster
        if (moved) moveCount += 1

        var deliveredNow = false
        if (carrying && rooster == layout.barn) {
            carrying = false
            delivered += 1
            deliveredNow = true
            event = TickEvent.DELIVER
        } else if (!carrying && parcels.contains(rooster)) {
            parcels = parcels.filterNot { it == rooster }
            carrying = true
            event = TickEvent.PICKUP
        }

        var nearMisses = state.nearMisses
        var signRefunds = state.signRefunds
        var finished = false
        var outcome: RunOutcome? = null
        val laneIndex = layout.laneIndexAt(rooster.row)
        if (laneIndex >= 0) {
            val lane = layout.lanes[laneIndex]
            val laneTick = laneTicks[laneIndex]
            val hit = LaneRhythm.occupied(lane, periods[laneIndex], laneTick, rooster.col, cols)
            if (hit) {
                finished = true
                outcome = RunOutcome.CLIPPED
                event = TickEvent.COLLISION
            } else {
                val before = LaneRhythm.occupied(lane, periods[laneIndex], laneTick - 1, rooster.col, cols)
                val after = LaneRhythm.occupied(lane, periods[laneIndex], laneTick + 1, rooster.col, cols)
                if (before || after) {
                    nearMisses += 1
                    if (nearMisses % nearMissRefund == 0) signRefunds += 1
                    event = TickEvent.NEAR_MISS
                }
            }
        }

        if (!finished && delivered >= required) {
            finished = true
            outcome = RunOutcome.DELIVERED
        }
        if (!finished && tick >= tickLimit) {
            finished = true
            outcome = endingForExhaustedRoute(delivered, required)
        }

        val rainy = layout.weather == Weather.RAIN
        val recoverPending = when {
            finished -> false
            !moved -> false
            deliveredNow -> rainy && moveCount % rainMovesPerRecover == 0
            else -> carrying || (rainy && moveCount % rainMovesPerRecover == 0)
        }

        return state.copy(
            tick = tick,
            rooster = rooster,
            routeIndex = routeIndex,
            carrying = carrying,
            parcels = parcels,
            delivered = delivered,
            moveCount = moveCount,
            recoverPending = recoverPending,
            pendingWait = false,
            laneTicks = laneTicks,
            lanePeriods = periods,
            frozenUntil = frozenUntil,
            signs = signs,
            nearMisses = nearMisses,
            signRefunds = signRefunds,
            patienceUsed = state.patienceUsed + (if (waitRequested) 1 else 0),
            windApplied = windApplied,
            finished = finished,
            outcome = outcome,
            lastEvent = event,
            lastLane = if (laneIndex >= 0) laneIndex else targetLane
        )
    }

    private fun endingForExhaustedRoute(delivered: Int, required: Int): RunOutcome = when {
        delivered >= required -> RunOutcome.DELIVERED
        delivered > 0 -> RunOutcome.ROUTE_COMPLETE
        else -> RunOutcome.OUT_OF_TICKS
    }
}
