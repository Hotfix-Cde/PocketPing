package com.pocketping.domain

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pocketping.data.PreferencesRepository
import com.pocketping.data.ReminderDatabase
import com.pocketping.data.ReminderRepository
import com.pocketping.data.ReminderScheduler
import com.pocketping.ui.MainViewModel

object PocketPingGraph {
    lateinit var appContext: Context
        private set
    private lateinit var repository: ReminderRepository
    private lateinit var prefs: PreferencesRepository
    private lateinit var scheduler: ReminderScheduler

    fun init(context: Context) {
        appContext = context.applicationContext
        scheduler = ReminderScheduler(appContext)
        repository = ReminderRepository(ReminderDatabase.get(appContext).reminderDao(), scheduler)
        prefs = PreferencesRepository(appContext)
    }

    fun viewModelFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MainViewModel(repository, prefs, scheduler) as T
    }
}
