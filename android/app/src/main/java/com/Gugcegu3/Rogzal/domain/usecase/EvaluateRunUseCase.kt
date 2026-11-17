package com.Gugcegu3.Rogzal.domain.usecase

import com.Gugcegu3.Rogzal.core.config.GameConfig
import com.Gugcegu3.Rogzal.domain.model.ClauseKind
import com.Gugcegu3.Rogzal.domain.model.ContractClause
import com.Gugcegu3.Rogzal.domain.model.LevelSpec
import com.Gugcegu3.Rogzal.domain.model.Medal
import com.Gugcegu3.Rogzal.domain.model.RunState

class EvaluateRunUseCase {

    fun clauses(spec: LevelSpec, state: RunState): List<ContractClause> {
        val signsSpent = state.signs.count { it.consumed }
        return listOf(
            ContractClause(
                kind = ClauseKind.DELIVER,
                target = spec.parcels,
                actual = state.delivered,
                met = state.delivered >= spec.parcels
            ),
            ContractClause(
                kind = ClauseKind.SIGNS,
                target = spec.signTarget,
                actual = signsSpent,
                met = signsSpent <= spec.signTarget
            ),
            ContractClause(
                kind = ClauseKind.TICKS,
                target = spec.tickTarget,
                actual = state.tick,
                met = state.tick <= spec.tickTarget
            )
        )
    }

    fun medal(clauses: List<ContractClause>): Medal {
        val delivered = clauses.first { it.kind == ClauseKind.DELIVER }.met
        if (!delivered) return Medal.NONE
        val extras = clauses.count { it.kind != ClauseKind.DELIVER && it.met }
        return when (extras) {
            2 -> Medal.GOLD
            1 -> Medal.SILVER
            else -> Medal.BRONZE
        }
    }

    fun score(spec: LevelSpec, state: RunState, ribbonOff: Boolean): Int {
        val ticksLeft = (spec.tickTarget - state.tick).coerceAtLeast(0)
        val signsLeft = (spec.signBudget + state.signRefunds - state.signs.count { it.consumed })
            .coerceAtLeast(0)
        val patienceLeft = (GameConfig.PATIENCE_TOKENS - state.patienceUsed).coerceAtLeast(0)
        val efficiency = if (state.delivered > 0) {
            GameConfig.SCORE_PER_TICK_LEFT * ticksLeft +
                GameConfig.SCORE_PER_SIGN_LEFT * signsLeft +
                GameConfig.SCORE_PER_PATIENCE_LEFT * patienceLeft
        } else {
            0
        }
        val base = GameConfig.SCORE_PER_PARCEL * state.delivered +
            GameConfig.SCORE_PER_NEAR_MISS * state.nearMisses +
            efficiency
        val multiplier = if (ribbonOff) GameConfig.RIBBON_OFF_BONUS else 1f
        return (base * multiplier).toInt()
    }
}
