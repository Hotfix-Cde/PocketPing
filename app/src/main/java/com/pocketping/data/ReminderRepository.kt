package com.pocketping.data

import com.pocketping.domain.ReminderDraft
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val dao: ReminderDao, private val scheduler: ReminderScheduler) {
    fun observeAll(): Flow<List<ReminderEntity>> = dao.observeAll()
    suspend fun saveDrafts(drafts: List<ReminderDraft>) {
        drafts.forEach { draft ->
            val id = dao.insert(ReminderEntity(title=draft.title.trim(), note=draft.note.trim(), category=draft.category.name, rawText=draft.rawText, dueAtMillis=draft.dueAtMillis, repeatType=draft.repeat.type.name, repeatInterval=draft.repeat.interval, repeatDaysMask=draft.repeat.daysMask))
            dao.getById(id)?.let(scheduler::schedule)
        }
    }
    suspend fun setCompleted(id: Long, completed: Boolean) {
        dao.setCompleted(id, completed, if (completed) System.currentTimeMillis() else null, System.currentTimeMillis())
        dao.getById(id)?.let { if (completed) scheduler.cancel(id) else scheduler.schedule(it) }
    }
    suspend fun deleteReminder(reminder: ReminderEntity) { dao.delete(reminder); scheduler.cancel(reminder.id); ReminderNotification.cancel(com.pocketping.domain.PocketPingGraph.appContext, reminder.id) }
    suspend fun rescheduleAll() { dao.getActiveOnce().forEach(scheduler::schedule) }
}
