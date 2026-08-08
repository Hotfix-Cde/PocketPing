package com.pocketping

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pocketping.domain.PocketPingGraph
import com.pocketping.domain.dueLabel
import com.pocketping.ui.MainViewModel
import com.pocketping.ui.theme.PocketPingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: MainViewModel = viewModel(factory = PocketPingGraph.viewModelFactory())
            val reminders by vm.reminders.collectAsStateWithLifecycle()
            val dark by vm.darkMode.collectAsStateWithLifecycle()
            val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
            LaunchedEffect(Unit) { if (Build.VERSION.SDK_INT >= 33) permission.launch(Manifest.permission.POST_NOTIFICATIONS) }
            PocketPingTheme(dark) { PocketPingScreen(vm, reminders) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PocketPingScreen(vm: MainViewModel, reminders: List<com.pocketping.data.ReminderEntity>) {
    var text by rememberSaveable { mutableStateOf("") }
    var search by rememberSaveable { mutableStateOf("") }
    val visible = reminders.filter { search.isBlank() || it.title.contains(search, true) || it.category.contains(search, true) }
    Scaffold(
        topBar = { TopAppBar(title = { Column { Text("PocketPing"); Text("Remember it before you forget it.", style = MaterialTheme.typography.labelSmall) } }) },
        floatingActionButton = { FloatingActionButton(onClick = { if (text.isNotBlank()) { vm.saveBrainDump(text); text="" } }) { Icon(Icons.Default.Add, "Add") } }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                OutlinedTextField(value=text, onValueChange={text=it}, modifier=Modifier.fillMaxWidth(), label={Text("What do you need to remember?")}, placeholder={Text("Try: Buy milk tomorrow at 7 PM")})
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value=search, onValueChange={search=it}, modifier=Modifier.fillMaxWidth(), singleLine=true, leadingIcon={Icon(Icons.Default.Search,"Search")}, label={Text("Search reminders")})
            }
            if (visible.isEmpty()) item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(24.dp)) { Text("Your pocket is quiet.", style=MaterialTheme.typography.titleMedium); Text("Add a reminder above and let PocketPing keep it safe.", color=MaterialTheme.colorScheme.onSurfaceVariant) } } }
            items(visible, key={it.id}) { reminder ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement=Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) { Text(reminder.title, style=MaterialTheme.typography.titleMedium); Text(reminder.category, style=MaterialTheme.typography.labelMedium); Text(reminder.dueLabel(), style=MaterialTheme.typography.bodySmall) }
                        IconButton(onClick={ { vm.setCompleted(reminder.id, !reminder.completed) } }) { Icon(Icons.Default.Check, "Complete") }
                    }
                }
            }
        }
    }
}
