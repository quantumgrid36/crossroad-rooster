package com.Gugcegu3.Rogzal.data.repository

import com.Gugcegu3.Rogzal.core.config.GameConfig
import com.Gugcegu3.Rogzal.data.local.PreferencesStore
import com.Gugcegu3.Rogzal.domain.model.GameSettings
import com.Gugcegu3.Rogzal.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val store: PreferencesStore) : SettingsRepository {

    override fun load(): GameSettings = GameSettings(
        tickMs = store.int(PreferencesStore.KEY_TICK_MS, GameConfig.TICK_STANDARD_MS),
        ribbon = store.boolean(PreferencesStore.KEY_RIBBON, true),
        highContrast = store.boolean(PreferencesStore.KEY_CONTRAST, false),
        confirmGo = store.boolean(PreferencesStore.KEY_CONFIRM_GO, false)
    )

    override fun save(settings: GameSettings) {
        store.putInt(PreferencesStore.KEY_TICK_MS, settings.tickMs)
        store.putBoolean(PreferencesStore.KEY_RIBBON, settings.ribbon)
        store.putBoolean(PreferencesStore.KEY_CONTRAST, settings.highContrast)
        store.putBoolean(PreferencesStore.KEY_CONFIRM_GO, settings.confirmGo)
    }
}
