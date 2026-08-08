package com.pocketping.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesRepository(context: Context) {
    private val prefs = context.getSharedPreferences("pocketping", Context.MODE_PRIVATE)
    val darkModeFlow: Flow<Boolean> = kotlinx.coroutines.flow.flow {
        emit(prefs.getBoolean("dark_mode", false))
    }
    suspend fun setDarkMode(enabled: Boolean) { prefs.edit().putBoolean("dark_mode", enabled).apply() }
}
