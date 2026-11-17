package com.Gugcegu3.Rogzal.domain.usecase

import com.Gugcegu3.Rogzal.domain.model.BoardLayout
import com.Gugcegu3.Rogzal.domain.model.Cell
import com.Gugcegu3.Rogzal.domain.model.Lane
import com.Gugcegu3.Rogzal.domain.model.LaneRhythm
import com.Gugcegu3.Rogzal.domain.model.LevelSpec
import com.Gugcegu3.Rogzal.domain.model.RowKind
import com.Gugcegu3.Rogzal.domain.model.Weather
import kotlin.random.Random

class GenerateLayoutUseCase(
    private val cols: Int,
    private val rows: Int,
    private val attempts: Int,
    private val grace: Int
) {

    operator fun invoke(spec: LevelSpec, attempt: Int): BoardLayout {
        val seed = spec.index * 7919 xor (attempt * 104729)
        var last: BoardLayout? = null
        for (step in 0 until attempts) {
            val random = Random(seed + step * 31)
            val candidate = build(spec, random, seed)
            last = candidate
            if (solvable(candidate, spec)) return candidate
        }
        val fallback = fallback(spec, seed)
        return if (solvable(fallback, spec)) fallback else (last ?: fallback)
    }

    private fun roadRows(laneCount: Int): List<Int> =
        if (laneCount >= 4) listOf(1, 3, 5, 6) else listOf(1, 3, 5)

    private fun rowKinds(laneCount: Int): List<RowKind> {
        val roads = roadRows(laneCount)
        return (0 until rows).map { if (roads.contains(it)) RowKind.ROAD else RowKind.VERGE }
    }

    private fun build(spec: LevelSpec, random: Random, seed: Int): BoardLayout {
        val kinds = rowKinds(spec.laneCount)
        val lanes = roadRows(spec.laneCount).map { row ->
            val period = random.nextInt(spec.minPeriod, spec.maxPeriod + 1)
            Lane(
                row = row,
                period = period,
                offset = random.nextInt(period),
                direction = if (random.nextBoolean()) 1 else -1,
                heavy = random.nextBoolean()
            )
        }
        val barnCol = random.nextInt(1, cols - 1)
        val startCol = random.nextInt(1, cols - 1)
        val barn = Cell(0, barnCol)
        val start = Cell(rows - 1, startCol)
        val vergeCells = buildList {
            for (row in 1 until rows) {
                if (kinds[row] != RowKind.VERGE) continue
                for (col in 0 until cols) {
                    val cell = Cell(row, col)
                    if (cell != start) add(cell)
                }
            }
        }
        val parcels = vergeCells.shuffled(random).take(spec.parcels)
        val roadCells = buildList {
            for (lane in lanes) {
                for (col in 0 until cols) add(Cell(lane.row, col))
            }
        }
        val shadows = roadCells.shuffled(random).take(spec.shadows)
        val weather = if (spec.index <= 2) {
            Weather.CLEAR
        } else {
            listOf(Weather.CLEAR, Weather.RAIN, Weather.FOG, Weather.WIND)[random.nextInt(4)]
        }
        return BoardLayout(
            rows = kinds,
            lanes = lanes,
            parcels = parcels,
            barn = barn,
            start = start,
            shadowCells = shadows,
            weather = weather,
            windLane = if (lanes.isEmpty()) -1 else random.nextInt(lanes.size),
            seed = seed
        )
    }

    private fun fallback(spec: LevelSpec, seed: Int): BoardLayout {
        val kinds = rowKinds(spec.laneCount)
        val column = cols / 2
        val lanes = roadRows(spec.laneCount).mapIndexed { index, row ->
            val period = spec.maxPeriod.coerceAtLeast(6)
            val arrival = rows - 1 - row
            val x = if (index % 2 == 0) column else cols - 1 - column
            val offset = ((arrival - x + 1) % period + period) % period
            Lane(
                row = row,
                period = period,
                offset = offset,
                direction = if (index % 2 == 0) 1 else -1,
                heavy = index % 2 == 1
            )
        }
        val vergeRows = (1 until rows).filter { kinds[it] == RowKind.VERGE && it != rows - 1 }
        val parcels = vergeRows.take(spec.parcels).map { Cell(it, column) }
        return BoardLayout(
            rows = kinds,
            lanes = lanes,
            parcels = parcels,
            barn = Cell(0, column),
            start = Cell(rows - 1, column),
            shadowCells = emptyList(),
            weather = Weather.CLEAR,
            windLane = -1,
            seed = seed
        )
    }

    private fun solvable(layout: BoardLayout, spec: LevelSpec): Boolean {
        val required = spec.parcels
        if (required == 0) return true
        val maskCount = 1 shl required
        val limit = spec.tickTarget + grace
        val layerSize = rows * cols * maskCount * 2 * 2 * 3
        var current = BooleanArray(layerSize)
        val startIndex = encode(layout.start.row, layout.start.col, maskCount - 1, false, false, 0, maskCount)
        current[startIndex] = true
        val rainy = layout.weather == Weather.RAIN
        for (tick in 1..limit) {
            val next = BooleanArray(layerSize)
            var alive = false
            for (index in current.indices) {
                if (!current[index]) continue
                val mod = index % 3
                var rest = index / 3
                val pending = rest % 2 == 1
                rest /= 2
                val carrying = rest % 2 == 1
                rest /= 2
                val mask = rest % maskCount
                rest /= maskCount
                val col = rest % cols
                val row = rest / cols
                val moves = if (pending) listOf(0) else listOf(0, 1, 2, 3, 4)
                for (code in moves) {
                    val dRow = when (code) { 1 -> -1; 2 -> 1; else -> 0 }
                    val dCol = when (code) { 3 -> -1; 4 -> 1; else -> 0 }
                    val newRow = row + dRow
                    val newCol = col + dCol
                    if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols) continue
                    val laneIndex = layout.laneIndexAt(newRow)
                    if (laneIndex >= 0) {
                        val lane = layout.lanes[laneIndex]
                        if (LaneRhythm.occupied(lane, lane.period, tick, newCol, cols)) continue
                    }
                    var newMask = mask
                    var newCarrying = carrying
                    var newMod = mod
                    val moved = code != 0
                    if (moved) {
                        newMod = (mod + 1) % 3
                    }
                    val cell = Cell(newRow, newCol)
                    var delivered = false
                    if (newCarrying && cell == layout.barn) {
                        newCarrying = false
                        delivered = true
                    } else if (!newCarrying) {
                        val parcelIndex = layout.parcels.indexOf(cell)
                        if (parcelIndex >= 0 && (newMask shr parcelIndex) and 1 == 1) {
                            newMask = newMask and (1 shl parcelIndex).inv()
                            newCarrying = true
                        }
                    }
                    val newPending = when {
                        !moved -> false
                        delivered -> rainy && newMod == 0
                        else -> newCarrying || (rainy && newMod == 0)
                    }
                    if (newMask == 0 && !newCarrying) return true
                    val target = encode(newRow, newCol, newMask, newCarrying, newPending, newMod, maskCount)
                    if (!next[target]) {
                        next[target] = true
                        alive = true
                    }
                }
            }
            if (!alive) return false
            current = next
        }
        return false
    }

    private fun encode(
        row: Int,
        col: Int,
        mask: Int,
        carrying: Boolean,
        pending: Boolean,
        mod: Int,
        maskCount: Int
    ): Int {
        var value = row * cols + col
        value = value * maskCount + mask
        value = value * 2 + (if (carrying) 1 else 0)
        value = value * 2 + (if (pending) 1 else 0)
        value = value * 3 + mod
        return value
    }
}
