package com.watchpoint.app.checkin

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.watchpoint.app.data.db.WatchPointDatabase
import com.watchpoint.app.MainActivity
import com.watchpoint.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Posts the daily check-in reminder notification when [ReminderScheduler]'s alarm fires. */
class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_MOTIVATIONAL = "motivational_quote"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                postNotification(context, intent, WatchPointDatabase.getInstance(context).checkInDao().getLatestMood())
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun postNotification(context: Context, intent: Intent, latestMood: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val openApp = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val isMotivational = intent.getBooleanExtra(EXTRA_MOTIVATIONAL, false)
        val quote = if (isMotivational) {
            context.resources.getStringArray(quoteArrayForMood(latestMood)).random()
        } else {
            context.getString(R.string.reminder_notification_body)
        }

        val notification = NotificationCompat.Builder(
            context,
            if (isMotivational) ReminderScheduler.MOTIVATIONAL_CHANNEL_ID else ReminderScheduler.CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                context.getString(
                    if (isMotivational) R.string.motivational_notification_title
                    else R.string.reminder_notification_title
                )
            )
            .setContentText(quote)
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(
            if (isMotivational) ReminderScheduler.MOTIVATIONAL_NOTIFICATION_ID
            else ReminderScheduler.NOTIFICATION_ID,
            notification
        )
    }

    private fun quoteArrayForMood(mood: String?): Int = when (mood) {
        "VeryLow", "Low" -> R.array.motivational_quotes_low
        "Good", "Great" -> R.array.motivational_quotes_positive
        else -> R.array.motivational_quotes_neutral
    }
}
