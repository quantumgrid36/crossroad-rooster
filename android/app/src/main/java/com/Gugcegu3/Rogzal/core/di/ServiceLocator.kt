package com.Gugcegu3.Rogzal.core.di

import android.content.Context
import com.Gugcegu3.Rogzal.core.config.GameConfig
import com.Gugcegu3.Rogzal.data.local.PreferencesStore
import com.Gugcegu3.Rogzal.data.repository.CampaignRepositoryImpl
import com.Gugcegu3.Rogzal.data.repository.SettingsRepositoryImpl
import com.Gugcegu3.Rogzal.domain.repository.CampaignRepository
import com.Gugcegu3.Rogzal.domain.repository.SettingsRepository
import com.Gugcegu3.Rogzal.domain.usecase.AdvanceTickUseCase
import com.Gugcegu3.Rogzal.domain.usecase.EvaluateRunUseCase
import com.Gugcegu3.Rogzal.domain.usecase.GenerateLayoutUseCase
import com.Gugcegu3.Rogzal.domain.usecase.ProjectBoardUseCase

object ServiceLocator {

    private var store: PreferencesStore? = null

    fun init(context: Context) {
        if (store == null) store = PreferencesStore(context)
    }

    private fun requireStore(): PreferencesStore {
        val current = store
        return current ?: throw IllegalStateException(MISSING_INIT)
    }

    val campaign: CampaignRepository by lazy { CampaignRepositoryImpl(requireStore()) }

    val settings: SettingsRepository by lazy { SettingsRepositoryImpl(requireStore()) }

    val generateLayout: GenerateLayoutUseCase by lazy {
        GenerateLayoutUseCase(
            cols = GameConfig.COLS,
            rows = GameConfig.ROWS,
            attempts = GameConfig.GENERATION_ATTEMPTS,
            grace = GameConfig.TICK_GRACE
        )
    }

    val advanceTick: AdvanceTickUseCase by lazy {
        AdvanceTickUseCase(
            cols = GameConfig.COLS,
            rows = GameConfig.ROWS,
            signHoldTicks = GameConfig.SIGN_HOLD_TICKS,
            rainMovesPerRecover = GameConfig.RAIN_MOVES_PER_RECOVER,
            nearMissRefund = GameConfig.NEAR_MISS_REFUND
        )
    }

    val projectBoard: ProjectBoardUseCase by lazy { ProjectBoardUseCase(GameConfig.COLS) }

    val evaluateRun: EvaluateRunUseCase by lazy { EvaluateRunUseCase() }

    private const val MISSING_INIT = "ServiceLocator.init was not called"
}
