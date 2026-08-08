package com.pocketping.domain

import com.pocketping.data.ReminderEntity
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

enum class ReminderCategory(val label: String) {
    School("School"), Work("Work"), Shopping("Shopping"), Money("Money"), Home("Home"), Personal("Personal");
    companion object { fun fromString(value: String): ReminderCategory? = entries.firstOrNull { it.name.equals(value, true) || it.label.equals(value, true) } }
}

enum class RepeatType { NONE, DAILY, WEEKLY, MONTHLY, INTERVAL_DAYS }

data class RepeatConfig(val type: RepeatType = RepeatType.NONE, val interval: Int = 1, val daysMask: Int = 0) {
    val isRecurring: Boolean get() = type != RepeatType.NONE
}

data class ReminderDraft(
    val title: String,
    val note: String = "",
    val category: ReminderCategory = ReminderCategory.Personal,
    val dueAtMillis: Long? = null,
    val repeat: RepeatConfig = RepeatConfig(),
    val rawText: String = title
)

fun RepeatConfig.toEntityFields(): Triple<String, Int, Int> = Triple(type.name, interval, daysMask)
fun ReminderEntity.toRepeatConfig(): RepeatConfig = RepeatConfig(runCatching { RepeatType.valueOf(repeatType) }.getOrDefault(RepeatType.NONE), repeatInterval.coerceAtLeast(1), repeatDaysMask)
fun ReminderEntity.isOverdue(nowMillis: Long = System.currentTimeMillis()): Boolean = !completed && dueAtMillis != null && dueAtMillis < nowMillis

fun ReminderEntity.dueLabel(zoneId: ZoneId = ZoneId.systemDefault()): String {
    val due = dueAtMillis ?: return "No time set"
    val zdt = Instant.ofEpochMilli(due).atZone(zoneId)
    val now = ZonedDateTime.now(zoneId)
    return when {
        zdt.toLocalDate() == now.toLocalDate() -> "Today at ${zdt.format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))}"
        zdt.toLocalDate() == now.toLocalDate().plusDays(1) -> "Tomorrow at ${zdt.format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))}"
        else -> zdt.format(java.time.format.DateTimeFormatter.ofPattern("EEE, d MMM • h:mm a"))
    }
}

fun dayMaskFor(dayOfWeek: DayOfWeek): Int = 1 shl (dayOfWeek.value - 1)
fun maskContains(mask: Int, dayOfWeek: DayOfWeek): Boolean = mask and dayMaskFor(dayOfWeek) != 0
fun weekdayMask(): Int = dayMaskFor(DayOfWeek.MONDAY) or dayMaskFor(DayOfWeek.TUESDAY) or dayMaskFor(DayOfWeek.WEDNESDAY) or dayMaskFor(DayOfWeek.THURSDAY) or dayMaskFor(DayOfWeek.FRIDAY)
