package com.watchpoint.app.checkin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.watchpoint.app.data.prefs.SettingsPreferences
import com.watchpoint.app.onboarding.TimeOfDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Re-arms reminder alarms after a reboot - AlarmManager alarms don't survive one on their own. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = SettingsPreferences(appContext).state.first()
                if (settings.reminderEnabled) {
                    ReminderScheduler.schedule(appContext, TimeOfDay(settings.reminderHour, settings.reminderMinute))
                }
                if (settings.motivationalQuotesEnabled) {
                    ReminderScheduler.scheduleMotivationalQuotes(appContext)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
