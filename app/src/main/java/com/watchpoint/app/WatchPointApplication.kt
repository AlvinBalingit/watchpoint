package com.watchpoint.app

import android.app.Application
import com.watchpoint.app.checkin.ReminderScheduler
import com.watchpoint.app.data.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WatchPointApplication : Application() {
    lateinit var container: AppContainer
        private set

    /** Lives for the whole process, so the sync trigger keeps running regardless of which screen is open. */
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.syncTrigger.start(applicationScope)
        applicationScope.launch {
            container.settingsRepository.observe().collectLatest { settings ->
                if (settings.enabled) {
                    ReminderScheduler.schedule(this@WatchPointApplication, settings.time)
                } else {
                    ReminderScheduler.cancel(this@WatchPointApplication)
                }

                if (settings.motivationalQuotesEnabled) {
                    ReminderScheduler.scheduleMotivationalQuotes(this@WatchPointApplication)
                } else {
                    ReminderScheduler.cancelMotivationalQuotes(this@WatchPointApplication)
                }
            }
        }
    }
}
