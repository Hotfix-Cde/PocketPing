package com.pocketping.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY completed ASC, COALESCE(dueAtMillis, createdAtMillis) ASC, createdAtMillis DESC")
    fun observeAll(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE completed = 0 ORDER BY COALESCE(dueAtMillis, createdAtMillis) ASC, createdAtMillis DESC")
    fun observeActive(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE completed = 0")
    suspend fun getActiveOnce(): List<ReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity): Long

    @Update
    suspend fun update(reminder: ReminderEntity)

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Query("UPDATE reminders SET completed = :completed, completedAtMillis = :completedAtMillis, updatedAtMillis = :updatedAtMillis WHERE id = :id")
    suspend fun setCompleted(id: Long, completed: Boolean, completedAtMillis: Long?, updatedAtMillis: Long)

    @Query("UPDATE reminders SET lastNotifiedAtMillis = :lastNotifiedAtMillis, updatedAtMillis = :updatedAtMillis WHERE id = :id")
    suspend fun setLastNotified(id: Long, lastNotifiedAtMillis: Long, updatedAtMillis: Long)

    @Query("UPDATE reminders SET dueAtMillis = :dueAtMillis, updatedAtMillis = :updatedAtMillis, completed = 0, completedAtMillis = NULL WHERE id = :id")
    suspend fun updateNextDue(id: Long, dueAtMillis: Long, updatedAtMillis: Long)
}
