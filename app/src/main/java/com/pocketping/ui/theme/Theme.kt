package com.pocketping.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val Light = lightColorScheme(primary = Indigo40, secondary = Teal40, tertiary = Rose40)
private val Dark = darkColorScheme(primary = Teal80, secondary = Indigo80, tertiary = Rose80)

@Composable
fun PocketPingTheme(darkMode: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkMode) Dark else Light, content = content)
}
