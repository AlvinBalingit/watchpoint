package com.watchpoint.app.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchpoint.app.data.repository.ReminderSettings
import com.watchpoint.app.data.repository.SettingsRepository
import com.watchpoint.app.onboarding.TimeOfDay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Reminder preferences, backed by DataStore via [SettingsRepository] so they survive app restarts. */
class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    val settings: StateFlow<ReminderSettings> = repository.observe()
        .stateIn(viewModelScope, SharingStarted.Eagerly, ReminderSettings(true, true, TimeOfDay(19, 0)))

    fun selectReminderEnabled(value: Boolean) {
        viewModelScope.launch { repository.setReminderEnabled(value) }
    }

    fun selectReminderTime(value: TimeOfDay) {
        viewModelScope.launch { repository.setReminderTime(value) }
    }

    fun selectMotivationalQuotesEnabled(value: Boolean) {
        viewModelScope.launch { repository.setMotivationalQuotesEnabled(value) }
    }
}
