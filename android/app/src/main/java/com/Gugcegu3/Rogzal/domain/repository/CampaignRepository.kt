package com.Gugcegu3.Rogzal.domain.repository

import com.Gugcegu3.Rogzal.domain.model.Medal
import com.Gugcegu3.Rogzal.domain.model.RunResult

interface CampaignRepository {
    fun unlockedLevels(): Int
    fun medal(levelIndex: Int): Medal
    fun bestScore(levelIndex: Int): Int
    fun bestTicks(levelIndex: Int): Int
    fun totalParcels(): Int
    fun bestNearMiss(): Int
    fun medalCount(): Int
    fun bestOverallScore(): Int
    fun saveRun(result: RunResult)
}
