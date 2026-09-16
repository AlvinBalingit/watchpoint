package com.watchpoint.app.data.repository

import com.watchpoint.app.data.prefs.SettingsPreferences
import com.watchpoint.app.data.prefs.SettingsState
import com.watchpoint.app.onboarding.TimeOfDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class ReminderSettings(
    val enabled: Boolean,
    val motivationalQuotesEnabled: Boolean,
    val time: TimeOfDay
)

/** Thin pass-through to DataStore, kept as a repository so SettingsViewModel doesn't need to know DataStore exists. */
class SettingsRepository(private val prefs: SettingsPreferences) {

    fun observe(): Flow<ReminderSettings> = prefs.state.map {
        ReminderSettings(
            it.reminderEnabled,
            it.motivationalQuotesEnabled,
            TimeOfDay(it.reminderHour, it.reminderMinute)
        )
    }

    suspend fun setReminderEnabled(value: Boolean) = prefs.setReminderEnabled(value)

    suspend fun setMotivationalQuotesEnabled(value: Boolean) = prefs.setMotivationalQuotesEnabled(value)

    suspend fun setReminderTime(value: TimeOfDay) = prefs.setReminderTime(value.hour, value.minute)

    /** Device-level (not account) preference: whether Quick Mode is the default for the next check-in. */
    val highDemandMode: Flow<Boolean> = prefs.state.map { it.highDemandMode }

    suspend fun setHighDemandMode(value: Boolean) = prefs.setHighDemandMode(value)
}
