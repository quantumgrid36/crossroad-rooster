package com.Gugcegu3.Rogzal.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun int(key: String, fallback: Int): Int = prefs.getInt(key, fallback)

    fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun boolean(key: String, fallback: Boolean): Boolean = prefs.getBoolean(key, fallback)

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    companion object {
        private const val FILE_NAME = "crossroad_rooster_prefs"
        const val KEY_LEVELS_UNLOCKED = "levels_unlocked"
        const val KEY_TOTAL_PARCELS = "total_parcels"
        const val KEY_BEST_NEAR_MISS = "best_near_miss"
        const val KEY_TICK_MS = "pref_tick_ms"
        const val KEY_RIBBON = "pref_ribbon"
        const val KEY_CONTRAST = "pref_contrast"
        const val KEY_CONFIRM_GO = "pref_confirm_go"
        const val PREFIX_MEDAL = "medal_"
        const val PREFIX_BEST_SCORE = "best_score_"
        const val PREFIX_BEST_TICKS = "best_ticks_"
    }
}
