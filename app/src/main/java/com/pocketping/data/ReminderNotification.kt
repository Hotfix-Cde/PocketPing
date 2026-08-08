package com.pocketping.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.pocketping.R

object ReminderNotification {
    const val EXTRA_REMINDER_ID = "reminder_id"
    private const val CHANNEL = "reminders"
    fun createChannel(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel(CHANNEL, context.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH).apply { description = context.getString(R.string.notification_channel_description) })
    }
    fun show(context: Context, reminder: ReminderEntity) {
        createChannel(context)
        val notification = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(reminder.title)
            .setContentText(if (reminder.note.isBlank()) "PocketPing reminder" else reminder.note)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        context.getSystemService(NotificationManager::class.java).notify(reminder.id.toInt(), notification)
    }
    fun cancel(context: Context, id: Long) { context.getSystemService(NotificationManager::class.java).cancel(id.toInt()) }
}
