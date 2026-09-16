package com.watchpoint.app.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "watchpoint_settings")

data class SettingsState(
    val reminderEnabled: Boolean,
    val motivationalQuotesEnabled: Boolean,
    val reminderHour: Int,
    val reminderMinute: Int
)

/** Small key-value app preferences - reminder settings - kept in DataStore rather than a Room table. */
class SettingsPreferences(private val context: Context) {

    private object Keys {
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val MOTIVATIONAL_QUOTES_ENABLED = booleanPreferencesKey("motivational_quotes_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
    }

    val state: Flow<SettingsState> = context.dataStore.data.map { prefs ->
        SettingsState(
            reminderEnabled = prefs[Keys.REMINDER_ENABLED] ?: true,
            motivationalQuotesEnabled = prefs[Keys.MOTIVATIONAL_QUOTES_ENABLED] ?: true,
            reminderHour = prefs[Keys.REMINDER_HOUR] ?: 19,
            reminderMinute = prefs[Keys.REMINDER_MINUTE] ?: 0
        )
    }

    suspend fun setReminderEnabled(value: Boolean) {
        context.dataStore.edit { it[Keys.REMINDER_ENABLED] = value }
    }

    suspend fun setMotivationalQuotesEnabled(value: Boolean) {
        context.dataStore.edit { it[Keys.MOTIVATIONAL_QUOTES_ENABLED] = value }
    }

    suspend fun setReminderTime(hour: Int, minute: Int) {
        context.dataStore.edit {
            it[Keys.REMINDER_HOUR] = hour
            it[Keys.REMINDER_MINUTE] = minute
        }
    }
}
