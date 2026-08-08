package com.pocketping.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pocketping.domain.PocketPingGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra(ReminderNotification.EXTRA_REMINDER_ID, -1L)
        if (id < 0) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = com.pocketping.data.ReminderDatabase.get(context)
                val reminder = db.reminderDao().getById(id)
                if (reminder != null && !reminder.completed) ReminderNotification.show(context, reminder)
            } finally { pending.finish() }
        }
    }
}
