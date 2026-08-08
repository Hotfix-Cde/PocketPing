package com.pocketping.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale

object ReminderParser {
    private val relative = Regex("(?i)\\bin (\\d+) (minute|minutes|hour|hours|day|days|week|weeks)\\b")
    private val time = Regex("(?i)\\bat\\s+(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)?\\b")
    private val weekday = Regex("(?i)\\b(next\\s+)?(monday|tuesday|wednesday|thursday|friday|saturday|sunday)\\b")

    fun parseBrainDump(raw: String, defaultCategory: ReminderCategory = ReminderCategory.Personal, zoneId: ZoneId = ZoneId.systemDefault()): List<ReminderDraft> = raw.split(Regex("[\\n;•]+" )).mapNotNull { parseLine(it.trim(), defaultCategory, zoneId) }

    fun parseLine(input: String, defaultCategory: ReminderCategory = ReminderCategory.Personal, zoneId: ZoneId = ZoneId.systemDefault()): ReminderDraft? {
        if (input.isBlank()) return null
        val now = ZonedDateTime.now(zoneId)
        val category = when {
            input.contains("school", true) -> ReminderCategory.School
            input.contains("work", true) -> ReminderCategory.Work
            input.contains("shopping", true) || input.contains("buy", true) -> ReminderCategory.Shopping
            input.contains("money", true) || input.contains("pay", true) -> ReminderCategory.Money
            input.contains("home", true) -> ReminderCategory.Home
            else -> defaultCategory
        }
        var due: Long? = null
        relative.find(input)?.let { m ->
            val n=m.groupValues[1].toLong(); due=when(m.groupValues[2].lowercase(Locale.US)) { "minute","minutes"->now.plusMinutes(n); "hour","hours"->now.plusHours(n); "day","days"->now.plusDays(n); else->now.plusWeeks(n) }.toInstant().toEpochMilli()
        }
        if (due == null && input.contains("tomorrow", true)) due = now.plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0).toInstant().toEpochMilli()
        weekday.find(input)?.let { m ->
            val target=DayOfWeek.valueOf(m.groupValues[2].uppercase(Locale.US)); var d=(target.value-now.dayOfWeek.value+7)%7; if(d==0 || m.groupValues[1].isNotBlank()) d=if(d==0)7 else d; due=now.plusDays(d.toLong()).withHour(9).withMinute(0).withSecond(0).withNano(0).toInstant().toEpochMilli()
        }
        time.find(input)?.let { m ->
            val h=m.groupValues[1].toInt(); val min=m.groupValues[2].toIntOrNull()?:0; val suffix=m.groupValues[3].lowercase(Locale.US); val hh=if(suffix=="pm") (h%12)+12 else if(suffix=="am") h%12 else h; var d=now.withHour(hh).withMinute(min).withSecond(0).withNano(0); if(d.isBefore(now)) d=d.plusDays(1); due=d.toInstant().toEpochMilli()
        }
        val cleaned=input.replace(relative," ").replace("tomorrow"," ",true).replace(weekday," ").replace(time," ").replace(Regex("\\s+")," ").trim().trimEnd('.',',')
        return ReminderDraft(cleaned.ifBlank { input }, category=category, dueAtMillis=due, rawText=input)
    }
}
