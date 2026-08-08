package com.pocketping.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings

class ReminderScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    fun canScheduleExact(): Boolean = Build.VERSION.SDK_INT < 31 || alarmManager.canScheduleExactAlarms()
    fun schedule(reminder: ReminderEntity) {
        val due = reminder.dueAtMillis ?: return
        if (reminder.completed || due <= System.currentTimeMillis()) return
        val intent = Intent(context, ReminderAlarmReceiver::class.java).putExtra(ReminderNotification.EXTRA_REMINDER_ID, reminder.id)
        val pi = PendingIntent.getBroadcast(context, reminder.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        if (Build.VERSION.SDK_INT >= 31 && canScheduleExact()) alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, due, pi)
        else alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, due, pi)
    }
    fun cancel(id: Long) {
        val intent = Intent(context, ReminderAlarmReceiver::class.java)
        val pi = PendingIntent.getBroadcast(context, id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pi)
    }
    fun requestExactAlarmPermissionIntent(): Intent? = if (Build.VERSION.SDK_INT >= 31 && !canScheduleExact()) Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).setData(android.net.Uri.parse("package:${context.packageName}")) else null
}
