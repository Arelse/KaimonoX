package com.kaimono.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaimono.app.ui.AppViewModel
import com.kaimono.app.ui.themeOptions

@Composable
fun SettingsScreen(vm: AppViewModel) {
    val user by vm.user.collectAsState()
    val theme by vm.theme.collectAsState()
    var confirmReseed by remember { mutableStateOf(false) }
    var confirmClearHistory by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Card(Modifier.fillMaxWidth()) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Person, null, modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(user ?: "Reader", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    Text("Local account • data stays on this device",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("Appearance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(8.dp)) {
                themeOptions.forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                        RadioButton(selected = theme == option.key,
                            onClick = { vm.setTheme(option.key) })
                        Spacer(Modifier.width(4.dp))
                        Column {
                            Text(option.label, style = MaterialTheme.typography.bodyLarge)
                            Text(option.description, style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("Data", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(8.dp)) {
                TextButton(onClick = { confirmReseed = true }) {
                    Icon(Icons.Filled.Refresh, null); Spacer(Modifier.width(8.dp))
                    Text("Re-seed demo data") }
                TextButton(onClick = { confirmClearHistory = true }) {
                    Icon(Icons.Filled.DeleteSweep, null); Spacer(Modifier.width(8.dp))
                    Text("Clear reading history") }
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("Account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))) {
            TextButton(onClick = { vm.signOut() }) {
                Icon(Icons.AutoMirrored.Filled.Logout, null, tint = MaterialTheme.colorScheme.error)
                Spacer(Modifier.width(8.dp))
                Text("Sign out", color = MaterialTheme.colorScheme.error)
            }
        }
        Spacer(Modifier.height(32.dp))
    }

    if (confirmReseed) {
        AlertDialog(onDismissRequest = { confirmReseed = false },
            title = { Text("Re-seed demo data?") },
            text = { Text("All your local changes will be replaced with the original demo dataset.") },
            confirmButton = {
                TextButton(onClick = { confirmReseed = false; vm.reseed() }) { Text("Re-seed") } },
            dismissButton = { TextButton(onClick = { confirmReseed = false }) { Text("Cancel") } })
    }
    if (confirmClearHistory) {
        AlertDialog(onDismissRequest = { confirmClearHistory = false },
            title = { Text("Clear history?") },
            text = { Text("Your continue-reading list will be emptied.") },
            confirmButton = {
                TextButton(onClick = { confirmClearHistory = false; vm.clearHistory() }) { Text("Clear") } },
            dismissButton = { TextButton(onClick = { confirmClearHistory = false }) { Text("Cancel") } })
    }
}
