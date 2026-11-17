package com.Gugcegu3.Rogzal.data.repository

import com.Gugcegu3.Rogzal.core.config.GameConfig
import com.Gugcegu3.Rogzal.data.local.PreferencesStore
import com.Gugcegu3.Rogzal.domain.model.Medal
import com.Gugcegu3.Rogzal.domain.model.RunResult
import com.Gugcegu3.Rogzal.domain.repository.CampaignRepository

class CampaignRepositoryImpl(private val store: PreferencesStore) : CampaignRepository {

    override fun unlockedLevels(): Int =
        store.int(PreferencesStore.KEY_LEVELS_UNLOCKED, 1).coerceIn(1, GameConfig.LEVEL_COUNT)

    override fun medal(levelIndex: Int): Medal {
        val raw = store.int(PreferencesStore.PREFIX_MEDAL + levelIndex, 0)
        return Medal.entries.getOrElse(raw) { Medal.NONE }
    }

    override fun bestScore(levelIndex: Int): Int =
        store.int(PreferencesStore.PREFIX_BEST_SCORE + levelIndex, 0)

    override fun bestTicks(levelIndex: Int): Int =
        store.int(PreferencesStore.PREFIX_BEST_TICKS + levelIndex, 0)

    override fun totalParcels(): Int = store.int(PreferencesStore.KEY_TOTAL_PARCELS, 0)

    override fun bestNearMiss(): Int = store.int(PreferencesStore.KEY_BEST_NEAR_MISS, 0)

    override fun medalCount(): Int =
        (1..GameConfig.LEVEL_COUNT).count { medal(it) != Medal.NONE }

    override fun bestOverallScore(): Int =
        (1..GameConfig.LEVEL_COUNT).maxOfOrNull { bestScore(it) } ?: 0

    override fun saveRun(result: RunResult) {
        val level = result.levelIndex
        if (result.score > bestScore(level)) {
            store.putInt(PreferencesStore.PREFIX_BEST_SCORE + level, result.score)
        }
        val previousTicks = bestTicks(level)
        if (result.cleared && (previousTicks == 0 || result.ticksUsed < previousTicks)) {
            store.putInt(PreferencesStore.PREFIX_BEST_TICKS + level, result.ticksUsed)
        }
        val previousMedal = medal(level)
        if (result.medal.ordinal > previousMedal.ordinal) {
            store.putInt(PreferencesStore.PREFIX_MEDAL + level, result.medal.ordinal)
        }
        if (result.delivered > 0) {
            store.putInt(
                PreferencesStore.KEY_TOTAL_PARCELS,
                totalParcels() + result.delivered
            )
        }
        if (result.nearMisses > bestNearMiss()) {
            store.putInt(PreferencesStore.KEY_BEST_NEAR_MISS, result.nearMisses)
        }
        if (result.cleared && level >= unlockedLevels() && level < GameConfig.LEVEL_COUNT) {
            store.putInt(PreferencesStore.KEY_LEVELS_UNLOCKED, level + 1)
        }
    }
}
