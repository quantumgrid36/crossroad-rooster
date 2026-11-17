package com.Gugcegu3.Rogzal.domain.repository

import com.Gugcegu3.Rogzal.domain.model.GameSettings

interface SettingsRepository {
    fun load(): GameSettings
    fun save(settings: GameSettings)
}
