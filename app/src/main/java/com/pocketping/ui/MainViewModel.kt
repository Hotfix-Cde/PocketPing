package com.pocketping.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketping.data.PreferencesRepository
import com.pocketping.data.ReminderEntity
import com.pocketping.data.ReminderRepository
import com.pocketping.data.ReminderScheduler
import com.pocketping.domain.ReminderDraft
import com.pocketping.domain.ReminderParser
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ReminderRepository, private val prefs: PreferencesRepository, private val scheduler: ReminderScheduler) : ViewModel() {
    val reminders: StateFlow<List<ReminderEntity>> = repository.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val darkMode: StateFlow<Boolean> = prefs.darkModeFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    fun saveDrafts(drafts: List<ReminderDraft>) = viewModelScope.launch { repository.saveDrafts(drafts) }
    fun saveBrainDump(raw: String) = saveDrafts(ReminderParser.parseBrainDump(raw))
    fun setCompleted(id: Long, completed: Boolean) = viewModelScope.launch { repository.setCompleted(id, completed) }
    fun deleteReminder(reminder: ReminderEntity) = viewModelScope.launch { repository.deleteReminder(reminder) }
    fun setDarkMode(enabled: Boolean) = viewModelScope.launch { prefs.setDarkMode(enabled) }
    fun canScheduleExactAlarms() = scheduler.canScheduleExact()
    fun exactAlarmIntent() = scheduler.requestExactAlarmPermissionIntent()
}
