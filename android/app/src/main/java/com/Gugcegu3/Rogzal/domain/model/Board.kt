package com.Gugcegu3.Rogzal.domain.model

data class Cell(val row: Int, val col: Int)

enum class RowKind { VERGE, ROAD }

enum class Weather { CLEAR, RAIN, FOG, WIND }

enum class Medal { NONE, BRONZE, SILVER, GOLD }

enum class Move(val dRow: Int, val dCol: Int) {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1),
    WAIT(0, 0)
}

data class RouteNode(val move: Move, val cell: Cell)

data class Lane(
    val row: Int,
    val period: Int,
    val offset: Int,
    val direction: Int,
    val heavy: Boolean
)

data class SignState(
    val row: Int,
    val col: Int,
    val laneIndex: Int,
    val consumed: Boolean = false,
    val activeUntil: Int = -1
)

data class BoardLayout(
    val rows: List<RowKind>,
    val lanes: List<Lane>,
    val parcels: List<Cell>,
    val barn: Cell,
    val start: Cell,
    val shadowCells: List<Cell>,
    val weather: Weather,
    val windLane: Int,
    val seed: Int
) {
    fun laneIndexAt(row: Int): Int = lanes.indexOfFirst { it.row == row }
}

object LaneRhythm {
    private fun floorMod(value: Int, divisor: Int): Int {
        if (divisor <= 0) return 0
        val rest = value % divisor
        return if (rest < 0) rest + divisor else rest
    }

    fun laneColumnIndex(lane: Lane, col: Int, cols: Int): Int =
        if (lane.direction > 0) col else cols - 1 - col

    fun occupied(lane: Lane, period: Int, laneTick: Int, col: Int, cols: Int): Boolean {
        if (period <= 0) return false
        val x = laneColumnIndex(lane, col, cols)
        return floorMod(laneTick - lane.offset - x, period) == 0
    }

    fun vehicleId(lane: Lane, period: Int, laneTick: Int, col: Int, cols: Int): Int {
        if (period <= 0) return 0
        val x = laneColumnIndex(lane, col, cols)
        return (laneTick - lane.offset - x) / period
    }
}
