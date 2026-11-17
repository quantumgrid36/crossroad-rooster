package com.Gugcegu3.Rogzal.data.sample

import com.Gugcegu3.Rogzal.R
import com.Gugcegu3.Rogzal.domain.model.LevelSpec

object SampleData {

    val levels: List<LevelSpec> = listOf(
        LevelSpec(1, R.string.level_oak_lane, 3, 6, 7, 1, 3, 2, 22, 0),
        LevelSpec(2, R.string.level_millpond, 3, 6, 7, 1, 3, 2, 24, 0),
        LevelSpec(3, R.string.level_harrow_bend, 3, 5, 7, 2, 3, 2, 26, 1),
        LevelSpec(4, R.string.level_silo_turn, 3, 5, 7, 2, 3, 2, 26, 1),
        LevelSpec(5, R.string.level_copper_gate, 3, 5, 6, 2, 2, 1, 28, 2),
        LevelSpec(6, R.string.level_thistle_row, 3, 5, 6, 2, 2, 1, 28, 2),
        LevelSpec(7, R.string.level_four_barns, 4, 5, 6, 2, 2, 1, 30, 2),
        LevelSpec(8, R.string.level_drovers_cut, 4, 4, 6, 3, 2, 1, 32, 3),
        LevelSpec(9, R.string.level_lantern_split, 4, 4, 6, 3, 2, 1, 32, 3),
        LevelSpec(10, R.string.level_haywain_cross, 4, 4, 5, 3, 1, 0, 34, 3),
        LevelSpec(11, R.string.level_nightfall_fork, 4, 4, 5, 3, 1, 0, 34, 4),
        LevelSpec(12, R.string.level_last_round, 4, 4, 5, 4, 1, 0, 36, 4)
    )

    val howToSteps: List<HowToStep> = listOf(
        HowToStep(R.drawable.ic_clock, R.string.howto_rhythm_title, R.string.howto_rhythm_body),
        HowToStep(R.drawable.ic_route, R.string.howto_route_title, R.string.howto_route_body),
        HowToStep(R.drawable.ic_sign, R.string.howto_sign_title, R.string.howto_sign_body),
        HowToStep(R.drawable.ic_medal, R.string.howto_contract_title, R.string.howto_contract_body)
    )

    fun levelAt(index: Int): LevelSpec = levels.getOrElse(index - 1) { levels.first() }
}

data class HowToStep(val iconRes: Int, val titleRes: Int, val bodyRes: Int)
