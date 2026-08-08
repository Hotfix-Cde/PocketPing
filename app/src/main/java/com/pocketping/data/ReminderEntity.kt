package com.pocketping.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val note: String = "",
    val category: String,
    val rawText: String = "",
    val dueAtMillis: Long? = null,
    val repeatType: String = "NONE",
    val repeatInterval: Int = 1,
    val repeatDaysMask: Int = 0,
    val completed: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis(),
    val completedAtMillis: Long? = null,
    val lastNotifiedAtMillis: Long? = null
)
