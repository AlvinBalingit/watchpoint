package com.watchpoint.app.checkin

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.watchpoint.app.R
import com.watchpoint.app.onboarding.TimeOfDay
import java.util.Calendar

/**
 * Schedules (and cancels) the daily check-in reminder via AlarmManager.
 *
 * AlarmManager alarms are cleared on reboot, so [BootReceiver] re-arms
 * whichever of these are enabled (per SettingsPreferences) when the device
 * finishes starting up. `setRepeating` (not exact-and-allow-while-idle) is
 * intentional: this is a wellness nudge, not a time-critical alarm, so it
 * doesn't need the SCHEDULE_EXACT_ALARM permission.
 */
object ReminderScheduler {

    const val CHANNEL_ID = "daily_checkin_reminder"
    const val NOTIFICATION_ID = 1001
    const val MOTIVATIONAL_CHANNEL_ID = "motivational_quotes"
    const val MOTIVATIONAL_NOTIFICATION_ID = 1002
    private const val REQUEST_CODE = 2001
    private const val MOTIVATIONAL_REQUEST_CODE = 2002

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.reminder_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)
        manager.createNotificationChannel(
            NotificationChannel(
                MOTIVATIONAL_CHANNEL_ID,
                context.getString(R.string.motivational_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
    }

    fun schedule(context: Context, time: TimeOfDay) {
        ensureChannel(context)
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val triggerAt = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1)
        }
        alarmManager.setRepeating(
            AlarmManager.RTC,
            triggerAt.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent(context)
        )
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager.cancel(pendingIntent(context))
    }

    /**
     * No-ops if the alarm is already pending. This is invoked every time the
     * app opens (settings observer in WatchPointApplication), so without this
     * check a frequent user would keep pushing the next quote 2 hours into
     * the future and rarely - if ever - actually get one.
     */
    fun scheduleMotivationalQuotes(context: Context) {
        ensureChannel(context)
        if (motivationalPendingIntentIfExists(context) != null) return
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val firstNotification = System.currentTimeMillis() + 2 * 60 * 60 * 1000L
        alarmManager.setRepeating(
            AlarmManager.RTC,
            firstNotification,
            2 * 60 * 60 * 1000L,
            motivationalPendingIntent(context)
        )
    }

    fun cancelMotivationalQuotes(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager.cancel(motivationalPendingIntent(context))
    }

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun motivationalIntent(context: Context): Intent =
        Intent(context, ReminderReceiver::class.java).putExtra(ReminderReceiver.EXTRA_MOTIVATIONAL, true)

    private fun motivationalPendingIntent(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            MOTIVATIONAL_REQUEST_CODE,
            motivationalIntent(context),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    /** Non-creating lookup, used only to detect whether an alarm is already pending. */
    private fun motivationalPendingIntentIfExists(context: Context): PendingIntent? =
        PendingIntent.getBroadcast(
            context,
            MOTIVATIONAL_REQUEST_CODE,
            motivationalIntent(context),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
}
