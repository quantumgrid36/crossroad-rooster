package com.Gugcegu3.Rogzal.domain.usecase

import com.Gugcegu3.Rogzal.domain.model.BoardLayout
import com.Gugcegu3.Rogzal.domain.model.LanePreview
import com.Gugcegu3.Rogzal.domain.model.LaneRhythm
import com.Gugcegu3.Rogzal.domain.model.RunState
import com.Gugcegu3.Rogzal.domain.model.VehicleView

class ProjectBoardUseCase(private val cols: Int) {

    fun vehicles(layout: BoardLayout, state: RunState): List<VehicleView> {
        val result = mutableListOf<VehicleView>()
        layout.lanes.forEachIndexed { index, lane ->
            val period = state.lanePeriods.getOrElse(index) { lane.period }
            val laneTick = state.laneTicks.getOrElse(index) { 0 }
            val frozen = state.tick <= state.frozenUntil.getOrElse(index) { -1 }
            for (col in 0 until cols) {
                if (!LaneRhythm.occupied(lane, period, laneTick, col, cols)) continue
                result.add(
                    VehicleView(
                        id = LaneRhythm.vehicleId(lane, period, laneTick, col, cols),
                        laneIndex = index,
                        row = lane.row,
                        col = col,
                        heavy = lane.heavy,
                        facingRight = lane.direction > 0,
                        frozen = frozen
                    )
                )
            }
        }
        return result
    }

    fun ribbon(
        layout: BoardLayout,
        state: RunState,
        ticksAhead: Int,
        revealedLanes: Set<Int>
    ): List<LanePreview> = layout.lanes.mapIndexed { index, lane ->
        val period = state.lanePeriods.getOrElse(index) { lane.period }
        val laneTick = state.laneTicks.getOrElse(index) { 0 }
        val column = state.rooster.col
        val hidden = layout.shadowCells.any { it.row == lane.row } && !revealedLanes.contains(index)
        LanePreview(
            laneIndex = index,
            row = lane.row,
            heavy = lane.heavy,
            hidden = hidden,
            pips = (1..ticksAhead).map {
                LaneRhythm.occupied(lane, period, laneTick + it, column, cols)
            }
        )
    }
}

